package com.qatarmuseums.qatarmuseumsapp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.Marker;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {
    private Context context;
    private Handler mHandler = new Handler();
    Marker marker;

    // Write Custom Constructor to pass Context
    public CustomTimerTask(Context con, Marker marker) {
        this.context = con;
        this.marker = marker;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        final long start = SystemClock.uptimeMillis();
                        final long duration = 1500;

                        final Interpolator interpolator = new BounceInterpolator();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                long elapsed = SystemClock.uptimeMillis() - start;
                                float t = Math.max(
                                        1 - interpolator.getInterpolation((float) elapsed
                                                / duration), 0);
                                marker.setAnchor(0.5f, 1.0f + t/2);

                                if (t > 0.0) {
                                    // Post again 16ms later.
                                    handler.postDelayed(this, 16);
                                }
                            }
                        });
                    }
                });
            }
        }).start();

    }

}
