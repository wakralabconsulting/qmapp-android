package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "traveldetailsarabic")
public class TravelDetailsTableArabic {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String content_ID;
    @ColumnInfo()
    private String travel_name;
    @ColumnInfo()
    private String travel_image;
    @ColumnInfo()
    private String travel_description;
    @ColumnInfo()
    private String travel_email;
    @ColumnInfo()
    private String contact_number;
    @ColumnInfo()
    private String promotional_code;
    @ColumnInfo()
    private String claim_offer;

    public TravelDetailsTableArabic(@NonNull String content_ID,
                                    String travel_name,
                                    String travel_image,
                                    String travel_description,
                                    String travel_email,
                                    String contact_number,
                                    String promotional_code, String claim_offer) {
        this.content_ID = content_ID;
        this.travel_name = travel_name;
        this.travel_image = travel_image;
        this.travel_description = travel_description;
        this.travel_email = travel_email;
        this.contact_number = contact_number;
        this.promotional_code = promotional_code;
        this.claim_offer = claim_offer;
    }

    @NonNull
    public String getContent_ID() {
        return content_ID;
    }

    public void setContent_ID(@NonNull String content_ID) {
        this.content_ID = content_ID;
    }

    public String getTravel_name() {
        return travel_name;
    }

    public void setTravel_name(String travel_name) {
        this.travel_name = travel_name;
    }

    public String getTravel_image() {
        return travel_image;
    }

    public void setTravel_image(String travel_image) {
        this.travel_image = travel_image;
    }

    public String getTravel_description() {
        return travel_description;
    }

    public void setTravel_description(String travel_description) {
        this.travel_description = travel_description;
    }

    public String getTravel_email() {
        return travel_email;
    }

    public void setTravel_email(String travel_email) {
        this.travel_email = travel_email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPromotional_code() {
        return promotional_code;
    }

    public void setPromotional_code(String promotional_code) {
        this.promotional_code = promotional_code;
    }

    public String getClaim_offer() {
        return claim_offer;
    }

    public void setClaim_offer(String claim_offer) {
        this.claim_offer = claim_offer;
    }
}
