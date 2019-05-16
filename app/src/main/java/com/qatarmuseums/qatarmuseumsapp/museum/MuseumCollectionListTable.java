package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "museumCollectionListTable")
public class MuseumCollectionListTable {

    @ColumnInfo()
    private String language;
    @NonNull
    @PrimaryKey()
    private String name;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private String museum_id;
    @ColumnInfo()
    private String collection_description;


    public MuseumCollectionListTable(@NonNull String name, String image,
                                     String museum_id,
                                     String collection_description,
                                     String language) {
        this.name = name;
        this.image = image;
        this.museum_id = museum_id;
        this.collection_description = collection_description;
        this.language = language;
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

    public void setMuseum_id(String museum_id) {
        this.museum_id = museum_id;
    }

    String getCollection_description() {
        return collection_description;
    }

    public String getLanguage() {
        return language;
    }
}
