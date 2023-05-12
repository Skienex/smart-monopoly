package com.github.skienex.monopoly.utils.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreetScheme {
    @JsonProperty(required = true)
    public String name;
    @JsonProperty(required = true)
    public int[] cost;
    @JsonProperty(required = true)
    public int[] rent;
}
