package com.rcon4games.tars.service;

import com.rcon4games.tars.Commands;
import com.rcon4games.tars.event.ConnectionListener;
import com.rcon4games.tars.event.ServerResponseDispatcher;
import com.rcon4games.tars.network.*;
import com.rcon4games.tars.utils.TextParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class SRPService implements ConnectionListener {

    Logger logger = LoggerFactory.getLogger(SRPService.class);


    @Value("${arkserver.rcon.password}")
    private String password;

    @Autowired
    private SRPConnection connection;

    private ServerResponseDispatcher serverResponseDispatcher;

    public void init() {
        logger.info("T.A.R.S : Hi Cooper !");
        connection.addConnectionListener(this);
        connection.open();
    }

    @Override
    public void onConnected() {
        logger.info("Connected");
        login();
    }

    public void onLogin() {
        listPlayers();
        Timer logTimer = new Timer();
        logTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Packet packet = new Packet(connection.getSequenceNumber(), PacketType.SERVERDATA_EXECCOMMAND.getValue(), Commands.GETGAMELOG);
                connection.send(packet);
            }
        }, 2000, 2000);
    }

    @Override
    public void onDisconnect(String message) {
        logger.error("Disconnected : {}", message);
    }

    @Override
    public void onServerStopRespondRconRequests() {
        connection.open();
    }

    @Override
    public void onReceive(TarsSocket connection, Packet packet) {
        if (packet.getType() == PacketType.SERVERDATA_AUTH_RESPONSE.getValue() && packet.getId() != -1) {
            logger.info("Authentication OK");
            onLogin();
        } else if (packet.getType() == PacketType.SERVERDATA_RESPONSE_VALUE.getValue()) {
            //logger.info(packet.getBody().trim());

            Packet requestPacket = ((SRPConnection)connection).getRequestPacket(packet.getId());

            if (serverResponseDispatcher != null && requestPacket != null) {
                if (requestPacket.getBody().equals(Commands.LISTPLAYERS)) {
                    serverResponseDispatcher.onListPlayers(TextParser.parsePlayers(packet.getBody()));
                } else if (requestPacket.getBody().equals(Commands.GETGAMELOG)) {
                    if (!packet.getBody().contains("Server received, But no response")) {
                        serverResponseDispatcher.onGetLog(packet.getBody());
                    }
                }
            }
        } else {
            logger.error("Authentication Failure");
        }
    }

    private void login() {
        Packet packet = new Packet(connection.getSequenceNumber(), PacketType.SERVERDATA_AUTH.getValue(), password);
        connection.send(packet);
    }

    public void setServerResponseDispatcher(ServerResponseDispatcher serverResponseDispatcher) {
        this.serverResponseDispatcher = serverResponseDispatcher;
    }

    public void sendChatMessageToAll(String message){
        Packet packet = new Packet(connection.getSequenceNumber(),PacketType.SERVERDATA_EXECCOMMAND.getValue(),Commands.SERVERCHAT + " " + message);
        connection.send(packet);
    }

    public void sendChatMessageToPlayer(String steamId, String message){

    }

    public void listPlayers() {
        Packet packet = new Packet(connection.getSequenceNumber(),PacketType.SERVERDATA_EXECCOMMAND.getValue(),Commands.LISTPLAYERS);
        connection.send(packet);
    }
}
