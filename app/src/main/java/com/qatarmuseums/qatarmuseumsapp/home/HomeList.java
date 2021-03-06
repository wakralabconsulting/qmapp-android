package com.qatarmuseums.qatarmuseumsapp.home;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class HomeList implements Comparable<HomeList> {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("tourguide_available")
    private String tourguideAvailable;
    @SerializedName("SORt_ID")
    private Integer sortId;

    public HomeList() {

    }

    public HomeList(String name, String id, String image, String tourguideAvailable, Integer sortId) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.tourguideAvailable = tourguideAvailable;
        this.sortId = sortId;
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

    public Integer getSortId() {
        return sortId;
    }

    @Override
    public int compareTo(@NonNull HomeList homeList) {
        return this.sortId.compareTo(homeList.sortId);
    }
}
