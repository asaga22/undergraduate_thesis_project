package com.elkasaga.undegraduatethesisproject.models;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private long day;
    private ArrayList<Itinerary> itinerary;

    public Day(long day, ArrayList<Itinerary> itinerary) {
        this.day = day;
        this.itinerary = itinerary;
    }

    public Day() {
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public ArrayList<Itinerary> getItinerary() {
        return itinerary;
    }

    public void setItinerary(ArrayList<Itinerary> itinerary) {
        this.itinerary = itinerary;
    }
}
