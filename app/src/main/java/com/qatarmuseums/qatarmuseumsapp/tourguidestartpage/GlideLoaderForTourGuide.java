package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Context;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import cn.lightsky.infiniteindicator.ImageLoader;

/**
 * Created by  exalture on 16/1/31.
 */
public class GlideLoaderForTourGuide implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {

        if (res instanceof String){
            GlideApp.with(context)
                    .load((String) res)
                    .centerCrop()
                    .into(targetView);
        }
    }
}
