package com.qatarmuseums.qatarmuseumsapp.educationactivity;

/**
 * Created by MoongedePC on 26-Jul-18.
 */

public class EducationEvents {
    String eventTitle,eventSubtitle,eventTimings,eventMaxNumber;

    public EducationEvents(String eventTitle, String eventSubtitle, String eventTimings, String eventMaxNumber) {
        this.eventTitle = eventTitle;
        this.eventSubtitle = eventSubtitle;
        this.eventTimings = eventTimings;
        this.eventMaxNumber = eventMaxNumber;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventSubtitle() {
        return eventSubtitle;
    }

    public void setEventSubtitle(String eventSubtitle) {
        this.eventSubtitle = eventSubtitle;
    }

    public String getEventTimings() {
        return eventTimings;
    }

    public void setEventTimings(String eventTimings) {
        this.eventTimings = eventTimings;
    }

    public String getEventMaxNumber() {
        return eventMaxNumber;
    }

    public void setEventMaxNumber(String eventMaxNumber) {
        this.eventMaxNumber = eventMaxNumber;
    }
}
