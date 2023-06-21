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
    private boolean activePlayerHasRolled;
    private boolean payMoneyQueue;
    private int payMoneyQueueAmount;
    private int taxes = 0;

    private GameManager(VariablesScheme variablesScheme) {
        this.variables = variablesScheme;
        for (int i = 0; i < variablesScheme.streets().length; i++) {
            StreetScheme scheme = variablesScheme.streets()[i];
            streets[i] = new Street(scheme.name(), scheme.cost(), scheme.rent(),
                    scheme.group(), scheme.mortgaged_sell(),
                    scheme.mortgaged_rebuy());
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

    public boolean isActivePlayerHasRolled() {
        return activePlayerHasRolled;
    }

    public void setActivePlayerHasRolled(boolean activePlayerHasRolled) {
        this.activePlayerHasRolled = activePlayerHasRolled;
    }

    public void payMoneyQueue(int amount, boolean payMoneyQueue) {
        this.payMoneyQueue = payMoneyQueue;
        this.payMoneyQueueAmount = amount;
    }

    public boolean isPayMoneyQueue() {
        return payMoneyQueue;
    }

    public int getPayQueueMoney() {
        return payMoneyQueueAmount;
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
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 10) {
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 20) {
            // Frei Parken: Steuern ausbezahlen
            player.addMoney(taxes);
            taxes = 0;
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 30) {
            // TODO: Player ins Gefängnis
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 2 || pos == 17 || pos == 33) {
            // TODO: Community Field
            int random = new Random().nextInt(15);
            switch (random) {
                case 0 -> {
                    if (player.getMoney() <= 50) {

                    }
                    return new FieldData.SpecialField(pos, street.name(),
                            "Schulgeld. Zahlen Sie M 50.");
                }
                case 1 -> {
                    player.subtractMoney(200);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Urlaubsgeld! Sie erhalten M 100.");
                }
                case 2 -> {
                    player.addMoney(50);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Ihre Lebensversicherung wird fällig. Sie erhalten M 100.");
                }
                case 3 -> {
                    player.subtractMoney(50);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Arzt-Kosten. Zahlen Sie M 50.");
                }
                case 4 -> {
                    player.addMoney(100);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Einkommenssteuerrückerstattung. Sie erhalten M 20.");
                }
                case 5 -> {
                    player.subtractMoney(100);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Krankenhausgebühren. Zahlen Sie M 100.");
                }
                case 6 -> {
                    player.addMoney(20);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Gehen Sie in das Gefängnis. Begeben Sie sich direkt dorthin. Gehen Sie nicht über Los. Ziehen Sie nicht M 200 ein.");
                }
                case 7 -> {
                    player.subtractMoney(20);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Sie erhalten auf Vorzugs-Aktien 7% Dividende: M 25.");
                }
                case 8 -> {
                    player.addMoney(10);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Sie haben Geburtstag. Jeder Spieler schenkt Ihnen M 10.");
                }
                case 9 -> {
                    player.subtractMoney(10);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Sie erben M 100.");
                }
                case 10 -> {
                    player.addMoney(100);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Aus Lagerverkäufen erhalten Sie M 50.");
                }
                case 11 -> {
                    player.subtractMoney(100);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Zweiter Preis im Schönheitswettbewerb. Sie erhalten M 10.");
                }
                case 12 -> {
                    player.addMoney(50);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Sie werden zu Straßenausbesserungsarbeiten herangezogen. Zahlen Sie M 40 je Haus und M 115 je Hotel an die Bank.");
                }
                case 13 -> {
                    player.subtractMoney(50);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Rücken Sie vor bis auf Los. (Ziehe M 200 ein).");
                }
                case 14 -> {
                    player.addMoney(40);
                    return new FieldData.SpecialField(pos, street.name(),
                            "Bank-Irrtum zu Ihren Gunsten. Ziehen Sie M 200 ein.");
                }
            }
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 7 || pos == 22 || pos == 36) {
            // TODO: Event Field
            return new FieldData.SpecialField(pos, street.name(), "");
        } else if (pos == 4 || pos == 38) {
            // Steuern bezahlen
            taxes += 200;
            player.subtractMoney(200);
            return new FieldData.SpecialField(pos, street.name(), "Steuern bezahlen. Sie erhalten M 200.");
        } else if (street.owner() == player) {
            if (street.level() == 6) {
                return new FieldData.OwnedByPlayer(pos, street.name(),
                        player.getId(), street.rent()[1], -1,
                        street.cost()[5] / 2, true);
            }
            // Spieler bestitz Feld schon
            return new FieldData.OwnedByPlayer(pos, street.name(),
                    player.getId(), street.rent()[street.level() - 1],
                    street.cost()[street.level()],
                    street.cost()[street.level() - 1] / 2, false);
        } else {
            if (street.owner() != null) {
                payRent(player);
                return new FieldData.OwnedByOtherPlayer(pos, street.name(),
                        street.owner().getId(),
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
            payMoneyQueue(street.rent()[street.level() - 1] ,true);
            return Status.NOT_ENOUGH_MONEY;
            // TODO: Möglichkeit Straßen/Häuser zu verkaufen anzeigen
        }
        payMoneyQueue(0 ,false);
        player.subtractMoney(street.rent()[street.level() - 1]);

        return Status.SUCCESS;
    }

    public Status buyStreet(Player player) {
        Street street = streets[player.getPosition()];
        if (street.group() == StreetGroup.COMMUNITY_FIELD || street.group() == StreetGroup.EVENT_FIELD || street.group() == StreetGroup.TAX
                || street.group() == StreetGroup.FREE_PARKING || street.group() == StreetGroup.PRISON || street.group() == StreetGroup.GO_TO_PRISON
                || street.group() == StreetGroup.START ) {
            return Status.STREET_NOT_BUYABLE;
        }
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

    public Status sellStreet(Player player, int pos) {
        Street street = streets[pos];
        if (street.owner() == player && street.level() > 0) {
            payMoneyQueue((street.cost()[0] / 2) ,false);
            player.addMoney((street.cost()[0] / 2));
            street.owner(null);
            street.levelDown();
            return Status.SUCCESS;
        }
        return Status.NOT_YOUR_STREET;
    }

    public Status buyHouse(Player player, int pos) {
        Street street = streets[pos];
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

    public Status sellHouse(Player player, int pos) {
        Street street = streets[pos];
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
        InputStream stream = GameManager.class.getResourceAsStream(
                "/variables.json");
        return new String(stream.readAllBytes());
    }

    public void incrementActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) {
            activePlayer = 0;
        }
    }

    public Street[] getAllStreets() {
        return streets;
    }
}
