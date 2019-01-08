package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SelfGuideStarterModel {
    @SerializedName("Title")
    private String title;
    @SerializedName("Description")
    private String description;
    @SerializedName("multimedia")
    private ArrayList<String> imageList;
    @SerializedName("Museums_entity")
    private String museumsEntity;
    @SerializedName("Nid")
    private String nid;

    public SelfGuideStarterModel(String title, String description, ArrayList<String> imageList,
                                 String museumsEntity, String nid) {
        this.title = title;
        this.description = description;
        this.imageList = imageList;
        this.museumsEntity = museumsEntity;
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public String getFirstImage() {
        return imageList.get(0);
    }

    public String getMuseumsEntity() {
        return museumsEntity;
    }

    public String getNid() {
        return nid;
    }
}
