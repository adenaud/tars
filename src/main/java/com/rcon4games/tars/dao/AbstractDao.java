package com.rcon4games.tars.dao;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Arrays;

public class AbstractDao {

    protected MongoDatabase mongoDatabase;

    @Value("${mongodb.host}")
    private String hostname;

    @Value("${mongodb.port}")
    private int port;

    @Value("${mongodb.database}")
    private String database;

    @Value("${mongodb.credential.user}")
    private String username;

    @Value("${mongodb.credential.password}")
    private String password;

    @Value("${mongodb.credential.database}")
    private String credentialDatabase;

    @PostConstruct
    private void init() {
        MongoCredential credential = MongoCredential.createScramSha1Credential(username, credentialDatabase, password.toCharArray());
        MongoClient client = new MongoClient(new ServerAddress(hostname, port), Arrays.asList(credential));
        mongoDatabase = client.getDatabase(database);
    }
}
