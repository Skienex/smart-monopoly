package com.github.skienex.monopoly.util.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VariablesScheme(@JsonProperty(required = true) StreetScheme[] streets) {
}
