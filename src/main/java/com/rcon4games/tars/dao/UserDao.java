package com.rcon4games.tars.dao;

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
        document.append("username",username);
        //document.append("password",);
        return uuid;
    }

    public void update(String userUuid, String username, String password, String email){
    }

}
