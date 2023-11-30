package com.simtech.app1.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationObservationPojo implements Serializable {

    private String date;
    private String location;
    private ArrayList<TrialObservationPojo> trials;

    public LocationObservationPojo(String date, String location, ArrayList<TrialObservationPojo> trials) {
        this.date = date;
        this.location = location;
        this.trials = trials;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<TrialObservationPojo> getTrials() {
        return trials;
    }

    public void setTrials(ArrayList<TrialObservationPojo> trials) {
        this.trials = trials;
    }
}
