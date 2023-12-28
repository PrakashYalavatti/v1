package com.simtech.app1.pojo.harvest;

import java.util.ArrayList;

public class HarvestDetailPojo {
    public String locationid;
    public String trialyear;
    public String trialtypeid;
    public String varietycode;
    public String replication;
    public ArrayList<HarvestDataPojo> harvest_data;

    public HarvestDetailPojo(String locationId, String yearOnly, String trialTypeId, String vatietyCode, String replicationName) {
        this.locationid = locationId;
        this.trialyear = yearOnly;
        this.trialtypeid = trialTypeId;
        this.varietycode = vatietyCode;
        this.replication = replicationName;
    }
}
