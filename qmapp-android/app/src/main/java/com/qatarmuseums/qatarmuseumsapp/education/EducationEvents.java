package com.qatarmuseums.qatarmuseumsapp.education;

import com.google.gson.annotations.SerializedName;

public class EducationEvents {

    @SerializedName("eid")
    private String eid;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String title;
    @SerializedName("short_desc")
    private String short_desc;
    @SerializedName("long_desc")
    private String long_desc;
    @SerializedName("registration")
    private String registration;
    @SerializedName("start_time")
    private String start_time;
    @SerializedName("end_time")
    private String end_time;
    @SerializedName("age_group")
    private String age_group;
    @SerializedName("program_type")
    private String program_type;
    @SerializedName("date")
    private String date;
    @SerializedName("filter")
    private String filter;
    @SerializedName("location")
    private String location;
    @SerializedName("max_group_size")
    private String max_group_size;
    @SerializedName("category")
    private String category;


    public EducationEvents(String eid, String filter, String title, String short_desc, String long_desc,
                           String location, String institution, String start_time, String end_time,
                           String max_group_size, String age_group,
                           String program_type, String category, String registration, String date) {
        this.eid = eid;
        this.filter = filter;
        this.title = title;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.location = location;
        this.institution = institution;
        this.start_time = start_time;
        this.end_time = end_time;
        this.max_group_size = max_group_size;
        this.age_group = age_group;
        this.program_type = program_type;
        this.category = category;
        this.registration = registration;
        this.date = date;
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

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getMax_group_size() {
        return max_group_size;
    }

    public void setMax_group_size(String max_group_size) {
        this.max_group_size = max_group_size;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    public String getProgram_type() {
        return program_type;
    }

    public void setProgram_type(String program_type) {
        this.program_type = program_type;
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
}
