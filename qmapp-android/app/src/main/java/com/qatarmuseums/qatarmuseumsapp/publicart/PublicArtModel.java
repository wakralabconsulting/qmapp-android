package com.qatarmuseums.qatarmuseumsapp.publicart;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MoongedePC on 10-Aug-18.
 */

public class PublicArtModel {
    @SerializedName("name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("Teaser_image")
    private String image;
    @SerializedName("short_description")
    private String shortDescription;
    @SerializedName("Description")
    private String longDescription;

    public PublicArtModel(String name, String id, String image, String shortDescription, String longDescription) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
