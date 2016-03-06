package com.rcon4games.tars.model;

import java.util.Date;

public class Log {

    private Date date;
    private String cssClass;
    private String log;

    public Log() {
    }

    public Log(Date date, String cssClass, String log) {
        this.date = date;
        this.cssClass = cssClass;
        this.log = log;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
