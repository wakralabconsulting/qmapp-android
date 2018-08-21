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
    private String museum_referance;

    public MuseumCollectionListTableArabic(@NonNull String name, String image, String museum_referance) {
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
        if (!(obj instanceof MuseumCollectionListTableArabic)) return false;

        MuseumCollectionListTableArabic museumCollectionListTableArabic = (MuseumCollectionListTableArabic) obj;
        return name != null ? museumCollectionListTableArabic.equals(museumCollectionListTableArabic.name) : museumCollectionListTableArabic.name == null;

    }

    @Override
    public String toString() {
        return "museumcollectionlisttableArabic{" +
                "name ='" + name + '\'' +
                ", image='" + image + '\'' +
                ",museum_referance='"+museum_referance+'\''+
                '}';
    }
}
