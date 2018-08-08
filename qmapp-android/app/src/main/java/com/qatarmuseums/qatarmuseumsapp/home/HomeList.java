package com.qatarmuseums.qatarmuseumsapp.home;


import com.google.gson.annotations.SerializedName;

public class HomeList {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("tourguide_available")
    private String tourguideAvailable;

    public HomeList() {

    }

    public HomeList(String name, String id, String image, String tourguideAvailable) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.tourguideAvailable = tourguideAvailable;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTourguideAvailable() {
        return tourguideAvailable;
    }

    public void setTourguideAvailable(String tourguideAvailable) {
        this.tourguideAvailable = tourguideAvailable;
    }

}
