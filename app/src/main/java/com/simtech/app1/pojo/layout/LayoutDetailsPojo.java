package com.simtech.app1.pojo.layout;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LayoutDetailsPojo{
    @SerializedName("data")
    private ArrayList<LocationDetailsDataPojo> data;

    public ArrayList<LocationDetailsDataPojo> getData() {
        return data;
    }

    public void setData(ArrayList<LocationDetailsDataPojo> data) {
        this.data = data;
    }
}
