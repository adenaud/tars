package com.rcon4games.tars.event;

import com.rcon4games.tars.network.Packet;
import com.rcon4games.tars.network.SRPConnection;
import com.rcon4games.tars.network.TarsSocket;

public interface ConnectionListener {
    void onConnected();
    void onDisconnect(String message);
    void onServerStopRespondRconRequests();
    void onReceive(TarsSocket connection, Packet packet);
}
