package com.qatarmuseums.qatarmuseumsapp.museum;


import android.content.Intent;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class MuseumActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, OnPageClickListener {
    private List<MuseumHScrollModel> museumHScrollModelList = new ArrayList<>();
    private MuseumHorizontalScrollViewAdapter museumHorizontalScrollViewAdapter;
    @BindView(R.id.horizontal_scrol_previous_icon)
    ImageView scrollBarPreviousIcon;
    LinearLayoutManager recyclerviewLayoutManager;
    @BindView(R.id.horizontal_scrol_next_icon)
    ImageView scrollBarNextIcon;
    @BindView(R.id.settings_page_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.horizontal_scrol_next_icon_layout)
    LinearLayout scrollBarNextIconLayout;
    @BindView(R.id.slider_image_title)
    TextView sliderImageTitle;
    @BindView(R.id.horizontal_scrol_previous_icon_layout)
    LinearLayout scrollBarPreviousIconLayout;
    private IndicatorConfiguration configuration;
    private InfiniteIndicator animCircleIndicator;
    private GlideLoader glideLoader;
    ArrayList<Page> ads;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        ButterKnife.bind(this);
        setToolbarForMuseumActivity();
        intent = getIntent();
        sliderImageTitle.setText(intent.getStringExtra("MUSEUMTITLE"));
        animCircleIndicator = (InfiniteIndicator) findViewById(R.id.main_indicator_default_circle);

        museumHorizontalScrollViewAdapter = new MuseumHorizontalScrollViewAdapter(this, museumHScrollModelList);
        recyclerviewLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(recyclerviewLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(museumHorizontalScrollViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = 0;
                int lastCompleteVisibleItemPosition = 0;
                int firstVisibleItemPosition = 0;
                lastCompleteVisibleItemPosition = ((LinearLayoutManager) recyclerView
                        .getLayoutManager()).findLastCompletelyVisibleItemPosition();
                lastVisibleItemPosition = ((LinearLayoutManager) recyclerView
                        .getLayoutManager()).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((LinearLayoutManager) recyclerView
                        .getLayoutManager()).findFirstVisibleItemPosition();
                 if (firstVisibleItemPosition == 0) {
                    showRightArrow();
                }else if (lastCompleteVisibleItemPosition ==museumHScrollModelList.size()-1) {
                    showLeftArrow();
                }else {
                     showBothArrows();
                 }

            }
        });
        SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
        snapHelperStart.attachToRecyclerView(recyclerView);
        prepareRecyclerViewData();
        getSliderImagesFromList();
        scrollBarNextIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(museumHorizontalScrollViewAdapter.getItemCount() - 1);
            }
        });
        scrollBarPreviousIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(0);
            }
        });

    }


    public void getSliderImagesFromList() {
        ads = new ArrayList<>();

        ads.add(new Page("", "http://www.qm.org.qa/sites/default/files/museum_of_islamic_art.png",
                null));
        ads.add(new Page("", "http://www.qm.org.qa/sites/default/files/national_museum_of_qatar.png",
                null));
        ads.add(new Page("", "http://www.qm.org.qa/sites/default/files/mathaf_arab_museum.png",
                null));
        ads.add(new Page("", "http://www.qm.org.qa/sites/default/files/firestation.png",
                null));
        ads.add(new Page("", "http://www.qm.org.qa/sites/default/files/qatar_olypic_sports_museum.png",
                null));

        loadAdsToSlider(ads);
    }


    public void showBothArrows() {
        scrollBarNextIcon.setVisibility(View.VISIBLE);
        scrollBarPreviousIcon.setVisibility(View.VISIBLE);
        scrollBarPreviousIconLayout.setVisibility(View.VISIBLE);
        scrollBarNextIconLayout.setVisibility(View.VISIBLE);
    }

    public void showLeftArrow() {
        scrollBarNextIcon.setVisibility(View.GONE);
        scrollBarNextIconLayout.setVisibility(View.INVISIBLE);
        scrollBarPreviousIconLayout.setVisibility(View.VISIBLE);
        scrollBarPreviousIcon.setVisibility(View.VISIBLE);

    }


    public void showRightArrow() {
        scrollBarNextIcon.setVisibility(View.VISIBLE);
        scrollBarPreviousIcon.setVisibility(View.GONE);
        scrollBarPreviousIconLayout.setVisibility(View.INVISIBLE);
        scrollBarNextIconLayout.setVisibility(View.VISIBLE);
    }

    public void prepareRecyclerViewData() {
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_tour_guide_text), R.drawable.audio_circle);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);


        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_parks_text), R.drawable.park_black);
        museumHScrollModelList.add(model);


        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }


    public void loadAdsToSlider(ArrayList<Page> adsImages) {
        if (adsImages.size() > 1) {
            glideLoader = new GlideLoader();
            if (getResources().getConfiguration().locale.getLanguage().equals("en")) {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .scrollDurationFactor(2)
                        .internal(3000)
                        .isLoop(true)
                        .isAutoScroll(false)
                        .onPageClickListener(this)
                        .direction(RIGHT)
                        .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                        .build();
                animCircleIndicator.init(configuration);
                animCircleIndicator.notifyDataChange(adsImages);
            } else {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .internal(3000)
                        .scrollDurationFactor(2)
                        .isLoop(true)
                        .isAutoScroll(false)
                        .onPageClickListener(this)
                        .direction(LEFT)
                        .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                        .build();
                animCircleIndicator.init(configuration);
                animCircleIndicator.notifyDataChange(adsImages);
            }

        } else {
            glideLoader = new GlideLoader();
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(glideLoader)
                    .isStopWhileTouch(true)
                    .onPageChangeListener(null)
                    .scrollDurationFactor(2)
                    .internal(3000)
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
