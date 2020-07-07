package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JoinGroupTourRequest {

    String requestid, tourid, uid, tourleader;
    private @ServerTimestamp
    Date timestamp;

    public JoinGroupTourRequest(String requestid, String tourid, String uid, String tourleader, Date timestamp) {
        this.requestid = requestid;
        this.tourid = tourid;
        this.uid = uid;
        this.tourleader = tourleader;
        this.timestamp = timestamp;
    }

    public JoinGroupTourRequest() {
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getTourid() {
        return tourid;
    }

    public void setTourid(String tourid) {
        this.tourid = tourid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTourleader() {
        return tourleader;
    }

    public void setTourleader(String tourleader) {
        this.tourleader = tourleader;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
