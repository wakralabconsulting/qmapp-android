package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;

public class TourGuideStartPageActivity extends AppCompatActivity {
    FrameLayout mainLayout;
    ImageView playButton;
    TextView museumTitle, museumDesc;
    Button startBtn;
    private Animation zoomOutAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_start_page);
        mainLayout = (FrameLayout)findViewById(R.id.layout_bg);
        playButton = (ImageView) findViewById(R.id.playBtn);
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
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
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
