package com.simtech.app1.pojo.layout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationDetailsDataPojo implements Serializable {
    @SerializedName("farmer_name")
    private String farmer_name;
    @SerializedName("location_id")
    private String location_id;
    @SerializedName("location_name")
    private String location_name;
    @SerializedName("n_observation_lines")
    private String n_observation_lines;
    @SerializedName("n_replications")
    private String n_replications;
    @SerializedName("purposes")
    private ArrayList<PurposePojo> purposes;
    @SerializedName("start_date")
    private String start_date;
    @SerializedName("state")
    private String state;
    @SerializedName("state_id")
    private String state_id;
    @SerializedName("trial_type_id")
    private String trial_type_id;
    @SerializedName("trial_type_name")
    private String trial_type_name;
    @SerializedName("trial_year")
    private String trial_year;

    public String getFarmer_name() {
        return farmer_name;
    }

    public void setFarmer_name(String farmer_name) {
        this.farmer_name = farmer_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getN_observation_lines() {
        return n_observation_lines;
    }

    public void setN_observation_lines(String n_observation_lines) {
        this.n_observation_lines = n_observation_lines;
    }

    public String getN_replications() {
        return n_replications;
    }

    public void setN_replications(String n_replications) {
        this.n_replications = n_replications;
    }

    public ArrayList<PurposePojo> getPurposes() {
        return purposes;
    }

    public void setPurposes(ArrayList<PurposePojo> purposes) {
        this.purposes = purposes;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getTrial_type_id() {
        return trial_type_id;
    }

    public void setTrial_type_id(String trial_type_id) {
        this.trial_type_id = trial_type_id;
    }

    public String getTrial_type_name() {
        return trial_type_name;
    }

    public void setTrial_type_name(String trial_type_name) {
        this.trial_type_name = trial_type_name;
    }

    public String getTrial_year() {
        return trial_year;
    }

    public void setTrial_year(String trial_year) {
        this.trial_year = trial_year;
    }
}
