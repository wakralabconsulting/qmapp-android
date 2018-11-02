package com.qatarmuseums.qatarmuseumsapp.publicart;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class PublicArtModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("images")
    private ArrayList<String> image;
    @SerializedName("short_description")
    private String shortDescription;
    @SerializedName("Description")
    private String longDescription;
    @SerializedName("longtitude")
    private String longitude;
    @SerializedName("Latitude")
    private String latitude;


    public PublicArtModel(String name, String id, ArrayList<String> image, String shortDescription, String longDescription,String latitude,
                          String longitude) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.latitude=latitude;
        this.longitude=longitude;
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

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
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
}
