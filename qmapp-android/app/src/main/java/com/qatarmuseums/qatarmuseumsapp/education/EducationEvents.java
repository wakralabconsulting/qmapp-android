package com.qatarmuseums.qatarmuseumsapp.education;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EducationEvents {

    @SerializedName("item_id")
    private String eid;
    @SerializedName("institution")
    private String institution;
    @SerializedName("title")
    private String title;
    @SerializedName("Introduction_Text")
    private String short_desc;
    @SerializedName("main_description")
    private String long_desc;
    @SerializedName("Register")
    private String registration;
    @SerializedName("start_Date")
    private ArrayList<String> start_time;
    @SerializedName("End_Date")
    private ArrayList<String> end_time;
    @SerializedName("age_group")
    private String age_group;
    @SerializedName("Programme_type")
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
    @SerializedName("field_eduprog_repeat_field_date")
    private ArrayList<String> field;
    @SerializedName("Age_group")
    private ArrayList<String> age;
    @SerializedName("Associated_topics")
    private ArrayList<String> associatedTopics;
    @SerializedName("Museum_Department")
    private String museumDepartment;



    public EducationEvents(String eid, String filter, String title, String short_desc, String long_desc,
                           String location, String institution, ArrayList<String> start_time, ArrayList<String> end_time,
                           String max_group_size, String age_group,
                           String program_type, String category,
                           String registration, String date, ArrayList<String> fieldVal,
                           ArrayList<String> age, ArrayList<String> topics,String museum) {
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
        this.field = fieldVal;
        this.age=age;
        this.associatedTopics=topics;
        this.museumDepartment=museum;

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

    public ArrayList<String> getStart_time() {
        return start_time;
    }

    public void setStart_time(ArrayList<String> start_time) {
        this.start_time = start_time;
    }

    public ArrayList<String> getEnd_time() {
        return end_time;
    }

    public void setEnd_time(ArrayList<String> end_time) {
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
