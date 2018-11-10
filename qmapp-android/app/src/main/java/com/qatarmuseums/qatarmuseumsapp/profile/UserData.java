package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserData extends ProfileDetails{
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



    public class Model {
        @SerializedName("und")
        private List<Und> und;

        public List<Und> getUnd() {
            return und;
        }

        public void setUnd(List<Und> und) {
            this.und = und;
        }
    }

    public class Und {
        @SerializedName(value = "value", alternate = {"iso2"})
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
