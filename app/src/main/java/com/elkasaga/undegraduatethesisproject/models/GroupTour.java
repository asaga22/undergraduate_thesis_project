package com.elkasaga.undegraduatethesisproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.util.Date;

public class GroupTour implements Parcelable {

    String tourtitle, tourid, tourleader, startdate, enddate, starttime, endtime;
    long tourstatus;

    public GroupTour(String tourtitle, String tourid, String tourleader, String startdate, String enddate, String starttime, String endtime, long tourstatus) {
        this.tourtitle = tourtitle;
        this.tourid = tourid;
        this.tourleader = tourleader;
        this.startdate = startdate;
        this.enddate = enddate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.tourstatus = tourstatus;
    }

    public GroupTour() {
    }

    protected GroupTour(Parcel in) {
        tourtitle = in.readString();
        tourid = in.readString();
        tourleader = in.readString();
        startdate = in.readString();
        enddate = in.readString();
        starttime = in.readString();
        endtime = in.readString();
        tourstatus = in.readLong();
    }

    public static final Creator<GroupTour> CREATOR = new Creator<GroupTour>() {
        @Override
        public GroupTour createFromParcel(Parcel in) {
            return new GroupTour(in);
        }

        @Override
        public GroupTour[] newArray(int size) {
            return new GroupTour[size];
        }
    };

    public String getTourtitle() {
        return tourtitle;
    }

    public void setTourtitle(String tourtitle) {
        this.tourtitle = tourtitle;
    }

    public String getTourid() {
        return tourid;
    }

    public void setTourid(String tourid) {
        this.tourid = tourid;
    }

    public String getTourleader() {
        return tourleader;
    }

    public void setTourleader(String tourleader) {
        this.tourleader = tourleader;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public long getTourstatus() {
        return tourstatus;
    }

    public void setTourstatus(long tourstatus) {
        this.tourstatus = tourstatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tourtitle);
        dest.writeString(tourid);
        dest.writeString(tourleader);
        dest.writeString(startdate);
        dest.writeString(enddate);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeLong(tourstatus);
    }
}
