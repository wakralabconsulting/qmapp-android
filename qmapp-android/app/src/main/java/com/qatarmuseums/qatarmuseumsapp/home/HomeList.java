package com.qatarmuseums.qatarmuseumsapp.home;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class HomeList implements Comparable<HomeList> {
    @SerializedName(value = "name", alternate = {"Banner_title"})
    private String name;
    @SerializedName(value = "id", alternate = {"full_content_ID"})
    private String id;
    @SerializedName(value = "image", alternate = {"banner_link"})
    private String image;
    @SerializedName("tourguide_available")
    private String tourGuideAvailable;
    @SerializedName("SORt_ID")
    private String sortId;

    public HomeList(String name, String id, String image, String tourGuideAvailable, String sortId) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.tourGuideAvailable = tourGuideAvailable;
        this.sortId = sortId;
    }

    HomeList(String name, String id, String image) {
        this.name = name;
        this.id = id;
        this.image = image;
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

    public String getTourGuideAvailable() {
        return tourGuideAvailable;
    }

    public String getSortId() {
        return sortId;
    }

    @Override
    public int compareTo(@NonNull HomeList homeList) {
        if (homeList.sortId != null && !this.sortId.trim().equals(""))
            return Integer.valueOf(this.sortId).compareTo(Integer.valueOf(homeList.sortId));
        else
            return 0;
    }
}
