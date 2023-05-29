package com.github.skienex.monopoly.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = FieldData.OwnedByPlayer.class, name = "OWNED_BY_PLAYER"),
        @JsonSubTypes.Type(value = FieldData.OwnedByOtherPlayer.class, name = "OWNED_BY_OTHER_PLAYER"),
        @JsonSubTypes.Type(value = FieldData.Free.class, name = "FREE"),
        @JsonSubTypes.Type(value = FieldData.NoStreet.class, name = "NO_STREET"),
})
public abstract class FieldData {
    public static class OwnedByPlayer extends FieldData {
        public final int id;
        public final String name;
        public final UUID owner;
        public final int rent;
        public final int buy;
        public final int sell;
        public final boolean maxed;

        public OwnedByPlayer(int id, String name, UUID owner, int rent, int buy, int sell, boolean maxed) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.rent = rent;
            this.buy = buy;
            this.sell = sell;
            this.maxed = maxed;
        }
    }

    public static class OwnedByOtherPlayer extends FieldData {
        public final int id;
        public final String name;
        public final UUID owner;
        public final int rent;

        public OwnedByOtherPlayer(int id, String name, UUID owner, int rent) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.rent = rent;
        }
    }

    public static class Free extends FieldData {
        public final int id;
        public final String name;
        public final int price;

        public Free(int id, String name, int price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    public static class NoStreet extends FieldData {
        public final int id;
        public final String name;

        public NoStreet(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
