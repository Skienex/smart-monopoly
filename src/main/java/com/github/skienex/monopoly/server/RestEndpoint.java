package com.github.skienex.monopoly.server;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.io.IOException;

@Controller
public class RestEndpoint {
    private final String game = new String(getClass().getResourceAsStream("/game.html").readAllBytes());

    public RestEndpoint() throws IOException {
    }

    @Get(value = "/game.html", produces = MediaType.TEXT_HTML)
    public String game() {
        return game;
    }
}
