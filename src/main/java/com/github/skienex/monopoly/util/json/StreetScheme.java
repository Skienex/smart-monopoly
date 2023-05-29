package com.github.skienex.monopoly.util.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.skienex.monopoly.game.StreetGroup;

public record StreetScheme(
        @JsonProperty(required = true) String name,
        @JsonProperty int[] cost,
        @JsonProperty int[] rent,
        @JsonProperty(required = true) StreetGroup group
) {
}
