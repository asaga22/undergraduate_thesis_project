package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.GeoPoint;

public class Participant {

    String uid, username, fullname, avatar;
    long category;
    boolean present, represent;

    public Participant(String uid, String username, String fullname, String avatar, long category, boolean present, boolean represent) {
        this.uid = uid;
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.category = category;
        this.present = present;
        this.represent = represent;
    }

    public Participant() {
    }

    public Participant(String uid) {
        this.uid = uid;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean isRepresent() {
        return represent;
    }

    public void setRepresent(boolean represent) {
        this.represent = represent;
    }
}
