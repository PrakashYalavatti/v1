package com.simtech.app1.pojo.layout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PurposePojo implements Serializable {
    @SerializedName("observations")
    private ArrayList<ObservationPojo> observations;
    @SerializedName("purpose")
    private String purpose;

    public ArrayList<ObservationPojo> getObservations() {
        return observations;
    }

    public void setObservations(ArrayList<ObservationPojo> observations) {
        this.observations = observations;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
