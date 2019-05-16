package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TourDetailsModel implements Parcelable, Comparable<TourDetailsModel> {
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
    @SerializedName("moderator_name")
    private String tourSpeakerName;
    @SerializedName("description_for_moderator")
    private String tourSpeakerInfo;
    @SerializedName(value = "nid", alternate = "node_id")
    private String nId;
    @SerializedName("Seats_remaining")
    private String seatsRemaining;
    private String eventTimeStampDiff;
    private long startTimeStamp;
    private long endTimeStamp;

    public TourDetailsModel(String tour_title, ArrayList<String> tour_images,
                            String tour_date, String tour_id,
                            String tour_contact_email, String tour_contact_phone,
                            String tour_latitude,
                            String tour_longtitude,
                            String tour_sort_id, String tour_body,
                            String tour_registered,
                            String seatsRemaining) {
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
        this.seatsRemaining = seatsRemaining;
    }

    protected TourDetailsModel(Parcel in) {
        tourTitle = in.readString();
        tourBody = in.readString();
        tourImage = in.createStringArrayList();
        tourDate = in.readString();
        tourEventId = in.readString();
        tourRegister = in.readString();
        tourContactEmail = in.readString();
        tourContactPhone = in.readString();
        tourLongtitude = in.readString();
        tourLatitude = in.readString();
        tourSortId = in.readString();
        tourRegistered = in.readString();
        tourSpeakerName = in.readString();
        tourSpeakerInfo = in.readString();
        nId = in.readString();
        seatsRemaining = in.readString();
        eventTimeStampDiff = in.readString();
        startTimeStamp = in.readLong();
        endTimeStamp = in.readLong();
    }

    public static final Creator<TourDetailsModel> CREATOR = new Creator<TourDetailsModel>() {
        @Override
        public TourDetailsModel createFromParcel(Parcel in) {
            return new TourDetailsModel(in);
        }

        @Override
        public TourDetailsModel[] newArray(int size) {
            return new TourDetailsModel[size];
        }
    };

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

    public String getTourSpeakerName() {
        return tourSpeakerName;
    }

    public void setTourSpeakerName(String tourSpeakerName) {
        this.tourSpeakerName = tourSpeakerName;
    }

    public String getTourSpeakerInfo() {
        return tourSpeakerInfo;
    }

    public void setTourSpeakerInfo(String tourSpeakerInfo) {
        this.tourSpeakerInfo = tourSpeakerInfo;
    }

    public String getNid() {
        return nId;
    }

    public void setNid(String nId) {
        this.nId = nId;
    }

    public Long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(Long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public Long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(Long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public String getEventTimeStampDiff() {
        return eventTimeStampDiff;
    }

    public void setEventTimeStampDiff(String eventTimeStampDiff) {
        this.eventTimeStampDiff = eventTimeStampDiff;
    }

    public String getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(String seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tourTitle);
        dest.writeString(tourBody);
        dest.writeStringList(tourImage);
        dest.writeString(tourDate);
        dest.writeString(tourEventId);
        dest.writeString(tourRegister);
        dest.writeString(tourContactEmail);
        dest.writeString(tourContactPhone);
        dest.writeString(tourLongtitude);
        dest.writeString(tourLatitude);
        dest.writeString(tourSortId);
        dest.writeString(tourRegistered);
        dest.writeString(tourSpeakerName);
        dest.writeString(tourSpeakerInfo);
        dest.writeString(nId);
        dest.writeString(seatsRemaining);
        dest.writeString(eventTimeStampDiff);
        dest.writeLong(startTimeStamp);
        dest.writeLong(endTimeStamp);
    }

    @Override
    public int compareTo(@NonNull TourDetailsModel tourDetailsModel) {
        if (tourDetailsModel.tourSortId != null && !this.tourSortId.trim().equals(""))
            return Integer.valueOf(this.tourSortId).compareTo(Integer.valueOf(tourDetailsModel.tourSortId));
        else
            return 0;
    }
}
