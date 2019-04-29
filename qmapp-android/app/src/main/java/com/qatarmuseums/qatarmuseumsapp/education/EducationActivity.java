package com.qatarmuseums.qatarmuseumsapp.education;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;

import timber.log.Timber;

public class EducationActivity extends AppCompatActivity {
    YouTubePlayerView youTubePlayerView;
    private String videoId = "2cEYXuCTJjQ";
    public View appBar;
    private View backButton;
    Button discoverButton;
    private Animation zoomOutAnimation;
    private LinearLayout youTubePlayerLayout;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        appBar = findViewById(R.id.app_bar);
        backButton = findViewById(R.id.toolbar_back);
        TextView longDescription = findViewById(R.id.long_description);
        discoverButton = findViewById(R.id.discover_btn);
        longDescription.setText(getString(R.string.education_long_description));
        backButton.setOnClickListener(v -> {
            Timber.i("onBackPressed()");
            onBackPressed();
        });
        discoverButton.setOnClickListener(v -> {
            Timber.i("Discover Button clicked");
            Intent intent = new Intent(EducationActivity.this, EducationCalendarActivity.class);
            startActivity(intent);
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        backButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        youTubePlayerLayout = findViewById(R.id.youtube_player_layout);
        initYouTubePlayerView();

    }

    public void initYouTubePlayerView() {
        Timber.i("initYouTubePlayerView()");
        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerLayout.addView(youTubePlayerView);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.initialize(
                initializedYouTubePlayer -> {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            Timber.i("Play video: %s", videoId);
                            initializedYouTubePlayer.cueVideo(videoId, 0);
                        }
                    });
                }, true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        youTubePlayerView.getPlayerUIController().getMenu().dismiss();
    }

    @Override
    public void onBackPressed() {
        Timber.i("onBackPressed()");
        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}
