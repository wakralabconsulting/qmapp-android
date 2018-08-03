package com.qatarmuseums.qatarmuseumsapp.commonpage;


import com.google.gson.annotations.SerializedName;

public class CommonModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName("LATEST_IMAGE")
    private String image;
    @SerializedName("start_Date")
    private String  startDate;
    @SerializedName("end_Date")
    private String  endDate;
    private Boolean isOpen, isfavourite;

    public CommonModel() {

    }


    public CommonModel(String id, String name, String startDate,
                       String endDate, String location, String image, Boolean isOpen,
                       Boolean isfavourite) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getEndDate(){return endDate;}

    public void setStartDate(){this.endDate=endDate;}

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
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
