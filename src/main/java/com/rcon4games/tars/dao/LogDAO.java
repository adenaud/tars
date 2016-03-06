package com.rcon4games.tars.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.rcon4games.tars.utils.TextParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Document doc = new Document().append("date", TextParser.parseDate(log)).append("log", log);
        mongoDatabase.getCollection("logs").insertOne(doc);
    }

}
