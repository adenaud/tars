package com.rcon4games.tars.service;

import com.rcon4games.tars.event.ConnectionListener;
import com.rcon4games.tars.event.ServerResponseDispatcher;
import com.rcon4games.tars.model.Player;
import com.rcon4games.tars.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Timer logTimer = new Timer();
        logTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Packet packet = new Packet(connection.getSequenceNumber(), PacketType.SERVERDATA_EXECCOMMAND.getValue(), "getgamelog");
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
            logger.info(packet.getBody().trim());

            Packet requestPacket = ((SRPConnection)connection).getRequestPacket(packet.getId());

            if (serverResponseDispatcher != null && requestPacket != null) {
                if (requestPacket.getBody().equals("ListPlayers")) {
                    serverResponseDispatcher.onListPlayers(getPlayers(packet.getBody()));
                } else if (requestPacket.getBody().equals("getgamelog")) {
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

    private List<Player> getPlayers(String messageBody) {
        List<Player> players = new ArrayList<>();
        String[] playersArray = messageBody.split("\n");

        if (!messageBody.startsWith("No Players Connected")) {

            for (String aPlayersArray : playersArray) {
                if (aPlayersArray.length() > 20) { // 20 = playerId + steamId min length

                    Pattern pattern = Pattern.compile("(\\d*)\\. (.+), ([0-9]+) ?");
                    Matcher matcher = pattern.matcher(aPlayersArray);

                    if (matcher.matches()) {
                        String name = matcher.group(2);
                        String steamId = matcher.group(3);
                        Player player = new Player(name, steamId);
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }
}
