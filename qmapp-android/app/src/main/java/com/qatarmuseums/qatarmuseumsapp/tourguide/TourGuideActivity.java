package com.qatarmuseums.qatarmuseumsapp.tourguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTable;
import com.qatarmuseums.qatarmuseumsapp.tourguidedetails.TourGuideDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class TourGuideActivity extends AppCompatActivity {
    TextView tourGuideMainTitle, tourGuideSubTitle, tourGuideMainDesc, tourGuideSubDesc;
    LinearLayout exploreLayout, tourGuideSubtitleLayout, retryLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    View backButton;
    private Animation zoomOutAnimation;
    private TourGuideAdapter mAdapter;
    private ArrayList<HomeList> tourGuideList = new ArrayList<>();
    private Intent navigationIntent;
    View retryButton;
    private ProgressBar progressBar;
    private RelativeLayout noResultFoundLayout;
    private NestedScrollView scrollviewContainer;
    private String appLanguage;
    private QMDatabase qmDatabase;
    HomePageTable homePageTable;
    int homePageTableRowCount;
    private FirebaseAnalytics mFireBaseAnalytics;
    private Bundle contentBundleParams;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();

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

        tourGuideSubtitleLayout.setVisibility(View.GONE);
        exploreLayout.setVisibility(View.GONE);
        tourGuideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
        tourGuideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));

        mAdapter = new TourGuideAdapter(this, tourGuideList, getString(R.string.tourguide_sidemenu_title));
        qmDatabase = QMDatabase.getInstance(TourGuideActivity.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        Timber.i("%s is clicked with ID: %s",
                                tourGuideList.get(position).getName().toUpperCase(),
                                tourGuideList.get(position).getId());
                        contentBundleParams = new Bundle();
                        contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, tourGuideMainTitle.getText().toString());
                        contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, tourGuideList.get(position).getId());
                        mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
                        if (tourGuideList.get(position).getId().equals("63") ||
                                tourGuideList.get(position).getId().equals("96") ||
                                tourGuideList.get(position).getId().equals("61") ||
                                tourGuideList.get(position).getId().equals("635") ||
                                tourGuideList.get(position).getId().equals("66") ||
                                tourGuideList.get(position).getId().equals("638")) {
                            navigationIntent = new Intent(TourGuideActivity.this,
                                    TourGuideDetailsActivity.class);
                            navigationIntent.putExtra("ID", tourGuideList.get(position).getId());
                            startActivity(navigationIntent);
                        } else {
                            new Util().showComingSoonDialog(TourGuideActivity.this, R.string.coming_soon_content_map);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        backButton.setOnClickListener(v -> {
            Timber.i("Back button clicked");
            onBackPressed();
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getTourGuidePageAPIData();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
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
        appLanguage = LocaleManager.getLanguage(this);
        if (new Util().isNetworkAvailable(this))
            getTourGuidePageAPIData();
        else
            getDataFromDataBase();
    }

    public void getTourGuidePageAPIData() {
        Timber.i("getTourGuidePageAPIData(language :%s)", appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getMuseumsList(appLanguage);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HomeList>> call,
                                   @NonNull Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("getTourGuidePageAPIData() - isSuccessful with size: %d", tourGuideList.size());
                        scrollviewContainer.setVisibility(View.VISIBLE);
                        tourGuideList.addAll(response.body());
                        // Removing Exhibiton from list
                        for (int i = 0; i < tourGuideList.size(); i++) {
                            if (tourGuideList.get(i).getId().equals("12186") ||
                                    tourGuideList.get(i).getId().equals("12181")) {
                                tourGuideList.remove(i);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        new RowCount(TourGuideActivity.this, appLanguage).execute();
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
            public void onFailure(@NonNull Call<ArrayList<HomeList>> call, @NonNull Throwable t) {
                Timber.e("getTourGuidePageAPIData() - onFailure: %s", t.getMessage());
                scrollviewContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public static class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<TourGuideActivity> activityReference;
        String language;

        RowCount(TourGuideActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language :%s)", language);
            return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRows(language);

        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().homePageTableRowCount = integer;
            if (activityReference.get().homePageTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckDBRowExist(activityReference.get(), language).execute();

            } else {
                Timber.i("Database Table have no data");
                new InsertDatabaseTask(activityReference.get(),
                        activityReference.get().homePageTable, language).execute();
            }

        }
    }


    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourGuideActivity> activityReference;
        private HomePageTable homePageTable;
        String language;

        CheckDBRowExist(TourGuideActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().tourGuideList.size() > 0) {
                for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                    int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().tourGuideList.get(i).getId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().tourGuideList.get(i).getId());
                        new UpdateHomePageTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting data to table(language :%s) with id: %s",
                                language, activityReference.get().tourGuideList.get(i).getId());
                        homePageTable = new HomePageTable(
                                Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                                activityReference.get().tourGuideList.get(i).getName(),
                                activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                                activityReference.get().tourGuideList.get(i).getImage(),
                                activityReference.get().tourGuideList.get(i).getSortId(),
                                language);
                        activityReference.get().qmDatabase.getHomePageTableDao().insertData(homePageTable);

                    }
                }

            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<TourGuideActivity> activityReference;
        private HomePageTable homePageTable;
        String language;

        InsertDatabaseTask(TourGuideActivity context, HomePageTable homePageTable, String lan) {
            activityReference = new WeakReference<>(context);
            this.homePageTable = homePageTable;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().tourGuideList != null) {
                for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                    Timber.i("Inserting data to table(language :%s) with id: %s",
                            language, activityReference.get().tourGuideList.get(i).getId());
                    homePageTable = new HomePageTable(
                            Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                            activityReference.get().tourGuideList.get(i).getName(),
                            activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                            activityReference.get().tourGuideList.get(i).getImage(),
                            activityReference.get().tourGuideList.get(i).getSortId(),
                            language);
                    activityReference.get().qmDatabase.getHomePageTableDao().insertData(homePageTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


    public static class UpdateHomePageTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourGuideActivity> activityReference;
        String language;
        int position;

        UpdateHomePageTable(TourGuideActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating data to table(language :%s) with id: %s",
                    language, activityReference.get().tourGuideList.get(position).getId());
            activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageTable(
                    activityReference.get().tourGuideList.get(position).getName(),
                    activityReference.get().tourGuideList.get(position).getTourGuideAvailable(),
                    activityReference.get().tourGuideList.get(position).getImage(),
                    activityReference.get().tourGuideList.get(position).getSortId(),
                    activityReference.get().tourGuideList.get(position).getId(),
                    language
            );
            return null;
        }

    }

    public static class RetrieveTableData extends AsyncTask<Void, Void, List<HomePageTable>> {
        private WeakReference<TourGuideActivity> activityReference;

        RetrieveTableData(TourGuideActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<HomePageTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageTable(language :%s)", activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getHomePageTableDao()
                    .getAllDataFromHomePageTable(activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<HomePageTable> homePageTables) {
            if (homePageTables.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        homePageTables.size());
                activityReference.get().tourGuideList.clear();
                for (int i = 0; i < homePageTables.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            homePageTables.get(i).getQatarMuseum_id());
                    HomeList exhibitionObject = new HomeList(homePageTables.get(i).getName()
                            , String.valueOf(homePageTables.get(i).getQatarMuseum_id()),
                            homePageTables.get(i).getImage(),
                            homePageTables.get(i).getTour_guide_available(),
                            homePageTables.get(i).getSortId());
                    activityReference.get().tourGuideList.add(i, exhibitionObject);
                }

                Collections.sort(activityReference.get().tourGuideList);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().scrollviewContainer.setVisibility(View.VISIBLE);
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().scrollviewContainer.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getDataFromDataBase() {
        Timber.i("getDataFromDataBase()");
        progressBar.setVisibility(View.VISIBLE);
        new RetrieveTableData(TourGuideActivity.this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAnalytics.setCurrentScreen(this, getString(R.string.tour_guide_page), null);
    }
}
