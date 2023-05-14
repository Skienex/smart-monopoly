package com.github.skienex.monopoly.rest.schemes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ClientPacket.Login.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = ClientPacket.StartGame.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = ClientPacket.RollDice.class, name = "ROLL_DICE"),
})
public abstract class ClientPacket {
    public static class Login extends ClientPacket {
        public final String name;

        @JsonCreator
        public Login(@JsonProperty(value = "name", required = true) String name) {
            this.name = name;
        }
    }

    public static class StartGame extends ClientPacket {
    }

    public static class RollDice extends ClientPacket {
    }
}
