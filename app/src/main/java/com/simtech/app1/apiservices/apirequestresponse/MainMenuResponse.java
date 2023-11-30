package com.simtech.app1.apiservices.apirequestresponse;

import com.google.gson.annotations.SerializedName;
import com.simtech.app1.pojo.TrialObservationPojo;
import com.simtech.app1.pojonew.TrialtypePojo;

import java.util.ArrayList;

public class MainMenuResponse{
    @SerializedName("data")
    private ArrayList<TrialObservationPojo> data;

    public MainMenuResponse(ArrayList<TrialObservationPojo> data) {
        this.data = data;
    }

    public ArrayList<TrialObservationPojo> getData() {
        return data;
    }

    public void setData(ArrayList<TrialObservationPojo> data) {
        this.data = data;
    }
}
