package com.github.skienex.monopoly.rest;

import com.github.skienex.monopoly.GameManager;
import com.github.skienex.monopoly.rest.schemes.CreateRequestScheme;
import com.github.skienex.monopoly.rest.schemes.ResponseScheme;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

@Controller("/api")
public class RestEndpoint {
    private GameManager manager;

    @Post(value = "/create")
    public HttpResponse<ResponseScheme> create(@Body CreateRequestScheme body) {
        if (manager != null) {
            return HttpResponse.status(HttpStatus.IM_USED)
                    .body(new ResponseScheme("Manager already exists"));
        }
        manager = new GameManager();
        return HttpResponse.status(HttpStatus.CREATED)
                .body(new ResponseScheme("Successfully created manager"));
    }

    @Get(value = "/websocket.html", produces = MediaType.TEXT_HTML)
    public String websocket() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Title</title>
                </head>
                <body>
                <div id="log">

                </div>

                <button onclick="onLogin()" id="login">Login</button>
                <button onclick="onStart()" id="start">Start Game</button>

                <script>
                    const webSocket = new WebSocket('ws://192.168.178.30:8080/game')
                    let connected = false

                    const log = document.getElementById("log")
                    const login = document.getElementById("login")
                    const game = document.getElementById("start")

                    webSocket.onopen = (event) => {
                        connected = true
                    }

                    webSocket.onmessage = (event) => {
                        const data = JSON.parse(event.data)
                        switch (data.type) {
                            case 'ERROR': {
                                log.innerText += `Error: ${data.message}\\n`
                                break
                            }
                            case 'LOGIN': {
                                log.innerText += `Logged in {admin: ${data.admin}, UUID: ${data.yourId}}\\n`
                                break
                            }
                            case 'START_GAME': {
                                log.innerText += `Started Game. Players: ${JSON.stringify(data.players)}\\n`
                                break
                            }
                            case 'MOVE_PLAYER': {

                                break
                            }
                        }
                    }

                    webSocket.onclose = (event) => {
                        connected = false
                        log.innerText += `Disconnected\\n`
                    }

                    function onLogin() {
                        webSocket.send(JSON.stringify({
                            type: 'LOGIN',
                            name: 'Skienex'
                        }))
                    }

                    function onStart() {
                        webSocket.send(JSON.stringify({
                            type: 'START_GAME'
                        }))
                    }
                </script>
                </body>
                </html>
                """;
    }

//    @Post(value = "/register")
//    public HttpResponse<ResponseScheme> register(@Body RegisterRequestScheme body) {
//        if (manager == null) {
//            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(new ResponseScheme("No manager available"));
//        }
//        int playerId = manager.addPlayer(body.displayName);
//        return HttpResponse.status(HttpStatus.OK).body(new ResponseScheme("Successfully registered user"));
//    }
}
