package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;

import java.util.ArrayList;

class PagerAdapter extends FragmentPagerAdapter {
    private int count;
    private ArrayList<ArtifactDetails> objectPreviewModels = new ArrayList<>();

    PagerAdapter(FragmentManager fm, int count, ArrayList<ArtifactDetails> objectPreviewModels) {
        super(fm);
        this.count = count;
        this.objectPreviewModels = objectPreviewModels;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        args.putString("GALLERY", objectPreviewModels.get(position).getGalleryNumber());
        args.putString("FLOOR", objectPreviewModels.get(position).getFloorLevel());
        args.putString("MAINTITLE", objectPreviewModels.get(position).getMainTitle());
        args.putString("ACCESIONNUMBER", objectPreviewModels.get(position).getAccessionNumber());
        args.putString("IMAGE", objectPreviewModels.get(position).getImage());
        args.putString("DESCRIPTION", objectPreviewModels.get(position).getCuratorialDescription());
        args.putString("HISTORY", objectPreviewModels.get(position).getObjectHistory());
        args.putString("SUMMARY", objectPreviewModels.get(position).getObjectENGSummary());
        args.putStringArrayList("IMAGES", objectPreviewModels.get(position).getImages());
        args.putString("AUDIO", objectPreviewModels.get(position).getAudioFile());
        if (position == getCount())
            args.putBoolean("isLast", true);
        final PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

}