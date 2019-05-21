package com.qatarmuseums.qatarmuseumsapp.museum;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.Converter;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTable;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import timber.log.Timber;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class MuseumActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, OnPageClickListener {
    private List<MuseumHScrollModel> museumHScrollModelList = new ArrayList<>();
    private MuseumHorizontalScrollViewAdapter museumHorizontalScrollViewAdapter;
    @BindView(R.id.horizontal_scrol_previous_icon)
    ImageView scrollBarPreviousIcon;
    LinearLayoutManager recyclerViewLayoutManager;
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
    ArrayList<Page> ads;
    Intent intent;
    SharedPreferences qmPreferences;
    ImageView sliderPlaceholderImage;
    Util util;
    ArrayList<MuseumAboutModel> museumAboutModels = new ArrayList<>();
    private String language;
    private String museumId;
    private BroadcastReceiver mNotificationBroadcastReceiver;
    private int badgeCount;
    private boolean isBanner;
    private MuseumAboutTable museumAboutTable;
    private QMDatabase qmDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        util = new Util();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = LocaleManager.getLanguage(this);
        intent = getIntent();
        sliderImageTitle.setText(intent.getStringExtra("MUSEUMTITLE"));
        museumId = intent.getStringExtra("MUSEUM_ID");
        isBanner = intent.getBooleanExtra("IS_BANNER", false);
        if (isBanner)
            setToolbarForMuseumLaunch();
        else
            setToolbarForMuseumActivity();
        animCircleIndicator = findViewById(R.id.main_indicator_default_circle);
        sliderPlaceholderImage = findViewById(R.id.ads_place_holder);
        qmDatabase = QMDatabase.getInstance(MuseumActivity.this);
        museumHorizontalScrollViewAdapter = new MuseumHorizontalScrollViewAdapter(this,
                museumHScrollModelList, intent.getStringExtra("MUSEUMTITLE"), museumId, getScreenWidth());
        animCircleIndicator.setVisibility(View.GONE);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            recyclerViewLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewLayoutManager.setStackFromEnd(false);
        } else {
            recyclerViewLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewLayoutManager.setStackFromEnd(true);
            recyclerViewLayoutManager.scrollToPosition(0);
        }

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(museumHorizontalScrollViewAdapter);

        if (museumId.equals("63") || museumId.equals("96") || museumId.equals("61") ||
                museumId.equals("635") || museumId.equals("66") || museumId.equals("638")) {

            switch (museumId) {
                case "63":
                case "96":
                    prepareRecyclerViewDataForMIA();
                    break;
                case "66":
                case "638":
                    prepareRecyclerViewDataForNMoQ();
                    break;
                default:
                    prepareRecyclerViewDataFor5();
                    break;
            }
            showRightArrow();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastCompleteVisibleItemPosition;
                    int firstVisibleItemPosition;
                    lastCompleteVisibleItemPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    firstVisibleItemPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                    if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
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

            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
                snapHelperStart.attachToRecyclerView(recyclerView);
            } else {
                SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.END);
                snapHelperStart.attachToRecyclerView(recyclerView);
            }

            scrollBarNextIconLayout.setOnClickListener(view -> {
                Timber.i("Scroll to Next button clicked");
                switch (museumId) {
                    case "63":
                    case "96":
                        recyclerViewLayoutManager.scrollToPosition(5);
                        break;
//                    case "66":
//                    case "638":
//                        recyclerViewLayoutManager.scrollToPosition(6);
//                        break;
                    default:
                        recyclerViewLayoutManager.smoothScrollToPosition(recyclerView, null, 4);
                        break;
                }

            });
            scrollBarPreviousIconLayout.setOnClickListener(view -> recyclerViewLayoutManager.smoothScrollToPosition(recyclerView, null, 0));
        } else if (isBanner)
            prepareRecyclerViewDataForLaunch();
        else
            prepareRecyclerViewData();
        getMuseumSliderImageFromDatabase();

        mNotificationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
                    if (badgeCount > 0) {
                        setBadge(badgeCount);
                    }
                }
            }
        };
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
        scrollBarPreviousIcon.setVisibility(View.GONE);
        scrollBarPreviousIconLayout.setVisibility(View.INVISIBLE);
        scrollBarNextIconLayout.setVisibility(View.VISIBLE);
        scrollBarNextIcon.setVisibility(View.VISIBLE);
    }

    public void prepareRecyclerViewData() {
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

    public void prepareRecyclerViewDataForLaunch() {
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_launch), R.drawable.about_launch);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_tours), R.drawable.tours_launch);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_travel), R.drawable.travel_launch);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_discussion), R.drawable.discussion_launch);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

    public void prepareRecyclerViewDataForMIA() {
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_tour_guide_text), R.drawable.audio_circle);

        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_parks_text), R.drawable.park_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

    public void prepareRecyclerViewDataForNMoQ() {
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_launch);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.facilities_txt), R.drawable.facilities_icon);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        // Commented for temporary due to no data

//        model = new MuseumHScrollModel(this,
//                getResources().getString(R.string.experience_txt), R.drawable.experience_icon);
//        museumHScrollModelList.add(model);
//
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_tour_guide_text), R.drawable.audio_circle);
        museumHScrollModelList.add(model);
//
//        model = new MuseumHScrollModel(this,
//                getResources().getString(R.string.side_menu_events_text), R.drawable.events_icon);
//        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_parks_text), R.drawable.park_black);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

    public void prepareRecyclerViewDataFor5() {
        museumHScrollModelList.clear();
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_tour_guide_text), R.drawable.audio_circle);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.side_menu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }


    @SuppressLint("RtlHardcoded")
    public void loadAdsToSlider(ArrayList<Page> adsImages) {
        Timber.i("loadAdsToSlider()");
        language = LocaleManager.getLanguage(this);
        GlideLoaderForMuseum glideLoader;
        if (adsImages.size() > 1) {
            glideLoader = new GlideLoaderForMuseum();
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                configuration = new IndicatorConfiguration.Builder()
                        .imageLoader(glideLoader)
                        .isStopWhileTouch(true)
                        .onPageChangeListener(this)
                        .scrollDurationFactor(2)
                        .internal(1500)
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
                        .internal(2000)
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
                    .internal(2000)
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (configuration != null)
            animCircleIndicator.start();
        updateBadge();
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        if (isBanner) {
            mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.nmoq_landing_page), null);
        } else {
            mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.landing_page), null);
        }
    }

    public void getSliderImagesFromAPI(Boolean noDataInDB) {
        Timber.i("getSliderImagesFromAPI(language :%s)", language);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<MuseumAboutModel>> call;
        if (isBanner)
            call = apiService.getLaunchMuseumAboutDetails(language, museumId);
        else
            call = apiService.getMuseumAboutDetails(language, museumId);
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<MuseumAboutModel>> call,
                                   @NonNull Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("getSliderImagesFromAPI() - isSuccessful for museum id: %s", museumId);
                        museumAboutModels.addAll(response.body());
                        if (museumAboutModels.size() != 0) {
                            if (museumAboutModels.get(0).getImageList().size() > 0) {
                                if (noDataInDB) {
                                    int imageSliderSize;
                                    ArrayList<String> sliderList = new ArrayList<>();
                                    imageSliderSize = museumAboutModels.get(0).getImageList().size();
                                    for (int i = 0; i < imageSliderSize; i++) {
                                        sliderList.add(i, museumAboutModels.get(0).getImageList().get(i));
                                    }
                                    setSliderImages(sliderList);
                                    sliderPlaceholderImage.setVisibility(View.GONE);
                                    animCircleIndicator.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Timber.i("Have no data for museum id: %s", museumId);
                                sliderPlaceholderImage.setVisibility(View.VISIBLE);
                                animCircleIndicator.setVisibility(View.GONE);
                            }

                        } else {
                            Timber.i("Have no data for museum id: %s", museumId);
                            sliderPlaceholderImage.setVisibility(View.VISIBLE);
                            animCircleIndicator.setVisibility(View.GONE);
                        }
                        new MuseumAboutRowCount(MuseumActivity.this, language).execute();

                    } else {
                        Timber.w("Response body null");
                        sliderPlaceholderImage.setVisibility(View.VISIBLE);
                        animCircleIndicator.setVisibility(View.GONE);
                    }

                } else {
                    Timber.w("Response not successful");
                    sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    animCircleIndicator.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<MuseumAboutModel>> call, @NonNull Throwable t) {
                Timber.e("getSliderImagesFromAPI() - onFailure: %s", t.getMessage());
                if (t instanceof IOException && noDataInDB) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());
                }
                if (noDataInDB) {
                    sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    animCircleIndicator.setVisibility(View.GONE);
                }
            }
        });


    }

    public void getMuseumSliderImageFromDatabase() {
        Timber.i("getMuseumSliderImageFromDatabase(language :%s)", language);
        new RetrieveMuseumAboutData(MuseumActivity.this, museumId).execute();
    }

    public static class MuseumAboutRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<MuseumActivity> activityReference;
        String language;


        MuseumAboutRowCount(MuseumActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckMuseumAboutDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("Table have no data");
                new InsertMuseumAboutDatabaseTask(activityReference.get(), activityReference.get().museumAboutTable,
                        language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language :%s)", language);
            return activityReference.get().qmDatabase.getMuseumAboutDao()
                    .getNumberOfRows(language);
        }
    }

    public static class CheckMuseumAboutDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<MuseumActivity> activityReference;
        private MuseumAboutTable museumAboutTable;
        String language;

        CheckMuseumAboutDBRowExist(MuseumActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Converter converters = new Converter();
            if (activityReference.get().museumAboutModels.size() > 0) {
                Timber.i("checkTableWithIdExist(language :%s)", language);
                for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                    Timber.i("checkTableWithIdExist(language :%s) for id: %s", language,
                            activityReference.get().museumAboutModels.get(i).getMuseumId());
                    int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkIdExist(
                            Integer.parseInt(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().museumAboutModels.get(i).getMuseumId());
                        new UpdateMuseumAboutDetailTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting data to Table(language :%s) with id: %s",
                                language, activityReference.get().museumAboutModels.get(i).getMuseumId());
                        museumAboutTable = new MuseumAboutTable(
                                activityReference.get().museumAboutModels.get(i).getName(),
                                Long.parseLong(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                                activityReference.get().museumAboutModels.get(i).getTourGuideAvailable(),
                                "",
                                "",
                                converters.fromArrayList(activityReference.get().museumAboutModels.get(i).getImageList()),
                                activityReference.get().museumAboutModels.get(i).getContactNumber(),
                                activityReference.get().museumAboutModels.get(i).getContactEmail(),
                                activityReference.get().museumAboutModels.get(i).getLatitude(),
                                activityReference.get().museumAboutModels.get(i).getLongitude(),
                                activityReference.get().museumAboutModels.get(i).getTourGuideAvailability(),
                                activityReference.get().museumAboutModels.get(i).getSubTitle(),
                                activityReference.get().museumAboutModels.get(i).getTimingInfo(),
                                activityReference.get().museumAboutModels.get(i).getEventDate(),
                                language);
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTable);

                    }
                }
            }

            return null;
        }
    }

    public static class InsertMuseumAboutDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<MuseumActivity> activityReference;
        private MuseumAboutTable museumAboutTable;
        String language;

        InsertMuseumAboutDatabaseTask(MuseumActivity context, MuseumAboutTable museumAboutTable,
                                      String lan) {
            activityReference = new WeakReference<>(context);
            this.museumAboutTable = museumAboutTable;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().museumAboutModels != null) {
                Converter converters = new Converter();
                for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                    Timber.i("Inserting data to Table(language :%s) with id: %s",
                            language, activityReference.get().museumAboutModels.get(i).getMuseumId());
                    museumAboutTable = new MuseumAboutTable(
                            activityReference.get().museumAboutModels.get(i).getName(),
                            Long.parseLong(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                            activityReference.get().museumAboutModels.get(i).getTourGuideAvailable(),
                            "",
                            "",
                            converters.fromArrayList(activityReference.get().museumAboutModels.get(i).getImageList()),
                            activityReference.get().museumAboutModels.get(i).getContactNumber(),
                            activityReference.get().museumAboutModels.get(i).getContactEmail(),
                            activityReference.get().museumAboutModels.get(i).getLatitude(),
                            activityReference.get().museumAboutModels.get(i).getLongitude(),
                            activityReference.get().museumAboutModels.get(i).getTourGuideAvailability(),
                            activityReference.get().museumAboutModels.get(i).getSubTitle(),
                            activityReference.get().museumAboutModels.get(i).getTimingInfo(),
                            activityReference.get().museumAboutModels.get(i).getEventDate(),
                            language);
                    activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class UpdateMuseumAboutDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<MuseumActivity> activityReference;
        String language;
        int position;

        UpdateMuseumAboutDetailTable(MuseumActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Converter converters = new Converter();
            Timber.i("Updating data to Table(language :%s) with id: %s",
                    language, activityReference.get().museumAboutModels.get(position).getMuseumId());
            activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutData(
                    activityReference.get().museumAboutModels.get(position).getName(),
                    activityReference.get().museumAboutModels.get(position).getTourGuideAvailable(),
                    "",
                    "",
                    converters.fromArrayList(activityReference.get().museumAboutModels.get(position).getImageList()),
                    activityReference.get().museumAboutModels.get(position).getContactNumber(),
                    activityReference.get().museumAboutModels.get(position).getContactEmail(),
                    activityReference.get().museumAboutModels.get(position).getLatitude(),
                    activityReference.get().museumAboutModels.get(position).getLongitude(),
                    activityReference.get().museumAboutModels.get(position).getTourGuideAvailability(),
                    activityReference.get().museumAboutModels.get(position).getSubTitle(),
                    activityReference.get().museumAboutModels.get(position).getTimingInfo(),
                    Long.parseLong(activityReference.get().museumAboutModels.get(position).getMuseumId()),
                    language);
            return null;
        }
    }

    public static class RetrieveMuseumAboutData extends AsyncTask<Void, Void, MuseumAboutTable> {
        private WeakReference<MuseumActivity> activityReference;
        String museumId;

        RetrieveMuseumAboutData(MuseumActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            this.museumId = museumId;
        }

        @Override
        protected MuseumAboutTable doInBackground(Void... voids) {
            Timber.i("getMuseumAboutData(id: %s, language: %s)", museumId, activityReference.get().language);
            return activityReference.get().qmDatabase.getMuseumAboutDao().
                    getMuseumAboutData(Integer.parseInt(museumId), activityReference.get().language);
        }

        @Override
        protected void onPostExecute(MuseumAboutTable museumAboutTable) {
            Converter converters = new Converter();
            if (museumAboutTable != null) {
                if (converters.fromString(museumAboutTable.getMuseum_image()) != null &&
                        converters.fromString(museumAboutTable.getMuseum_image()).size() > 0) {
                    Timber.i("Set images from database");
                    int imageSliderSize;
                    ArrayList<String> sliderList = new ArrayList<>();
                    imageSliderSize = converters.fromString(museumAboutTable.getMuseum_image()).size();

                    for (int i = 0; i < imageSliderSize; i++) {
                        sliderList.add(i, converters.fromString(museumAboutTable.getMuseum_image()).get(i));
                    }

                    activityReference.get().setSliderImages(sliderList);
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.GONE);
                    activityReference.get().animCircleIndicator.setVisibility(View.VISIBLE);
                } else {
                    Timber.i("Have no data in database");
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    activityReference.get().animCircleIndicator.setVisibility(View.GONE);
                }
                activityReference.get().getSliderImagesFromAPI(false);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().getSliderImagesFromAPI(true);
                activityReference.get().sliderPlaceholderImage.setVisibility(View.VISIBLE);
                activityReference.get().animCircleIndicator.setVisibility(View.GONE);
            }

        }
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width / 5;
        return width;
    }
}
