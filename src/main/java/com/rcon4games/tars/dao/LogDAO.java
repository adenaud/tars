package com.rcon4games.tars.dao;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.rcon4games.tars.utils.TextParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

@Repository
public class LogDAO extends AbstractDao{

    public void writeLog(String log) {
        Document doc = new Document().append("date", TextParser.parseLogDate(log)).append("log", log);
        mongoDatabase.getCollection("logs").insertOne(doc);
    }

    public List<String> getLatest() {
        List<String> logs = new ArrayList<>();
        FindIterable<Document> iterable = mongoDatabase.getCollection("logs").find().sort(new Document("date", -1));
        iterable.forEach((Block<Document>) document -> logs.add((String) document.get("log")));
        return logs;
    }

    public List<String> search(String regex) {
        List<String> logs = new ArrayList<>();
        FindIterable<Document> iterable = mongoDatabase.getCollection("logs").find(Filters.regex("log", Pattern.compile(regex))).sort(new Document("date", -1));
        iterable.forEach((Block<Document>) document -> logs.add((String) document.get("log")));
        return logs;
    }
}
