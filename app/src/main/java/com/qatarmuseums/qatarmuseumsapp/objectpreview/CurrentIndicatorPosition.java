package com.qatarmuseums.qatarmuseumsapp.objectpreview;

class CurrentIndicatorPosition {
    private int currentPosition;


    CurrentIndicatorPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }


    int getOriginalPosition() {
        return currentPosition;
    }

    int getCurrentPosition() {
        return currentPosition % 5;
    }
}
