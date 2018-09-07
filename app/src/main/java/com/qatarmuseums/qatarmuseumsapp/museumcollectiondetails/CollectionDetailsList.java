package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import com.google.gson.annotations.SerializedName;

public class CollectionDetailsList {
    @SerializedName("Title")
    private String mainTitle;
    @SerializedName("about")
    private String about;
    @SerializedName("image_highlight")
    private String image1;
    @SerializedName("image_main")
    private String image2;
    @SerializedName("short_description")
    private String firstDescription;
    @SerializedName("highlight_description")
    private String secondDescription;
    @SerializedName("long_description")
    private String thirdDescription;

    public CollectionDetailsList(String mainTitle, String about,
                                 String image1, String image2, String firstDescription,
                                 String secondDescription, String thirdDescription) {
        this.mainTitle = mainTitle;
        this.about = about;
        this.image1 = image1;
        this.image2 = image2;
        this.firstDescription = firstDescription;
        this.secondDescription = secondDescription;
        this.thirdDescription = thirdDescription;
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

    public String getImage2() {
        return image2;
    }

    public String getFirstDescription() {
        return firstDescription;
    }

    public String getSecondDescription() {
        return secondDescription;
    }

    public String getThirdDescription() {
        return thirdDescription;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }
}
