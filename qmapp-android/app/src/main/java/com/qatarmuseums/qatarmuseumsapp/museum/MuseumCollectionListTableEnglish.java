package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "museumcollectionlisttableEnglish")
public class MuseumCollectionListTableEnglish {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String name;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private String museum_referance;

    public MuseumCollectionListTableEnglish(@NonNull String name, String image, String museum_referance) {
        this.name = name;
        this.image = image;
        this.museum_referance = museum_referance;
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

    public String getMuseum_referance() {
        return museum_referance;
    }

    public void setMuseum_referance(String museum_referance) {
        this.museum_referance = museum_referance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MuseumCollectionListTableEnglish)) return false;

        MuseumCollectionListTableEnglish museumCollectionListTableEnglish = (MuseumCollectionListTableEnglish) obj;
        return name != null ? museumCollectionListTableEnglish.equals(museumCollectionListTableEnglish.name) : museumCollectionListTableEnglish.name == null;

    }

    @Override
    public String toString() {
        return "museumcollectionlisttableEnglish{" +
                "name ='" + name + '\'' +
                ", image='" + image + '\'' +
                ",museum_referance='"+museum_referance+'\''+
                '}';
    }
}
