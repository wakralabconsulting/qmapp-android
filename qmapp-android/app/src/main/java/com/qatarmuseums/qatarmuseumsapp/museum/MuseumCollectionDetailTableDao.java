package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface MuseumCollectionDetailTableDao {
    @Query("SELECT * FROM museumcollectiondetailtableEnglish")
    List<MuseumCollectionDetailTableEnglish> getAllDataFromMuseumDetailEnglishTable();

    @Query("SELECT * FROM museumcollectiondetailtableArabic")
    List<MuseumCollectionDetailTableArabic> getAllDataFromMuseumDetailArabicTable();

    @Query("SELECT * FROM museumcollectiondetailtableEnglish WHERE detail_title = :detailTitleFromApi")
    List<MuseumCollectionListTableEnglish> getDataFromEnglishTableWithTitle(String detailTitleFromApi);

    @Query("SELECT * FROM museumcollectiondetailtableArabic WHERE detail_title = :detailTitleFromApi")
    List<MuseumCollectionListTableArabic> getDataFromArabicTableWithTitle(String detailTitleFromApi);

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableEnglish WHERE detail_title = :titleFromAPI")
    int checkTitleExistEnglish(String titleFromAPI);

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableEnglish WHERE detail_title = :titleFromAPI")
    int checkTitleExistArabic(String titleFromAPI);

    @Query("UPDATE museumcollectiondetailtableEnglish SET detail_about = :detailAboutFromApi," +
            "detail_image1 = :image1FromApi,detail_image2=:image2FromApi," +
            " detail_description1=:description1FromApi, detail_description2=:description2FromApi , detail_description3=:description3FromApi " +
            "WHERE detail_title=:titleFromApi")
    void updateMuseumListTableEnglish(String detailAboutFromApi, String image1FromApi, String image2FromApi,
                                      String description1FromApi,String description2FromApi,
                                      String description3FromApi,String titleFromApi);

    @Query("UPDATE museumcollectiondetailtableArabic SET detail_about = :detailAboutFromApi," +
            "detail_image1 = :image1FromApi,detail_image2=:image2FromApi," +
            " detail_description1=:description1FromApi, detail_description2=:description2FromApi , detail_description3=:description3FromApi " +
            "WHERE detail_title=:titleFromApi")
    void updateMuseumListTableArabic(String detailAboutFromApi, String image1FromApi, String image2FromApi,
                                      String description1FromApi,String description2FromApi,
                                      String description3FromApi,String titleFromApi);


    @Insert
    void insertEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);

    @Insert
    void insertArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Update
    void updateEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);

    @Update
    void updateArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Delete
    void deleteEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);

    @Delete
    void deleteArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Delete
    void deleteEnglishTable(MuseumCollectionListTableEnglish... museumCollectionListTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(MuseumCollectionListTableArabic... museumCollectionListTableArabics);      // Note... is varargs, here note is an array


}
