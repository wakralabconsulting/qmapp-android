package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ObjectPreviewModel {
    @SerializedName("Title")
    private String title;
    @SerializedName("Accession_Number")
    private String accessionNumber;
    @SerializedName("nid")
    private String nId;
    @SerializedName("Curatorial_Description")
    private String description;
    @SerializedName("Diam")
    private String diam;
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
    @SerializedName("floor_level")
    private String floorLevel;
    @SerializedName("galley_Number")
    private String galleyNumber;
    @SerializedName("Artist/Creator/Author")
    private String artistCreatorAuthor;
    @SerializedName("Period/Style")
    private String periodStyle;
    @SerializedName("Technique_&_Materials")
    private String techniqueandMaterials;
    @SerializedName("artifact_number")
    private String artifactNumber;
    @SerializedName("artifact_position")
    private String artifactPosition;
    @SerializedName("audio_descriptif")
    private String audioDescription;
    @SerializedName("audio_file")
    private String audioFile;
    @SerializedName("images")
    private ArrayList<String> images;

    public ObjectPreviewModel(String title, String accessionNumber, String nId, String description,
                              String diam, String dimensions, String mainTitle,
                              String objectENGSummary, String objectHistory,
                              String production, String productionDates, String image,
                              String tourGuideId, String floorLevel, String galleyNumber,
                              String artistCreatorAuthor, String periodStyle,
                              String techniqueandMaterials) {
        this.title = title;
        this.accessionNumber = accessionNumber;
        this.nId = nId;
        this.description = description;
        this.diam = diam;
        this.dimensions = dimensions;
        this.mainTitle = mainTitle;
        this.objectENGSummary = objectENGSummary;
        this.objectHistory = objectHistory;
        this.production = production;
        this.productionDates = productionDates;
        this.image = image;
        this.tourGuideId = tourGuideId;
        this.floorLevel = floorLevel;
        this.galleyNumber = galleyNumber;
        this.artistCreatorAuthor = artistCreatorAuthor;
        this.periodStyle = periodStyle;
        this.techniqueandMaterials = techniqueandMaterials;
    }

    public ObjectPreviewModel(String mainTitle, String accessionNumber, String production,
                              String productionDates, String periodStyle,
                              String techniqueandMaterials,
                              String dimensions) {
        this.accessionNumber = accessionNumber;
        this.dimensions = dimensions;
        this.mainTitle = mainTitle;
        this.production = production;
        this.productionDates = productionDates;
        this.periodStyle = periodStyle;
        this.techniqueandMaterials = techniqueandMaterials;
    }


    public String getTitle() {
        return title;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public String getnId() {
        return nId;
    }

    public String getDescription() {
        return description;
    }

    public String getDiam() {
        return diam;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getObjectENGSummary() {
        return objectENGSummary;
    }

    public String getObjectHistory() {
        return objectHistory;
    }

    public String getProduction() {
        return production;
    }

    public String getProductionDates() {
        return productionDates;
    }

    public String getImage() {
        return image;
    }

    public String getTourGuideId() {
        return tourGuideId;
    }

    public String getFloorLevel() {
        return floorLevel;
    }

    public String getGalleyNumber() {
        return galleyNumber;
    }

    public String getArtistCreatorAuthor() {
        return artistCreatorAuthor;
    }

    public String getPeriodStyle() {
        return periodStyle;
    }

    public String getTechniqueandMaterials() {
        return techniqueandMaterials;
    }

    public String getArtifactNumber() {
        return artifactNumber;
    }

    public String getArtifactPosition() {
        return artifactPosition;
    }

    public String getAudioDescription() {
        return audioDescription;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public ArrayList<String> getImages() {
        return images;
    }
}
