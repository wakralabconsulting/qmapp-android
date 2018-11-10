package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "artifactTableArabic")
public class ArtifactTableArabic {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long nid;
    @ColumnInfo()
    private String title;
    @ColumnInfo()
    private String accessionNumber;
    @ColumnInfo()
    private String tourGuideId;
    @ColumnInfo()
    private String mainTitle;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private String artifactPosition;
    @ColumnInfo()
    private String audioFile;
    @ColumnInfo()
    private String audioDescription;
    @ColumnInfo()
    private String curatorialDescription;
    @ColumnInfo()
    private String images;
    @ColumnInfo()
    private String floorLevel;
    @ColumnInfo()
    private String galleryNumber;
    @ColumnInfo()
    private String objectHistory;
    @ColumnInfo()
    private String production;
    @ColumnInfo()
    private String productionDates;
    @ColumnInfo()
    private String periodStyle;
    @ColumnInfo()
    private String artistCreatorAuthor;
    @ColumnInfo()
    private String techniqueMaterials;
    @ColumnInfo()
    private String artifactNumber;
    @ColumnInfo()
    private String diam;
    @ColumnInfo()
    private String dimensions;
    @ColumnInfo()
    private String sortId;
    @ColumnInfo()
    private String thumbImage;


    public ArtifactTableArabic(long nid, String title, String accessionNumber, String tourGuideId,
                               String mainTitle, String image, String artifactPosition, String audioFile,
                               String audioDescription, String curatorialDescription, String images,
                               String floorLevel, String galleryNumber, String objectHistory,
                               String production, String productionDates, String periodStyle,
                               String artistCreatorAuthor, String techniqueMaterials, String artifactNumber,
                               String dimensions, String sortId, String thumbImage) {
        this.nid = nid;
        this.title = title;
        this.accessionNumber = accessionNumber;
        this.tourGuideId = tourGuideId;
        this.mainTitle = mainTitle;
        this.image = image;
        this.artifactPosition = artifactPosition;
        this.audioFile = audioFile;
        this.audioDescription = audioDescription;
        this.curatorialDescription = curatorialDescription;
        this.images = images;
        this.floorLevel = floorLevel;
        this.galleryNumber = galleryNumber;
        this.objectHistory = objectHistory;
        this.production = production;
        this.productionDates = productionDates;
        this.periodStyle = periodStyle;
        this.artistCreatorAuthor = artistCreatorAuthor;
        this.techniqueMaterials = techniqueMaterials;
        this.artifactNumber = artifactNumber;
        this.dimensions = dimensions;
        this.sortId = sortId;
        this.thumbImage = thumbImage;
    }

    @NonNull
    public long getNid() {
        return nid;
    }

    public void setNid(@NonNull long nid) {
        this.nid = nid;
    }

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

    public String getTourGuideId() {
        return tourGuideId;
    }

    public void setTourGuideId(String tourGuideId) {
        this.tourGuideId = tourGuideId;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtifactPosition() {
        return artifactPosition;
    }

    public void setArtifactPosition(String artifactPosition) {
        this.artifactPosition = artifactPosition;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getAudioDescription() {
        return audioDescription;
    }

    public void setAudioDescription(String audioDescription) {
        this.audioDescription = audioDescription;
    }

    public String getCuratorialDescription() {
        return curatorialDescription;
    }

    public void setCuratorialDescription(String curatorialDescription) {
        this.curatorialDescription = curatorialDescription;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getPeriodStyle() {
        return periodStyle;
    }

    public void setPeriodStyle(String periodStyle) {
        this.periodStyle = periodStyle;
    }

    public String getArtistCreatorAuthor() {
        return artistCreatorAuthor;
    }

    public void setArtistCreatorAuthor(String artistCreatorAuthor) {
        this.artistCreatorAuthor = artistCreatorAuthor;
    }

    public String getTechniqueMaterials() {
        return techniqueMaterials;
    }

    public void setTechniqueMaterials(String techniqueMaterials) {
        this.techniqueMaterials = techniqueMaterials;
    }

    public String getArtifactNumber() {
        return artifactNumber;
    }

    public void setArtifactNumber(String artifactNumber) {
        this.artifactNumber = artifactNumber;
    }

    public String getDiam() {
        return diam;
    }

    public void setDiam(String diam) {
        this.diam = diam;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }
}
