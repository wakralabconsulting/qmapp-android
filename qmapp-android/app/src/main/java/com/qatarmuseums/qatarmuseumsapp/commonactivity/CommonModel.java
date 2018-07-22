package com.qatarmuseums.qatarmuseumsapp.commonactivity;


public class CommonModel {
    private String name, nameDescription;
    private String id;
    private String image;
    private Boolean isOpen, isfavourite;

    public CommonModel() {

    }

    public CommonModel(String id, String name, String nameDescription, String image, Boolean isOpen,
                       Boolean isfavourite) {
        this.id = id;
        this.name = name;
        this.nameDescription = nameDescription;
        this.image = image;
        this.isOpen = isOpen;
        this.isfavourite = isfavourite;
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

    public String getNameDescription() {
        return nameDescription;
    }

    public void setNameDescription(String nameDescription) {
        this.nameDescription = nameDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getStatusTag() {
        return isOpen;
    }

    public void setStatusTag(Boolean open) {
        isOpen = open;
    }

    public Boolean getIsfavourite() {
        return isfavourite;
    }

    public void setIsfavourite(Boolean isfavourite) {
        this.isfavourite = isfavourite;
    }
}
