package com.github.skienex.monopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skienex.monopoly.game.*;
import com.github.skienex.monopoly.util.json.StreetScheme;
import com.github.skienex.monopoly.util.json.VariablesScheme;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameManager {
    private final Dice dice = new Dice();
    private final Map<UUID, Integer> playerIndices = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private final Street[] streets = new Street[40];
    private boolean started;
    private int activePlayer;

    private GameManager(VariablesScheme variablesScheme) {
        for (int i = 0; i < variablesScheme.streets().length; i++) {
            StreetScheme scheme = variablesScheme.streets()[i];
            streets[i] = new Street(scheme.name(), scheme.cost(), scheme.rent(), scheme.group());
        }
    }

    public static GameManager create() {
        try {
            return new GameManager(getJSONVariables());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startGame() {
        this.started = true;
        Collections.shuffle(players);
        playerIndices.clear();
        for (int i = 0; i < players.size(); i++) {
            playerIndices.put(players.get(i).getId(), i);
        }
    }

    public Player getPlayer(UUID id) {
        Integer index = playerIndices.get(id);
        if (index == null) {
            return null;
        }
        return players.get(index);
    }

    public Player addPlayer(UUID id) {
        Player player = new Player(id);
        if (players.isEmpty()) {
            player.setAdmin(true);
        }
        playerIndices.put(id, players.size());
        players.add(player);
        return player;
    }

    public Player getActivePlayer() {
        return players.get(activePlayer);
    }

    public Dice getDice() {
        return dice;
    }

    public void move(Player player, Dice.Roll roll) {
        // TODO: Show roll numbers (number 1 and 2)
        player.addPosition(roll.value());
        System.out.println("Position: " + player.getPosition());
        // TODO: Move player to field
        FieldManager.fieldManager(this, player);
    }

    public FieldData fieldData(Player player) {
        int pos = player.getPosition();
        Street street = streets[pos];
        if (pos % 10 == 0) {
            // Keine Straße und somit nicht bebaubar
            return new FieldData.NoStreet(pos, street.name());
        } else if (street.owner() == player) {
            if (street.level() == 6) {
                return new FieldData.OwnedByPlayer(pos,
                        street.name(), player.getId(), street.rent()[1],
                        -1, street.cost()[5] - 1, true);
            }
            // Spieler bestitz Feld schon
            return new FieldData.OwnedByPlayer(pos,
                    street.name(), player.getId(), street.rent()[1],
                    street.cost()[street.level()], street.cost()[street.level()] - 1, false);
        } else {
            if (street.owner() != null) {
                return new FieldData.OwnedByOtherPlayer(pos, street.name(), street.owner().getId(),
                        street.rent()[1]);
            }
            return new FieldData.Free(pos, street.name(), street.cost()[0]);
        }
    }

    public Status buyStreet(int index, Player player) {
        Street street = streets[index];
        if (player.getMoney() < street.cost()[0]) {
            return Status.NOT_ENOUGH_MONEY;
        }
        if (street.level() > 0) {
            return Status.STREET_ALREADY_OWNED;
        }
        player.subtractMoney(street.cost()[0]);
        street.levelUp();
        street.owner(player);
        return Status.SUCCESS;
    }

    public Status sellStreet(int index, Player player) {
        Street street = streets[index];
        if (street.owner() == player && street.level() > 0) {
            player.addMoney((street.cost()[0] / 2));
            street.owner(null);
            street.levelDown();
            return Status.SUCCESS;
        }
        return Status.NOT_YOUR_STREET;
    }

    public Status buyHouse() {
        return Status.PLACEHOLDER;
    }

    public Status sellHouse() {
        return Status.PLACEHOLDER;
    }

    public boolean hasStarted() {
        return started;
    }

    private static VariablesScheme getJSONVariables() throws Exception {
        // Get variables from JSON/Jackson
        String variablesJson = readVariables();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(variablesJson, VariablesScheme.class);
    }

    private static String readVariables() throws IOException {
        // Manage stream
        InputStream stream = GameManager.class.getResourceAsStream("/variables.json");
        return new String(stream.readAllBytes());
    }
}
