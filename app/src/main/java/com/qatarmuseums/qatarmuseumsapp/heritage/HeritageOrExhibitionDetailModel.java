package com.qatarmuseums.qatarmuseumsapp.heritage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HeritageOrExhibitionDetailModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName("images")
    private ArrayList<String> image;
    @SerializedName("Longitude")
    private String longitude;
    @SerializedName("Latitude")
    private String latitude;
    @SerializedName(value = "short_description", alternate = {"Short_description"})
    private String shortDescription;
    @SerializedName(value = "long_description", alternate = {"Long_description"})
    private String longDescription;
    @SerializedName("start_Date")
    private String startDate;
    @SerializedName("end_Date")
    private String endDate;

    public HeritageOrExhibitionDetailModel(String name, String id,
                                           String location, ArrayList<String> image,
                                           String longitude, String latitude,
                                           String shortDescription, String longDescription,
                                           String startDate, String endDate) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList image) {
        this.image = image;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}