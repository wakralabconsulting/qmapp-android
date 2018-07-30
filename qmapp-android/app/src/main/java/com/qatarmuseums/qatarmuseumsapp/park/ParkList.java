package com.qatarmuseums.qatarmuseumsapp.park;


public class ParkList {
    private String mainTitle;
    private String title;
    private String image;
    private String shortDescription;
    private String longDescription;
    private String latitude;
    private String longitude;
    private String timingInfo;


    public ParkList() {

    }

    public ParkList(String mainTitle, String title, String shortDescription, String image,
                    String longDescription, String latitude, String longitude, String timingInfo) {
        this.mainTitle = mainTitle;
        this.title = title;
        this.shortDescription = shortDescription;
        this.image = image;
        this.longDescription = longDescription;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
