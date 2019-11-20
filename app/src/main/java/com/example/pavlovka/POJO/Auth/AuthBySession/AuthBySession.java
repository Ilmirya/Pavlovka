package com.example.pavlovka.POJO.Auth.AuthBySession;

import com.example.pavlovka.POJO.HeadApi;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthBySession{
    @SerializedName("sessionId")
    @Expose
    public String sessionId;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getSessionId() {
        return sessionId;
    }
}