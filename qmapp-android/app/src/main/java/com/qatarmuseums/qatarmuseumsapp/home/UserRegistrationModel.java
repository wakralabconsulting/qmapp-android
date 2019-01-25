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

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    public String getNumberOfReservations() {
        return numberOfReservations;
    }
}
