package com.elkasaga.undegraduatethesisproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Presence {

    long checkedby;
    private @ServerTimestamp
    Date timestamp;
    Participant participant;

    public Presence(long checkedby, Date timestamp, Participant participant) {
        this.checkedby = checkedby;
        this.timestamp = timestamp;
        this.participant = participant;
    }

    public Presence() {
    }

    public long getCheckedby() {
        return checkedby;
    }

    public void setCheckedby(long checkedby) {
        this.checkedby = checkedby;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
