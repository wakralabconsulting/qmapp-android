package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "museumcollectiondetailtableArabic")
public class MuseumCollectionDetailTableArabic {
    @ColumnInfo()
    private String category_id;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo()
    private String detail_title;
    @ColumnInfo()
    private String detail_about;
    @ColumnInfo()
    private String detail_image1;
//    @ColumnInfo()
//    private String detail_image2;
//    @ColumnInfo()
//    private String detail_description1;
//    @ColumnInfo()
//    private String detail_description2;
//    @ColumnInfo()
//    private String detail_description3;

//    public MuseumCollectionDetailTableArabic(String category_id, @NonNull String detail_title, String detail_about,
//                                             String detail_image1, String detail_image2, String detail_description1,
//                                             String detail_description2, String detail_description3) {
//        this.category_id = category_id;
//        this.detail_title = detail_title;
//        this.detail_about = detail_about;
//        this.detail_image1 = detail_image1;
//        this.detail_image2 = detail_image2;
//        this.detail_description1 = detail_description1;
//        this.detail_description2 = detail_description2;
//        this.detail_description3 = detail_description3;
//    }


    public MuseumCollectionDetailTableArabic(String category_id, @NonNull String detail_title, String detail_image1, String detail_about) {
        this.category_id = category_id;
        this.detail_title = detail_title;
        this.detail_image1 = detail_image1;
        this.detail_about = detail_about;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getDetail_title() {
        return detail_title;
    }

    public String getDetail_about() {
        return detail_about;
    }

    public String getDetail_image1() {
        return detail_image1;
    }

//    public String getDetail_image2() {
//        return detail_image2;
//    }

//    public String getDetail_description1() {
//        return detail_description1;
//    }

//    public String getDetail_description2() {
//        return detail_description2;
//    }

//    public String getDetail_description3() {
//        return detail_description3;
//    }
    @Override
    public String toString() {
        return "museumcollectiondetailtableArabic{" +
                "category_id ='" + category_id + '\'' +
                ", detail_title='" + detail_title + '\'' +
               /* ",detail_about='"+detail_about+'\''+*/
                "detail_image1 ='" + detail_image1 + '\'' +
               /* ", detail_image2='" + detail_image2 + '\'' +*/
                "detail_about ='" + detail_about + '\'' +
               /* ", detail_description2='" + detail_description2 + '\'' +
                ", detail_description3='" + detail_description3 + '\'' +*/
                '}';
    }
}
