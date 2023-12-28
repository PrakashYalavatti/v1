package com.simtech.app1.pojo.harvest;

public class HarvestDataPojo {
    public String observationcategory;
    public String dimension;
    public String observation;
    public String observationvalue;

    public HarvestDataPojo(String observationcategory, String dimension, String observation, String observationvalue) {
        this.observationcategory = observationcategory;
        this.dimension = dimension;
        this.observation = observation;
        this.observationvalue = observationvalue;
    }
}
