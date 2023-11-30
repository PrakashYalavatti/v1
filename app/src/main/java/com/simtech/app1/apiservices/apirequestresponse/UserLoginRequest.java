package com.simtech.app1.apiservices.apirequestresponse;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequest {

    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
