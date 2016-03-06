package com.rcon4games.tars.service;

import com.rcon4games.tars.dao.LogDAO;
import com.rcon4games.tars.event.ServerResponseDispatcher;
import com.rcon4games.tars.model.Player;
import com.rcon4games.tars.utils.TextParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Scope("singleton")
public class TarsService implements ServerResponseDispatcher {
    private Logger logger = LoggerFactory.getLogger(TarsService.class);

    @Autowired
    private LogDAO logDAO;

    @Autowired
    private SRPService srpService;

    @Autowired
    private TarsServerService serverService;

    private List<Player> players;

    public void init() {
        players = new ArrayList<>();
        serverService.init();
        srpService.setServerResponseDispatcher(this);
        srpService.init();

    }

    @Override
    public void onListPlayers(List<Player> players) {
            this.players = players;
    }

    @Override
    public void onGetLog(String log) {
        String[] logLines = log.trim().split("\\n");
        for (String line : logLines) {
            logDAO.writeLog(line);
            long dateDiff = Math.abs(new Date().getTime() / 1000 - (TextParser.parseLogDate(line).getTime() / 1000));
            if (dateDiff < 600 && (log.contains(" joined this ARK!") || log.contains(" left this ARK!"))) {
                srpService.listPlayers();
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
}
