package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NMoQParkListDetails implements Comparable<NMoQParkListDetails> {
    @SerializedName("title")
    private String mainTitle;
    @SerializedName("Nid")
    private String nid;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("Description")
    private String Description;
    @SerializedName("sort_id")
    private String sort_id;

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

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    @Override
    public int compareTo(@NonNull NMoQParkListDetails nMoQParkListDetails) {
        if (nMoQParkListDetails.getSort_id() != null)
            return Integer.valueOf(this.sort_id).compareTo(Integer.valueOf(nMoQParkListDetails.getSort_id()));
        else
            return 0;
    }
}
