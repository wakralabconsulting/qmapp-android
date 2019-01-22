package com.qatarmuseums.qatarmuseumsapp.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.HomeActivity;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;


public class SplashActivity extends Activity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        GifDrawable gifFromResource = null;
        try {
            gifFromResource = new GifDrawable(getResources(), R.raw.qm_logo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gifFromResource != null) {
            gifFromResource.setLoopCount(1);
            gifFromResource.addAnimationListener(loopNumber -> {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


}
