package com.qatarmuseums.qatarmuseumsapp.objectpreview;

public class CurrentIndicatorPosition {
    private int currentPosition;


    public CurrentIndicatorPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }


    public int getOriginalPosition() {
        return currentPosition;
    }
    public int getCurrentPosition() {
        return currentPosition%5;
    }
}
