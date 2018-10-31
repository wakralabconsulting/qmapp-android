package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class SelfGuidedStartPageActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, OnPageClickListener {
    ImageView playButton;
    TextView museumTitle, museumDesc;
    Button startBtn, retryButton;
    private Animation zoomOutAnimation;
    private Intent intent;
    Util util;
    String museumId, tourName;
    private IndicatorConfiguration configuration;
    private InfiniteIndicator animCircleIndicator;
    private GlideLoaderForTourGuide glideLoader;
    SharedPreferences qmPreferences;
    int appLanguage;
    private String language;
    ArrayList<SelfGuideStarterModel> selfGuideStarterModels = new ArrayList<>();
    ArrayList<Page> ads;
    ImageView sliderPlaceholderImage;
    TextView noResultFoundTxt;
    int selfGuideStartRowCount;
    QMDatabase qmDatabase;
    TourGuideStartPageEnglish tourGuideStartPageEnglish;
    TourGuideStartPageArabic tourGuideStartPageArabic;
    private SelfGuideStarterModel selfGuideStarterModel;
    private String tourId;
    private ProgressBar progressLoader;
    LinearLayout retryLayout;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_guided_starter);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        playButton = (ImageView) findViewById(R.id.playBtn);
        util = new Util();
        qmDatabase = QMDatabase.getInstance(SelfGuidedStartPageActivity.this);
        museumTitle = (TextView) findViewById(R.id.museum_tittle);
        sliderPlaceholderImage = (ImageView) findViewById(R.id.ads_place_holder);
        museumDesc = (TextView) findViewById(R.id.museum_desc);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);
        mainLayout = findViewById(R.id.main_content);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        animCircleIndicator = (InfiniteIndicator) findViewById(R.id.main_indicator_default_circle);
        startBtn = (Button) findViewById(R.id.start_btn);
        progressLoader = findViewById(R.id.progress_bar_loading);
        intent = getIntent();
        museumId = intent.getStringExtra("MUSEUM_ID");
        tourId = intent.getStringExtra("TOUR_ID");
        tourName = intent.getStringExtra("TOUR_NAME");
        museumTitle.setText(tourName);
        if (util.isNetworkAvailable(SelfGuidedStartPageActivity.this))
            getSliderImagesandDdetailsfromAPI();
        else
            getSliderImagesandDdetailsfromDatabase(tourId, appLanguage);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);

        intent = getIntent();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelfGuidedStartPageActivity.this, ObjectPreviewActivity.class);
                i.putExtra("TOUR_ID", tourId);
                startActivity(i);
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSliderImagesandDdetailsfromAPI();
                progressLoader.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            }
        });
        retryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        retryButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
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


    public void getSliderImagesandDdetailsfromAPI() {
        progressLoader.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        sliderPlaceholderImage.setVisibility(View.VISIBLE);
        Call<ArrayList<SelfGuideStarterModel>> call = apiService.getSelfGuideStarterPageDetails(language, museumId);
        call.enqueue(new Callback<ArrayList<SelfGuideStarterModel>>() {
            @Override
            public void onResponse(Call<ArrayList<SelfGuideStarterModel>> call, Response<ArrayList<SelfGuideStarterModel>> response) {
                progressLoader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        selfGuideStarterModels.addAll(response.body());
                        mainLayout.setVisibility(View.VISIBLE);
                        retryLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.GONE);
                        if (selfGuideStarterModels.size() != 0) {
                            for (int i = 0; i < selfGuideStarterModels.size(); i++) {
                                //Temporary
                                if (selfGuideStarterModels.get(i).getNid().equals(tourId))
                                    selfGuideStarterModel = selfGuideStarterModels.get(i);
                            }
                            if (selfGuideStarterModel != null) {
                                museumDesc.setText(selfGuideStarterModel.getDescription());
                                if (selfGuideStarterModel.getImageList().size() > 0) {
                                    ArrayList<String> sliderList = new ArrayList<>();
                                    for (int i = 0; i < selfGuideStarterModel.getImageList().size(); i++) {
                                        sliderList.add(i, selfGuideStarterModel.getImageList().get(i));
                                    }
                                    setSliderImages(sliderList);
                                    new SelfGuideStartRowCount(SelfGuidedStartPageActivity.this, language).execute();
                                    sliderPlaceholderImage.setVisibility(View.GONE);
                                    animCircleIndicator.setVisibility(View.VISIBLE);
                                } else {
                                    sliderPlaceholderImage.setVisibility(View.VISIBLE);
                                    animCircleIndicator.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            sliderPlaceholderImage.setVisibility(View.VISIBLE);
                            animCircleIndicator.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.GONE);
                            noResultFoundTxt.setVisibility(View.VISIBLE);
                        }

                    } else {
                        sliderPlaceholderImage.setVisibility(View.VISIBLE);
                        animCircleIndicator.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.GONE);
                        retryLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    sliderPlaceholderImage.setVisibility(View.VISIBLE);
                    animCircleIndicator.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SelfGuideStarterModel>> call, Throwable t) {
                animCircleIndicator.setVisibility(View.GONE);
                mainLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressLoader.setVisibility(View.GONE);
            }
        });
    }

    public void loadAdsToSlider(ArrayList<Page> adsImages) {
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (adsImages.size() > 1) {
            glideLoader = new GlideLoaderForTourGuide();
            if (appLanguage == 1) {
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

    public void getSliderImagesandDdetailsfromDatabase(String id, int language) {
        if (language == 1) {
            new RetriveSelfGuideStartDataEnglish(SelfGuidedStartPageActivity.this, language, id).execute();
        } else {
            new RetriveSelfGuideStartDataArabic(SelfGuidedStartPageActivity.this, language, id).execute();
        }
    }

    public void setSliderImages(ArrayList<String> sliderImageList) {
        ads = new ArrayList<>();
        for (int i = 0; i < sliderImageList.size(); i++) {
            ads.add(new Page("", sliderImageList.get(i),
                    null));
        }
        loadAdsToSlider(ads);
    }


    public class SelfGuideStartRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        String language;


        SelfGuideStartRowCount(SelfGuidedStartPageActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            selfGuideStartRowCount = integer;
            if (selfGuideStartRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckTourGuideStartPageDBRowExist(SelfGuidedStartPageActivity.this, language).execute();
            } else {
                //create databse
                new InsertTourGuideStartPageDatabaseTask(SelfGuidedStartPageActivity.this, tourGuideStartPageEnglish,
                        tourGuideStartPageArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getTourGuideStartPageDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getTourGuideStartPageDao().getNumberOfRowsArabic();
            }


        }
    }

    public class CheckTourGuideStartPageDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        private TourGuideStartPageEnglish tourGuideStartPageEnglish;
        private TourGuideStartPageArabic tourGuideStartPageArabic;
        String language;

        CheckTourGuideStartPageDBRowExist(SelfGuidedStartPageActivity context, String apiLanguage) {
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
            if (selfGuideStarterModels.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < selfGuideStarterModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTourGuideStartPageDao().checkEnglishIdExist(
                                selfGuideStarterModels.get(i).getNid());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateTourGuideStartPageDetailTable(SelfGuidedStartPageActivity.this,
                                    language, i).execute();

                        } else {
                            tourGuideStartPageEnglish = new TourGuideStartPageEnglish(selfGuideStarterModels.get(i).getTitle(),
                                    selfGuideStarterModels.get(i).getNid(),
                                    selfGuideStarterModels.get(i).getMuseumsEntity(),
                                    selfGuideStarterModels.get(i).getDescription());
                            activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < selfGuideStarterModels.size(); i++) {

                        int n = activityReference.get().qmDatabase.getTourGuideStartPageDao().checkArabicIdExist(
                                selfGuideStarterModels.get(i).getNid());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateTourGuideStartPageDetailTable(SelfGuidedStartPageActivity.this, language, i).execute();

                        } else {
                            tourGuideStartPageArabic = new TourGuideStartPageArabic(selfGuideStarterModels.get(i).getTitle(),
                                    selfGuideStarterModels.get(i).getNid(),
                                    selfGuideStarterModels.get(i).getMuseumsEntity(),
                                    selfGuideStarterModels.get(i).getDescription());
                            activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageArabic);

                        }
                    }
                }
            }

            return null;
        }
    }

    public class InsertTourGuideStartPageDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        private TourGuideStartPageEnglish tourGuideStartPageEnglish;
        private TourGuideStartPageArabic tourGuideStartPageArabic;
        String language;

        InsertTourGuideStartPageDatabaseTask(SelfGuidedStartPageActivity context, TourGuideStartPageEnglish tourGuideStartPageEnglish,
                                             TourGuideStartPageArabic tourGuideStartPageArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.tourGuideStartPageEnglish = tourGuideStartPageEnglish;
            this.tourGuideStartPageArabic = tourGuideStartPageArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (selfGuideStarterModels != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < selfGuideStarterModels.size(); i++) {
                        tourGuideStartPageEnglish = new TourGuideStartPageEnglish(selfGuideStarterModels.get(i).getTitle(),
                                selfGuideStarterModels.get(i).getNid(),
                                selfGuideStarterModels.get(i).getMuseumsEntity(),
                                selfGuideStarterModels.get(i).getDescription());
                        activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageEnglish);

                    }
                } else {
                    for (int i = 0; i < selfGuideStarterModels.size(); i++) {
                        tourGuideStartPageArabic = new TourGuideStartPageArabic(selfGuideStarterModels.get(i).getTitle(),
                                selfGuideStarterModels.get(i).getNid(),
                                selfGuideStarterModels.get(i).getMuseumsEntity(),
                                selfGuideStarterModels.get(i).getDescription());
                        activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageArabic);

                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public class UpdateTourGuideStartPageDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        String language;
        int position;

        UpdateTourGuideStartPageDetailTable(SelfGuidedStartPageActivity context, String apiLanguage, int p) {
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
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getTourGuideStartPageDao().updateTourGuideStartDataEnglish(
                        selfGuideStarterModels.get(position).getTitle(), selfGuideStarterModels.get(position).getDescription(),
                        selfGuideStarterModels.get(position).getMuseumsEntity(),
                        selfGuideStarterModels.get(position).getNid()
                );
            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getTourGuideStartPageDao().updateTourGuideStartDataArabic(
                        selfGuideStarterModels.get(position).getTitle(), selfGuideStarterModels.get(position).getDescription(),
                        selfGuideStarterModels.get(position).getMuseumsEntity(),
                        selfGuideStarterModels.get(position).getNid()
                );

            }
            return null;
        }
    }

    public class RetriveSelfGuideStartDataEnglish extends AsyncTask<Void, Void, TourGuideStartPageEnglish> {
        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        int language;
        String tourId;

        RetriveSelfGuideStartDataEnglish(SelfGuidedStartPageActivity context, int appLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.tourId = tourId;
        }

        @Override
        protected TourGuideStartPageEnglish doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourGuideStartPageDao().
                    getTourGuideStartPageDataEnglish(tourId);
        }

        @Override
        protected void onPostExecute(TourGuideStartPageEnglish tourGuideStartPageEnglish) {
            if (tourGuideStartPageEnglish != null) {
                retryLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                museumDesc.setText(tourGuideStartPageEnglish.getDescription());
            } else {
                retryLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }

        }
    }

    public class RetriveSelfGuideStartDataArabic extends AsyncTask<Void, Void, TourGuideStartPageArabic> {
        private WeakReference<SelfGuidedStartPageActivity> activityReference;
        int language;
        String tourId;

        RetriveSelfGuideStartDataArabic(SelfGuidedStartPageActivity context, int appLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.tourId = tourId;
        }

        @Override
        protected TourGuideStartPageArabic doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourGuideStartPageDao().
                    getTourGuideStartPageDataArabic(tourId);
        }

        @Override
        protected void onPostExecute(TourGuideStartPageArabic tourGuideStartPageArabic) {
            if (tourGuideStartPageArabic != null) {
                retryLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                museumDesc.setText(tourGuideStartPageArabic.getDescription());
            } else {
                retryLayout.setVisibility(View.VISIBLE);
            }

        }
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
