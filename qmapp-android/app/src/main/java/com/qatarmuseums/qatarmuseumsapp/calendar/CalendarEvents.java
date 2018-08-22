package com.qatarmuseums.qatarmuseumsapp.calendar;

import com.google.gson.annotations.SerializedName;

public class CalendarEvents {
    @SerializedName("eid")
    private String calanderId;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String eventTitle;
    @SerializedName("short_desc")
    private String eventTimings;
    @SerializedName("long_desc")
    private String eventDetails;
    @SerializedName("registration")
    private Boolean registration;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("age_group")
    private String ageGroup;
    @SerializedName("program_type")
    private String programType;
    @SerializedName("date")
    private Long eventDate;

    public CalendarEvents(String eventTitle, String eventSubtitle, String eventTimings,
                          String eventDetails, String startTime, String endTime, Boolean registration, Long eventDate) {
        this.institution = eventTitle;
        this.eventTitle = eventSubtitle;
        this.eventTimings = eventTimings;
        this.eventDetails = eventDetails;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registration = registration;
        this.eventDate = eventDate;
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

    public String getCalanderId() {
        return calanderId;
    }

    public void setCalanderId(String calanderId) {
        this.calanderId = calanderId;
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

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }
}
