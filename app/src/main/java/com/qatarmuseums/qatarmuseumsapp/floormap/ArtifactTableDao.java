package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ArtifactTableDao {
    @Query("SELECT * FROM artifactTableEnglish")
    List<ArtifactTableEnglish> getAllDataFromArtifactEnglishTable();

    @Query("SELECT * FROM artifactTableArabic")
    List<ArtifactTableArabic> getAllDataFromArtifactArabicTable();

    @Query("SELECT * FROM artifactTableEnglish WHERE tourGuideId = :tourId")
    List<ArtifactTableEnglish> getDataFromArtifactEnglishTable(String tourId);

    @Query("SELECT * FROM artifactTableArabic WHERE tourGuideId = :tourId")
    List<ArtifactTableArabic> getDataFromArtifactArabicTable(String tourId);


    @Query("SELECT COUNT(nid) FROM artifactTableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(nid) FROM artifactTableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(nid) FROM artifactTableEnglish WHERE nid = :nidFromAPI")
    int checkNidExistEnglish(int nidFromAPI);

    @Query("SELECT COUNT(nid) FROM artifactTableArabic WHERE nid = :nidFromAPI")
    int checkNidExistArabic(int nidFromAPI);

    @Query("UPDATE artifactTableEnglish SET title = :title," +
            "accessionNumber = :accessionNumber, tourGuideId = :tourGuideId, mainTitle = :mainTitle, " +
            "image = :image, artifactPosition = :artifactPosition, audioFile = :audioFile, " +
            "audioDescription = :audioDescription, curatorialDescription = :curatorialDescription," +
            " images = :images, floorLevel = :floorLevel, " +
            "galleryNumber = :galleryNumber, objectHistory = :objectHistory, production = :production, " +
            "productionDates = :productionDates, periodStyle = :periodStyle, artistCreatorAuthor = :artistCreatorAuthor, " +
            "techniqueMaterials = :techniqueMaterials, artifactNumber = :artifactNumber, " +
            "dimensions = :dimensions, sortId = :sortId, thumbImage = :thumbImage  WHERE nid=:nid")
    void updateArtifactEnglish(String nid, String title, String accessionNumber, String tourGuideId,
                               String mainTitle, String image, String artifactPosition,
                               String audioFile, String audioDescription, String curatorialDescription,
                               String images, String floorLevel, String galleryNumber, String objectHistory,
                               String production, String productionDates, String periodStyle,
                               String artistCreatorAuthor, String techniqueMaterials,
                               String artifactNumber, String dimensions, String sortId, String thumbImage);

    @Query("UPDATE artifactTableArabic SET title = :title," +
            "accessionNumber = :accessionNumber, tourGuideId = :tourGuideId, mainTitle = :mainTitle, " +
            "image = :image, artifactPosition = :artifactPosition, audioFile = :audioFile, " +
            "audioDescription = :audioDescription, curatorialDescription = :curatorialDescription," +
            " images = :images,  floorLevel = :floorLevel, " +
            "galleryNumber = :galleryNumber, objectHistory = :objectHistory, production = :production, " +
            "productionDates = :productionDates, periodStyle = :periodStyle, artistCreatorAuthor = :artistCreatorAuthor, " +
            "techniqueMaterials = :techniqueMaterials, artifactNumber = :artifactNumber, " +
            "dimensions = :dimensions, sortId = :sortId, thumbImage = :thumbImage WHERE nid=:nid")
    void updateArtifactArabic(String nid, String title, String accessionNumber, String tourGuideId,
                              String mainTitle, String image, String artifactPosition,
                              String audioFile, String audioDescription, String curatorialDescription,
                              String images, String floorLevel, String galleryNumber, String objectHistory,
                              String production, String productionDates, String periodStyle,
                              String artistCreatorAuthor, String techniqueMaterials,
                              String artifactNumber, String dimensions, String sortId, String thumbImage);


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(ArtifactTableEnglish artifactTableEnglish);

    @Insert
    void insertArabicTable(ArtifactTableArabic artifactTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(ArtifactTableEnglish artifactTableEnglish);

    @Update
    void updateArabicTable(ArtifactTableArabic artifactTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(ArtifactTableEnglish artifactTableEnglish);

    @Delete
    void deleteArabicTable(ArtifactTableArabic artifactTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(ArtifactTableEnglish... artifactTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(ArtifactTableEnglish... artifactTableEnglishes);      // Note... is varargs, here note is an array


}
