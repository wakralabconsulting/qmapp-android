package com.qatarmuseums.qatarmuseumsapp.park;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NMoQParkList {

    @SerializedName(value = "Title", alternate = "title")
    private String mainTitle;
    @SerializedName("Nid")
    private String nid;
    @SerializedName(value = "images", alternate = "images ")
    private ArrayList<String> image;
    @SerializedName("sort_id")
    private String sortId;


    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getNid() {
        return nid;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }
}
