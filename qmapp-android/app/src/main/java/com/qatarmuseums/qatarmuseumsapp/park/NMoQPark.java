package com.qatarmuseums.qatarmuseumsapp.park;


import com.google.gson.annotations.SerializedName;

public class NMoQPark {

    @SerializedName("nid")
    private String nid;
    @SerializedName("title")
    private String mainTitle;
    @SerializedName("Main_description")
    private String mainDescription;
    @SerializedName(value = "park_title", alternate = "park_title ")
    private String parkTitle;
    @SerializedName("park_description")
    private String parkDescription;
    @SerializedName("park_hours_title")
    private String parkHoursTitle;
    @SerializedName("parks_hours_description")
    private String parksHoursDescription;
    @SerializedName("location_title")
    private String locationTitle;
    @SerializedName("latitude_nmoq")
    private String latitudeNMoQ;
    @SerializedName("longtitude_nmoq")
    private String longitudeNMoQ;

    NMoQPark(String nid, String mainTitle, String mainDescription, String parkTitle, String parkDescription, String parkHoursTitle, String parksHoursDescription, String locationTitle, String latitudeNMoQ, String longitudeNMoQ) {
        this.nid = nid;
        this.mainTitle = mainTitle;
        this.mainDescription = mainDescription;
        this.parkTitle = parkTitle;
        this.parkDescription = parkDescription;
        this.parkHoursTitle = parkHoursTitle;
        this.parksHoursDescription = parksHoursDescription;
        this.locationTitle = locationTitle;
        this.latitudeNMoQ = latitudeNMoQ;
        this.longitudeNMoQ = longitudeNMoQ;
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

    String getMainDescription() {
        return mainDescription;
    }

    void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    String getParkTitle() {
        return parkTitle;
    }

    void setParkTitle(String parkTitle) {
        this.parkTitle = parkTitle;
    }

    String getParkDescription() {
        return parkDescription;
    }

    void setParkDescription(String parkDescription) {
        this.parkDescription = parkDescription;
    }

    String getParkHoursTitle() {
        return parkHoursTitle;
    }

    String getParksHoursDescription() {
        return parksHoursDescription;
    }

    String getLocationTitle() {
        return locationTitle;
    }

    String getLatitudeNMoQ() {
        return latitudeNMoQ;
    }

    String getLongitudeNMoQ() {
        return longitudeNMoQ;
    }

    public void setParkHoursTitle(String parkHoursTitle) {
        this.parkHoursTitle = parkHoursTitle;
    }

    public void setParksHoursDescription(String parksHoursDescription) {
        this.parksHoursDescription = parksHoursDescription;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public void setLatitudeNMoQ(String latitudeNMoQ) {
        this.latitudeNMoQ = latitudeNMoQ;
    }

    public void setLongitudeNMoQ(String longitudeNMoQ) {
        this.longitudeNMoQ = longitudeNMoQ;
    }
}
