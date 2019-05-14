package com.qatarmuseums.qatarmuseumsapp.notification;


public class NotificationModel {
    private String title;
    private String language;
    private String number;

    NotificationModel(String title) {

        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
