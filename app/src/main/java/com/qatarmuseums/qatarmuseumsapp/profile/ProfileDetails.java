package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

public class ProfileDetails {
    @SerializedName("sessid")
    private String sessionId;
    @SerializedName("session_name")
    private String sessionName;
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private UserData user;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }
}
