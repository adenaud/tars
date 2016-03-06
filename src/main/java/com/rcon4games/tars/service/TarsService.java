package com.rcon4games.tars.service;

import com.rcon4games.tars.dao.LogDAO;
import com.rcon4games.tars.event.ServerResponseDispatcher;
import com.rcon4games.tars.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarsService implements ServerResponseDispatcher {
    private Logger logger = LoggerFactory.getLogger(TarsService.class);

    @Autowired
    private LogDAO logDAO;

    @Autowired
    private SRPService srpService;

    @Autowired
    private TarsServerService serverService;

    public void init() {
        serverService.init();

        srpService.setServerResponseDispatcher(this);
        srpService.init();
    }

    @Override
    public void onListPlayers(List<Player> players) {

    }

    @Override
    public void onGetLog(String log) {
        String[] logLines = log.trim().split("\\n");
        for (String line : logLines) {
            logger.debug(line);
            logDAO.writeLog(line);
        }
    }
}
