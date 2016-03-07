package com.rcon4games.tars.model;

public class Player {

    private String name;
    private String steamId;

    public Player() {
    }

    public Player(String name, String steamId) {
        this.name = name;
        this.steamId = steamId;
    }

    public String getName() {
        return name;
    }

    public String getSteamId() {
        return steamId;
    }
}
