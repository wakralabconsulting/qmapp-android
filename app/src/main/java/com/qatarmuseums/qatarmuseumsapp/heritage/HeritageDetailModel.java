package com.qatarmuseums.qatarmuseumsapp.heritage;

import com.google.gson.annotations.SerializedName;

public class HeritageDetailModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName("LATEST_IMAGE")
    private String image;
    @SerializedName("Longitude")
    private String longitude;
    @SerializedName("Latitude")
    private String latitude;
    @SerializedName("short_description")
    private String shortDescription;
    @SerializedName("long_description")
    private String longDescription;

    public HeritageDetailModel() {
    }

    public HeritageDetailModel(String name, String id, String location,
                               String image, String longitude, String latitude,
                               String shortDescription, String longDescription) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
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
}