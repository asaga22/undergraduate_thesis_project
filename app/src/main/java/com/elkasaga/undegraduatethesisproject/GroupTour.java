package com.elkasaga.undegraduatethesisproject;

import java.sql.Time;
import java.util.Date;

public class GroupTour {

    String tour_title, tour_id, tour_leader;
    Date start_date, end_date;
    Time start_time, end_time;
    long tour_status;

    public GroupTour(String tour_title, String tour_id, String tour_leader, Date start_date, Date end_date, Time start_time, Time end_time, long tour_status) {
        this.tour_title = tour_title;
        this.tour_id = tour_id;
        this.tour_leader = tour_leader;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.tour_status = tour_status;
    }

    public GroupTour() {
    }

    public String getTour_title() {
        return tour_title;
    }

    public void setTour_title(String tour_title) {
        this.tour_title = tour_title;
    }

    public String getTour_id() {
        return tour_id;
    }

    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }

    public String getTour_leader() {
        return tour_leader;
    }

    public void setTour_leader(String tour_leader) {
        this.tour_leader = tour_leader;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
    }

    public long getTour_status() {
        return tour_status;
    }

    public void setTour_status(long tour_status) {
        this.tour_status = tour_status;
    }
}
