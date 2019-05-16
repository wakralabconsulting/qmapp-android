package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "artifactTable")
public class ArtifactTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
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


    public ArtifactTable(long nid, String title, String accessionNumber, String tourGuideId,
                         String mainTitle, String image, String artifactPosition, String audioFile,
                         String audioDescription, String curatorialDescription, String images,
                         String floorLevel, String galleryNumber, String objectHistory,
                         String production, String productionDates, String periodStyle,
                         String artistCreatorAuthor, String techniqueMaterials, String artifactNumber,
                         String dimensions, String sortId, String thumbImage, String language) {
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
        this.language = language;
    }

    public long getNid() {
        return nid;
    }

    public void setNid(long nid) {
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

    public String getTourGuideId() {
        return tourGuideId;
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

    public String getAudioFile() {
        return audioFile;
    }

    public String getAudioDescription() {
        return audioDescription;
    }

    public String getCuratorialDescription() {
        return curatorialDescription;
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

    public String getGalleryNumber() {
        return galleryNumber;
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

    public String getPeriodStyle() {
        return periodStyle;
    }

    public String getArtistCreatorAuthor() {
        return artistCreatorAuthor;
    }

    public String getTechniqueMaterials() {
        return techniqueMaterials;
    }

    public String getArtifactNumber() {
        return artifactNumber;
    }

    String getDiam() {
        return diam;
    }

    void setDiam(String diam) {
        this.diam = diam;
    }

    public String getDimensions() {
        return dimensions;
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

    public String getLanguage() {
        return language;
    }
}
