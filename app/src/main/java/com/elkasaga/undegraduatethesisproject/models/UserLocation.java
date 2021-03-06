package com.elkasaga.undegraduatethesisproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation implements Parcelable {

    private GeoPoint geoPoint;
    private @ServerTimestamp
    Date timestamp;
    private User user;
    private Participant participant;

    public UserLocation(GeoPoint geoPoint, Date timestamp, User user) {

        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UserLocation(GeoPoint geoPoint, Date timestamp, Participant participant) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.participant = participant;
    }

    public UserLocation(GeoPoint geoPoint, Date timestamp, User user, Participant participant) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.participant = participant;
        this.user = user;
    }

    public UserLocation(){
    }

    protected UserLocation(Parcel in) {
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
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

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geoPoint=" + geoPoint +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                ", participant "+ participant +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
