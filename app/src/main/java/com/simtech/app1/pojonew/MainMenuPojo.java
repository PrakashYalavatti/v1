package com.simtech.app1.pojonew;

import com.simtech.app1.pojo.TrialObservationPojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainMenuPojo implements Serializable {
    private List<MainMenuPojo> data = new ArrayList<MainMenuPojo>();
    public List<MainMenuPojo> getData() {
        return data;
    }
    public void setData(List<MainMenuPojo> data) {
        this.data = data;
    }
}
