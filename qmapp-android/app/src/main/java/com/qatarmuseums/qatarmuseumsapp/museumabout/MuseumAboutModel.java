package com.qatarmuseums.qatarmuseumsapp.museumabout;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MuseumAboutModel {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String museumId;
    @SerializedName("tourguide_available")
    private String tourGuideAvailable;
    @SerializedName("Mobile_descriptif")
    private ArrayList<String> descriptionList;
    @SerializedName("Multimedia_file")
    private ArrayList<String> imageList;
    @SerializedName("contact_number")
    private String contactNumber;
    @SerializedName("contact_email")
    private String contactEmail;
    @SerializedName("mobile_longtitude")
    private String longitude;
    @SerializedName("mobile_latitude")
    private String latitude;
    @SerializedName("tour_guide_availability")
    private String tourGuideAvailability;
    @SerializedName("Subtitle")
    private String subTitle;
    @SerializedName("opening_time")
    private String timingInfo;
    @SerializedName("multimedia_video")
    private ArrayList<String> videoInfo;

    public MuseumAboutModel() {
    }

    public MuseumAboutModel(String name, String museumId, String tourGuideAvailable,
                            ArrayList<String> descriptionList, ArrayList<String> imageList,
                            String contactNumber, String contactEmail, String longitude,
                            String latitude, String tourGuideAvailability, String subTitle,
                            String timingInfo,ArrayList<String> videoInfo) {
        this.name = name;
        this.museumId = museumId;
        this.tourGuideAvailable = tourGuideAvailable;
        this.descriptionList = descriptionList;
        this.imageList = imageList;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.tourGuideAvailability = tourGuideAvailability;
        this.subTitle = subTitle;
        this.timingInfo = timingInfo;
        this.videoInfo=videoInfo;
    }


    public String getName() {
        return name;
    }

    public String getMuseumId() {
        return museumId;
    }

    public String getTourGuideAvailable() {
        return tourGuideAvailable;
    }

    public ArrayList<String> getDescriptionList() {
        return descriptionList;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getTourGuideAvailability() {
        return tourGuideAvailability;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getTimingInfo() {
        return timingInfo;
    }

    public ArrayList<String> getVideoInfo() {
        return videoInfo;
    }
}



