package com.simtech.app1.pojo;

import com.google.gson.annotations.SerializedName;
import com.simtech.app1.pojonew.TrialtypePojo;

import java.io.Serializable;
import java.util.ArrayList;

public class TrialObservationPojo implements Serializable {
        @SerializedName("locationid")
        private String locationid;
        @SerializedName("locationname")
        private String locationname;
        @SerializedName("startdate")
        private String startdate;
        @SerializedName("trialtype")
        private ArrayList<TrialtypePojo> trialtype;
        @SerializedName("username")
        private String username;

        public TrialObservationPojo(String locationid, String locationname, String startdate, ArrayList<TrialtypePojo> trialtype, String username) {
            this.locationid = locationid;
            this.locationname = locationname;
            this.startdate = startdate;
            this.trialtype = trialtype;
            this.username = username;
        }

        public String getLocationid() {
            return locationid;
        }

        public void setLocationid(String locationid) {
            this.locationid = locationid;
        }

        public String getLocationname() {
            return locationname;
        }

        public void setLocationname(String locationname) {
            this.locationname = locationname;
        }

        public String getStartdate() {
            return startdate;
        }

        public void setStartdate(String startdate) {
            this.startdate = startdate;
        }

        public ArrayList<TrialtypePojo> getTrialtype() {
            return trialtype;
        }

        public void setTrialtype(ArrayList<TrialtypePojo> trialtype) {
            this.trialtype = trialtype;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
}
