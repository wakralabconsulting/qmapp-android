package com.qatarmuseums.qatarmuseumsapp.park;


import com.google.gson.annotations.SerializedName;

public class ParkList {

    @SerializedName("Title")
    private String mainTitle;
    @SerializedName("Description")
    private String shortDescription;
    @SerializedName("image")
    private String image;
    @SerializedName("sort_id")
    private String sortId;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("timingInfo")
    private String timingInfo;


    public ParkList() {
        }

    public ParkList(String mainTitle, String shortDescription, String image,
                    String latitude, String longitude, String timingInfo) {
        this.mainTitle = mainTitle;
        this.shortDescription = shortDescription;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timingInfo = timingInfo;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }


    public void setLongDescription(String longDescription) {
        longDescription = longDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimingInfo() {
        return timingInfo;
    }

    public void setTimingInfo(String timingInfo) {
        this.timingInfo = timingInfo;
    }
}
