package com.qatarmuseums.qatarmuseumsapp.notification;


public class NotificationModel {
    private String title;
    private String id;

    public NotificationModel() {

    }

    public NotificationModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
