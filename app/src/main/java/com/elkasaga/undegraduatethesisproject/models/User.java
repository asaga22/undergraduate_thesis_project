package com.elkasaga.undegraduatethesisproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    String uid, email, fullname, username, avatar;
    private long ongoingtour, upcomingtour, foregoingtour, category;

    public User(String uid, String email, String fullname, String username, long ongoingtour, long upcomingtour, long foregoingtour, long category, String avatar) {
        this.uid = uid;
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.ongoingtour = ongoingtour;
        this.upcomingtour = upcomingtour;
        this.foregoingtour = foregoingtour;
        this.category = category;
        this.avatar = avatar;
    }

    public User() {
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        fullname = in.readString();
        username = in.readString();
        avatar = in.readString();
        ongoingtour = in.readLong();
        upcomingtour = in.readLong();
        foregoingtour = in.readLong();
        category = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getOngoingtour() {
        return ongoingtour;
    }

    public void setOngoingtour(long ongoingtour) {
        this.ongoingtour = ongoingtour;
    }

    public long getUpcomingtour() {
        return upcomingtour;
    }

    public void setUpcomingtour(long upcomingtour) {
        this.upcomingtour = upcomingtour;
    }

    public long getForegoingtour() {
        return foregoingtour;
    }

    public void setForegoingtour(long foregoingtour) {
        this.foregoingtour = foregoingtour;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(fullname);
        dest.writeString(username);
        dest.writeString(avatar);
        dest.writeLong(ongoingtour);
        dest.writeLong(upcomingtour);
        dest.writeLong(foregoingtour);
        dest.writeLong(category);
    }
}
