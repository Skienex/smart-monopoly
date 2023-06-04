package com.github.skienex.monopoly.server;

import com.github.skienex.monopoly.GameManager;
import com.github.skienex.monopoly.game.FieldData;
import com.github.skienex.monopoly.game.Player;
import com.github.skienex.monopoly.game.Dice;
import com.github.skienex.monopoly.game.Status;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ServerWebSocket("/game")
public class PlayerSocket {
    private final WebSocketBroadcaster broadcaster;
    private final Map<WebSocketSession, UUID> sessions;
    private final Map<UUID, String> names;
    private final Object lock;
    private GameManager manager;

    public PlayerSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
        this.sessions = new HashMap<>();
        this.names = new HashMap<>();
        this.lock = new Object();
    }

    @OnOpen
    public void onOpen(WebSocketSession session) {
    }

    @OnMessage
    public void onMessage(ClientPacket packet, WebSocketSession session) {
        switch (packet) {
            case ClientPacket.KeepAlive keepAlive -> {}
            case ClientPacket.Login login -> {
                boolean admin;
                UUID id;
                synchronized (lock) {
                    if (sessions.containsKey(session)) {
                        session.sendAsync(ServerPacket.error(Status.ALREADY_LOGGED_IN));
                        return;
                    }
                    if (login.name.isBlank()) {
                        session.sendAsync(ServerPacket.error(Status.NO_NAME_DEFINED));
                    }
                    id = UUID.randomUUID();
                    sessions.put(session, id);
                    names.put(id, login.name);
                    admin = manager == null;
                    if (manager == null) {
                        manager = GameManager.create();
                        if (manager == null) {
                            session.sendAsync(ServerPacket.error(Status.UNABLE_TO_CREATE_GAME));
                            return;
                        }
                    }
                    if (manager.hasStarted()) {
                        session.sendAsync(ServerPacket.error(Status.GAME_ALREADY_STARTED));
                        return;
                    }
                    Player player = manager.addPlayer(id);
                    player.setAdmin(admin);
                }
                session.sendAsync(new ServerPacket.Login(admin, id));
                broadcaster.broadcastAsync(new ServerPacket.UpdatePlayers(names));
            }
            case ClientPacket.StartGame startGame -> {
                synchronized (lock) {
                    UUID id = sessions.get(session);
                    if (id == null) {
                        session.sendAsync(ServerPacket.error(Status.NOT_LOGGED_IN));
                        return;
                    }
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    if (manager.hasStarted()) {
                        session.sendAsync(ServerPacket.error(Status.GAME_ALREADY_STARTED));
                        return;
                    }
                    Player player = manager.getPlayer(id);
                    if (!player.isAdmin()) {
                        session.sendAsync(ServerPacket.error(Status.NO_PERMISSION));
                        return;
                    }
                    manager.startGame();
                    broadcaster.broadcastAsync(new ServerPacket.StartGame(names, manager.variables()));
                }
            }
            case ClientPacket.RollDice rollDice -> {
                synchronized (lock) {
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    UUID id = sessions.get(session);
                    Player activePlayer = manager.activePlayer();
                    if (!activePlayer.getId().equals(id)) {
                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
                        return;
                    }
                    Dice.Roll roll = manager.dice().roll();
                    final int old_position = manager.getPlayer(activePlayer.getId()).getPosition();
                    session.sendAsync(new ServerPacket.Roll(id, roll.firstNumber(), roll.secondNumber()));
                    manager.move(activePlayer, roll);
                    session.sendAsync(new ServerPacket.MovePlayer(activePlayer.getId(),
                            manager.getPlayer(activePlayer.getId()).getPosition(), old_position));
                    FieldData data = manager.fieldData(activePlayer);
                    session.sendAsync(new ServerPacket.FieldData(data));
//                    manager.incrementActivePlayer();
//                    broadcaster.broadcastAsync(new ServerPacket.MovePlayer(id, activePlayer.getPosition()));
//                    broadcaster.broadcastAsync(new ServerPacket.NextRoll(id, roll.firstNumber(), roll.secondNumber(),
//                            manager.getActivePlayer().getId()));
                }
            }
//            case ClientPacket.PayRent payRent -> {
//                synchronized (lock) {
//                    if (manager == null) {
//                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
//                        return;
//                    }
//                    UUID id = sessions.get(session);
//                    Player activePlayer = manager.activePlayer();
//                    if (!activePlayer.getId().equals(id)) {
//                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
//                        return;
//                    }
//                    Status status = manager.payRent(activePlayer);
//                    if (status != Status.SUCCESS) {
//                        session.sendAsync(ServerPacket.error(status));
//                        return;
//                    }
//                }
//            }
            case ClientPacket.BuyStreet buyStreet -> {
                synchronized (lock) {
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    UUID id = sessions.get(session);
                    Player activePlayer = manager.activePlayer();
                    if (!activePlayer.getId().equals(id)) {
                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
                        return;
                    }
                    Status status = manager.buyStreet(activePlayer);
                    session.sendAsync(ServerPacket.error(status));
                    session.sendAsync(new ServerPacket.Money(activePlayer.getMoney()));

                    FieldData data = manager.fieldData(activePlayer);
                    session.sendAsync(new ServerPacket.FieldData(data));
                }
            }
            case ClientPacket.SellStreet sellStreet -> {
                synchronized (lock) {
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    UUID id = sessions.get(session);
                    Player activePlayer = manager.activePlayer();
                    if (!activePlayer.getId().equals(id)) {
                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
                        return;
                    }
                    Status status = manager.sellStreet(activePlayer);
                    session.sendAsync(ServerPacket.error(status));
                    session.sendAsync(new ServerPacket.Money(activePlayer.getMoney()));

                    FieldData data = manager.fieldData(activePlayer);
                    session.sendAsync(new ServerPacket.FieldData(data));
                }
            }
            case ClientPacket.BuyHouse buyHouse -> {
                synchronized (lock) {
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    UUID id = sessions.get(session);
                    Player activePlayer = manager.activePlayer();
                    if (!activePlayer.getId().equals(id)) {
                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
                        return;
                    }
                    Status status = manager.buyHouse(activePlayer);
                    session.sendAsync(ServerPacket.error(status));
                    session.sendAsync(new ServerPacket.Money(activePlayer.getMoney()));

                    FieldData data = manager.fieldData(activePlayer);
                    session.sendAsync(new ServerPacket.FieldData(data));
                }
            }
            case ClientPacket.SellHouse sellHouse -> {
                synchronized (lock) {
                    if (manager == null) {
                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
                        return;
                    }
                    UUID id = sessions.get(session);
                    Player activePlayer = manager.activePlayer();
                    if (!activePlayer.getId().equals(id)) {
                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
                        return;
                    }
                    Status status = manager.sellHouse(activePlayer);
                    session.sendAsync(ServerPacket.error(status));
                    session.sendAsync(new ServerPacket.Money(activePlayer.getMoney()));

                    FieldData data = manager.fieldData(activePlayer);
                    session.sendAsync(new ServerPacket.FieldData(data));
                }
            }
//            case ClientPacket.SpecialField specialField -> {
//                synchronized (lock) {
//                    if (manager == null) {
//                        session.sendAsync(ServerPacket.error(Status.NO_GAME_EXISTS));
//                        return;
//                    }
//                    UUID id = sessions.get(session);
//                    Player activePlayer = manager.activePlayer();
//                    if (!activePlayer.getId().equals(id)) {
//                        session.sendAsync(ServerPacket.error(Status.NOT_YOUR_TURN));
//                        return;
//                    }
//                    session.sendAsync(new ServerPacket.Debug(manager.specialField(activePlayer)));
//                    }
//                }
            default -> session.sendAsync(ServerPacket.error(Status.PACKET_NOT_IMPLEMENTED));
        }
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        // TODO: Mark session as closed and reset all streets from the player and set money to 0.
        broadcaster.broadcastAsync(ServerPacket.error(Status.PLAYER_DISCONNECTED));
    }
}
