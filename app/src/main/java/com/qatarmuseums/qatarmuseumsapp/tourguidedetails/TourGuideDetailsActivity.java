package com.qatarmuseums.qatarmuseumsapp.tourguidedetails;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuideStarterModel;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuidedStartPageActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPage;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class TourGuideDetailsActivity extends AppCompatActivity {
    TextView tourGuideMainTitle, tourGuideSubTitle, tourGuideMainDesc, tourGuideSubDesc;
    LinearLayout exploreLayout, tourGuideSubtitleLayout, retryLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    ImageView backButton;
    private Animation zoomOutAnimation, exploreZoomOutAnimation;
    private TourGuideDetailsAdapter mAdapter;
    private ArrayList<SelfGuideStarterModel> tourGuideList = new ArrayList<>();
    private Intent navigationIntent;
    private String museumId;
    Button retryButton;
    private ProgressBar progressBar;
    private NestedScrollView scrollviewContainer;
    private RelativeLayout noResultFoundLayout;
    private String appLanguage;
    int selfGuideStartRowCount;
    QMDatabase qmDatabase;
    TourGuideStartPage tourGuideStartPage;
    private Convertor converters;
    private FirebaseAnalytics mFireBaseAnalytics;
    private Bundle contentBundleParams;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        museumId = intent.getStringExtra("ID");
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        tourGuideMainTitle = findViewById(R.id.tourguide_tittle);
        tourGuideSubTitle = findViewById(R.id.tourguide_subtittle);
        tourGuideMainDesc = findViewById(R.id.tourguide_title_desc);
        tourGuideSubDesc = findViewById(R.id.tourguide_subtitle_desc);
        exploreLayout = findViewById(R.id.explore_layout);
        tourGuideSubtitleLayout = findViewById(R.id.tourguide_subtitle_layout);
        recyclerView = findViewById(R.id.tourguide_recycler_view);
        backButton = findViewById(R.id.toolbar_close);
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
                        Timber.i("%s is clicked with ID: %s",
                                tourGuideList.get(position).getTitle().toUpperCase(),
                                tourGuideList.get(position).getNid());
                        contentBundleParams = new Bundle();
                        contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, tourGuideMainTitle.getText().toString());
                        contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID,  tourGuideList.get(position).getNid());
                        mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
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
                            navigationIntent.putExtra("TITLE", tourGuideMainTitle.getText().toString());
                            startActivity(navigationIntent);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        if (museumId.equals("63") || museumId.equals("96")) {
            tourGuideMainTitle.setText(getString(R.string.mia_tour_guide));
            tourGuideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));
            tourGuideSubTitle.setText(getString(R.string.tourguide_title));
            tourGuideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
            tourGuideList.clear();
        } else if (museumId.equals("66") || museumId.equals("638")) {
            tourGuideMainTitle.setText(getString(R.string.nmoq_tour_guide));
            tourGuideMainDesc.setVisibility(View.GONE);
            exploreLayout.setVisibility(View.GONE);
            tourGuideSubTitle.setText(getString(R.string.tourguide_title));
            tourGuideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
            tourGuideList.clear();
        }
        backButton.setOnClickListener(v -> {
            Timber.i("Back button clicked");
            onBackPressed();
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        exploreZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getTourGuideDetailsPageAPIData();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });

        exploreLayout.setOnTouchListener((v, event) -> {
            Timber.i("Explore button clicked");
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
                contentBundleParams = new Bundle();
                contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, tourGuideMainTitle.getText().toString());
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, "Explore");
                mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
                Intent i = new Intent(TourGuideDetailsActivity.this, FloorMapActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        converters = new Convertor();
        appLanguage = LocaleManager.getLanguage(this);
        if (new Util().isNetworkAvailable(this))
            getTourGuideDetailsPageAPIData();
        else
            getSliderImagesAndDetailsFromDatabase(museumId);
    }

    public void getTourGuideDetailsPageAPIData() {
        Timber.i("getTourGuideDetailsPageAPIData(language :%s)", appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<SelfGuideStarterModel>> call = apiService.getSelfGuideStarterPageDetails(appLanguage, museumId);
        call.enqueue(new Callback<ArrayList<SelfGuideStarterModel>>() {
            @Override
            public void onResponse(Call<ArrayList<SelfGuideStarterModel>> call, Response<ArrayList<SelfGuideStarterModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getTourGuideDetailsPageAPIData() - isSuccessful with size: %d", tourGuideList.size());
                        scrollviewContainer.setVisibility(View.VISIBLE);
                        tourGuideList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        new SelfGuideStartRowCount(TourGuideDetailsActivity.this, appLanguage).execute();
                    } else {
                        Timber.w("Response have no data");
                        scrollviewContainer.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("Response not successful %s", getResources().getString(R.string.error_logout));
                    scrollviewContainer.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<SelfGuideStarterModel>> call, Throwable t) {
                Timber.e("getTourGuideDetailsPageAPIData() - onFailure: %s", t.getMessage());
                scrollviewContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public static class SelfGuideStartRowCount extends AsyncTask<Void, Void, Integer> {

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
            activityReference.get().selfGuideStartRowCount = integer;
            if (activityReference.get().selfGuideStartRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckTourGuideStartPageDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("Database Table have no data");
                new InsertTourGuideStartPageDatabaseTask(activityReference.get(),
                        activityReference.get().tourGuideStartPage, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language :%s)", language);
            return activityReference.get().qmDatabase.getTourGuideStartPageDao()
                    .getNumberOfRows(language);
        }
    }

    public static class CheckTourGuideStartPageDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<TourGuideDetailsActivity> activityReference;
        private TourGuideStartPage tourGuideStartPage;
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
            if (activityReference.get().tourGuideList.size() > 0) {
                for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                    int n = activityReference.get().qmDatabase.getTourGuideStartPageDao().checkIdExist(
                            activityReference.get().tourGuideList.get(i).getNid(), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().tourGuideList.get(i).getNid());
                        new UpdateTourGuideStartPageDetailTable(activityReference.get(),
                                language, i).execute();

                    } else {
                        Timber.i("Inserting data to table(language :%s) with id: %s",
                                language, activityReference.get().tourGuideList.get(i).getNid());
                        tourGuideStartPage = new TourGuideStartPage(
                                activityReference.get().tourGuideList.get(i).getTitle(),
                                activityReference.get().tourGuideList.get(i).getNid(),
                                activityReference.get().tourGuideList.get(i).getMuseumsEntity(),
                                activityReference.get().tourGuideList.get(i).getDescription(),
                                activityReference.get().converters.fromArrayList(activityReference.get().tourGuideList.get(i).getImageList()),
                                language
                        );
                        activityReference.get().qmDatabase.getTourGuideStartPageDao().insertData(tourGuideStartPage);

                    }
                }
            }

            return null;
        }
    }

    public static class InsertTourGuideStartPageDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<TourGuideDetailsActivity> activityReference;
        private TourGuideStartPage tourGuideStartPage;
        String language;

        InsertTourGuideStartPageDatabaseTask(TourGuideDetailsActivity context, TourGuideStartPage tourGuideStartPage,
                                             String lan) {
            activityReference = new WeakReference<>(context);
            this.tourGuideStartPage = tourGuideStartPage;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().tourGuideList != null) {
                for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                    Timber.i("Inserting data to table(language :%s) with id: %s",
                            language, activityReference.get().tourGuideList.get(i).getNid());
                    tourGuideStartPage = new TourGuideStartPage(
                            activityReference.get().tourGuideList.get(i).getTitle(),
                            activityReference.get().tourGuideList.get(i).getNid(),
                            activityReference.get().tourGuideList.get(i).getMuseumsEntity(),
                            activityReference.get().tourGuideList.get(i).getDescription(),
                            activityReference.get().converters.fromArrayList(activityReference.get().tourGuideList.get(i).getImageList()),
                            language);
                    activityReference.get().qmDatabase.getTourGuideStartPageDao().insertData(tourGuideStartPage);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class UpdateTourGuideStartPageDetailTable extends AsyncTask<Void, Void, Void> {

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
            Timber.i("Updating data to table(language :%s) with id: %s",
                    language, activityReference.get().tourGuideList.get(position).getNid());
            activityReference.get().qmDatabase.getTourGuideStartPageDao().updateTourGuideStartData(
                    activityReference.get().tourGuideList.get(position).getTitle(),
                    activityReference.get().tourGuideList.get(position).getDescription(),
                    activityReference.get().tourGuideList.get(position).getMuseumsEntity(),
                    activityReference.get().converters.fromArrayList(activityReference.get().tourGuideList.get(position).getImageList()),
                    activityReference.get().tourGuideList.get(position).getNid(),
                    language
            );
            return null;
        }
    }

    public static class RetrieveTourGuideDetailsData extends AsyncTask<Void, Void, List<TourGuideStartPage>> {
        private WeakReference<TourGuideDetailsActivity> activityReference;
        String museumId;

        RetrieveTourGuideDetailsData(TourGuideDetailsActivity context,
                                     String museumId) {
            activityReference = new WeakReference<>(context);
            this.museumId = museumId;
        }

        @Override
        protected List<TourGuideStartPage> doInBackground(Void... voids) {
            Timber.i("getTourGuideDetails(id: %s, language: %s)", museumId,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getTourGuideStartPageDao().
                    getTourGuideDetails(museumId, activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<TourGuideStartPage> tourGuideStartPages) {
            if (tourGuideStartPages != null && tourGuideStartPages.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        tourGuideStartPages.size());
                activityReference.get().retryLayout.setVisibility(View.GONE);
                activityReference.get().scrollviewContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < tourGuideStartPages.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            tourGuideStartPages.get(i).getNid());
                    SelfGuideStarterModel model = new SelfGuideStarterModel(
                            tourGuideStartPages.get(i).getTitle(),
                            tourGuideStartPages.get(i).getDescription(),
                            activityReference.get().converters.fromString(tourGuideStartPages.get(i).getImages()),
                            tourGuideStartPages.get(i).getMuseum_entity(),
                            tourGuideStartPages.get(i).getNid());
                    activityReference.get().tourGuideList.add(model);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                Timber.i("Have no data in database");
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                activityReference.get().scrollviewContainer.setVisibility(View.GONE);
            }

        }
    }

    public void getSliderImagesAndDetailsFromDatabase(String museumId) {
        Timber.i("getSliderImagesAndDetailsFromDatabase()");
        new RetrieveTourGuideDetailsData(TourGuideDetailsActivity.this, museumId).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAnalytics.setCurrentScreen(this, getString(R.string.tour_guide_details_page), null);
    }
}
