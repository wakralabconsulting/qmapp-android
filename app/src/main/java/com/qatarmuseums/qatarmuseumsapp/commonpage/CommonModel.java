package com.qatarmuseums.qatarmuseumsapp.commonpage;


import com.google.gson.annotations.SerializedName;

public class CommonModel {
    @SerializedName(value="name", alternate={"Name"})
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName(value="LATEST_IMAGE", alternate={"image","latest_image","image_path"})
    private String image;
    @SerializedName("start_Date")
    private String startDate;
    @SerializedName("end_Date")
    private String endDate;
    @SerializedName("sort_coefficient")
    private String sortCoefficient;
    @SerializedName("Longitude")
    private String longitude;
    @SerializedName("Latitude")
    private String latitude;
    @SerializedName(value="SORT_ID", alternate={"sort_id", "Sort_Id"})
    private String sortId;
    private Boolean isOpen, isfavourite;
    @SerializedName("opening_time")
    private String openingTime;
    @SerializedName("close_time")
    private String closingTime;
    @SerializedName("Description")
    private String description;
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

    public CommonModel(String id, String name, String image, String latitude,
                       String longitude) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CommonModel(String name, String id, String location, String image,
                       String sortId, String openingTime, String closingTime,
                       String description) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.image = image;
        this.sortId = sortId;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.description = description;
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

    public String getEndDate() {
        return endDate;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public void setStartDate() {
        this.endDate = endDate;
    }

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

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSortCoefficient() {
        return sortCoefficient;
    }

    public void setSortCoefficient(String sortCoefficient) {
        this.sortCoefficient = sortCoefficient;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
