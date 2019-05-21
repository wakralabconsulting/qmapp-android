package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import timber.log.Timber;

import static com.qatarmuseums.qatarmuseumsapp.utils.Util.setupItem;

public class HorizontalPagerAdapter extends PagerAdapter {


    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> imageList = new ArrayList<>();
    private HorizontalLayoutFragment fragment;
    private DetailsActivity activity;

    HorizontalPagerAdapter(Context mContext, HorizontalLayoutFragment fragment, ArrayList<String> images) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
        this.imageList = images;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final View view;
        view = mLayoutInflater.inflate(R.layout.item_view, container, false);
        Util.LibraryObject[] LIBRARIES = new Util.LibraryObject[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            LIBRARIES[i] = new Util.LibraryObject(imageList.get(i));
        }
        setupItem(view, LIBRARIES[position], mContext);
        FrameLayout layout = view.findViewById(R.id.layout_frame);
        view.setOnClickListener(v -> {
            Timber.i("Clicked position: %d ", position);
            if (fragment != null) {
                fragment.getItemPosition(position);
            } else {
                activity.getItemPosition();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
