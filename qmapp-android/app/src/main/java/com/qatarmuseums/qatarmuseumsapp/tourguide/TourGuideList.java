package com.qatarmuseums.qatarmuseumsapp.tourguide;

public class TourGuideList {
    String name;
    String museumId;
    String tourId;
    String imageurl;
    int image;
    String tourguideId;

    public TourGuideList(String name, String tourId, int image, String museumId) {
        this.name = name;
        this.image = image;
        this.tourId = tourId;
        this.museumId = museumId;
    }

    public TourGuideList(String name, String museumId, String imageurl) {
        this.name = name;
        this.imageurl = imageurl;
        this.museumId = museumId;
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

    public String getMuseumId() {
        return museumId;
    }

    public String getTourId() {
        return tourId;
    }

}
