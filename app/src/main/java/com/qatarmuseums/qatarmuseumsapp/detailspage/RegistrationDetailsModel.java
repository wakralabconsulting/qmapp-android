package com.qatarmuseums.qatarmuseumsapp.detailspage;

import com.google.gson.annotations.SerializedName;
import com.qatarmuseums.qatarmuseumsapp.profile.Model;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;

public class RegistrationDetailsModel {
    @SerializedName("registration_id")
    private String registrationID;
    @SerializedName("type")
    private String registrationType;
    @SerializedName("entity_id")
    private String registrationentityId;
    @SerializedName("entity_type")
    private String registrationentityType;
    @SerializedName("anon_mail")
    private String registrationAnon_mail;
    @SerializedName("user_uid")
    private String registrationUserUid;
    @SerializedName("count")
    private String registrationCount;
    @SerializedName("author_uid")
    private String registrationAuthorUid;
    @SerializedName("state")
    private String registrationState;
    @SerializedName("created")
    private String registrationCreated;
    @SerializedName("updated")
    private String registrationUpdated;
    @SerializedName("field_confirm_attendance")
    private Model registrationConfirmAttendance;
    @SerializedName("field_number_of_attendees")
    private Model registrationNumberOfAttendees;
    @SerializedName("field_first_name_")
    private Model registrationFirstName;
    @SerializedName("field_nmoq_last_name")
    private Model registrationLastName;
    @SerializedName("field_membership_number")
    private Model registrationMembershipNumber;
    @SerializedName("field_qma_edu_reg_date")
    private Model registrationRegDate;

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public String getRegistrationentityId() {
        return registrationentityId;
    }

    public void setRegistrationentityId(String registrationentityId) {
        this.registrationentityId = registrationentityId;
    }

    public String getRegistrationentityType() {
        return registrationentityType;
    }

    public void setRegistrationentityType(String registrationentityType) {
        this.registrationentityType = registrationentityType;
    }

    public String getRegistrationAnon_mail() {
        return registrationAnon_mail;
    }

    public void setRegistrationAnon_mail(String registrationAnon_mail) {
        this.registrationAnon_mail = registrationAnon_mail;
    }

    public String getRegistrationUserUid() {
        return registrationUserUid;
    }

    public void setRegistrationUserUid(String registrationUserUid) {
        this.registrationUserUid = registrationUserUid;
    }

    public String getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(String registrationCount) {
        this.registrationCount = registrationCount;
    }

    public String getRegistrationAuthorUid() {
        return registrationAuthorUid;
    }

    public void setRegistrationAuthorUid(String registrationAuthorUid) {
        this.registrationAuthorUid = registrationAuthorUid;
    }

    public String getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }

    public String getRegistrationCreated() {
        return registrationCreated;
    }

    public void setRegistrationCreated(String registrationCreated) {
        this.registrationCreated = registrationCreated;
    }

    public String getRegistrationUpdated() {
        return registrationUpdated;
    }

    public void setRegistrationUpdated(String registrationUpdated) {
        this.registrationUpdated = registrationUpdated;
    }

    public Model getRegistrationConfirmAttendance() {
        return registrationConfirmAttendance;
    }

    public void setRegistrationConfirmAttendance(Model registrationConfirmAttendance) {
        this.registrationConfirmAttendance = registrationConfirmAttendance;
    }

    public Model getRegistrationNumberOfAttendees() {
        return registrationNumberOfAttendees;
    }

    public void setRegistrationNumberOfAttendees(Model registrationNumberOfAttendees) {
        this.registrationNumberOfAttendees = registrationNumberOfAttendees;
    }

    public Model getRegistrationFirstName() {
        return registrationFirstName;
    }

    public void setRegistrationFirstName(Model registrationFirstName) {
        this.registrationFirstName = registrationFirstName;
    }

    public Model getRegistrationLastName() {
        return registrationLastName;
    }

    public void setRegistrationLastName(Model registrationLastName) {
        this.registrationLastName = registrationLastName;
    }

    public Model getRegistrationMembershipNumber() {
        return registrationMembershipNumber;
    }

    public void setRegistrationMembershipNumber(Model registrationMembershipNumber) {
        this.registrationMembershipNumber = registrationMembershipNumber;
    }

    public Model getRegistrationRegDate() {
        return registrationRegDate;
    }

    public void setRegistrationRegDate(Model registrationRegDate) {
        this.registrationRegDate = registrationRegDate;
    }
}
