package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Entity(tableName = "museumcollectionlisttableArabic")
public class MuseumCollectionListTableArabic {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String name;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private String museum_id;
    @ColumnInfo()
    private String category;
    @ColumnInfo()
    private String collection_description;
    @ColumnInfo()
    private String detail_title;
    @ColumnInfo()
    private String detail_about;
    @ColumnInfo()
    private String detail_image1;
    @ColumnInfo()
    private String detail_image2;
    @ColumnInfo()
    private String detail_description1;
    @ColumnInfo()
    private String detail_description2;
    @ColumnInfo()
    private String detail_description3;

    public MuseumCollectionListTableArabic(@NonNull String name, String image, String museum_id, String category,
                                           String collection_description, String detail_title, String detail_about, String detail_image1, String detail_image2, String detail_description1,
                                           String detail_description2, String detail_description3) {
        this.name = name;
        this.image = image;
        this.museum_id = museum_id;
        this.category = category;
        this.collection_description = collection_description;
        this.detail_title = detail_title;
        this.detail_about = detail_about;
        this.detail_image1 = detail_image1;
        this.detail_image2 = detail_image2;
        this.detail_description1 = detail_description1;
        this.detail_description2 = detail_description2;
        this.detail_description3 = detail_description3;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMuseum_id() {
        return museum_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCollection_description() {
        return collection_description;
    }

    public void setCollection_description(String collection_description) {
        this.collection_description = collection_description;
    }

    public void setMuseum_id(String museum_id) {
        this.museum_id = museum_id;

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

    public String getDetail_image2() {
        return detail_image2;
    }

    public String getDetail_description1() {
        return detail_description1;
    }

    public String getDetail_description2() {
        return detail_description2;
    }

    public String getDetail_description3() {
        return detail_description3;
    }

    @Override
    public String toString() {
        return "museumcollectionlisttableArabic{" +
                "name ='" + name + '\'' +
                ", image='" + image + '\'' +
                ",museum_id='"+museum_id+'\''+
                "category ='" + category + '\'' +
                ", collection_description='" + collection_description + '\'' +
                ",detail_title='"+detail_title+'\''+
                ", detail_about='" + detail_about + '\'' +
                ",detail_image1='"+detail_image1+'\''+
                "detail_image2 ='" + detail_image2 + '\'' +
                ", detail_description1='" + detail_description1 + '\'' +
                ",detail_description2='"+detail_description2+'\''+
                ",detail_description3='"+detail_description3+'\''+
                '}';
    }
}
