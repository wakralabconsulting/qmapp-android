package com.qatarmuseums.qatarmuseumsapp.notification;


public class NotificationModel {
    private String info;
    private String id;

    public NotificationModel() {

    }

    public NotificationModel(String id, String info) {
        this.id = id;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
