package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.qatarmuseums.qatarmuseumsapp.R;

public class TourGuideStartPageActivity extends AppCompatActivity {
    FrameLayout mainLayout;
    ImageView playButton;
    TextView museumTitle, museumDesc;
    Button startBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_start_page);
        mainLayout = (FrameLayout)findViewById(R.id.layout_bg);
        playButton = (ImageView) findViewById(R.id.playBtn);
        museumTitle = (TextView) findViewById(R.id.museum_tittle);
        museumDesc = (TextView) findViewById(R.id.museum_desc);
        startBtn = (Button) findViewById(R.id.start_btn);
        mainLayout.setBackground(this.getDrawable(R.drawable.museum_of_islamic));
        museumTitle.setText(getString(R.string.mia_tour_guide));
        museumDesc.setText(getString(R.string.tourguide_title_desc));

    }
}
