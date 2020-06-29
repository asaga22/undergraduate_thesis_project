package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.GeoPoint;

public class Participant {

    String uid, username, fullname, avatar;
    long is_present, is_represent;
    GeoPoint geoPoint;

    public Participant(String uid, String username, String fullname, long is_present, long is_represent, GeoPoint geoPoint, String avatar) {
        this.uid = uid;
        this.username = username;
        this.fullname = fullname;
        this.is_present = is_present;
        this.is_represent = is_represent;
        this.geoPoint = geoPoint;
        this.avatar = avatar;
    }

    public Participant() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public long getIs_present() {
        return is_present;
    }

    public void setIs_present(long is_present) {
        this.is_present = is_present;
    }

    public long getIs_represent() {
        return is_represent;
    }

    public void setIs_represent(long is_represent) {
        this.is_represent = is_represent;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
