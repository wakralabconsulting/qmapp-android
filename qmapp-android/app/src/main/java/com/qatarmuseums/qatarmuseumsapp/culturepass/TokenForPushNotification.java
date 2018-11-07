package com.qatarmuseums.qatarmuseumsapp.culturepass;

public class TokenForPushNotification {
    private String pushNotificationToken, type;

    public TokenForPushNotification(String token, String type) {
        this.pushNotificationToken = token;
        this.type = type;
    }

    public String getToken() {
        return pushNotificationToken;
    }

    public String getType() {
        return type;
    }
}
