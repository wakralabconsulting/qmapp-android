package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

public class CollectionDetailsList {
    private String mainTitle;
    private String subTitle;
    private String image1;
    private String image2;
    private String firstDescription;
    private String secondDescription;
    private String thirdDescription;


    public CollectionDetailsList() {

    }

    public CollectionDetailsList(String mainTitle, String subTitle, String image1,
                                 String image2, String firstDescription,
                                 String secondDescription, String thirdDescription) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.image1 = image1;
        this.image2 = image2;
        this.firstDescription = firstDescription;
        this.secondDescription = secondDescription;
        this.thirdDescription = thirdDescription;

    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
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
}
