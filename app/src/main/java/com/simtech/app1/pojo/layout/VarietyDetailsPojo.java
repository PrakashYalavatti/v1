package com.simtech.app1.pojo.layout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VarietyDetailsPojo implements Serializable {
    @SerializedName("observedvalue")
    private String observedvalue;
    @SerializedName("varietycode")
    private String varietycode;
    @SerializedName("varietyname")
    private String varietyname;

    public String getObservedvalue() {
        return observedvalue;
    }

    public void setObservedvalue(String observedvalue) {
        this.observedvalue = observedvalue;
    }

    public String getVarietycode() {
        return varietycode;
    }

    public void setVarietycode(String varietycode) {
        this.varietycode = varietycode;
    }

    public String getVarietyname() {
        return varietyname;
    }

    public void setVarietyname(String varietyname) {
        this.varietyname = varietyname;
    }
}
