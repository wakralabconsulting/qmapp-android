package com.qatarmuseums.qatarmuseumsapp.museum;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableEnglish;
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
    private BroadcastReceiver mNotificationBroadcastReceiver;
    private int badgeCount;
    private boolean isBanner;
    private MuseumAboutTableEnglish museumAboutTableEnglish;
    private MuseumAboutTableArabic museumAboutTableArabic;
    private QMDatabase qmDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        ButterKnife.bind(this);
        util = new Util();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        intent = getIntent();
        sliderImageTitle.setText(intent.getStringExtra("MUSEUMTITLE"));
        museumId = intent.getStringExtra("MUSEUM_ID");
        isBanner = intent.getBooleanExtra("IS_BANNER", false);
        if (isBanner)
            setToolbarForMuseumLaunchy();
        else
            setToolbarForMuseumActivity();
        animCircleIndicator = (InfiniteIndicator) findViewById(R.id.main_indicator_default_circle);
        sliderPlaceholderImage = (ImageView) findViewById(R.id.ads_place_holder);
        qmDatabase = QMDatabase.getInstance(MuseumActivity.this);
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
                            .getLayoutManager()).findFirstCompletelyVisibleItemPosition();

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
                    if (museumId.equals("63") || museumId.equals("96"))
                        recyclerviewLayoutManager.scrollToPosition(5);
                    else {
                        recyclerviewLayoutManager.smoothScrollToPosition(recyclerView, null, 4);
                    }

                }
            });
            scrollBarPreviousIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recyclerviewLayoutManager.smoothScrollToPosition(recyclerView, null, 0);

                }
            });
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

    }

    public void getSliderImagesfromAPI(Boolean noDataInDB) {
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<MuseumAboutModel>> call;
        if (appLanguage == english) {
            language = "en";
        } else {
            language = "ar";
        }
        if (isBanner)
            call = apiService.getLaunchMuseumAboutDetails(language, museumId);
        else
            call = apiService.getMuseumAboutDetails(language, museumId);
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MuseumAboutModel>> call, Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        museumAboutModels.addAll(response.body());
                        if (museumAboutModels.size() != 0) {
                            if (museumAboutModels.get(0).getImageList().size() > 0) {
                                if (noDataInDB) {
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
                                }
                            } else {
                                sliderPlaceholderImage.setVisibility(View.VISIBLE);
                                animCircleIndicator.setVisibility(View.GONE);
                            }

                        } else {
                            sliderPlaceholderImage.setVisibility(View.VISIBLE);
                            animCircleIndicator.setVisibility(View.GONE);
                        }
                        new MuseumAboutRowCount(MuseumActivity.this, language).execute();

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
        if (appLanguage == english) {
            new RetriveMuseumAboutDataEnglish(MuseumActivity.this, museumId).execute();
        } else {
            new RetriveMuseumAboutDataArabic(MuseumActivity.this, museumId).execute();
        }
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
            Integer museumAboutRowCount = integer;
            if (museumAboutRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumAboutDBRowExist(activityReference.get(), language).execute();
            } else {
                //create databse
                new InsertMuseumAboutDatabaseTask(activityReference.get(), activityReference.get().museumAboutTableEnglish,
                        activityReference.get().museumAboutTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsArabic();
            }


        }
    }

    public static class CheckMuseumAboutDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<MuseumActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
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
            Convertor converters = new Convertor();
            if (activityReference.get().museumAboutModels.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumAboutDetailTable(activityReference.get(), language, i).execute();

                        } else {
                            museumAboutTableEnglish = new MuseumAboutTableEnglish(
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
                                    activityReference.get().museumAboutModels.get(i).getEventDate());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumAboutDetailTable(activityReference.get(), language, i).execute();

                        } else {
                            museumAboutTableArabic = new MuseumAboutTableArabic(
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
                                    activityReference.get().museumAboutModels.get(i).getEventDate());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                        }
                    }
                }
            }

            return null;
        }
    }

    public static class InsertMuseumAboutDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<MuseumActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
        String language;

        InsertMuseumAboutDatabaseTask(MuseumActivity context, MuseumAboutTableEnglish museumAboutTableEnglish,
                                      MuseumAboutTableArabic museumAboutTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.museumAboutTableEnglish = museumAboutTableEnglish;
            this.museumAboutTableArabic = museumAboutTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().museumAboutModels != null) {
                Convertor converters = new Convertor();
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                        museumAboutTableEnglish = new MuseumAboutTableEnglish(
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
                                activityReference.get().museumAboutModels.get(i).getEventDate());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                    }
                } else {
                    for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                        museumAboutTableArabic = new MuseumAboutTableArabic(
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
                                activityReference.get().museumAboutModels.get(i).getEventDate());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                    }
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
            Convertor converters = new Convertor();
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataEnglish(
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
                        Long.parseLong(activityReference.get().museumAboutModels.get(position).getMuseumId()));
            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataArabic(
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
                        Long.parseLong(activityReference.get().museumAboutModels.get(position).getMuseumId()));

            }
            return null;
        }
    }

    public static class RetriveMuseumAboutDataEnglish extends AsyncTask<Void, Void, MuseumAboutTableEnglish> {
        private WeakReference<MuseumActivity> activityReference;
        String museumId;

        RetriveMuseumAboutDataEnglish(MuseumActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            this.museumId = museumId;
        }

        @Override
        protected MuseumAboutTableEnglish doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getMuseumAboutDataEnglish(Integer.parseInt(museumId));
        }

        @Override
        protected void onPostExecute(MuseumAboutTableEnglish museumAboutTableEnglish) {
            Convertor converters = new Convertor();
            if (museumAboutTableEnglish != null) {
                if (converters.fromString(museumAboutTableEnglish.getMuseum_image()) != null &&
                        converters.fromString(museumAboutTableEnglish.getMuseum_image()).size() > 0) {
                    int imageSliderSize;
                    ArrayList<String> sliderList = new ArrayList<>();
                    if (converters.fromString(museumAboutTableEnglish.getMuseum_image()).size() >= 4) {
                        imageSliderSize = 4;
                    } else {
                        imageSliderSize = converters.fromString(museumAboutTableEnglish.getMuseum_image()).size();
                    }

                    for (int i = 1; i < imageSliderSize; i++) {
                        sliderList.add(i - 1, converters.fromString(museumAboutTableEnglish.getMuseum_image()).get(i));
                    }

                    activityReference.get().setSliderImages(sliderList);
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.GONE);
                    activityReference.get().animCircleIndicator.setVisibility(View.VISIBLE);
                } else {
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    activityReference.get().animCircleIndicator.setVisibility(View.GONE);
                }
                activityReference.get().getSliderImagesfromAPI(false);
            } else {
                activityReference.get().getSliderImagesfromAPI(true);
                activityReference.get().sliderPlaceholderImage.setVisibility(View.VISIBLE);
                activityReference.get().animCircleIndicator.setVisibility(View.GONE);
            }

        }
    }

    public static class RetriveMuseumAboutDataArabic extends AsyncTask<Void, Void, MuseumAboutTableArabic> {
        private WeakReference<MuseumActivity> activityReference;
        String museumId;

        RetriveMuseumAboutDataArabic(MuseumActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            this.museumId = museumId;
        }

        @Override
        protected MuseumAboutTableArabic doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getMuseumAboutDataArabic(Integer.parseInt(museumId));
        }

        @Override
        protected void onPostExecute(MuseumAboutTableArabic museumAboutTableArabic) {
            if (museumAboutTableArabic != null) {
                Convertor converters = new Convertor();
                if (converters.fromString(museumAboutTableArabic.getMuseum_image()) != null &&
                        converters.fromString(museumAboutTableArabic.getMuseum_image()).size() > 0) {
                    int imageSliderSize;
                    ArrayList<String> sliderList = new ArrayList<>();
                    if (converters.fromString(museumAboutTableArabic.getMuseum_image()).size() >= 4) {
                        imageSliderSize = 4;
                    } else {
                        imageSliderSize = converters.fromString(museumAboutTableArabic.getMuseum_image()).size();
                    }

                    for (int i = 1; i < imageSliderSize; i++) {
                        sliderList.add(i - 1, converters.fromString(museumAboutTableArabic.getMuseum_image()).get(i));
                    }

                    activityReference.get().setSliderImages(sliderList);
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.GONE);
                    activityReference.get().animCircleIndicator.setVisibility(View.VISIBLE);
                } else {
                    activityReference.get().sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    activityReference.get().animCircleIndicator.setVisibility(View.GONE);
                }

            } else {
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
