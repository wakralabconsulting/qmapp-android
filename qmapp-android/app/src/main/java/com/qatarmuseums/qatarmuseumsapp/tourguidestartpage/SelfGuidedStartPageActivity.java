package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewActivity;

public class SelfGuidedStartPageActivity extends AppCompatActivity {
    FrameLayout mainLayout;
    ImageView playButton, objectImage;
    TextView museumTitle, museumDesc;
    Button startBtn;
    private Animation zoomOutAnimation;
    private Intent intent;
    String tourId, tourName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_guided_starter);

        mainLayout = (FrameLayout) findViewById(R.id.layout_bg);
        playButton = (ImageView) findViewById(R.id.playBtn);
        objectImage = (ImageView) findViewById(R.id.object_image);
        museumTitle = (TextView) findViewById(R.id.museum_tittle);
        museumDesc = (TextView) findViewById(R.id.museum_desc);
        startBtn = (Button) findViewById(R.id.start_btn);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        zoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(SelfGuidedStartPageActivity.this, ObjectPreviewActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        intent = getIntent();

        tourId = intent.getStringExtra("ID");
        tourName = intent.getStringExtra("TOUR_NAME");

        objectImage.setImageDrawable(this.getDrawable(R.drawable.science_tour_object));
        museumTitle.setText(tourName);
        museumDesc.setText(getString(R.string.tourguide_title_desc));


        startBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

    }
}
