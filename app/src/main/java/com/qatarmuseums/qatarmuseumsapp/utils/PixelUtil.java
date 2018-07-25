package com.qatarmuseums.qatarmuseumsapp.utils;

import android.content.Context;

public class PixelUtil {


    public static int dp2px(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }
}
