package com.rcon4games.tars.dao;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserDao extends AbstractDao {

    /**
     * Create a new user
     * @param username
     * @param password
     * @param email
     * @return The uuid of the new user.
     */
    public String create(String username, String password, String email) {
        String uuid = UUID.randomUUID().toString();
        Document document = new Document();
        document.append("uuid","uuid");
        document.append("username",username);
        document.append("password", DigestUtils.md5Hex(password));
        document.append("email","email");
        mongoDatabase.getCollection("users").insertOne(document);
        return uuid;
    }

    public void update(String userUuid, String username, String password, String email){
    }

}
