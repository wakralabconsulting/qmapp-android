package com.qatarmuseums.qatarmuseumsapp.calendar;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CalendarEvents {
    @SerializedName("item_id")
    private long eventId;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String eventTitle;
    @SerializedName("Introduction_Text")
    private String eventTimings;
    @SerializedName("main_description")
    private String eventDetails;
    @SerializedName("Register")
    private boolean registration;
    @SerializedName("start_Date")
    private ArrayList<String> startTime;
    @SerializedName("End_Date")
    private ArrayList<String> endTime;
    @SerializedName("age_group")
    private String ageGroup;
    @SerializedName("Programme_type")
    private String programType;
    @SerializedName("date")
    private long eventDate;
    @SerializedName("filter")
    private String filter;
    @SerializedName("location")
    private String eventLocation;
    @SerializedName("max_group_size")
    private long maxGroupSize;
    @SerializedName("category")
    private String eventCategory;
    @SerializedName("field_eduprog_repeat_field_date")
    private ArrayList<String> field;
    @SerializedName("Age_group")
    private ArrayList<String> age;
    @SerializedName("Associated_topics")
    private ArrayList<String> associatedTopics;
    @SerializedName("Museum_Department")
    private String museumDepartment;

    public CalendarEvents(long eid, String institution, String title, String shortDescription,
                          String eventDetails, ArrayList<String> startTime,
                          ArrayList<String> endTime, boolean registration, long eventDate,
                          String filter, String location, long maxGroupSize, String category,
                          String programType, String ageGroup, ArrayList<String> filedVal,
                          ArrayList<String> age, ArrayList<String> topics, String museum) {
        this.institution = institution;
        this.eventTitle = title;
        this.eventTimings = shortDescription;
        this.eventDetails = eventDetails;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registration = registration;
        this.eventDate = eventDate;
        this.eventId = eid;
        this.filter = filter;
        this.eventLocation = location;
        this.maxGroupSize = maxGroupSize;
        this.eventCategory = category;
        this.programType = programType;
        this.ageGroup = ageGroup;
        this.field = filedVal;
        this.age = age;
        this.associatedTopics = topics;
        this.museumDepartment = museum;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventTimings() {
        return eventTimings;
    }

    public void setEventTimings(String eventTimings) {
        this.eventTimings = eventTimings;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public Boolean getRegistration() {
        return registration;
    }

    public void setRegistration(Boolean registration) {
        this.registration = registration;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
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

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public long getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(long maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public ArrayList<String> getField() {
        return field;
    }

    public void setField(ArrayList<String> field) {
        this.field = field;
    }

    public ArrayList<String> getAge() {
        return age;
    }

    public void setAge(ArrayList<String> age) {
        this.age = age;
    }

    public ArrayList<String> getAssociatedTopics() {
        return associatedTopics;
    }

    public void setAssociatedTopics(ArrayList<String> associatedTopics) {
        this.associatedTopics = associatedTopics;
    }

    public String getMuseumDepartment() {
        return museumDepartment;
    }

    public void setMuseumDepartment(String museumDepartment) {
        this.museumDepartment = museumDepartment;
    }
}
