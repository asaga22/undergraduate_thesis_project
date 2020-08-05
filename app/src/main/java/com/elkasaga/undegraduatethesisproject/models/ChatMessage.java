package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class ChatMessage {

    private String messageid, message;
    private boolean systemgenerated;
    private @ServerTimestamp
    Date timestamp;
    User user;
    ArrayList<String> readByList;

    public ChatMessage(String messageid, String message, boolean systemgenerated, Date timestamp, User user, ArrayList<String> readByList) {
        this.messageid = messageid;
        this.message = message;
        this.systemgenerated = systemgenerated;
        this.timestamp = timestamp;
        this.user = user;
        this.readByList = readByList;
    }

    public ChatMessage() {
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSystemgenerated() {
        return systemgenerated;
    }

    public void setSystemgenerated(boolean systemgenerated) {
        this.systemgenerated = systemgenerated;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<String> getReadByList() {
        return readByList;
    }

    public void setReadByList(ArrayList<String> readByList) {
        this.readByList = readByList;
    }
}
