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
    private final VariablesScheme variables;
    private final Street[] streets = new Street[40];
    private boolean started;
    private int activePlayer;

    private GameManager(VariablesScheme variablesScheme) {
        this.variables = variablesScheme;
        for (int i = 0; i < variablesScheme.streets().length; i++) {
            StreetScheme scheme = variablesScheme.streets()[i];
            streets[i] = new Street(scheme.name(), scheme.cost(), scheme.rent(), scheme.group());
        }
    }

    public static GameManager create() {
        try {
            return new GameManager(getJSONVariables());
        } catch (Exception e) {
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

    public Player activePlayer() {
        return players.get(activePlayer);
    }

    public Dice dice() {
        return dice;
    }

    public VariablesScheme variables() {
        return variables;
    }

    public void move(Player player, Dice.Roll roll) {
        player.addPosition(roll.value());
    }

    public FieldData fieldData(Player player) {
        int pos = player.getPosition();
        Street street = streets[pos];
        if (pos == 0) {
            return new FieldData.SpecialField(pos, street.name());
        } else if (pos == 10) {
            return new FieldData.SpecialField(pos, street.name());
        } else if (pos == 20) {
            return new FieldData.SpecialField(pos, street.name());
        } else if (pos == 30) {
            return new FieldData.SpecialField(pos, street.name());
        } else if (street.owner() == player) {
            if (street.level() == 6) {
                return new FieldData.OwnedByPlayer(pos,
                        street.name(), player.getId(), street.rent()[1], -1, street.cost()[5] / 2, true);
            }
            // Spieler bestitz Feld schon
            return new FieldData.OwnedByPlayer(pos,
                    street.name(), player.getId(), street.rent()[street.level() - 1],
                    street.cost()[street.level()], street.cost()[street.level() - 1] / 2, false);
        } else {
            if (street.owner() != null) {
                payRent(player);
                return new FieldData.OwnedByOtherPlayer(pos, street.name(), street.owner().getId(),
                        street.rent()[street.level() - 1]);
            }
            return new FieldData.Free(pos, street.name(), street.cost()[0]);
        }
    }

    public Status payRent(Player player) {
        Street street = streets[player.getPosition()];
        if (street.owner() == player) {
            return Status.YOUR_STREET;
        }
        if (player.getMoney() < street.rent()[street.level() - 1]) {
            return Status.NOT_ENOUGH_MONEY;
            // TODO: Möglichkeit Straßen/Häuser zu verkaufen anzeigen
        }
        player.subtractMoney(street.rent()[street.level() - 1]);

        return Status.SUCCESS;
    }

    public Status buyStreet(Player player) {
        Street street = streets[player.getPosition()];
        // TODO: Check with street group if the street is buyable and not a traion station or utility
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

    public Status sellStreet(Player player) {
        Street street = streets[player.getPosition()];
        if (street.owner() == player && street.level() > 0) {
            player.addMoney((street.cost()[0] / 2));
            street.owner(null);
            street.levelDown();
            return Status.SUCCESS;
        }
        return Status.NOT_YOUR_STREET;
    }

    public Status buyHouse(Player player) {
        Street street = streets[player.getPosition()];
        if (street.owner() != player) {
            return Status.NOT_YOUR_STREET;
        }
        if (street.level() == 6) {
            return Status.STREET_MAXED;
        }
        if (player.getMoney() < street.cost()[street.level()]) {
            return Status.NOT_ENOUGH_MONEY;
        }
        int nextLevel = street.level() + 1;
        for (Street street1 : streets) {
            if (street1.group() != street.group()) {
                continue;
            }
            if (street1.owner() != player) {
                return Status.INCOMPLETE_GROUP;
            }
            if (Math.abs(street1.level() - nextLevel) > 1) {
                return Status.DISMANTLE_OTHER_THINKS_FIRST;
            }
        }
        player.subtractMoney(street.cost()[street.level()]);
        street.levelUp();
        return Status.SUCCESS;
    }

    public Status sellHouse(Player player) {
        Street street = streets[player.getPosition()];
        if (street.owner() != player) {
            return Status.NOT_YOUR_STREET;
        }
        if (street.level() < 2) {
            return Status.NO_HOUSE_ON_STREET;
        }
        int nextLevel = street.level() - 1;
        for (Street street1 : streets) {
            if (street1.group() != street.group()) {
                continue;
            }
            if (street1.owner() != player) {
                return Status.INCOMPLETE_GROUP;
            }
            if (Math.abs(street1.level() - nextLevel) > 1) {
                return Status.DISMANTLE_OTHER_THINKS_FIRST;
            }
        }
        player.addMoney(street.cost()[street.level()] / 2);
        street.levelDown();
        return Status.SUCCESS;
    }

    public String specialField(Player player) {
        Random random = new Random();

        return variables.specialActions()[random.nextInt(Integer.parseInt("21"))];
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

    public void incrementActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) {
            activePlayer = 0;
        }
    }
}
