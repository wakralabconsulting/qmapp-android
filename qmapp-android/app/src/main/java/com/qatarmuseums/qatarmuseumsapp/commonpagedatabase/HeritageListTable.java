package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "heritagelist")
public class HeritageListTable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long heritage_id;
    @ColumnInfo()
    private String heritage_name;
    @ColumnInfo()
    private String heritage_image;
    @ColumnInfo()
    private String heritage_sortid;

    public String getHeritage_name_arabic() {
        return heritage_name_arabic;
    }

    public void setHeritage_name_arabic(String heritage_name_arabic) {
        this.heritage_name_arabic = heritage_name_arabic;
    }

    @ColumnInfo()
    private String heritage_name_arabic;

    public HeritageListTable(@NonNull long heritage_id, String heritage_name, String heritage_image, String heritage_sortid) {
        this.heritage_id = heritage_id;
        this.heritage_name = heritage_name;
        this.heritage_image = heritage_image;
        this.heritage_sortid = heritage_sortid;
    }

    @NonNull
    public long getHeritage_id() {
        return heritage_id;
    }

    public void setHeritage_id(@NonNull long heritage_id) {
        this.heritage_id = heritage_id;
    }

    public String getHeritage_name() {
        return heritage_name;
    }

    public void setHeritage_name(String heritage_name) {
        this.heritage_name = heritage_name;
    }

    public String getHeritage_image() {
        return heritage_image;
    }

    public void setHeritage_image(String heritage_image) {
        this.heritage_image = heritage_image;
    }

    public String getHeritage_sortid() {
        return heritage_sortid;
    }

    public void setHeritage_sortid(String heritage_sortid) {
        this.heritage_sortid = heritage_sortid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HeritageListTable)) return false;

        HeritageListTable heritageListTable = (HeritageListTable) obj;

        if (heritage_id != heritageListTable.heritage_id) return false;
        return heritage_name != null ? heritageListTable.equals(heritageListTable.heritage_name) : heritageListTable.heritage_name == null;

    }

    @Override
    public String toString() {
        return "heritagelist{" +
                "heritage_name=" + heritage_name +
                ", heritage_id='" + heritage_id + '\'' +
                ", heritage_image='" + heritage_image + '\'' +
                ",heritage_sortid='" + heritage_sortid + '\'' +
                '}';
    }
}
