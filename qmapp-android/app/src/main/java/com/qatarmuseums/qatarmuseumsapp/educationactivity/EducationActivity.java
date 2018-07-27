package com.qatarmuseums.qatarmuseumsapp.educationactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.FullScreenHelper;

public class EducationActivity extends AppCompatActivity {
    private FullScreenHelper fullScreenHelper = new FullScreenHelper(this);
    YouTubePlayerView youTubePlayerView;
    private String videoId = "2cEYXuCTJjQ";
    public View appBar;
    private View backButton;
    Button discoverButton;
    private Animation zoomOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        appBar = findViewById(R.id.app_bar);
        backButton = findViewById(R.id.toolbar_back);
        discoverButton = (Button) findViewById(R.id.discover_btn);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        initYouTubePlayerView();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Discover button action
                Intent intent=new Intent(EducationActivity.this,EducationCalendarActivity.class);
                startActivity(intent);
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }

    public void initYouTubePlayerView() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.initialize(
                new YouTubePlayerInitListener() {
                    @Override
                    public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                        initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady() {
                                initializedYouTubePlayer.loadVideo(videoId, 0);
                            }
                        });
//                        addFullScreenListenerToPlayer(initializedYouTubePlayer);
                    }
                }, true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        youTubePlayerView.getPlayerUIController().getMenu().dismiss();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
    }

    private void addFullScreenListenerToPlayer(final YouTubePlayer youTubePlayer) {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelper.enterFullScreen();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenHelper.exitFullScreen();
            }
        });
    }
}
