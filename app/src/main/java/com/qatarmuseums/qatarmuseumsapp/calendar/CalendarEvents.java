package com.qatarmuseums.qatarmuseumsapp.calendar;

import com.google.gson.annotations.SerializedName;

public class CalendarEvents {
    @SerializedName("eid")
    private long eventId;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String eventTitle;
    @SerializedName("short_desc")
    private String eventTimings;
    @SerializedName("long_desc")
    private String eventDetails;
    @SerializedName("registration")
    private boolean registration;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("age_group")
    private String ageGroup;
    @SerializedName("program_type")
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

    public CalendarEvents(long eid, String institution, String title, String shortDescription, String eventDetails,
                          String startTime, String endTime, boolean registration, long eventDate,
                          String filter, String location, long maxGroupSize, String category,
                          String programType, String ageGroup) {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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

}
