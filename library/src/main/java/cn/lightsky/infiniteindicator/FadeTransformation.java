package cn.lightsky.infiniteindicator;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FadeTransformation implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.65f;
    private static final float MIN_ALPHA = 0.3f;
    @Override
    public void transformPage(View view, float position) {
//        if (position <-1){  // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            page.setAlpha(0);
//
//        }
//        else if (position <=1){ // [-1,1]
//
//            page.setScaleX(Math.max(MIN_SCALE,1-Math.abs(position)));
//            page.setScaleY(Math.max(MIN_SCALE,1-Math.abs(position)));
//            page.setAlpha(Math.max(MIN_ALPHA,1-Math.abs(position)));
//
//        }
//        else {  // (1,+Infinity]
//            // This page is way off-screen to the right.
//            page.setAlpha(0);
//
//        }
        if(position <= -1.0F || position >= 1.0F) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(1.0F - Math.abs(position));
        }

    }
}
