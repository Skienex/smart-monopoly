package com.github.skienex.monopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skienex.monopoly.game.FieldData;
import com.github.skienex.monopoly.utils.Dice;
import com.github.skienex.monopoly.game.FieldManager;
import com.github.skienex.monopoly.utils.json.Player;
import com.github.skienex.monopoly.utils.json.VariablesScheme;
import com.github.skienex.monopoly.utils.scanner.TextInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameManager {
    private final Dice dice = new Dice();
    private final Map<UUID, Integer> playerIndices = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private boolean started;

    public void startGame() {
        this.started = true;
//        VariablesScheme variables = getJSONVariables();
        if (true) {
            return;
        }

        if (TextInput.input("Würfeln? (y / n): ").equalsIgnoreCase("n")) {
            return;
        }

        Dice.Roll roll = dice.roll();
        System.out.println("Roll: " + roll);

        int allPlayers = players.size();
        System.out.println("All Players: " + allPlayers);

        int player = new Random().nextInt(allPlayers);
        System.out.println("Player" + player);

//        move(getPlayer(player), roll);

        if (roll.pasch()) {
            System.out.println("Roll: Pasch");
            startGame();
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

    public int getPlayerCount() {
        return players.size();
    }

    public void move(Player player, Dice.Roll roll) {
        // TODO: Show roll numbers (number 1 and 2)
        player.addPosition(roll.value());
        System.out.println("Position: " + player.getPosition());
        // TODO: Move player to field

        FieldManager.fieldManager(this, player);
    }

    public FieldData fieldData(Player player) {
        System.out.println("Street: " + player.getPosition());
        if (player.getPosition() % 10 == 0) {
            // Keine Straße und somit nicht bebaubar
            return FieldData.NO_STREET;
        } else if (player.getOwnedStreets()[player.getPosition()]) {
            // Spieler bestitz Feld schon
            return FieldData.OWNED_BY_PLAYER;
        } else {
            for (Player player1 : players) {
                if (player1.ownsStreet(player.getPosition())) {
                    // Feld von anderem Spieler
                    return FieldData.OWNED_BY_OTHER_PLAYER;
                }
            }
            return FieldData.FREE;
        }
    }

    public boolean hasStarted() {
        return started;
    }

    private static VariablesScheme getJSONVariables() throws Exception {
        // Get variables from JSON/Jackson
        String variablesJson = readVariables();
        ObjectMapper objectMapper = new ObjectMapper();
        VariablesScheme variables = objectMapper.readValue(variablesJson, VariablesScheme.class);

        return variables;
    }

    private static String readVariables() throws IOException {
        // Manage stream
        InputStream stream = GameManager.class.getResourceAsStream("/variables.json");
        return new String(stream.readAllBytes());
    }
}
