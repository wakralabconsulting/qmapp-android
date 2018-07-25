package com.qatarmuseums.qatarmuseumsapp.commonactivity;


public class CommonModel {
    private String name, date, location;
    private String id;
    private String image;
    private Boolean isOpen, isfavourite;

    public CommonModel() {

    }


    public CommonModel(String id, String name, String exhibitionDate, String location, String image, Boolean isOpen,
                       Boolean isfavourite) {
        this.id = id;
        this.name = name;
        this.date = exhibitionDate;
        this.location = location;
        this.image = image;
        this.isOpen = isOpen;
        this.isfavourite = isfavourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getStatusTag() {
        return isOpen;
    }

    public void setStatusTag(Boolean open) {
        isOpen = open;
    }

    public Boolean getIsfavourite() {
        return isfavourite;
    }

    public void setIsfavourite(Boolean isfavourite) {
        this.isfavourite = isfavourite;
    }
}
