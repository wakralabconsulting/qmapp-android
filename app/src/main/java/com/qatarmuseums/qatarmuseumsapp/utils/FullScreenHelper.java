package com.qatarmuseums.qatarmuseumsapp.utils;

import android.app.Activity;
import android.view.View;

/**
 * Class responsible for changing the view from full screen to non-full screen and vice versa.
 *
 * @author Pierfrancesco Soffritti
 */
public class FullScreenHelper {

    private Activity context;
    private View[] views;

    public FullScreenHelper(Activity context, View... views) {
        this.context = context;
        this.views = views;
    }

    public void enterFullScreen() {
        View decorView = context.getWindow().getDecorView();
        hideSystemUI(decorView);

        for (View view : views) {
            view.setVisibility(View.GONE);
            view.invalidate();
        }
    }

    public void exitFullScreen() {
        View decorView = context.getWindow().getDecorView();
        showSystemUI(decorView);

        for (View view : views) {
            view.setVisibility(View.VISIBLE);
            view.invalidate();
        }
    }

    private void hideSystemUI(View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showSystemUI(View mDecorView) {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}