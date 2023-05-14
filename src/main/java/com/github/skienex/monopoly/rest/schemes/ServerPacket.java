package com.github.skienex.monopoly.rest.schemes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ServerPacket.Error.class, name = "ERROR"),
        @JsonSubTypes.Type(value = ServerPacket.Login.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = ServerPacket.StartGame.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = ServerPacket.MovePlayer.class, name = "MOVE_PLAYER"),
})
public abstract class ServerPacket {
    public static class Error extends ServerPacket {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class Login extends ServerPacket {
        public final boolean admin;
        public final UUID yourId;

        public Login(boolean admin, UUID yourId) {
            this.admin = admin;
            this.yourId = yourId;
        }
    }

    public static class StartGame extends ServerPacket {
        public final Map<UUID, String> players;

        public StartGame(Map<UUID, String> players) {
            this.players = players;
        }
    }

    public static class MovePlayer extends ServerPacket {
        public final UUID uuid;
        public final int roll;
        public final boolean pasch;
        public final int position;

        public MovePlayer(String playerName, UUID uuid, int roll, boolean pasch, int position) {
            this.uuid = uuid;
            this.roll = roll;
            this.pasch = pasch;
            this.position = position;
        }
    }
}
