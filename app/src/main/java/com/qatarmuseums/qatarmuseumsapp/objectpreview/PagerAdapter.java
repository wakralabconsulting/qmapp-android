package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position , position == getCount() );
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "Page" + position;
//    }
}