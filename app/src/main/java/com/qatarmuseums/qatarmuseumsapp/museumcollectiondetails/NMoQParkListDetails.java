package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NMoQParkListDetails implements Comparable<NMoQParkListDetails> {
    @SerializedName("Nid")
    private String nid;
    @SerializedName("title")
    private String mainTitle;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("Description")
    private String Description;
    @SerializedName("sort_id")
    private String sortId;

    public NMoQParkListDetails(String nid, String mainTitle, ArrayList<String> images, String description, String sortId) {
        this.nid = nid;
        this.mainTitle = mainTitle;
        this.images = images;
        Description = description;
        this.sortId = sortId;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    @Override
    public int compareTo(@NonNull NMoQParkListDetails nMoQParkListDetails) {
        if (nMoQParkListDetails.getSortId() != null && !this.sortId.trim().equals(""))
            return Integer.valueOf(this.sortId).compareTo(Integer.valueOf(nMoQParkListDetails.getSortId()));
        else
            return 0;
    }
}
