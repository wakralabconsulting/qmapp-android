package com.qatarmuseums.qatarmuseumsapp.education;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Events {
    @SerializedName("item_id")
    private String eid;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String title;
    @SerializedName("Introduction_Text")
    private String shortDescription;
    @SerializedName("main_description")
    private String longDescription;
    @SerializedName("Register")
    private String registration;
    @SerializedName("start_Date")
    private ArrayList<String> startTime;
    @SerializedName("End_Date")
    private ArrayList<String> endTime;
    @SerializedName("Programme_type")
    private String programType;
    @SerializedName("date")
    private String date;
    @SerializedName("filter")
    private String filter;
    @SerializedName("location")
    private String location;
    @SerializedName("max_group_size")
    private String maxGroupSize;
    @SerializedName("category")
    private String category;
    @SerializedName("field_eduprog_repeat_field_date")
    private ArrayList<String> field;
    @SerializedName("Age_group")
    private ArrayList<String> ageGroup;
    @SerializedName("Associated_topics")
    private ArrayList<String> associatedTopics;
    @SerializedName("Museum_Department")
    private String museumDepartment;

    public Events(String eid, String filter, String title, String shortDescription,
                  String longDescription, String location, String institution,
                  ArrayList<String> startTime, ArrayList<String> endTime,
                  String maxGroupSize, String programType,
                  String category, String registration, String date,
                  ArrayList<String> fieldVal, ArrayList<String> ageGroup,
                  ArrayList<String> topics, String museum) {
        this.eid = eid;
        this.filter = filter;
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.location = location;
        this.institution = institution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxGroupSize = maxGroupSize;
        this.programType = programType;
        this.category = category;
        this.registration = registration;
        this.date = date;
        this.field = fieldVal;
        this.ageGroup = ageGroup;
        this.associatedTopics = topics;
        this.museumDepartment = museum;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public ArrayList<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(ArrayList<String> startTime) {
        this.startTime = startTime;
    }

    public ArrayList<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(ArrayList<String> endTime) {
        this.endTime = endTime;
    }

    public String getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(String maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getField() {
        return field;
    }

    public void setField(ArrayList<String> field) {
        this.field = field;
    }

    public ArrayList<String> getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(ArrayList<String> ageGroup) {
        this.ageGroup = ageGroup;
    }

    public ArrayList<String> getAssociatedTopics() {
        return associatedTopics;
    }

    public String getMuseumDepartment() {
        return museumDepartment;
    }

}
