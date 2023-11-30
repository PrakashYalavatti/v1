package com.simtech.app1.pojo;

import java.io.Serializable;

public class RVChildItem implements Serializable {
        // Declaration of the variable
        private String varietyCode;

        // Constructor of the class
        // to initialize the variable*
        public RVChildItem(String varietyCode)
        {
            this.varietyCode = varietyCode;
        }

        // Getter and Setter method
        // for the parameter
        public String getVarietyCode()
        {
            return varietyCode;
        }

        public void setVarietyCode(
                String varietyCode)
        {
                varietyCode = varietyCode;
        }
}