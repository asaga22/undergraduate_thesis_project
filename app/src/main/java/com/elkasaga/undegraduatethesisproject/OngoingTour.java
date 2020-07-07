package com.elkasaga.undegraduatethesisproject;

import android.app.Application;

import com.elkasaga.undegraduatethesisproject.models.GroupTour;

public class OngoingTour extends Application {

    private GroupTour groupTour = null;

    public GroupTour getGroupTour() {
        return groupTour;
    }

    public void setGroupTour(GroupTour groupTour) {
        this.groupTour = groupTour;
    }
}
