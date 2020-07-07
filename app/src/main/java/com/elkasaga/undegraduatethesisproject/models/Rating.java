package com.elkasaga.undegraduatethesisproject.models;

public class Rating {

    float tourrate, tourleaderrate;
    String abouttour, abouttourleader, tourid, uid;

    public Rating(float tourrate, float tourleaderrate, String abouttour, String abouttourleader, String tourid, String uid) {
        this.tourrate = tourrate;
        this.tourleaderrate = tourleaderrate;
        this.abouttour = abouttour;
        this.abouttourleader = abouttourleader;
        this.tourid = tourid;
        this.uid = uid;
    }

    public Rating() {
    }

    public float getTourrate() {
        return tourrate;
    }

    public void setTourrate(float tourrate) {
        this.tourrate = tourrate;
    }

    public float getTourleaderrate() {
        return tourleaderrate;
    }

    public void setTourleaderrate(float tourleaderrate) {
        this.tourleaderrate = tourleaderrate;
    }

    public String getAbouttour() {
        return abouttour;
    }

    public void setAbouttour(String abouttour) {
        this.abouttour = abouttour;
    }

    public String getAbouttourleader() {
        return abouttourleader;
    }

    public void setAbouttourleader(String abouttourleader) {
        this.abouttourleader = abouttourleader;
    }

    public String getTourid() {
        return tourid;
    }

    public void setTourid(String tourid) {
        this.tourid = tourid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
