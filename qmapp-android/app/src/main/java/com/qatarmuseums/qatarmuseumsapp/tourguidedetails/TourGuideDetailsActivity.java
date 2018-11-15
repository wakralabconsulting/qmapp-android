package com.qatarmuseums.qatarmuseumsapp.tourguidedetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguide.TourGuideList;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuideStarterModel;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuidedStartPageActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageArabic;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourGuideDetailsActivity extends AppCompatActivity {
    TextView tourguideMainTitle, tourguideSubTitle, tourguideMainDesc, tourguideSubDesc;
    LinearLayout exploreLayout, tourguideSubtitleLayout, retryLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    ImageView backButton;
    private Animation zoomOutAnimation, exploreZoomOutAnimation;
    private TourGuideDetailsAdapter mAdapter;
    private ArrayList<SelfGuideStarterModel> tourGuideList = new ArrayList<>();
    TourGuideList tourguideObject;
    private Intent navigationIntent;
    private String museumId;
    Button retryButton;
    private ProgressBar progressBar;
    private NestedScrollView scrollviewContainer;
    private RelativeLayout noResultFoundLayout;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    int selfGuideStartRowCount;
    QMDatabase qmDatabase;
    TourGuideStartPageEnglish tourGuideStartPageEnglish;
    TourGuideStartPageArabic tourGuideStartPageArabic;
    private Convertor converters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        museumId = intent.getStringExtra("ID");

        tourguideMainTitle = (TextView) findViewById(R.id.tourguide_tittle);
        tourguideSubTitle = (TextView) findViewById(R.id.tourguide_subtittle);
        tourguideMainDesc = (TextView) findViewById(R.id.tourguide_title_desc);
        tourguideSubDesc = (TextView) findViewById(R.id.tourguide_subtitle_desc);
        exploreLayout = (LinearLayout) findViewById(R.id.explore_layout);
        tourguideSubtitleLayout = (LinearLayout) findViewById(R.id.tourguide_subtitle_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tourguide_recycler_view);
        backButton = (ImageView) findViewById(R.id.toolbar_close);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        progressBar = findViewById(R.id.progressBarLoading);
        noResultFoundLayout = findViewById(R.id.no_result_layout);
        scrollviewContainer = findViewById(R.id.scroll_view_container);
        qmDatabase = QMDatabase.getInstance(TourGuideDetailsActivity.this);

        mAdapter = new TourGuideDetailsAdapter(this, tourGuideList, "");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        if (tourGuideList.get(position).getTitle().equals(getString(R.string.coming_soon_txt))) {
                            new Util().showComingSoonDialog(TourGuideDetailsActivity.this,
                                    R.string.coming_soon_content_map);
                        } else {
                            navigationIntent = new Intent(TourGuideDetailsActivity.this,
                                    SelfGuidedStartPageActivity.class);
                            navigationIntent.putExtra("MUSEUM_ID", tourGuideList.get(position).getMuseumsEntity());
                            navigationIntent.putExtra("TOUR_NAME", tourGuideList.get(position).getTitle());
                            navigationIntent.putExtra("TOUR_ID", tourGuideList.get(position).getNid());
                            navigationIntent.putExtra("DESCRIPTION", tourGuideList.get(position).getDescription());
                            navigationIntent.putExtra("IMAGES", tourGuideList.get(position).getImageList());
                            navigationIntent.putExtra("TITLE", tourguideMainTitle.getText().toString());
                            startActivity(navigationIntent);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        if (museumId.equals("63") || museumId.equals("96")) {
            tourguideMainTitle.setText(getString(R.string.mia_tour_guide));
            tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));
            tourguideSubTitle.setText(getString(R.string.tourguide_title));
            tourguideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
            tourGuideList.clear();
        }
        backButton.setOnClickListener(v -> onBackPressed());
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        exploreZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        retryButton.setOnClickListener(v -> {
            getTourGuideDetailsPageAPIData(appLanguage);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });

        exploreLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    exploreLayout.startAnimation(exploreZoomOutAnimation);
                    break;
            }
            return false;
        });

        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        backButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        exploreZoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(TourGuideDetailsActivity.this, FloorMapActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        converters = new Convertor();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (new Util().isNetworkAvailable(this))
            getTourGuideDetailsPageAPIData(appLanguage);
        else
            getSliderImagesandDdetailsfromDatabase(appLanguage, museumId);
    }

    public void getTourGuideDetailsPageAPIData(int lan) {
        progressBar.setVisibility(View.VISIBLE);
        int appLanguage = lan;
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<SelfGuideStarterModel>> call = apiService.getSelfGuideStarterPageDetails(language, museumId);
        call.enqueue(new Callback<ArrayList<SelfGuideStarterModel>>() {
            @Override
            public void onResponse(Call<ArrayList<SelfGuideStarterModel>> call, Response<ArrayList<SelfGuideStarterModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        scrollviewContainer.setVisibility(View.VISIBLE);
                        tourGuideList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        new SelfGuideStartRowCount(TourGuideDetailsActivity.this, language).execute();
                    } else {
                        scrollviewContainer.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    scrollviewContainer.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<SelfGuideStarterModel>> call, Throwable t) {
                scrollviewContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public class SelfGuideStartRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<TourGuideDetailsActivity> activityReference;
        String language;


        SelfGuideStartRowCount(TourGuideDetailsActivity context, String apiLanguage) {
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
                new CheckTourGuideStartPageDBRowExist(TourGuideDetailsActivity.this, language).execute();
            } else {
                new InsertTourGuideStartPageDatabaseTask(TourGuideDetailsActivity.this, tourGuideStartPageEnglish,
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

        private WeakReference<TourGuideDetailsActivity> activityReference;
        private TourGuideStartPageEnglish tourGuideStartPageEnglish;
        private TourGuideStartPageArabic tourGuideStartPageArabic;
        String language;

        CheckTourGuideStartPageDBRowExist(TourGuideDetailsActivity context, String apiLanguage) {
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
            if (tourGuideList.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTourGuideStartPageDao().checkEnglishIdExist(
                                tourGuideList.get(i).getNid());
                        if (n > 0) {
                            new UpdateTourGuideStartPageDetailTable(TourGuideDetailsActivity.this,
                                    language, i).execute();

                        } else {
                            tourGuideStartPageEnglish = new TourGuideStartPageEnglish(
                                    tourGuideList.get(i).getTitle(),
                                    tourGuideList.get(i).getNid(),
                                    tourGuideList.get(i).getMuseumsEntity(),
                                    tourGuideList.get(i).getDescription(),
                                    converters.fromArrayList(tourGuideList.get(i).getImageList())
                            );
                            activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < tourGuideList.size(); i++) {

                        int n = activityReference.get().qmDatabase.getTourGuideStartPageDao().checkArabicIdExist(
                                tourGuideList.get(i).getNid());
                        if (n > 0) {
                            new UpdateTourGuideStartPageDetailTable(TourGuideDetailsActivity.this, language, i).execute();

                        } else {
                            tourGuideStartPageArabic = new TourGuideStartPageArabic(
                                    tourGuideList.get(i).getTitle(),
                                    tourGuideList.get(i).getNid(),
                                    tourGuideList.get(i).getMuseumsEntity(),
                                    tourGuideList.get(i).getDescription(),
                                    converters.fromArrayList(tourGuideList.get(i).getImageList()));
                            activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageArabic);

                        }
                    }
                }
            }

            return null;
        }
    }

    public class InsertTourGuideStartPageDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<TourGuideDetailsActivity> activityReference;
        private TourGuideStartPageEnglish tourGuideStartPageEnglish;
        private TourGuideStartPageArabic tourGuideStartPageArabic;
        String language;

        InsertTourGuideStartPageDatabaseTask(TourGuideDetailsActivity context, TourGuideStartPageEnglish tourGuideStartPageEnglish,
                                             TourGuideStartPageArabic tourGuideStartPageArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.tourGuideStartPageEnglish = tourGuideStartPageEnglish;
            this.tourGuideStartPageArabic = tourGuideStartPageArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (tourGuideList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        tourGuideStartPageEnglish = new TourGuideStartPageEnglish(tourGuideList.get(i).getTitle(),
                                tourGuideList.get(i).getNid(),
                                tourGuideList.get(i).getMuseumsEntity(),
                                tourGuideList.get(i).getDescription(),
                                converters.fromArrayList(tourGuideList.get(i).getImageList()));
                        activityReference.get().qmDatabase.getTourGuideStartPageDao().insert(tourGuideStartPageEnglish);

                    }
                } else {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        tourGuideStartPageArabic = new TourGuideStartPageArabic(tourGuideList.get(i).getTitle(),
                                tourGuideList.get(i).getNid(),
                                tourGuideList.get(i).getMuseumsEntity(),
                                tourGuideList.get(i).getDescription(),
                                converters.fromArrayList(tourGuideList.get(i).getImageList()));
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

        private WeakReference<TourGuideDetailsActivity> activityReference;
        String language;
        int position;

        UpdateTourGuideStartPageDetailTable(TourGuideDetailsActivity context, String apiLanguage, int p) {
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
                activityReference.get().qmDatabase.getTourGuideStartPageDao().updateTourGuideStartDataEnglish(
                        tourGuideList.get(position).getTitle(),
                        tourGuideList.get(position).getDescription(),
                        tourGuideList.get(position).getMuseumsEntity(),
                        converters.fromArrayList(tourGuideList.get(position).getImageList()),
                        tourGuideList.get(position).getNid()
                );
            } else {
                activityReference.get().qmDatabase.getTourGuideStartPageDao().updateTourGuideStartDataArabic(
                        tourGuideList.get(position).getTitle(),
                        tourGuideList.get(position).getDescription(),
                        tourGuideList.get(position).getMuseumsEntity(),
                        converters.fromArrayList(tourGuideList.get(position).getImageList()),
                        tourGuideList.get(position).getNid()
                );

            }
            return null;
        }
    }

    public class RetriveTourGuideDetailsDataEnglish extends AsyncTask<Void, Void, List<TourGuideStartPageEnglish>> {
        private WeakReference<TourGuideDetailsActivity> activityReference;
        int language;
        String museumId;

        RetriveTourGuideDetailsDataEnglish(TourGuideDetailsActivity context, int appLanguage,
                                           String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.museumId = museumId;
        }

        @Override
        protected List<TourGuideStartPageEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourGuideStartPageDao().
                    getTourGuideDetailsEnglish(museumId);
        }

        @Override
        protected void onPostExecute(List<TourGuideStartPageEnglish> tourGuideStartPageEnglish) {
            if (tourGuideStartPageEnglish != null && tourGuideStartPageEnglish.size() > 0) {
                retryLayout.setVisibility(View.GONE);
                scrollviewContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < tourGuideStartPageEnglish.size(); i++) {
                    SelfGuideStarterModel model = new SelfGuideStarterModel(
                            tourGuideStartPageEnglish.get(i).getTitle(),
                            tourGuideStartPageEnglish.get(i).getDescription(),
                            converters.fromString(tourGuideStartPageEnglish.get(i).getImages()),
                            tourGuideStartPageEnglish.get(i).getMuseum_entity());
                    tourGuideList.add(model);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                retryLayout.setVisibility(View.VISIBLE);
                scrollviewContainer.setVisibility(View.GONE);
            }

        }
    }

    public class RetriveTourGuideDetailsDataArabic extends AsyncTask<Void, Void, List<TourGuideStartPageArabic>> {
        private WeakReference<TourGuideDetailsActivity> activityReference;
        int language;
        String museumId;

        RetriveTourGuideDetailsDataArabic(TourGuideDetailsActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.museumId = museumId;
        }

        @Override
        protected List<TourGuideStartPageArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourGuideStartPageDao().
                    getTourGuideDetailsArabic(museumId);
        }

        @Override
        protected void onPostExecute(List<TourGuideStartPageArabic> tourGuideStartPageArabic) {
            if (tourGuideStartPageArabic != null && tourGuideStartPageArabic.size() > 0) {
                retryLayout.setVisibility(View.GONE);
                scrollviewContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < tourGuideStartPageArabic.size(); i++) {
                    SelfGuideStarterModel model = new SelfGuideStarterModel(
                            tourGuideStartPageArabic.get(i).getTitle(),
                            tourGuideStartPageArabic.get(i).getDescription(),
                            converters.fromString(tourGuideStartPageArabic.get(i).getImages()),
                            tourGuideStartPageArabic.get(i).getMuseum_entity());
                    tourGuideList.add(model);
                }
                mAdapter.notifyDataSetChanged();

            } else {
                retryLayout.setVisibility(View.VISIBLE);
                scrollviewContainer.setVisibility(View.GONE);

            }

        }
    }

    public void getSliderImagesandDdetailsfromDatabase(int language, String museumId) {
        if (language == 1) {
            new RetriveTourGuideDetailsDataEnglish(TourGuideDetailsActivity.this, language, museumId).execute();
        } else {
            new RetriveTourGuideDetailsDataArabic(TourGuideDetailsActivity.this, language, museumId).execute();
        }
    }

}
