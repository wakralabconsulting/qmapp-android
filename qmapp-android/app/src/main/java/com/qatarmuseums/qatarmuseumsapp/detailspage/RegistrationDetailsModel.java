package com.qatarmuseums.qatarmuseumsapp.detailspage;

import com.google.gson.annotations.SerializedName;
import com.qatarmuseums.qatarmuseumsapp.profile.Model;

public class RegistrationDetailsModel {
    @SerializedName("registration_id")
    private String registrationID;
    @SerializedName("type")
    private String registrationType;
    @SerializedName("entity_id")
    private String registrationEntityId;
    @SerializedName("entity_type")
    private String registrationEntityType;
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

    public RegistrationDetailsModel(String registrationID, String registrationType, String registrationEntityId,
                                    String registrationEntityType, String registrationUserUid,
                                    String registrationCount, String registrationAuthorUid, String registrationState, String registrationCreated,
                                    String registrationUpdated, Model registrationConfirmAttendance, Model registrationNumberOfAttendees,
                                    Model registrationFirstName, Model registrationLastName,
                                    Model registrationMembershipNumber, Model registrationRegDate) {
        this.registrationID = registrationID;
        this.registrationType = registrationType;
        this.registrationEntityId = registrationEntityId;
        this.registrationEntityType = registrationEntityType;
        this.registrationUserUid = registrationUserUid;
        this.registrationCount = registrationCount;
        this.registrationAuthorUid = registrationAuthorUid;
        this.registrationState = registrationState;
        this.registrationCreated = registrationCreated;
        this.registrationUpdated = registrationUpdated;
        this.registrationConfirmAttendance = registrationConfirmAttendance;
        this.registrationNumberOfAttendees = registrationNumberOfAttendees;
        this.registrationFirstName = registrationFirstName;
        this.registrationLastName = registrationLastName;
        this.registrationMembershipNumber = registrationMembershipNumber;
        this.registrationRegDate = registrationRegDate;
    }

    RegistrationDetailsModel(String registrationType, String registrationEntityId, String registrationEntityType,
                             String registrationUserUid, String registrationCount,
                             String registrationAuthorUid, String registrationState, String registrationCreated,
                             String registrationUpdated, Model registrationConfirmAttendance, Model registrationNumberOfAttendees,
                             Model registrationFirstName, Model registrationLastName, Model registrationMembershipNumber, Model registrationRegDate) {
        this.registrationType = registrationType;
        this.registrationEntityId = registrationEntityId;
        this.registrationEntityType = registrationEntityType;
        this.registrationUserUid = registrationUserUid;
        this.registrationCount = registrationCount;
        this.registrationAuthorUid = registrationAuthorUid;
        this.registrationState = registrationState;
        this.registrationCreated = registrationCreated;
        this.registrationUpdated = registrationUpdated;
        this.registrationConfirmAttendance = registrationConfirmAttendance;
        this.registrationNumberOfAttendees = registrationNumberOfAttendees;
        this.registrationFirstName = registrationFirstName;
        this.registrationLastName = registrationLastName;
        this.registrationMembershipNumber = registrationMembershipNumber;
        this.registrationRegDate = registrationRegDate;
    }

    String getRegistrationID() {
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

    public String getRegistrationEntityId() {
        return registrationEntityId;
    }

    public void setRegistrationEntityId(String registrationEntityId) {
        this.registrationEntityId = registrationEntityId;
    }

    public String getRegistrationEntityType() {
        return registrationEntityType;
    }

    public void setRegistrationEntityType(String registrationEntityType) {
        this.registrationEntityType = registrationEntityType;
    }

    public String getRegistrationUserUid() {
        return registrationUserUid;
    }

    public void setRegistrationUserUid(String registrationUserUid) {
        this.registrationUserUid = registrationUserUid;
    }

    String getRegistrationCount() {
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

    void setRegistrationState(String registrationState) {
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
