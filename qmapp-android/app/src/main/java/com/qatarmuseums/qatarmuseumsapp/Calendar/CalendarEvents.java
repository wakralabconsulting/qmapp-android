package com.qatarmuseums.qatarmuseumsapp.Calendar;

/**
 * Created by MoongedePC on 23-Jul-18.
 */

public class CalendarEvents {

    String eventTitle,eventSubtitle,eventTimings,eventDetails;

    public CalendarEvents(String eventTitle, String eventSubtitle, String eventTimings, String eventDetails) {
        this.eventTitle = eventTitle;
        this.eventSubtitle = eventSubtitle;
        this.eventTimings = eventTimings;
        this.eventDetails = eventDetails;
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

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }
}
