package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface MuseumCollectionDetailTableDao {
    @Query("SELECT * FROM museumcollectiondetailtableEnglish WHERE category_id = :categoryIdFromAPI")
    List<MuseumCollectionDetailTableEnglish> getAllDataFromMuseumDetailEnglishTable(String categoryIdFromAPI);

    @Query("SELECT * FROM museumcollectiondetailtableArabic WHERE category_id = :categoryIdFromAPI")
    List<MuseumCollectionDetailTableArabic> getAllDataFromMuseumDetailArabicTable(String categoryIdFromAPI);

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableEnglish WHERE detail_title = :titleFromAPI")
    int checkTitleExistEnglish(String titleFromAPI);

    @Query("SELECT COUNT(detail_title) FROM museumcollectiondetailtableArabic WHERE detail_title = :titleFromAPI")
    int checkTitleExistArabic(String titleFromAPI);

    @Query("UPDATE museumcollectiondetailtableEnglish SET category_id = :categoryIdFromApi," +
            "detail_image1 = :image1FromApi," +
            " detail_about=:aboutFromApi WHERE detail_title=:titleFromApi")
    void updateMuseumDetailTableEnglish(String categoryIdFromApi, String image1FromApi,
                                      String aboutFromApi,String titleFromApi);

    @Query("UPDATE museumcollectiondetailtableArabic SET category_id = :categoryIdFromApi," +
            "detail_image1 = :image1FromApi," +
            " detail_about=:aboutFromApi WHERE detail_title=:titleFromApi")
    void updateMuseumDetailTableArabic(String categoryIdFromApi, String image1FromApi,
                                       String aboutFromApi,String titleFromApi);


    @Insert
    void insertEnglishTable(MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish);

    @Insert
    void insertArabicTable(MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic);



}
