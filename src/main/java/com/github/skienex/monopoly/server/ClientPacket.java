package com.github.skienex.monopoly.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ClientPacket.Handshake.class, name = "HANDSHAKE"),
        @JsonSubTypes.Type(value = ClientPacket.KeepAlive.class, name = "KEEP_ALIVE"),
        @JsonSubTypes.Type(value = ClientPacket.Login.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = ClientPacket.StartGame.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = ClientPacket.RollDice.class, name = "ROLL_DICE"),
        @JsonSubTypes.Type(value = ClientPacket.BuyStreet.class, name = "BUY_STREET"),
        @JsonSubTypes.Type(value = ClientPacket.SellStreet.class, name = "SELL_STREET"), // Straße komplett verkaufen
        @JsonSubTypes.Type(value = ClientPacket.BuyHouse.class, name = "BUY_HOUSE"),
        @JsonSubTypes.Type(value = ClientPacket.SellHouse.class, name = "SELL_HOUSE"),
        @JsonSubTypes.Type(value = ClientPacket.Confirm.class, name = "CONFIRM"),
        @JsonSubTypes.Type(value = ClientPacket.EndTurn.class, name = "END_TURN"),
})
public abstract class ClientPacket {
    public static class Handshake extends ClientPacket {
    }

    public static class KeepAlive extends ClientPacket {
    }

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

    public static class BuyStreet extends ClientPacket {
    }

    public static class SellStreet extends ClientPacket {
        public final int pos;

        public SellStreet(int pos) {
            this.pos = pos;
        }
    }

    public static class BuyHouse extends ClientPacket {
        public final int pos;

        public BuyHouse(int pos) {
            this.pos = pos;
        }
    }

    public static class SellHouse extends ClientPacket {
        public final int pos;

        public SellHouse(@JsonProperty(defaultValue = "-1") int pos) {
            this.pos = pos;
        }
    }

    public static class Confirm extends ClientPacket {
    }

    public static class EndTurn extends ClientPacket {
    }
}
