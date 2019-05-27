package com.qatarmuseums.qatarmuseumsapp.home;


import com.google.gson.annotations.SerializedName;

public class UserRegistrationModel {
    @SerializedName("Title")
    private String eventTitle;
    @SerializedName("event_ID")
    private String eventID;
    @SerializedName(value = "reg_id", alternate = "Registration_ID")
    private String regID;
    @SerializedName("Seats")
    private String numberOfReservations;

    public UserRegistrationModel() {

    }

    public UserRegistrationModel(String eventTitle, String eventID, String regId) {
        this.eventTitle = eventTitle;
        this.eventID = eventID;
        this.regID = regId;
    }

    String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    String getNumberOfReservations() {
        return numberOfReservations;
    }
}
