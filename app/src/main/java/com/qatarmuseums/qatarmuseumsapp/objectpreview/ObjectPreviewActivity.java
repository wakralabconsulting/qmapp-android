package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


import com.badoualy.stepperindicator.StepperIndicator;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;

public class ObjectPreviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView backBtn, shareBtn, locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.back_btn);
        shareBtn = findViewById(R.id.share_btn);
        locationBtn = findViewById(R.id.location_btn);
        setSupportActionBar(toolbar);
        final ViewPager pager = findViewById(R.id.pager);
        assert pager != null;
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        final StepperIndicator indicator = findViewById(R.id.stepper_indicator);
        // We keep last page for a "finishing" page
        indicator.setViewPager(pager, true);
        indicator.setDoneIcon(null);
        indicator.setShowDoneIcon(false);

        indicator.addOnStepClickListener(new StepperIndicator.OnStepClickListener() {
            @Override
            public void onStepClicked(int step) {
                pager.setCurrentItem(step, true);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ObjectPreviewActivity.this, FloorMapActivity.class);
                startActivity(i);
            }
        });
    }
}
