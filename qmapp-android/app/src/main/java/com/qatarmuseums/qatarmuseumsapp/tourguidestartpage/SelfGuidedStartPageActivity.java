package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class SelfGuidedStartPageActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, OnPageClickListener {
    ImageView playButton;
    TextView museumTitle, museumDesc;
    Button startBtn;
    private Animation zoomOutAnimation;
    private Intent intent;
    Util util;
    String museumId, tourName, description, toolarTitle;
    private IndicatorConfiguration configuration;
    private InfiniteIndicator animCircleIndicator;
    private GlideLoaderForTourGuide glideLoader;
    String appLanguage;
    ArrayList<Page> ads;
    ImageView sliderPlaceholderImage;
    private String tourId;
    private RelativeLayout mainLayout;
    private ArrayList<String> imageList;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private ImageView backArrow;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_guided_starter);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        appLanguage = LocaleManager.getLanguage(this);
        playButton = (ImageView) findViewById(R.id.playBtn);
        util = new Util();
        museumTitle = (TextView) findViewById(R.id.museum_tittle);
        sliderPlaceholderImage = (ImageView) findViewById(R.id.ads_place_holder);
        museumDesc = (TextView) findViewById(R.id.museum_desc);

        mainLayout = findViewById(R.id.main_content);
        animCircleIndicator = (InfiniteIndicator) findViewById(R.id.main_indicator_default_circle);
        startBtn = (Button) findViewById(R.id.start_btn);
        intent = getIntent();
        museumId = intent.getStringExtra("MUSEUM_ID");
        tourId = intent.getStringExtra("TOUR_ID");
        tourName = intent.getStringExtra("TOUR_NAME");
        description = intent.getStringExtra("DESCRIPTION");
        imageList = intent.getStringArrayListExtra("IMAGES");
        toolarTitle = intent.getStringExtra("TITLE");
        toolbar_title.setText(toolarTitle);
        museumTitle.setText(tourName);
        museumDesc.setText(description);
        ArrayList<String> sliderList = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            sliderList.add(i, imageList.get(i));
        }
        setSliderImages(sliderList);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);

        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        intent = getIntent();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelfGuidedStartPageActivity.this, ObjectPreviewActivity.class);
                i.putExtra("TOUR_ID", tourId);
                startActivity(i);
            }
        });
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


    public void loadAdsToSlider(ArrayList<Page> adsImages) {
        appLanguage = LocaleManager.getLanguage(this);
        if (adsImages.size() > 1) {
            glideLoader = new GlideLoaderForTourGuide();
            if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .scrollDurationFactor(2)
                        .internal(4000)
                        .isLoop(true)
                        .isAutoScroll(true)
                        .onPageClickListener(this)
                        .direction(RIGHT)
                        .isDrawIndicator(false)
                        .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                        .build();
                animCircleIndicator.init(configuration);
                animCircleIndicator.notifyDataChange(adsImages);
            } else {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .internal(4000)
                        .scrollDurationFactor(2)
                        .isLoop(true)
                        .isAutoScroll(true)
                        .isDrawIndicator(false)
                        .onPageClickListener(this)
                        .direction(LEFT)
                        .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                        .build();
                animCircleIndicator.init(configuration);
                animCircleIndicator.notifyDataChange(adsImages);
            }

        } else {
            glideLoader = new GlideLoaderForTourGuide();
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(glideLoader)
                    .isStopWhileTouch(true)
                    .onPageChangeListener(null)
                    .scrollDurationFactor(2)
                    .internal(4000)
                    .isLoop(false)
                    .isAutoScroll(false)
                    .onPageClickListener(null)
                    .direction(RIGHT)
                    .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                    .build();
            animCircleIndicator.init(configuration);
            animCircleIndicator.notifyDataChange(adsImages);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageClick(int position, Page page) {

    }


    public void setSliderImages(ArrayList<String> sliderImageList) {
        ads = new ArrayList<>();
        for (int i = 0; i < sliderImageList.size(); i++) {
            ads.add(new Page("", sliderImageList.get(i),
                    null));
        }
        loadAdsToSlider(ads);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (configuration != null)
            animCircleIndicator.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (configuration != null)
            animCircleIndicator.start();
        super.onResume();
        if (configuration != null)
            animCircleIndicator.start();


    }

}
