package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import static com.qatarmuseums.qatarmuseumsapp.utils.Util.setupItem;

public class HorizontalPagerAdapter extends PagerAdapter {


    private Context mContext;
    private LayoutInflater mLayoutInflater;
    ArrayList<String> imageList = new ArrayList<>();
    HorizontalLayoutFragment fragment;
    DetailsActivity activity;
    private Util.LibraryObject[] LIBRARIES;

    public HorizontalPagerAdapter(Context mContext, HorizontalLayoutFragment fragment, ArrayList<String> images) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
        this.imageList = images;
    }

    public HorizontalPagerAdapter(Context mContext, DetailsActivity activity) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.activity = activity;
    }

//                    {
//                    new Util.LibraryObject(
//                            R.drawable.collection_image2
//                    ),
//                    new Util.LibraryObject(
//                            R.drawable.collection_image2
//                    ),
//                    new Util.LibraryObject(
//                            R.drawable.museum_of_islamic_art
//                    ),
//                    new Util.LibraryObject(
//                            R.drawable.science_tour
//                    ),
//                    new Util.LibraryObject(
//                            R.drawable.coming_soon_1
//                    ),
//                    new Util.LibraryObject(
//                            R.drawable.science_tour_object2
//                    )
//            };


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
        LIBRARIES = new Util.LibraryObject[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
           LIBRARIES[i] =new Util.LibraryObject(imageList.get(i));
        }
        setupItem(view, LIBRARIES[position], mContext);
        FrameLayout layout = view.findViewById(R.id.layout_frame);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
