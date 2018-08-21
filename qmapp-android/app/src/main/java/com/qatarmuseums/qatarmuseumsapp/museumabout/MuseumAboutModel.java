package com.qatarmuseums.qatarmuseumsapp.museumabout;

import com.google.gson.annotations.SerializedName;

public class MuseumAboutModel {
    @SerializedName("title")
    private String title;
    @SerializedName("mid")
    private String museumId;
    @SerializedName("image")
    private String image;
    @SerializedName("Longitude")
    private String longitude;
    @SerializedName("Latitude")
    private String latitude;
    @SerializedName("short_desc")
    private String shortDescription;
    @SerializedName("long_desc")
    private String longDescription;
    @SerializedName("subtitle")
    private String subTitle;
    @SerializedName("filter")
    private String filter;
    @SerializedName("contact")
    private String contact;
    @SerializedName("opening_time")
    private String timingInfo;

    public MuseumAboutModel() {
    }

    public MuseumAboutModel(String title, String museumId, String image,
                            String longitude, String latitude, String shortDescription,
                            String longDescription, String subTitle, String filter,
                            String contact, String timingInfo) {
        this.title = title;
        this.museumId = museumId;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.subTitle = subTitle;
        this.filter = filter;
        this.contact = contact;
        this.timingInfo = timingInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMuseumId() {
        return museumId;
    }

    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTimingInfo() {
        return timingInfo;
    }

    public void setTimingInfo(String timingInfo) {
        this.timingInfo = timingInfo;
    }
}



