package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ArtifactTableDao {
    @Query("SELECT * FROM artifactTable WHERE language = :language")
    List<ArtifactTable> getAllDataFromArtifactTable(String language);

    @Query("SELECT * FROM artifactTable WHERE tourGuideId = :tourId AND language = :language")
    List<ArtifactTable> getDataFromArtifactTableWithId(String tourId, String language);

    @Query("SELECT COUNT(nid) FROM artifactTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(nid) FROM artifactTable WHERE nid = :nidFromAPI AND language = :language")
    int checkNidExist(int nidFromAPI, String language);

    @Query("UPDATE artifactTable SET title = :title," +
            "accessionNumber = :accessionNumber, tourGuideId = :tourGuideId, mainTitle = :mainTitle, " +
            "image = :image, artifactPosition = :artifactPosition, audioFile = :audioFile, " +
            "audioDescription = :audioDescription, curatorialDescription = :curatorialDescription," +
            " images = :images, floorLevel = :floorLevel, " +
            "galleryNumber = :galleryNumber, objectHistory = :objectHistory, production = :production, " +
            "productionDates = :productionDates, periodStyle = :periodStyle, artistCreatorAuthor = :artistCreatorAuthor, " +
            "techniqueMaterials = :techniqueMaterials, artifactNumber = :artifactNumber, " +
            "dimensions = :dimensions, sortId = :sortId, thumbImage = :thumbImage  WHERE nid = :nid AND language = :language")
    void updateArtifact(String nid, String title, String accessionNumber, String tourGuideId,
                        String mainTitle, String image, String artifactPosition,
                        String audioFile, String audioDescription, String curatorialDescription,
                        String images, String floorLevel, String galleryNumber, String objectHistory,
                        String production, String productionDates, String periodStyle,
                        String artistCreatorAuthor, String techniqueMaterials,
                        String artifactNumber, String dimensions, String sortId, String thumbImage,
                        String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertData(ArtifactTable artifactTable);

}
