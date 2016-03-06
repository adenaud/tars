package com.rcon4games.tars.event;

import com.rcon4games.tars.network.Packet;

import java.net.Socket;

public interface ServerConnectionListener {

    void onServerRequest(Socket client, Packet requestPacket);
}
