package com.github.skienex.monopoly.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ClientPacket.KeepAlive.class, name = "KEEP_ALIVE"),
        @JsonSubTypes.Type(value = ClientPacket.Login.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = ClientPacket.StartGame.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = ClientPacket.RollDice.class, name = "ROLL_DICE"),
        @JsonSubTypes.Type(value = ClientPacket.PayRent.class, name = "PAY_RENT"),
        @JsonSubTypes.Type(value = ClientPacket.GetRent.class, name = "GET_RENT"),
        @JsonSubTypes.Type(value = ClientPacket.BuyStreet.class, name = "BUY_STREET"),
        @JsonSubTypes.Type(value = ClientPacket.SellStreet.class, name = "SELL_STREET"), // Straße komplett verkaufen
        @JsonSubTypes.Type(value = ClientPacket.BuyHouse.class, name = "BUY_HOUSE"),
        @JsonSubTypes.Type(value = ClientPacket.SellHouse.class, name = "SELL_HOUSE"),
        @JsonSubTypes.Type(value = ClientPacket.SpecialField.class, name = "SPECIAL_FIELD"),
})
public abstract class ClientPacket {
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

    public static class PayRent extends ClientPacket {
    }

    public static class GetRent extends ClientPacket {
    }

    public static class BuyStreet extends ClientPacket {
    }

    public static class SellStreet extends ClientPacket {
    }

    public static class BuyHouse extends ClientPacket {
    }

    public static class SellHouse extends ClientPacket {
    }

    public static class SpecialField extends ClientPacket {
    }
}
