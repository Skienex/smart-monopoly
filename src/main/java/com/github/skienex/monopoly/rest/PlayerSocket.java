package com.github.skienex.monopoly.rest;

import com.github.skienex.monopoly.GameManager;
import com.github.skienex.monopoly.rest.schemes.ClientPacket;
import com.github.skienex.monopoly.rest.schemes.ServerPacket;
import com.github.skienex.monopoly.utils.json.Player;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

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
            case ClientPacket.Login login -> {
                boolean admin;
                UUID id;
                synchronized (lock) {
                    if (sessions.containsKey(session)) {
                        // TODO: bereits eingeloggt
                        session.sendAsync(new ServerPacket.Error("Already logged in"));
                        return;
                    }
                    id = UUID.randomUUID();
                    sessions.put(session, id);
                    names.put(id, login.name);
                    admin = manager == null;
                    if (manager == null) {
                        manager = new GameManager();
                    }
                    if (manager.hasStarted()) {
                        // TODO: Spiel bereits gestartet
                        session.sendAsync(new ServerPacket.Error("Game already started"));
                        return;
                    }
                    Player player = manager.addPlayer(id);
                    player.setAdmin(admin);
                }
                // TODO: Erfolg melden
                session.sendAsync(new ServerPacket.Login(admin, id));
            }
            case ClientPacket.StartGame startGame -> {
                synchronized (lock) {
                    UUID id = sessions.get(session);
                    if (id == null) {
                        // TODO: nicht eingeloggt
                        session.sendAsync(new ServerPacket.Error("Not logged in"));
                        return;
                    }
                    if (manager == null) {
                        // TODO: kein manager existiert
                        session.sendAsync(new ServerPacket.Error("No game exists currently"));
                        return;
                    }
                    if (manager.hasStarted()) {
                        // TODO: game bereits gestartet
                        session.sendAsync(new ServerPacket.Error("Game has already started"));
                        return;
                    }
                    Player player = manager.getPlayer(id);
                    if (!player.isAdmin()) {
                        // TODO: kein admin
                        session.sendAsync(new ServerPacket.Error("No permission to start game"));
                        return;
                    }
                    manager.startGame();
                }
                // TODO: start with first move
                broadcaster.broadcastAsync(new ServerPacket.StartGame(names));
            }
            default -> {
                // TODO: fehler
                session.sendAsync(new ServerPacket.Error("Packet not implemented"));
            }
        }
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        // TODO
    }
}
