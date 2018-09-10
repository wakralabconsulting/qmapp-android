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


    public MuseumCollectionListTableArabic(@NonNull String name, String image, String museum_id, String category,
                                           String collection_description) {
        this.name = name;
        this.image = image;
        this.museum_id = museum_id;
        this.category = category;
        this.collection_description = collection_description;

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



    @Override
    public String toString() {
        return "museumcollectionlisttableArabic{" +
                "name ='" + name + '\'' +
                ", image='" + image + '\'' +
                ",museum_id='"+museum_id+'\''+
                "category ='" + category + '\'' +
                ", collection_description='" + collection_description + '\'' +
                '}';
    }
}
