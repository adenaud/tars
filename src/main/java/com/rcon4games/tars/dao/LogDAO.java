package com.rcon4games.tars.dao;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.rcon4games.tars.utils.TextParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
public class LogDAO {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @Value("${mongodb.host}")
    private String hostname;

    @Value("${mongodb.port}")
    private int port;

    @Value("${mongodb.database}")
    private String database;


    @PostConstruct
    private void init() {
        mongoClient = new MongoClient(hostname, port);
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public void writeLog(String log) {
        Document doc = new Document().append("date", TextParser.parseLogDate(log)).append("log", log);
        mongoDatabase.getCollection("logs").insertOne(doc);
    }

    public List<String> getLatest() {
        List<String> logs = new ArrayList<>();
        FindIterable<Document> iterable =  mongoDatabase.getCollection("logs").find().sort(new Document("date",-1)).limit(50);
        iterable.forEach((Block<Document>) document -> logs.add((String) document.get("log")));
        return logs;
    }
}
