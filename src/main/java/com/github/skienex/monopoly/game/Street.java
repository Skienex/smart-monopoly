package com.github.skienex.monopoly.game;

public class Street {
    private final String name;
    private final int[] cost;
    private final int[] rent;
    private final StreetGroup group;
    private Player owner;
    private int level;

    public Street(String name, int[] cost, int[] rent, StreetGroup group) {
        this.name = name;
        this.cost = cost;
        this.rent = rent;
        this.group = group;
    }

    public String name() {
        return name;
    }

    public int[] cost() {
        return cost;
    }

    public int[] rent() {
        return rent;
    }

    public StreetGroup group() {
        return group;
    }

    public Player owner() {
        return owner;
    }

    public void owner(Player player) {
        this.owner = owner;
    }

    public int level() {
        return level;
    }

    public void levelUp() {
        level++;
    }
    public void levelDown() {
        level--;
    }
}
