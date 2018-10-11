package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;

import java.util.ArrayList;

class PagerAdapter extends FragmentPagerAdapter {
    int count;
    ArrayList<ArtifactDetails> objectPreviewModels = new ArrayList<>();
    PageFragment pageFragment;

    public PagerAdapter(FragmentManager fm, int count, ArrayList<ArtifactDetails> objectPreviewModels) {
        super(fm);
        this.count = count;
        this.objectPreviewModels = objectPreviewModels;
        pageFragment = new PageFragment();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        return pageFragment.newInstance(position, position == getCount(),
                objectPreviewModels.get(position).getMainTitle(), objectPreviewModels.get(position).getAccessionNumber(),
                objectPreviewModels.get(position).getImage(), objectPreviewModels.get(position).getProduction(),
                objectPreviewModels.get(position).getProductionDates(), objectPreviewModels.get(position).getPeriodStyle(),
                objectPreviewModels.get(position).getTechniqueMaterials(), objectPreviewModels.get(position).getDimensions(),
                objectPreviewModels.get(position).getCuratorialDescription(), objectPreviewModels.get(position).getObjectHistory(),
                objectPreviewModels.get(position).getObjectENGSummary(), objectPreviewModels.get(position).getImages());
    }

}