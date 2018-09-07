package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import com.google.gson.annotations.SerializedName;

public class CollectionDetailsList {
    @SerializedName(value = "name", alternate = {"Name","الاسم"})
    private String name;
    @SerializedName("Title")
    private String mainTitle;
    @SerializedName("location")
    private String location;
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

    public CollectionDetailsList(String name, String mainTitle, String location,
                                 String image1, String image2, String firstDescription,
                                 String secondDescription, String thirdDescription) {
        this.name = name;
        this.mainTitle = mainTitle;
        this.location = location;
        this.image1 = image1;
        this.image2 = image2;
        this.firstDescription = firstDescription;
        this.secondDescription = secondDescription;
        this.thirdDescription = thirdDescription;
    }

    public String getName() {
        return name;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getLocation() {
        return location;
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
