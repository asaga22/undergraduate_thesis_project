package com.elkasaga.undegraduatethesisproject.models;

public class Itinerary {

    String itineraryid, date, starttime, endtime, description;
    long itinerarystatus, day, itinerarycategory, indexstarttime;

    public Itinerary(String itineraryid, String date, String starttime, String endtime, String description, long itinerarystatus, long day, long itinerarycategory, long indexstarttime) {
        this.itineraryid = itineraryid;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.description = description;
        this.itinerarystatus = itinerarystatus;
        this.day = day;
        this.itinerarycategory = itinerarycategory;
        this.indexstarttime = indexstarttime;
    }

    public Itinerary() {
    }

    public String getItineraryid() {
        return itineraryid;
    }

    public void setItineraryid(String itineraryid) {
        this.itineraryid = itineraryid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getItinerarystatus() {
        return itinerarystatus;
    }

    public void setItinerarystatus(long itinerarystatus) {
        this.itinerarystatus = itinerarystatus;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getItinerarycategory() {
        return itinerarycategory;
    }

    public void setItinerarycategory(long itinerarycategory) {
        this.itinerarycategory = itinerarycategory;
    }

    public long getIndexstarttime() {
        return indexstarttime;
    }

    public void setIndexstarttime(long indexstarttime) {
        this.indexstarttime = indexstarttime;
    }
}
