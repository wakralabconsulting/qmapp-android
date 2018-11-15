package com.qatarmuseums.qatarmuseumsapp.notification;


public class NotificationModel {
    private String title;
    private String number;

    public NotificationModel(String title) {

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
}
