package com.elkasaga.undegraduatethesisproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Participant implements Parcelable {

    String uid, username, fullname, avatar;
    long category, present;
    boolean represent;

    public Participant(String uid, String username, String fullname, String avatar, long category, long present, boolean represent) {
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

    protected Participant(Parcel in) {
        uid = in.readString();
        username = in.readString();
        fullname = in.readString();
        avatar = in.readString();
        category = in.readLong();
        present = in.readLong();
        represent = in.readByte() != 0;
    }

    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

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

    public long getPresent() {
        return present;
    }

    public void setPresent(long present) {
        this.present = present;
    }

    public boolean isRepresent() {
        return represent;
    }

    public void setRepresent(boolean represent) {
        this.represent = represent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(fullname);
        dest.writeString(avatar);
        dest.writeLong(category);
        dest.writeLong(present);
        dest.writeByte((byte) (represent ? 1 : 0));
    }
}
