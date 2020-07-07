package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JoinRequestRespond {

    String uid, tourid, requestid;
    boolean accepted;
    private @ServerTimestamp
    Date timestamp;

    public JoinRequestRespond(String uid, String tourid, String requestid, boolean accepted, Date timestamp) {
        this.uid = uid;
        this.tourid = tourid;
        this.requestid = requestid;
        this.accepted = accepted;
        this.timestamp = timestamp;
    }

    public JoinRequestRespond() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTourid() {
        return tourid;
    }

    public void setTourid(String tourid) {
        this.tourid = tourid;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
