package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {

    private String messageid, message;
    private @ServerTimestamp
    Date timestamp;
    User user;

    public ChatMessage(String messageid, String message, Date timestamp, User user) {
        this.messageid = messageid;
        this.message = message;
        this.timestamp = timestamp;
        this.user = user;
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
}
