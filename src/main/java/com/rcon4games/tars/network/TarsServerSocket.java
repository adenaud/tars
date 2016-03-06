package com.rcon4games.tars.network;

import com.rcon4games.tars.event.ServerConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component
public class TarsServerSocket implements TarsSocket {
    private Logger logger = LoggerFactory.getLogger(TarsServerSocket.class);

    @Value("${tars.port}")
    private int port;

    private List<Socket> clients;

    private ServerSocket server;
    private boolean runServerThread;
    private boolean runReceiveThread;
    private Thread receiveThread;


    private ServerConnectionListener serverConnectionListener;

    public TarsServerSocket() {
        clients = new ArrayList<>();
    }

    public void init() {
        runServerThread = true;
        try {
            server = new ServerSocket(port);

            Thread serverThread = new Thread(() -> {
                while (runServerThread) {
                    try {
                        clients.add(server.accept());
                    } catch (IOException e) {
                        logger.error("Unable to start server : {}", e.getMessage());
                    }
                    runReceiveThread = true;
                    beginReceive();
                }

            }, "TarsServerThread");
            serverThread.start();

        } catch (IOException e) {
            runServerThread = false;
            logger.error("Unable to start server : {}", e.getMessage());
        }
    }

    private void beginReceive() {
        for (Socket client : clients) {
            receiveThread = new Thread(() -> {
                receive(client);
            }, "TarsServerReceiveThread");
            receiveThread.start();
        }
    }

    private void receive(Socket client) {
        if (runReceiveThread) {
            try {
                InputStream inputStream = client.getInputStream();
                byte[] request = new byte[4096];
                int result = inputStream.read(request, 0, request.length);

                if (result > 0) {
                    handleRequest(client, request);
                }
                receive(client);
            } catch (IOException e) {
                logger.error("Unable to receive : {}", e.getMessage());
            }
        }
    }

    private void handleRequest(Socket client, byte[] request) {
        Packet packet = new Packet(request);
        if (serverConnectionListener != null) {
            serverConnectionListener.onServerRequest(client, packet);
        }
    }

    public void sendResponse(Socket client, Packet response) {
        try {
            client.getOutputStream().write(response.encode());
        } catch (IOException e) {
            logger.error("Unable to respond to server request.");
        }
    }

    public void sendEventToAll(Packet packet) {
        for (Socket client : clients) {
            sendResponse(client,packet);
        }

    }

    public void setServerConnectionListener(ServerConnectionListener serverConnectionListener) {
        this.serverConnectionListener = serverConnectionListener;
    }
}
