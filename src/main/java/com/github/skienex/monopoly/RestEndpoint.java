package com.github.skienex.monopoly;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

@Controller("/api")
public class RestEndpoint {
    @Get(value = "/{variable}", processes = MediaType.TEXT_JSON)
    public String abc(@PathVariable Integer variable) {
        System.out.println(variable);
        return """
    {"status": "OK"}
    """;
    }
}
