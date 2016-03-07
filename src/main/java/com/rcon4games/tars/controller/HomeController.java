package com.rcon4games.tars.controller;

import com.rcon4games.tars.dao.LogDAO;
import com.rcon4games.tars.model.Log;
import com.rcon4games.tars.service.TarsService;
import com.rcon4games.tars.utils.TextParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class HomeController {

    @Autowired
    private TarsService tarsService;

    @Autowired
    private LogDAO logDAO;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("logs", formatLog(logDAO.getLatest()));
        model.addAttribute("players",tarsService.getPlayers());
        return "home";
    }


    @RequestMapping(path = "/message", method = RequestMethod.POST)
    public String sendMessage(@RequestParam(value = "message", required = true) String message, Model model) {
        model.addAttribute("logs", formatLog(logDAO.getLatest()));
        model.addAttribute("players",tarsService.getPlayers());
        tarsService.sendChatMessageToAll(message);
        return "home";
    }

    private List<Log> formatLog(List<String> logsStrings) {
        List<Log> logs = new ArrayList<>();

        for (String logString : logsStrings) {
            Log log = new Log();

            //TODO Replace this crap by regex
            if(logString.contains("joined this ARK!") || logString.contains("left this ARK!")){
                log.setCssClass("joinleft");
            }
            else if(logString.contains("was killed by") || logString.contains("was killed!")){
                log.setCssClass("kill");
            }
            else if(logString.contains("Tamed a ")){
                log.setCssClass("tame");
            }
            else if(logString.contains("SERVER:")){
                log.setCssClass("server");
            }
            log.setDate(TextParser.parseLogDate(logString));
            log.setLog(TextParser.pareLogContent(logString));

            logs.add(log);
        }
        Collections.reverse(logs);
        return logs;
    }

    /*
    private String formatHtml(String content) {
        content = content.trim();
        content += "\n";
        String html = content.replaceAll("(.*)left this ARK!", "<span class=\"joinleft\">$0</span>");
        html = html.replaceAll("(.*)joined this ARK!", "<span class=\"joinleft\">$0</span>");
        html = html.replaceAll("(.*)was killed by(.*)", "<span class=\"kill\">$0</span>");
        html = html.replaceAll("(.*)was killed!", "<span class=\"kill\">$0</span>");
        html = html.replaceAll("(.*)Tamed a ([A-z ]*) \\- (.*)", "<span class=\"tame\">$0</span>");
        html = html.replaceAll("(.*)SERVER:(.*)", "<span class=\"server\">$0</span>");
        html = html.replaceAll("\\n", "<br>\n");
        html = html.replaceAll("<br>\\n <br>\\n ", "<br>\n");
        return html;
    }*/
}
