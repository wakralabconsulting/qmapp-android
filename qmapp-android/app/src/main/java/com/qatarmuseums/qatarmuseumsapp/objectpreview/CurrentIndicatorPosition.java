package com.qatarmuseums.qatarmuseumsapp.objectpreview;

public class CurrentIndicatorPosition {
    private int currentPosition;

    public CurrentIndicatorPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
