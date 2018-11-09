package com.qatarmuseums.qatarmuseumsapp.culturepass;

public class TokenForPushNotification {
    private String token, type;

    public TokenForPushNotification(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
