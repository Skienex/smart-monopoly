package com.github.skienex.monopoly.game;

public class Street {
    private final String name;
    private final int[] cost;
    private final int[] rent;
    private final StreetGroup group;
    private Player owner;
    private int level;
    private final int mortgagedSell;
    private final int mortgagedRebuy;

    public Street(String name, int[] cost, int[] rent, StreetGroup group, int mortgagedSell, int mortgagedRebuy) {
        this.name = name;
        this.cost = cost;
        this.rent = rent;
        this.group = group;
        this.mortgagedSell = mortgagedSell;
        this.mortgagedRebuy = mortgagedRebuy;
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

    public void owner(Player owner) {
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

    public int mortgageSell() {
        return mortgagedSell;
    }

    public int mortageRebuy() {
        return mortgagedRebuy;
    }
}
