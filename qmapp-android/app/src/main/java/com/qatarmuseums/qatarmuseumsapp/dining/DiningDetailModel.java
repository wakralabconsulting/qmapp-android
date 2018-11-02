package com.qatarmuseums.qatarmuseumsapp.dining;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiningDetailModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName("image")
    private String image;
    @SerializedName("opening_time")
    private String openingTime;
    @SerializedName("close_time")
    private String closingTime;
    @SerializedName("Description")
    private String description;
    @SerializedName("sort_id")
    private String sortId;
    @SerializedName("Latitude")
    private String latitude;
    @SerializedName("Longitude")
    private String longitude;
    @SerializedName("images")
    private ArrayList<String> images;
    public DiningDetailModel() {
    }
    
    public DiningDetailModel(String name, String id, String location, String image,
                             String openingTime, String closingTime,
                             String description, String sortId,
                             String latitude, String longitude,ArrayList<String> images) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.image = image;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.description = description;
        this.sortId = sortId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images=images;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
