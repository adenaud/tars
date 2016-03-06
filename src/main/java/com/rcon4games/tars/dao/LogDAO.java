package com.rcon4games.tars.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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
        Document doc = new Document().append("date", parseDate(log)).append("log", log);
        mongoDatabase.getCollection("logs").insertOne(doc);
    }


    private Date parseDate(String logLine) {
        Pattern pattern = Pattern.compile("([0-9]{4})\\.([0-9]{2})\\.([0-9]{2})_([0-9]{2}).([0-9]{2}).([0-9]{2}):(.*)");
        Matcher matcher = pattern.matcher(logLine);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (matcher.find()) {
            calendar.set(Calendar.YEAR, Integer.valueOf(matcher.group(1)));
            calendar.set(Calendar.MONTH, Integer.valueOf(matcher.group(2)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(matcher.group(3)));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(matcher.group(4)));
            calendar.set(Calendar.MINUTE, Integer.valueOf(matcher.group(5)));
            calendar.set(Calendar.SECOND, Integer.valueOf(matcher.group(6)));
        }
        return calendar.getTime();
    }

}
