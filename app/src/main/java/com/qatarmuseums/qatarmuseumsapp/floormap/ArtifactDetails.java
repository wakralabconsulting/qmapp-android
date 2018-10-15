package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ArtifactDetails implements Parcelable{
    @SerializedName("Title")
    private String title;
    @SerializedName("Accession_Number")
    private String accessionNumber;
    @SerializedName("nid")
    private String nid;
    @SerializedName("Curatorial_Description")
    private String curatorialDescription;
    @SerializedName("Dimensions")
    private String dimensions;
    @SerializedName("Main_Title")
    private String mainTitle;
    @SerializedName("Object_ENG_Summary")
    private String objectENGSummary;
    @SerializedName("Object_History")
    private String objectHistory;
    @SerializedName("Production")
    private String production;
    @SerializedName("Production_dates")
    private String productionDates;
    @SerializedName("Image")
    private String image;
    @SerializedName("tour_guide_id")
    private String tourGuideId;
    @SerializedName("artifact_number")
    private String artifactNumber;
    @SerializedName("artifact_position")
    private String artifactPosition;
    @SerializedName("audio_descriptif")
    private String audioDescriptif;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("audio_file")
    private String audioFile;
    @SerializedName("floor_level")
    private String floorLevel;
    @SerializedName("gallery_number")
    private String galleryNumber;
    @SerializedName("sort_id")
    private String sortId;
    @SerializedName("Artist/Creator/Author")
    private String ArtistCreatorAuthor;
    @SerializedName("Technique_&_Materials")
    private String TechniqueMaterials;
    @SerializedName("Period/Style")
    private String PeriodStyle;

    protected ArtifactDetails(Parcel in) {
        title = in.readString();
        accessionNumber = in.readString();
        nid = in.readString();
        curatorialDescription = in.readString();
        dimensions = in.readString();
        mainTitle = in.readString();
        objectENGSummary = in.readString();
        objectHistory = in.readString();
        production = in.readString();
        productionDates = in.readString();
        image = in.readString();
        tourGuideId = in.readString();
        artifactNumber = in.readString();
        artifactPosition = in.readString();
        audioDescriptif = in.readString();
        images = in.createStringArrayList();
        audioFile = in.readString();
        floorLevel = in.readString();
        galleryNumber = in.readString();
        sortId = in.readString();
        ArtistCreatorAuthor = in.readString();
        TechniqueMaterials = in.readString();
        PeriodStyle = in.readString();
    }

    public static final Creator<ArtifactDetails> CREATOR = new Creator<ArtifactDetails>() {
        @Override
        public ArtifactDetails createFromParcel(Parcel in) {
            return new ArtifactDetails(in);
        }

        @Override
        public ArtifactDetails[] newArray(int size) {
            return new ArtifactDetails[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getCuratorialDescription() {
        return curatorialDescription;
    }

    public void setCuratorialDescription(String curatorialDescription) {
        this.curatorialDescription = curatorialDescription;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getObjectENGSummary() {
        return objectENGSummary;
    }

    public void setObjectENGSummary(String objectENGSummary) {
        this.objectENGSummary = objectENGSummary;
    }

    public String getObjectHistory() {
        return objectHistory;
    }

    public void setObjectHistory(String objectHistory) {
        this.objectHistory = objectHistory;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getProductionDates() {
        return productionDates;
    }

    public void setProductionDates(String productionDates) {
        this.productionDates = productionDates;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTourGuideId() {
        return tourGuideId;
    }

    public void setTourGuideId(String tourGuideId) {
        this.tourGuideId = tourGuideId;
    }

    public String getArtifactNumber() {
        return artifactNumber;
    }

    public void setArtifactNumber(String artifactNumber) {
        this.artifactNumber = artifactNumber;
    }

    public String getArtifactPosition() {
        return artifactPosition;
    }

    public void setArtifactPosition(String artifactPosition) {
        this.artifactPosition = artifactPosition;
    }

    public String getAudioDescriptif() {
        return audioDescriptif;
    }

    public void setAudioDescriptif(String audioDescriptif) {
        this.audioDescriptif = audioDescriptif;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(String floorLevel) {
        this.floorLevel = floorLevel;
    }

    public String getGalleryNumber() {
        return galleryNumber;
    }

    public void setGalleryNumber(String galleryNumber) {
        this.galleryNumber = galleryNumber;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getArtistCreatorAuthor() {
        return ArtistCreatorAuthor;
    }

    public void setArtistCreatorAuthor(String artistCreatorAuthor) {
        ArtistCreatorAuthor = artistCreatorAuthor;
    }

    public String getTechniqueMaterials() {
        return TechniqueMaterials;
    }

    public void setTechniqueMaterials(String techniqueMaterials) {
        TechniqueMaterials = techniqueMaterials;
    }

    public String getPeriodStyle() {
        return PeriodStyle;
    }

    public void setPeriodStyle(String periodStyle) {
        PeriodStyle = periodStyle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(accessionNumber);
        dest.writeString(nid);
        dest.writeString(curatorialDescription);
        dest.writeString(dimensions);
        dest.writeString(mainTitle);
        dest.writeString(objectENGSummary);
        dest.writeString(objectHistory);
        dest.writeString(production);
        dest.writeString(productionDates);
        dest.writeString(image);
        dest.writeString(tourGuideId);
        dest.writeString(artifactNumber);
        dest.writeString(artifactPosition);
        dest.writeString(audioDescriptif);
        dest.writeStringList(images);
        dest.writeString(audioFile);
        dest.writeString(floorLevel);
        dest.writeString(galleryNumber);
        dest.writeString(sortId);
        dest.writeString(ArtistCreatorAuthor);
        dest.writeString(TechniqueMaterials);
        dest.writeString(PeriodStyle);
    }
}
