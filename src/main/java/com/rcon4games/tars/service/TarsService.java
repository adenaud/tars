package com.rcon4games.tars.service;

import com.rcon4games.tars.network.SRPConnection;
import com.rcon4games.tars.network.TarsServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TarsService {

    @Autowired
    private TarsServer server;
    private SRPConnection connection;

    public void init() {
        System.out.println("T.A.R.S : Hi Cooper !");
    }
}
