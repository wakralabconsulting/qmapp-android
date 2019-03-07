package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import cn.lightsky.infiniteindicator.ImageLoader;

public class GlideLoaderForMuseum implements ImageLoader {

    @Override
    public void load(Context context, ImageView targetView, Object res) {

        if (res instanceof String) {
            GlideApp.with(context)
                    .load((String) res)
                    .centerCrop()
                    .into(targetView);
        }
    }
}