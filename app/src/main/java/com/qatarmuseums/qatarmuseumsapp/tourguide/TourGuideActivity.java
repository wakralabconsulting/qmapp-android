package com.qatarmuseums.qatarmuseumsapp.tourguide;

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

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
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

public class TourGuideActivity extends AppCompatActivity {
    TextView tourguideMainTitle, tourguideSubTitle, tourguideMainDesc, tourguideSubDesc;
    LinearLayout exploreLayout, tourguideSubtitleLayout, retryLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    ImageView backButton;
    private Animation zoomOutAnimation;
    private TourGuideAdapter mAdapter;
    private ArrayList<HomeList> tourGuideList = new ArrayList<>();
    TourGuideList tourguideObject;
    private Intent navigationIntent;
    Button retryButton;
    private ProgressBar progressBar;
    private RelativeLayout noResultFoundLayout;
    private NestedScrollView scrollviewContainer;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    private QMDatabase qmDatabase;
    HomePageTableEnglish homePageTableEnglish;
    HomePageTableArabic homePageTableArabic;
    int homePageTableRowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();

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

        tourguideSubtitleLayout.setVisibility(View.GONE);
        exploreLayout.setVisibility(View.GONE);
        tourguideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
        tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));

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
                        if (tourGuideList.get(position).getId().equals("63") ||
                                tourGuideList.get(position).getId().equals("96") ||
                                tourGuideList.get(position).getId().equals("61") ||
                                tourGuideList.get(position).getId().equals("635")) {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTourGuidePageAPIData(appLanguage);
                progressBar.setVisibility(View.VISIBLE);
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

        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (new Util().isNetworkAvailable(this))
            getTourGuidePageAPIData(appLanguage);
        else
            getDataFromDataBase(appLanguage);
    }

    public void getTourGuidePageAPIData(int lan) {
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
        Call<ArrayList<HomeList>> call = apiService.getHomepageDetails(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(Call<ArrayList<HomeList>> call, Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
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
                        new RowCount(TourGuideActivity.this, language).execute();
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
            public void onFailure(Call<ArrayList<HomeList>> call, Throwable t) {
                scrollviewContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<TourGuideActivity> activityReference;
        String language;

        RowCount(TourGuideActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            homePageTableRowCount = integer;
            if (homePageTableRowCount > 0) {
                new CheckDBRowExist(TourGuideActivity.this, language).execute();

            } else {
                new InsertDatabaseTask(TourGuideActivity.this, homePageTableEnglish,
                        homePageTableArabic, language).execute();

            }

        }
    }


    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
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
            if (tourGuideList.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistEnglish(
                                Integer.parseInt(tourGuideList.get(i).getId()));
                        if (n > 0) {
                            new UpdateHomePageTable(TourGuideActivity.this, language, i).execute();

                        } else {
                            homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(tourGuideList.get(i).getId()),
                                    tourGuideList.get(i).getName(),
                                    tourGuideList.get(i).getTourguideAvailable().toString(),
                                    tourGuideList.get(i).getImage(),
                                    tourGuideList.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistArabic(
                                Integer.parseInt(tourGuideList.get(i).getId()));
                        if (n > 0) {
                            new UpdateHomePageTable(TourGuideActivity.this, language, i).execute();

                        } else {
                            homePageTableArabic = new HomePageTableArabic(Long.parseLong(tourGuideList.get(i).getId()),
                                    tourGuideList.get(i).getName(),
                                    tourGuideList.get(i).getTourguideAvailable().toString(),
                                    tourGuideList.get(i).getImage(),
                                    tourGuideList.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
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
            if (tourGuideList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(tourGuideList.get(i).getId()),
                                tourGuideList.get(i).getName(),
                                tourGuideList.get(i).getTourguideAvailable().toString(),
                                tourGuideList.get(i).getImage(),
                                tourGuideList.get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);
                    }
                } else {
                    for (int i = 0; i < tourGuideList.size(); i++) {
                        homePageTableArabic = new HomePageTableArabic(Long.parseLong(tourGuideList.get(i).getId()),
                                tourGuideList.get(i).getName(),
                                tourGuideList.get(i).getTourguideAvailable().toString(),
                                tourGuideList.get(i).getImage(),
                                tourGuideList.get(i).getSortId());
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


    public class UpdateHomePageTable extends AsyncTask<Void, Void, Void> {
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
            if (language.equals("en")) {
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageEnglish(
                        tourGuideList.get(position).getName(), tourGuideList.get(position).getTourguideAvailable().toString(),
                        tourGuideList.get(position).getImage(), tourGuideList.get(position).getSortId(),
                        tourGuideList.get(position).getId()
                );

            } else {
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageArabic(
                        tourGuideList.get(position).getName(), tourGuideList.get(position).getTourguideAvailable().toString(),
                        tourGuideList.get(position).getImage(), tourGuideList.get(position).getSortId(),
                        tourGuideList.get(position).getId()
                );
            }

            return null;
        }

    }

    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<HomePageTableEnglish>> {
        private WeakReference<TourGuideActivity> activityReference;
        int language;

        RetriveEnglishTableData(TourGuideActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected List<HomePageTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableEnglish> homePageTableEnglishes) {
            if (homePageTableEnglishes.size() > 0) {
                tourGuideList.clear();
                for (int i = 0; i < homePageTableEnglishes.size(); i++) {
                    HomeList exhibitonObject = new HomeList(homePageTableEnglishes.get(i).getName()
                            , String.valueOf(homePageTableEnglishes.get(i).getQatarmuseum_id()),
                            homePageTableEnglishes.get(i).getImage(),
                            homePageTableEnglishes.get(i).getTourguide_available(),
                            homePageTableEnglishes.get(i).getSortId());
                    tourGuideList.add(i, exhibitonObject);
                }

                Collections.sort(tourGuideList);
                mAdapter.notifyDataSetChanged();
                scrollviewContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                scrollviewContainer.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    public class RetriveArabicTableData extends AsyncTask<Void, Void,
            List<HomePageTableArabic>> {
        private WeakReference<TourGuideActivity> activityReference;
        int language;

        RetriveArabicTableData(TourGuideActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }


        @Override
        protected List<HomePageTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableArabic> homePageTableArabics) {
            if (homePageTableArabics.size() > 0) {
                tourGuideList.clear();
                for (int i = 0; i < homePageTableArabics.size(); i++) {
                    HomeList exhibitonObject = new HomeList(homePageTableArabics.get(i).getName()
                            , String.valueOf(homePageTableArabics.get(i).getQatarmuseum_id()),
                            homePageTableArabics.get(i).getImage(),
                            homePageTableArabics.get(i).getTourguide_available(),
                            homePageTableArabics.get(i).getSortId());
                    tourGuideList.add(i, exhibitonObject);
                }

                Collections.sort(tourGuideList);
                mAdapter.notifyDataSetChanged();
                scrollviewContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                scrollviewContainer.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getDataFromDataBase(int language) {
        progressBar.setVisibility(View.VISIBLE);
        if (language == 1)
            new RetriveEnglishTableData(TourGuideActivity.this, language).execute();
        else
            new RetriveArabicTableData(TourGuideActivity.this, language).execute();
    }

}
