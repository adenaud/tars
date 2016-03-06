package com.rcon4games.tars.network;

import com.rcon4games.tars.event.ConnectionListener;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
@Scope("singleton")
public class SRPConnection implements TarsSocket {

    @Value("${arkserver.hostname}")
    private String hostname;

    @Value("${arkserver.rcon.port}")
    private int port;


    Logger logger = LoggerFactory.getLogger(SRPConnection.class);


    private Thread connectionThread;
    private Thread receiveThread;

    private int sequenceNumber;
    private final LinkedMap<Integer, Packet> outgoingPackets;

    private boolean isConnected;
    private boolean reconnecting;

    private List<ConnectionListener> connectionListeners;

    private Socket socket;
    private boolean runReceiveThread;
    private Date lastPacketTime;


    public SRPConnection() {
        isConnected = false;
        runReceiveThread = false;
        outgoingPackets = new LinkedMap<>();
        lastPacketTime = new Date();
        connectionListeners = new ArrayList<>();
    }

    public void open() {
        if (!isConnected) {

            logger.info("Connecting to {}:{}", hostname, port);
            connectionThread = new Thread(() -> {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(hostname, port));
                    runReceiveThread = true;
                    isConnected = true;
                    beginReceive();
                    for (ConnectionListener connectionListener : connectionListeners) {
                        connectionListener.onConnected();
                    }
                } catch (IOException e) {
                    for (ConnectionListener connectionListener : connectionListeners) {
                        connectionListener.onDisconnect(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            connectionThread.start();
        }
    }

    private void beginReceive() {
        receiveThread = new Thread(this::receive, "ReceiveThread");
        receiveThread.start();
    }

    private void receive() {
        if (runReceiveThread) {
            InputStream inputStream;
            try {

                if (new Date().getTime() - lastPacketTime.getTime() > 3000) {
                    reconnect("The server has stopped to responding to RCON requests.");
                }
                inputStream = socket.getInputStream();
                byte[] packetSize = new byte[4];
                int packetSizeInt = 0;
                int sizeLength = inputStream.read(packetSize, 0, packetSize.length);
                if (sizeLength == 4 && !PacketUtils.isText(packetSize)) {
                    packetSizeInt = PacketUtils.getPacketSize(packetSize) + 10;
                }

                final byte[] response;
                if (!PacketUtils.isText(packetSize)) {
                    response = new byte[packetSizeInt];
                } else {
                    response = new byte[Packet.PACKET_MAX_LENGTH];
                }

                int responseLength = inputStream.read(response, 0, response.length);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(packetSize);
                byteArrayOutputStream.write(response);
                final byte[] packetBuffer = byteArrayOutputStream.toByteArray();


                if (responseLength > 0) {

                    if (PacketUtils.isStartPacket(packetBuffer)) {
                        final Packet packet = new Packet(packetBuffer);
                        if ((packet.getId() == -1 || packet.getId() > 0)) {
                            lastPacketTime = new Date();
                            Thread thread = new Thread(() -> {
                                logger.debug("Receive : {}", packet.getBody());

                                for (ConnectionListener connectionListener : connectionListeners) {

                                    connectionListener.onReceive(SRPConnection.this, packet);
                                }
                            }, "ResponseExecThread");
                            thread.start();
                        }
                    } else {
                        final Packet lastPacket = outgoingPackets.get(outgoingPackets.lastKey());

                        Thread thread = new Thread(() -> {
                            if (lastPacket.getBody().equals("getgamelog")) {
                                Packet packet = new Packet(lastPacket.getId(), PacketType.SERVERDATA_RESPONSE_VALUE.getValue(), new String(packetBuffer));
                                for (ConnectionListener connectionListener : connectionListeners) {
                                    connectionListener.onReceive(SRPConnection.this, packet);
                                }
                            } else if (lastPacket.getBody().equals("ListPlayers")) {
                                Packet packet = new Packet(getSequenceNumber(), PacketType.SERVERDATA_EXECCOMMAND.getValue(), "ListPlayers");
                                send(packet);
                            }
                        }, "ResponseExecThread");
                        thread.start();
                    }
                }

                receive();
            } catch (IOException e) {
                logger.error("Unable to receive packet : {}", e.getMessage());
            }
        }
    }

    public void send(Packet packet) {
        logger.debug("Send : {}", packet.getBody());
        synchronized (outgoingPackets) {
            this.outgoingPackets.put(packet.getId(), packet);
        }
        try {
            byte[] data = packet.encode();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
        } catch (IOException e) {
            isConnected = false;
            logger.error("Unable to send packet : {}", e.getMessage());
            reconnect("Connection lost.");
        }

    }

    private void reconnect(String message) {
        if (!reconnecting) {
            reconnecting = true;
            try {
                close();
            } catch (IOException e) {
                logger.error("Unable to close client : " + e.getMessage(), e);
            }
            logger.error(message);
            logger.error("Reconnecting ...");
            for (ConnectionListener connectionListener : connectionListeners) {
                {
                    connectionListener.onServerStopRespondRconRequests();
                }
            }
        }
    }

    public synchronized int getSequenceNumber() {
        return ++sequenceNumber;
    }


    private void close() throws IOException {
        if (socket != null) {
            socket.close();
            isConnected = false;
            runReceiveThread = false;
            receiveThread.interrupt();
            connectionThread.interrupt();
        }
        outgoingPackets.clear();
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.add(connectionListener);
    }

    public synchronized Packet getRequestPacket(int id) {
        return outgoingPackets.get((Integer) id);
    }
}
