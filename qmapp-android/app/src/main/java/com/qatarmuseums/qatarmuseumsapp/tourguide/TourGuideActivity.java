package com.qatarmuseums.qatarmuseumsapp.tourguide;

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

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableArabic;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableEnglish;
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
    ImageView backButton;
    private Animation zoomOutAnimation;
    private TourGuideAdapter mAdapter;
    private ArrayList<HomeList> tourGuideList = new ArrayList<>();
    private Intent navigationIntent;
    Button retryButton;
    private ProgressBar progressBar;
    private RelativeLayout noResultFoundLayout;
    private NestedScrollView scrollviewContainer;
    private String appLanguage;
    private QMDatabase qmDatabase;
    HomePageTableEnglish homePageTableEnglish;
    HomePageTableArabic homePageTableArabic;
    int homePageTableRowCount;

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
        Timber.i("getTourGuidePageAPIData()");
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getMuseumsList(appLanguage);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(Call<ArrayList<HomeList>> call, Response<ArrayList<HomeList>> response) {
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
            public void onFailure(Call<ArrayList<HomeList>> call, Throwable t) {
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
            Timber.i("getNumberOfRows%s()", language.toUpperCase());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsArabic();

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
                        activityReference.get().homePageTableEnglish,
                        activityReference.get().homePageTableArabic, language).execute();
            }

        }
    }


    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourGuideActivity> activityReference;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        CheckDBRowExist(TourGuideActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().tourGuideList.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistEnglish(
                                Integer.parseInt(activityReference.get().tourGuideList.get(i).getId()));
                        if (n > 0) {
                            Timber.i("Row exist in database(%s) for id: %s", language.toUpperCase(),
                                    activityReference.get().tourGuideList.get(i).getId());
                            new UpdateHomePageTable(activityReference.get(), language, i).execute();

                        } else {
                            Timber.i("Inserting data to table(%s) with id: %s",
                                    language.toUpperCase(), activityReference.get().tourGuideList.get(i).getId());
                            homePageTableEnglish = new HomePageTableEnglish(
                                    Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                                    activityReference.get().tourGuideList.get(i).getName(),
                                    activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                                    activityReference.get().tourGuideList.get(i).getImage(),
                                    activityReference.get().tourGuideList.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistArabic(
                                Integer.parseInt(activityReference.get().tourGuideList.get(i).getId()));
                        if (n > 0) {
                            Timber.i("Row exist in database(%s) for id: %s", language.toUpperCase(),
                                    activityReference.get().tourGuideList.get(i).getId());
                            new UpdateHomePageTable(activityReference.get(), language, i).execute();

                        } else {
                            Timber.i("Inserting data to table(%s) with id: %s",
                                    language.toUpperCase(), activityReference.get().tourGuideList.get(i).getId());
                            homePageTableArabic = new HomePageTableArabic(
                                    Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                                    activityReference.get().tourGuideList.get(i).getName(),
                                    activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                                    activityReference.get().tourGuideList.get(i).getImage(),
                                    activityReference.get().tourGuideList.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<TourGuideActivity> activityReference;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        InsertDatabaseTask(TourGuideActivity context, HomePageTableEnglish homePageTableEnglish,
                           HomePageTableArabic homePageTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.homePageTableEnglish = homePageTableEnglish;
            this.homePageTableArabic = homePageTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().tourGuideList != null) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                        Timber.i("Inserting data to table(%s) with id: %s",
                                language.toUpperCase(), activityReference.get().tourGuideList.get(i).getId());
                        homePageTableEnglish = new HomePageTableEnglish(
                                Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                                activityReference.get().tourGuideList.get(i).getName(),
                                activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                                activityReference.get().tourGuideList.get(i).getImage(),
                                activityReference.get().tourGuideList.get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().tourGuideList.size(); i++) {
                        Timber.i("Inserting data to table(%s) with id: %s",
                                language.toUpperCase(), activityReference.get().tourGuideList.get(i).getId());
                        homePageTableArabic = new HomePageTableArabic(Long.parseLong(activityReference.get().tourGuideList.get(i).getId()),
                                activityReference.get().tourGuideList.get(i).getName(),
                                activityReference.get().tourGuideList.get(i).getTourGuideAvailable(),
                                activityReference.get().tourGuideList.get(i).getImage(),
                                activityReference.get().tourGuideList.get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

                    }
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
            Timber.i("Updating data to table(%s) with id: %s",
                    language.toUpperCase(), activityReference.get().tourGuideList.get(position).getId());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageEnglish(
                        activityReference.get().tourGuideList.get(position).getName(),
                        activityReference.get().tourGuideList.get(position).getTourGuideAvailable(),
                        activityReference.get().tourGuideList.get(position).getImage(),
                        activityReference.get().tourGuideList.get(position).getSortId(),
                        activityReference.get().tourGuideList.get(position).getId()
                );

            } else {
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageArabic(
                        activityReference.get().tourGuideList.get(position).getName(),
                        activityReference.get().tourGuideList.get(position).getTourGuideAvailable(),
                        activityReference.get().tourGuideList.get(position).getImage(),
                        activityReference.get().tourGuideList.get(position).getSortId(),
                        activityReference.get().tourGuideList.get(position).getId()
                );
            }

            return null;
        }

    }

    public static class RetrieveEnglishTableData extends AsyncTask<Void, Void, List<HomePageTableEnglish>> {
        private WeakReference<TourGuideActivity> activityReference;

        RetrieveEnglishTableData(TourGuideActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<HomePageTableEnglish> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageEnglishTable()");
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableEnglish> homePageTableEnglishes) {
            if (homePageTableEnglishes.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        homePageTableEnglishes.size());
                activityReference.get().tourGuideList.clear();
                for (int i = 0; i < homePageTableEnglishes.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            homePageTableEnglishes.get(i).getQatarmuseum_id());
                    HomeList exhibitionObject = new HomeList(homePageTableEnglishes.get(i).getName()
                            , String.valueOf(homePageTableEnglishes.get(i).getQatarmuseum_id()),
                            homePageTableEnglishes.get(i).getImage(),
                            homePageTableEnglishes.get(i).getTourguide_available(),
                            homePageTableEnglishes.get(i).getSortId());
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

    public static class RetrieveArabicTableData extends AsyncTask<Void, Void,
            List<HomePageTableArabic>> {
        private WeakReference<TourGuideActivity> activityReference;

        RetrieveArabicTableData(TourGuideActivity context) {
            activityReference = new WeakReference<>(context);

        }


        @Override
        protected List<HomePageTableArabic> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageArabicTable()");
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableArabic> homePageTableArabics) {
            if (homePageTableArabics.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        homePageTableArabics.size());
                activityReference.get().tourGuideList.clear();
                for (int i = 0; i < homePageTableArabics.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            homePageTableArabics.get(i).getQatarmuseum_id());
                    HomeList exhibitionObject = new HomeList(homePageTableArabics.get(i).getName()
                            , String.valueOf(homePageTableArabics.get(i).getQatarmuseum_id()),
                            homePageTableArabics.get(i).getImage(),
                            homePageTableArabics.get(i).getTourguide_available(),
                            homePageTableArabics.get(i).getSortId());
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
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH))
            new RetrieveEnglishTableData(TourGuideActivity.this).execute();
        else
            new RetrieveArabicTableData(TourGuideActivity.this).execute();
    }

}
