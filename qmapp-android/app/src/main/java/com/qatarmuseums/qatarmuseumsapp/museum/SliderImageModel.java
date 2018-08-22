package com.qatarmuseums.qatarmuseumsapp.museum;

import com.google.gson.annotations.SerializedName;

public class SliderImageModel {
    @SerializedName("image1")
    private String image1;
    @SerializedName("image2")
    private String image2;
    @SerializedName("image3")
    private String image3;

    public SliderImageModel(String image1, String image2, String image3) {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }
}
