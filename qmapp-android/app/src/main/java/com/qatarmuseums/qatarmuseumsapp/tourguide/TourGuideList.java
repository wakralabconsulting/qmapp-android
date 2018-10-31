package com.qatarmuseums.qatarmuseumsapp.tourguide;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TourGuideList {
    @SerializedName("Title")
    private String name;
    @SerializedName("Museums_entity")
    private String museumId;
    @SerializedName("Nid")
    private String tourId;
    @SerializedName("multimedia")
    private ArrayList<String> imageurl;
    int image;
    String tourguideId;

    public TourGuideList(String name, String tourId, int image, String museumId) {
        this.name = name;
        this.image = image;
        this.tourId = tourId;
        this.museumId = museumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuseumId() {
        return museumId;
    }

    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public ArrayList<String> getImageurl() {
        return imageurl;
    }

    public String getFirstImageurl() {
        return imageurl.get(0);
    }

    public void setImageurl(ArrayList<String> imageurl) {
        this.imageurl = imageurl;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTourguideId() {
        return tourguideId;
    }

    public void setTourguideId(String tourguideId) {
        this.tourguideId = tourguideId;
    }
}
