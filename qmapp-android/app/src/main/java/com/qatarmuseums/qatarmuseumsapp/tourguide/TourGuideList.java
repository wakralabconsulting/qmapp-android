package com.qatarmuseums.qatarmuseumsapp.tourguide;

public class TourGuideList {
    String name;
    String imageurl;
    int image;

    public TourGuideList(String name, int image) {
        this.name = name;
        this.image = image;
    }
    public TourGuideList(String name, String imageurl) {
        this.name = name;
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public int getImage() {
        return image;
    }
}
