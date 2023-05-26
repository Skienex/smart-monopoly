package com.github.skienex.monopoly.rest.schemes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ServerPacket.Error.class, name = "ERROR"),
        @JsonSubTypes.Type(value = ServerPacket.Debug.class, name = "DEBUG"),
        @JsonSubTypes.Type(value = ServerPacket.Login.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = ServerPacket.StartGame.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = ServerPacket.FieldData.class, name = "FIELD_DATA"),
        @JsonSubTypes.Type(value = ServerPacket.Roll.class, name = "ROLL"),
        @JsonSubTypes.Type(value = ServerPacket.MovePlayer.class, name = "MOVE_PLAYER"),
})
public abstract class ServerPacket {
    public static class Error extends ServerPacket {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class Debug extends ServerPacket {
        public final String message;

        public Debug(String message) {
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

    public static class FieldData extends ServerPacket {
        public final com.github.skienex.monopoly.game.FieldData data;

        public FieldData(com.github.skienex.monopoly.game.FieldData data) {
            this.data = data;
        }
    }

    public static class Roll extends ServerPacket {
        // Player that rolled the dice
        public final UUID id;
        // First roll
        public final int first;
        // Second roll
        public final int second;

        public Roll(UUID id, int first, int second) {
            this.id = id;
            this.first = first;
            this.second = second;
        }
    }

    public static class MovePlayer extends ServerPacket {
        public final UUID id;
        public final int position;

        public MovePlayer(UUID id, int position) {
            this.id = id;
            this.position = position;
        }
    }
}
