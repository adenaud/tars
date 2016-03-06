package com.rcon4games.tars.utils;

import com.rcon4games.tars.model.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {

    public static Date parseLogDate(String logLine) {
        Pattern pattern = Pattern.compile("([0-9]{4})\\.([0-9]{2})\\.([0-9]{2})_([0-9]{2}).([0-9]{2}).([0-9]{2})(.*)");
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

    public static String pareLogContent(String logLine) {
        String result = "";
        Pattern pattern = Pattern.compile("([0-9]{4})\\.([0-9]{2})\\.([0-9]{2})_([0-9]{2}).([0-9]{2}).([0-9]{2}): (.*)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find()) {
            result = matcher.group(7);
        }
        return result;
    }

    public static List<Player> parsePlayers(String buffer) {
        List<Player> players = new ArrayList<>();
        String[] playersArray = buffer.split("\n");
        if (!buffer.startsWith("No Players Connected")) {
            for (String aPlayersArray : playersArray) {
                if (aPlayersArray.length() > 20) { // 20 = playerId + steamId min length
                    Pattern pattern = Pattern.compile("(\\d*)\\. (.+), ([0-9]+) ?");
                    Matcher matcher = pattern.matcher(aPlayersArray);
                    if (matcher.matches()) {
                        String name = matcher.group(2);
                        String steamId = matcher.group(3);
                        Player player = new Player(name, steamId);
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }
}
