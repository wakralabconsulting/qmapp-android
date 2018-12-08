package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TourDetailsModel {
    @SerializedName("Title")
    private String tourTitle;
    @SerializedName("Body")
    private String tourBody;
    @SerializedName("image_banner")
    private ArrayList<String> tourImage;
    @SerializedName("Date")
    private String tourDate;
    @SerializedName("NMoq_event")
    private String tourEventId;
    @SerializedName("Register")
    private String tourRegister;
    @SerializedName("contact_email")
    private String tourContactEmail;
    @SerializedName("contact_phone")
    private String tourContactPhone;
    @SerializedName("longtitude")
    private String tourLongtitude;
    @SerializedName("latitude")
    private String tourLatitude;
    @SerializedName("sort_id")
    private String tourSortId;
    @SerializedName("registered")
    private String tourRegistered;

    public TourDetailsModel(String tour_title, ArrayList<String> tour_images,
                            String tour_date, String tour_id,
                            String tour_contact_email, String tour_contact_phone,
                            String tour_latitude,
                            String tour_longtitude,
                            String tour_sort_id, String tour_body,
                            String tour_registered) {
        this.tourTitle = tour_title;
        this.tourImage = tour_images;
        this.tourDate = tour_date;
        this.tourContactEmail = tour_contact_email;
        this.tourContactPhone = tour_contact_phone;
        this.tourLatitude = tour_latitude;
        this.tourLongtitude = tour_longtitude;
        this.tourSortId = tour_sort_id;
        this.tourBody = tour_body;
        this.tourRegistered = tour_registered;
        this.tourEventId = tour_id;
    }

    public String getTourTitle() {
        return tourTitle;
    }

    public void setTourTitle(String tourTitle) {
        this.tourTitle = tourTitle;
    }

    public String getTourBody() {
        return tourBody;
    }

    public void setTourBody(String tourBody) {
        this.tourBody = tourBody;
    }

    public ArrayList<String> getTourImage() {
        return tourImage;
    }

    public void setTourImage(ArrayList<String> tourImage) {
        this.tourImage = tourImage;
    }

    public String getTourDate() {
        return tourDate;
    }

    public void setTourDate(String tourDate) {
        this.tourDate = tourDate;
    }

    public String getTourEventId() {
        return tourEventId;
    }

    public void setTourEventId(String tourEventId) {
        this.tourEventId = tourEventId;
    }

    public String getTourRegister() {
        return tourRegister;
    }

    public void setTourRegister(String tourRegister) {
        this.tourRegister = tourRegister;
    }

    public String getTourContactEmail() {
        return tourContactEmail;
    }

    public void setTourContactEmail(String tourContactEmail) {
        this.tourContactEmail = tourContactEmail;
    }

    public String getTourContactPhone() {
        return tourContactPhone;
    }

    public void setTourContactPhone(String tourContactPhone) {
        this.tourContactPhone = tourContactPhone;
    }

    public String getTourLongtitude() {
        return tourLongtitude;
    }

    public void setTourLongtitude(String tourLongtitude) {
        this.tourLongtitude = tourLongtitude;
    }

    public String getTourLatitude() {
        return tourLatitude;
    }

    public void setTourLatitude(String tourLatitude) {
        this.tourLatitude = tourLatitude;
    }

    public String getTourSortId() {
        return tourSortId;
    }

    public void setTourSortId(String tourSortId) {
        this.tourSortId = tourSortId;
    }

    public String getTourRegistered() {
        return tourRegistered;
    }

    public void setTourRegistered(String tourRegistered) {
        this.tourRegistered = tourRegistered;
    }
}
