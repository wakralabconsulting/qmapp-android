package com.qatarmuseums.qatarmuseumsapp.utils;

public interface IPullZoom {

    boolean isReadyForPullStart();

    void onPullZooming(int newScrollValue);

    void onPullZoomEnd();

}
