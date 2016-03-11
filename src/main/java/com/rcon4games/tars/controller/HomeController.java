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

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(@RequestParam(value = "search-type", required = true) String type,
                         @RequestParam(value = "query", required = true) String query, Model model) {
        String regex = ".*"+query+".*";
        switch (type) {
            case "startswith":
                regex = "[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}_[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}: " + query + ".*";
                break;
            case "endswith":
                regex = ".*" + query + "$";
                break;
            case "regex":
                regex = query;
                break;
        }
        model.addAttribute("logs", formatLog(logDAO.search(regex)));
        model.addAttribute("players",tarsService.getPlayers());
        return "home";
    }

    private List<Log> formatLog(List<Log> logs) {
        for (Log log : logs) {
            //TODO Replace this crap by regex
            if(log.getLog().contains("joined this ARK!") || log.getLog().contains("left this ARK!")){
                log.setCssClass("joinleft");
            }
            else if(log.getLog().contains("was killed by") || log.getLog().contains("was killed!")){
                log.setCssClass("kill");
            }
            else if(log.getLog().contains("Tamed a ")){
                log.setCssClass("tame");
            }
            else if(log.getLog().contains("SERVER:")){
                log.setCssClass("server");
            }

            log.setLog(TextParser.pareLogContent(log.getLog()));
        }
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
