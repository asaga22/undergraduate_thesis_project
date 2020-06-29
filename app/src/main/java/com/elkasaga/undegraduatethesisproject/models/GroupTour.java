package com.elkasaga.undegraduatethesisproject.models;

import java.sql.Time;
import java.util.Date;

public class GroupTour {

    String tourtitle, tourid, tourleader, startdate, endate, starttime, endtime;
    long tourstatus;

    public GroupTour(String tourtitle, String tourid, String tourleader, String startdate, String endate, String starttime, String endtime, long tourstatus) {
        this.tourtitle = tourtitle;
        this.tourid = tourid;
        this.tourleader = tourleader;
        this.startdate = startdate;
        this.endate = endate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.tourstatus = tourstatus;
    }

    public GroupTour() {
    }

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

    public String getEndate() {
        return endate;
    }

    public void setEndate(String endate) {
        this.endate = endate;
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
}
