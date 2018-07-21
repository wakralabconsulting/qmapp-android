package com.qatarmuseums.qatarmuseumsapp.homeactivity;


public class HomeList {
    private String name;
    private String id;
    private String image;
    private Integer sortCoefficient;
    private Boolean tourguideAvailable;

    public HomeList() {

    }

    public HomeList(String name, String id, String image, Integer sortCoefficient, Boolean tourguideAvailable) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.sortCoefficient = sortCoefficient;
        this.tourguideAvailable = tourguideAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSortCoefficient() {
        return sortCoefficient;
    }

    public void setSortCoefficient(Integer sortCoefficient) {
        this.sortCoefficient = sortCoefficient;
    }

    public Boolean getTourguideAvailable() {
        return tourguideAvailable;
    }

    public void setTourguideAvailable(Boolean tourguideAvailable) {
        this.tourguideAvailable = tourguideAvailable;
    }

}
