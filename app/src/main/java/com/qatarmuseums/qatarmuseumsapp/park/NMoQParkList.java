package com.qatarmuseums.qatarmuseumsapp.park;


import com.google.gson.annotations.SerializedName;

public class NMoQParkList {

    @SerializedName("Title")
    private String mainTitle;
    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("sort_id")
    private String sortId;


    NMoQParkList(String id, String mainTitle, String image,
                 String sortId) {
        this.mainTitle = mainTitle;
        this.id = id;
        this.image = image;
        this.sortId = sortId;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }
}
