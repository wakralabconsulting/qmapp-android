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

    CalendarEvents(long eid, String institution, String title, String shortDescription,
                   String eventDetails, ArrayList<String> startTime,
                   ArrayList<String> endTime, boolean registration,
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

    String getEventTitle() {
        return eventTitle;
    }

    void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    String getEventTimings() {
        return eventTimings;
    }

    void setEventTimings(String eventTimings) {
        this.eventTimings = eventTimings;
    }

    String getEventDetails() {
        return eventDetails;
    }

    void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    Boolean getRegistration() {
        return registration;
    }

    long getEventId() {
        return eventId;
    }

    ArrayList<String> getStartTime() {
        return startTime;
    }

    void setStartTime(ArrayList<String> startTime) {
        this.startTime = startTime;
    }

    ArrayList<String> getEndTime() {
        return endTime;
    }

    void setEndTime(ArrayList<String> endTime) {
        this.endTime = endTime;
    }

    String getAgeGroup() {
        return ageGroup;
    }

    String getProgramType() {
        return programType;
    }

    void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    String getEventLocation() {
        return eventLocation;
    }

    long getMaxGroupSize() {
        return maxGroupSize;
    }

    String getEventCategory() {
        return eventCategory;
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

    String getMuseumDepartment() {
        return museumDepartment;
    }

}
