package com.qatarmuseums.qatarmuseumsapp.museum;


import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private GlideLoaderForMuseum glideLoader;
    ArrayList<Page> ads;
    Intent intent;
    SharedPreferences qmPreferences;
    int appLanguage;
    private final int english = 1;
    ImageView sliderPlaceholderImage;
    Util util;
    ArrayList<MuseumAboutModel> museumAboutModels = new ArrayList<>();
    private String language;
    private String museumId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        ButterKnife.bind(this);
        setToolbarForMuseumActivity();
        util = new Util();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        intent = getIntent();
        sliderImageTitle.setText(intent.getStringExtra("MUSEUMTITLE"));
        museumId = intent.getStringExtra("MUSEUM_ID");
        animCircleIndicator = (InfiniteIndicator) findViewById(R.id.main_indicator_default_circle);
        sliderPlaceholderImage = (ImageView) findViewById(R.id.ads_place_holder);

        museumHorizontalScrollViewAdapter = new MuseumHorizontalScrollViewAdapter(this,
                museumHScrollModelList, intent.getStringExtra("MUSEUMTITLE"), museumId, getScreenWidth());
        animCircleIndicator.setVisibility(View.GONE);
        if (appLanguage == english) {
            recyclerviewLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerviewLayoutManager.setStackFromEnd(false);
        } else {
            recyclerviewLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerviewLayoutManager.setStackFromEnd(true);
            recyclerviewLayoutManager.scrollToPosition(0);
        }

        recyclerView.setLayoutManager(recyclerviewLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(museumHorizontalScrollViewAdapter);

        if (museumId.equals("63") || museumId.equals("96") || museumId.equals("61") || museumId.equals("66") ||
                museumId.equals("635") || museumId.equals("638")) {

            if (museumId.equals("63") || museumId.equals("96"))
                prepareRecyclerViewDataForMIA();
            else
                prepareRecyclerViewDataFor5();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastCompleteVisibleItemPosition = 0;
                    int firstVisibleItemPosition = 0;
                    lastCompleteVisibleItemPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    firstVisibleItemPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findFirstVisibleItemPosition();

                    if (appLanguage == english) {
                        if (firstVisibleItemPosition == 0) {
                            showRightArrow();
                        } else if (lastCompleteVisibleItemPosition == museumHScrollModelList.size() - 1) {
                            showLeftArrow();
                        } else {
                            showBothArrows();
                        }
                    } else {
                        if (firstVisibleItemPosition == 0) {
                            showRightArrow();
                        } else if (lastCompleteVisibleItemPosition == museumHScrollModelList.size() - 1) {

                            showLeftArrow();
                        } else {
                            showBothArrows();
                        }

                    }

                }
            });

            if (appLanguage == english) {
                SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
                snapHelperStart.attachToRecyclerView(recyclerView);
            } else {
                SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.END);
                snapHelperStart.attachToRecyclerView(recyclerView);
            }

            scrollBarNextIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerviewLayoutManager.scrollToPosition(5);

                }
            });
            scrollBarPreviousIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerviewLayoutManager.scrollToPosition(0);

                }
            });
        } else
            prepareRecyclerViewData();

        getSliderImagesfromAPI();
    }

    public void setSliderImages(ArrayList<String> sliderImageList) {
        ads = new ArrayList<>();
        for (int i = 0; i < sliderImageList.size(); i++) {
            ads.add(new Page("", sliderImageList.get(i),
                    null));
        }
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
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

    public void prepareRecyclerViewDataForMIA() {
        museumHScrollModelList.clear();
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

    public void prepareRecyclerViewDataFor5() {
        museumHScrollModelList.clear();
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
                getResources().getString(R.string.sidemenu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }


    public void loadAdsToSlider(ArrayList<Page> adsImages) {
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (adsImages.size() > 1) {
            glideLoader = new GlideLoaderForMuseum();
            if (appLanguage == english) {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .scrollDurationFactor(2)
                        .internal(3000)
                        .isLoop(true)
                        .isAutoScroll(true)
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
                        .internal(4000)
                        .scrollDurationFactor(2)
                        .isLoop(true)
                        .isAutoScroll(true)
                        .onPageClickListener(this)
                        .direction(LEFT)
                        .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                        .build();
                animCircleIndicator.init(configuration);
                animCircleIndicator.notifyDataChange(adsImages);
            }

        } else {
            glideLoader = new GlideLoaderForMuseum();
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

    public void getSliderImagesfromAPI() {
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        if (appLanguage == english) {
            language = "en";
        } else {
            language = "ar";
        }
        Call<ArrayList<MuseumAboutModel>> call = apiService.getMuseumAboutDetails(language, museumId);
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MuseumAboutModel>> call, Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        museumAboutModels.addAll(response.body());
                        if (museumAboutModels.size() != 0) {
                            if (museumAboutModels.get(0).getImageList().size() > 0) {
                                int imageSliderSize;
                                ArrayList<String> sliderList = new ArrayList<>();
                                if (museumAboutModels.get(0).getImageList().size() >= 4) {
                                    imageSliderSize = 4;
                                } else {
                                    imageSliderSize = museumAboutModels.get(0).getImageList().size();
                                }

                                for (int i = 1; i < imageSliderSize; i++) {
                                    sliderList.add(i - 1, museumAboutModels.get(0).getImageList().get(i));
                                }

                                setSliderImages(sliderList);
                                sliderPlaceholderImage.setVisibility(View.GONE);
                                animCircleIndicator.setVisibility(View.VISIBLE);
                            } else {
                                sliderPlaceholderImage.setVisibility(View.VISIBLE);
                                animCircleIndicator.setVisibility(View.GONE);
                            }

                        } else {
                            sliderPlaceholderImage.setVisibility(View.VISIBLE);
                            animCircleIndicator.setVisibility(View.GONE);
                        }

                    } else {
                        sliderPlaceholderImage.setVisibility(View.VISIBLE);
                        animCircleIndicator.setVisibility(View.GONE);
                    }

                } else {
                    sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    animCircleIndicator.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MuseumAboutModel>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                sliderPlaceholderImage.setVisibility(View.VISIBLE);
                animCircleIndicator.setVisibility(View.GONE);
            }
        });


    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width / 5;
        return width;
    }
}
