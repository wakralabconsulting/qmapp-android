package com.qatarmuseums.qatarmuseumsapp.park;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NMoQParkList implements Comparable<NMoQParkList> {

    @SerializedName("Nid")
    private String nid;
    @SerializedName(value = "Title", alternate = "title")
    private String mainTitle;
    @SerializedName("sort_id")
    private String sortId;
    @SerializedName(value = "images", alternate = "images ")
    private ArrayList<String> image;

    NMoQParkList(String nid, String mainTitle, String sortId, ArrayList<String> image) {
        this.nid = nid;
        this.mainTitle = mainTitle;
        this.sortId = sortId;
        this.image = image;
    }

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

    @Override
    public int compareTo(@NonNull NMoQParkList nMoQParkList) {
        if (nMoQParkList.sortId != null)
            return Integer.valueOf(this.sortId).compareTo(Integer.valueOf(nMoQParkList.sortId));
        else
            return 0;


    }
}
