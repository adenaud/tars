package com.rcon4games.tars.service;

import com.rcon4games.tars.Commands;
import com.rcon4games.tars.dao.LogDAO;
import com.rcon4games.tars.event.ConnectionListener;
import com.rcon4games.tars.event.ServerConnectionListener;
import com.rcon4games.tars.network.*;
import com.rcon4games.tars.utils.TextParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.Date;

@Service
public class TarsServerService implements ServerConnectionListener {


    @Autowired
    private TarsServerSocket tarsServerSocket;

    @Autowired
    private SRPConnection srpConnection;

    @Autowired
    private LogDAO logDAO;

    public void init() {
        tarsServerSocket.init();
        tarsServerSocket.setServerConnectionListener(this);
    }

    @Override
    public void onServerRequest(Socket client, Packet requestPacket) {
        String command = requestPacket.getBody();
        if (command.startsWith(Commands.GETGAMELOG)) {
            Packet responsePacket = new Packet();
            if(command.startsWith(Commands.GETGAMELOG_DATE)){
                Date date = TextParser.parseLogDate(command.replace(Commands.GETGAMELOG_DATE+" ",""));
                //TODO call dao
            }else{
                responsePacket.setId(requestPacket.getId());
                responsePacket.setType(PacketType.SERVERDATA_RESPONSE_VALUE.getValue());
                responsePacket.setBody(Commands.GETGAMELOG + " doesn't exist anymore. Use "+Commands.GETGAMELOG_DATE+" <date(yyyy.mm.dd_hh.mm.ss)>");
            }
            tarsServerSocket.sendResponse(client,responsePacket);

        } else {

            srpConnection.addConnectionListener(new ConnectionListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onDisconnect(String message) {
                }

                @Override
                public void onServerStopRespondRconRequests() {
                }

                @Override
                public void onReceive(TarsSocket connection, Packet packet) {
                    if (packet.getId() == requestPacket.getId()) {
                        Packet responsePacket = new Packet(requestPacket.getId(), PacketType.SERVERDATA_RESPONSE_VALUE.getValue(), packet.getBody());
                        tarsServerSocket.sendResponse(client, responsePacket);
                    }

                }
            });

            Packet requestForwardPacket = new Packet(srpConnection.getSequenceNumber(), PacketType.SERVERDATA_EXECCOMMAND.getValue(), requestPacket.getBody());
            srpConnection.send(requestForwardPacket);
        }
    }

    public void sendOnGetLog(String log){
        Packet packet = new Packet(-1,PacketType.SERVER_EVENT.getValue(),log);
        tarsServerSocket.sendEventToAll(packet);
    }

    public void sendOnPlayerJoin(String playername){

    }
    public void sendOnPlayerLeft(String playername){

    }

}
