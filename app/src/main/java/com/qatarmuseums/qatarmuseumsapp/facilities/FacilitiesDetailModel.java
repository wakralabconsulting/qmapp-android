package com.qatarmuseums.qatarmuseumsapp.facilities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FacilitiesDetailModel {

    @SerializedName("Title")
    private String facilitiesTitle;
    @SerializedName("images")
    private ArrayList<String> facilityImage;
    @SerializedName("Subtitle")
    private String facilitiesSubtitle;
    @SerializedName("Description")
    private String facilityDescription;
    @SerializedName("timing")
    private String facilitiesTiming;
    @SerializedName("title_timing")
    private String facilityTitleTiming;
    @SerializedName("nid")
    private String facilitiesId;
    @SerializedName(value = "Longitude", alternate = {"longtitude"})
    private String longitude;

    @SerializedName("category")
    private String facilitiesCategoryId;
    @SerializedName(value = "Latitude", alternate = {"latitude "})
    private String latitude;
    @SerializedName("location title")
    private String locationTitle;


    public FacilitiesDetailModel(String facilitiesTitle, ArrayList<String> facilityImage, String facilitiesSubtitle,
                                 String facilityDescription, String facilitiesTiming, String facilityTitleTiming, String facilitiesId,
                                 String longitude, String facilitiesCategoryId, String latitude, String locationTitle) {
        this.facilitiesTitle = facilitiesTitle;
        this.facilityImage = facilityImage;
        this.facilitiesSubtitle = facilitiesSubtitle;
        this.facilityDescription = facilityDescription;
        this.facilitiesTiming = facilitiesTiming;
        this.facilityTitleTiming = facilityTitleTiming;
        this.facilitiesId = facilitiesId;
        this.longitude = longitude;
        this.facilitiesCategoryId = facilitiesCategoryId;
        this.latitude = latitude;
        this.locationTitle = locationTitle;
    }


    public String getFacilitiesTitle() {
        return facilitiesTitle;
    }

    public ArrayList<String> getFacilityImage() {
        return facilityImage;
    }

    public String getFacilitiesSubtitle() {
        return facilitiesSubtitle;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public String getFacilitiesTiming() {
        return facilitiesTiming;
    }

    public String getFacilityTitleTiming() {
        return facilityTitleTiming;
    }

    public String getFacilitiesId() {
        return facilitiesId;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFacilitiesCategoryId() {
        return facilitiesCategoryId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setFacilitiesTitle(String facilitiesTitle) {
        this.facilitiesTitle = facilitiesTitle;
    }

    public void setFacilitiesSubtitle(String facilitiesSubtitle) {
        this.facilitiesSubtitle = facilitiesSubtitle;
    }

    public void setFacilityDescription(String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    public void setFacilitiesTiming(String facilitiesTiming) {
        this.facilitiesTiming = facilitiesTiming;
    }

    public void setFacilityTitleTiming(String facilityTitleTiming) {
        this.facilityTitleTiming = facilityTitleTiming;
    }

    public void setFacilitiesId(String facilitiesId) {
        this.facilitiesId = facilitiesId;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setFacilitiesCategoryId(String facilitiesCategoryId) {
        this.facilitiesCategoryId = facilitiesCategoryId;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }
}
