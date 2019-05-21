package com.qatarmuseums.qatarmuseumsapp.commonlistpage;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommonListModel implements Comparable<CommonListModel> {

    @SerializedName(value = "name", alternate = {"Name", "Title", "subtitle"})
    private String name;
    @SerializedName(value = "ID", alternate = {"full_content_ID", "nid"})
    private String id;
    @SerializedName("Location")
    private String location;
    @SerializedName(value = "LATEST_IMAGE", alternate = {"image", "latest_image", "image_path", "banner_link"})
    private String image;
    @SerializedName("start_Date")
    private String startDate;
    @SerializedName("end_Date")
    private String endDate;
    @SerializedName(value = "Longitude", alternate = {"longtitude"})
    private String longitude;
    @SerializedName(value = "Latitude", alternate = {"latitude "})
    private String latitude;
    @SerializedName(value = "SORT_ID", alternate = {"sort_id", "Sort_Id", "sort_coefficient"})
    private String sortId;
    private Boolean isFavourite;
    @SerializedName("opening_time")
    private String openingTime;
    @SerializedName("close_time")
    private String closingTime;
    @SerializedName(value = "Description", alternate = {"collection_description", "Introduction_Text"})
    private String description;
    @SerializedName("Museums_reference")
    private String museumReference;
    @SerializedName(value = "museum_id", alternate = {"museums"})
    private String museumId;
    @SerializedName("category")
    private long category;

    @SerializedName("NMoq_event_Date")
    private String eventDate;
    @SerializedName("Day")
    private String eventDay;
    @SerializedName(value = "Images", alternate = {"images", "images "})
    private ArrayList<String> images;
    @SerializedName("email")
    private String email;
    @SerializedName("contact_number")
    private String contactNumber;
    @SerializedName("Promotional_code")
    private String promotionalCode;
    @SerializedName("claim_offer")
    private String claimOffer;
    @SerializedName("moderator_name")
    private String moderatorName;
    @SerializedName("description_for_moderator")
    private String descriptionForModerator;
    @SerializedName("Status")
    private String exhibitionStatus;
    @SerializedName("Display_date")
    private String exhibitionDisplayDate;

    private Boolean isTour;
    private Boolean isTravel;

    private boolean imageTypeIsArray = false;

    CommonListModel(String name, String image, String museumReference) {
        this.name = name;
        this.image = image;
        this.museumReference = museumReference;
    }

    CommonListModel(String name, String sortId, String id, String images) {
        this.name = name;
        this.sortId = sortId;
        this.id = id;
        this.image = images;
    }

    CommonListModel(String name, String sortId, String id, String images, String latitude, String longitude) {
        this.name = name;
        this.sortId = sortId;
        this.id = id;
        this.image = images;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    CommonListModel(String id, String name, String location, String image, String exhibitionStatus,
                    String exhibitionDisplayDate, String startDate, String eventDate, String museumId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.image = image;
        this.exhibitionStatus = exhibitionStatus;
        this.exhibitionDisplayDate = exhibitionDisplayDate;
        this.startDate = startDate;
        this.endDate = eventDate;
        this.museumId = museumId;

    }

    CommonListModel(String name, String image, String description, String email, String contactNumber,
                    String promotionalCode, String claimOffer, String contentId,
                    boolean isTravel) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.email = email;
        this.contactNumber = contactNumber;
        this.promotionalCode = promotionalCode;
        this.claimOffer = claimOffer;
        this.id = contentId;
        this.isTravel = isTravel;
    }

    CommonListModel(String name, String id, String location, String image,
                    String sortId, String openingTime, String closingTime,
                    String description) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.image = image;
        this.sortId = sortId;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.description = description;
    }

    CommonListModel(String id, String eventDay, String eventDate, String name,
                    ArrayList<String> images, boolean isTour) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.eventDay = eventDay;
        this.eventDate = eventDate;
        this.isTour = isTour;
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

    String getEndDate() {
        return endDate;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    String getStartDate() {
        return startDate;
    }

    void setStartDate(String date) {
        this.startDate = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    Boolean getIsFavourite() {
        return isFavourite;
    }

    void setIsFavourite(Boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String getMuseumReference() {
        return museumReference;
    }


    public String getMuseumId() {
        return museumId;
    }

    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }

    public long getCategory() {
        return category;
    }

    void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    String getEventDate() {
        return eventDate;
    }

    String getEventDay() {
        return eventDay;
    }

    Boolean getIsTour() {
        return isTour;
    }

    Boolean getIsTravel() {
        return isTravel;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    String getEmail() {
        return email;
    }

    String getContactNumber() {
        return contactNumber;
    }

    String getPromotionalCode() {
        return promotionalCode;
    }

    String getClaimOffer() {
        return claimOffer;
    }

    String getExhibitionStatus() {
        return exhibitionStatus;
    }

    String getDisplayDate() {
        return exhibitionDisplayDate;
    }

    @Override
    public int compareTo(@NonNull CommonListModel commonListModel) {
        if (commonListModel.sortId != null && !this.sortId.trim().equals(""))
            return Integer.valueOf(this.sortId).compareTo(Integer.valueOf(commonListModel.sortId));
        else
            return 0;
    }

    boolean isImageTypeIsArray() {
        return imageTypeIsArray;
    }

    void setImageTypeIsArray(boolean imageTypeIsArray) {
        this.imageTypeIsArray = imageTypeIsArray;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getModeratorName() {
        return moderatorName;
    }

    public String getDescriptionForModerator() {
        return descriptionForModerator;
    }
}

