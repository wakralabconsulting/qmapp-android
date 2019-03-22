package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import com.google.gson.annotations.SerializedName;

public class CollectionDetailsList {
    @SerializedName("Title")
    private String mainTitle;
    @SerializedName("Body")
    private String about;
    @SerializedName("image")
    private String image1;
    @SerializedName("Category_collection")
    private String categoryName;

    public CollectionDetailsList(String mainTitle, String image1, String about,
                                 String categoryName) {
        this.mainTitle = mainTitle;
        this.image1 = image1;
        this.about = about;
        this.categoryName = categoryName;
    }

    public CollectionDetailsList(String mainTitle, String image1, String about) {
        this.mainTitle = mainTitle;
        this.image1 = image1;
        this.about = about;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getAbout() {
        return about;
    }

    public String getImage1() {
        return image1;
    }


    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
