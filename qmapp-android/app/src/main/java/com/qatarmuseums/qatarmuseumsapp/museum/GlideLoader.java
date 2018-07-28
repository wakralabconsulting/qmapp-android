package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.homeactivity.GlideApp;

import cn.lightsky.infiniteindicator.ImageLoader;

/**
 * Created by  exalture on 16/1/31.
 */
public class GlideLoader implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {

        if (res instanceof String){
            GlideApp.with(context)
                    .load((String) res)
                    .centerCrop()
                    .placeholder(R.drawable.placeholdeer)
                    .into(targetView);
        }
    }
}