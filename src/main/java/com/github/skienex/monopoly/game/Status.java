package com.github.skienex.monopoly.game;

public enum Status {
    SUCCESS("Success"),
    ALREADY_LOGGED_IN("You are already logged in"),
    UNABLE_TO_CREATE_GAME("Unable to create game"),
    GAME_ALREADY_STARTED("The game has already started"),
    NOT_LOGGED_IN("You are not logged in"),
    NO_GAME_EXISTS("Not game currently exists"),
    NO_PERMISSION("You do not have sufficient permissions"),
    NOT_YOUR_TURN("This is not your turn"),
    NOT_ENOUGH_MONEY("You currently dont have enought money to do this"), // (broke)
    NOT_YOUR_STREET("The Street is not yours"),
    STREET_NOT_BUYABLE("Street is not buyable"),
    STREET_ALREADY_OWNED("Street is already owned by another player"),
    PACKET_NOT_IMPLEMENTED("Packet not implemented yet"),
    YOUR_STREET("This is your street!"),
    STREET_MAXED("This street is already maxed"),
    INCOMPLETE_GROUP("You dont have to complete group of streets to do this"),
    BUILD_OTHER_BUILDINGS_FIRST("You have to build other thinks first"),
    DISMANTLE_OTHER_THINKS_FIRST("You have to dismantle other thinks first"),
    NO_HOUSE_ON_STREET("You dont have a house to dismantle on this street"),
    PLACEHOLDER("PLACEHOLDER");

    private final String message;

    Status(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
