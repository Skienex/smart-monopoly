package com.github.skienex.monopoly.game;

import java.util.UUID;

public class Player {
    private final UUID id;
    private int money;
    private int position;
    private boolean admin;

    public Player(UUID id) {
        this.id = id;
        this.money = 2500;
        this.position = 0;
        this.admin = false;
    }

    public UUID getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public int getPosition() {
        return position;
    }

    public void addPosition(int value) {
        position = (position + value) % 40;
    }

    public void subPosition(int value) {
        position = (position + 40 - value) % 40;
    }

    public void setPosition(int position) {
        this.position = position % 40;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
