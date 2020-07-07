package com.elkasaga.undegraduatethesisproject.models;

public class RepresentedPassanger {

    private String fullname, representedby;

    public RepresentedPassanger(String fullname, String representedby) {
        this.fullname = fullname;
        this.representedby = representedby;
    }

    public RepresentedPassanger() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRepresentedby() {
        return representedby;
    }

    public void setRepresentedby(String representedby) {
        this.representedby = representedby;
    }

}
