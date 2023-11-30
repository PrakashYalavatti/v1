package com.simtech.app1.pojo.layout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ObservationPojo implements Serializable {
    @SerializedName("observation")
    private String observation;
    @SerializedName("varieties")
    private ArrayList<VarietyDetailsPojo> varieties;

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public ArrayList<VarietyDetailsPojo> getVarieties() {
        return varieties;
    }

    public void setVarieties(ArrayList<VarietyDetailsPojo> varieties) {
        this.varieties = varieties;
    }
}
