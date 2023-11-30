package com.simtech.app1.apiservices.apirequestresponse;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {
    @SerializedName("status")
    public int status;
    @SerializedName("access_token")
    public String access_token;
    @SerializedName("message")
    public String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
