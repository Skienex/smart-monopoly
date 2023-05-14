package com.github.skienex.monopoly.rest.schemes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseScheme {
    private final String message;

    public ResponseScheme(@JsonProperty String message) {
        this.message = message;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }
}
