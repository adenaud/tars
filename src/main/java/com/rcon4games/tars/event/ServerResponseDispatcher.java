package com.rcon4games.tars.event;

import com.rcon4games.tars.model.Player;

import java.util.List;

public interface ServerResponseDispatcher {
    void onListPlayers(List<Player> players);
    void onGetLog(String log);
}
