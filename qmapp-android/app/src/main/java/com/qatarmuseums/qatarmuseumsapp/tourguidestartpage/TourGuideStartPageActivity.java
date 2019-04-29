package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;

public class TourGuideStartPageActivity extends AppCompatActivity {
    FrameLayout mainLayout;
    ImageView playButton;
    TextView museumTitle, museumDesc;
    Button startBtn;
    private Animation zoomOutAnimation;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_start_page);
        mainLayout = findViewById(R.id.layout_bg);
        playButton = findViewById(R.id.playBtn);
        museumTitle = findViewById(R.id.museum_tittle);
        museumDesc = findViewById(R.id.museum_desc);
        startBtn = findViewById(R.id.start_btn);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        zoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(TourGuideStartPageActivity.this, FloorMapActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainLayout.setBackground(this.getDrawable(R.drawable.museum_of_islamic));
        museumTitle.setText(getString(R.string.mia_tour_guide));
        museumDesc.setText(getString(R.string.tourguide_title_desc));

        startBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.tour_guided_starter_page), null);

    }
}
