package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

public class UserData extends ProfileDetails {
    @SerializedName("uid")
    private String uId;
    @SerializedName("picture")
    private String picture;
    @SerializedName("name")
    private String name;
    @SerializedName("field_first_name")
    private Model firstName;
    @SerializedName("field_last_name")
    private Model lastName;
    @SerializedName("mail")
    private String mail;
    @SerializedName("field_date_of_birth")
    private Object dateOfBirth;
    @SerializedName("field_location")
    private Model country;
    @SerializedName("field_nationality")
    private Model nationality;
    @SerializedName("field_rsvp_attendance")
    private Object rsvpAttendance;


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getFirstName() {
        return firstName;
    }

    public void setFirstName(Model firstName) {
        this.firstName = firstName;
    }

    public Model getLastName() {
        return lastName;
    }

    public void setLastName(Model lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Object getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Object dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Model getCountry() {
        return country;
    }

    public void setCountry(Model country) {
        this.country = country;
    }

    public Model getNationality() {
        return nationality;
    }

    public void setNationality(Model nationality) {
        this.nationality = nationality;
    }

    public Object getRsvpAttendance() {
        return rsvpAttendance;
    }

    public void setRsvpAttendance(Object rsvpAttendance) {
        this.rsvpAttendance = rsvpAttendance;
    }


}
