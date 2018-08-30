package com.qatarmuseums.qatarmuseumsapp.tourguide;

public class TourGuideList {
    String name;
    int image;

    public TourGuideList(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
