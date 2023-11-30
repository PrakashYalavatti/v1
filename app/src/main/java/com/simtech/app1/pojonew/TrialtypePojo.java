package com.simtech.app1.pojonew;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrialtypePojo implements Serializable {
    @SerializedName("trialstatus")
    private String trialstatus;
    @SerializedName("trialtypeid")
    private String trialtypeid;
    @SerializedName("trialtypename")
    private String trialtypename;

    public String getTrialstatus() {
        return trialstatus;
    }

    public void setTrialstatus(String trialstatus) {
        this.trialstatus = trialstatus;
    }

    public String getTrialtypeid() {
        return trialtypeid;
    }

    public void setTrialtypeid(String trialtypeid) {
        this.trialtypeid = trialtypeid;
    }

    public String getTrialtypename() {
        return trialtypename;
    }

    public void setTrialtypename(String trialtypename) {
        this.trialtypename = trialtypename;
    }
}
