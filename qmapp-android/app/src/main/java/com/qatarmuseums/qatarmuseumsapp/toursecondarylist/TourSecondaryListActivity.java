package com.qatarmuseums.qatarmuseumsapp.toursecondarylist;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourSecondaryListActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Intent intent;
    private String mainTitle;
    private Toolbar toolbar;
    private TextView title, toolbarTitle;
    private TextView shortDescription;
    private ImageView toolbarBack;
    private Animation zoomOutAnimation;
    private Button retryButton;
    private LinearLayout retryLayout;
    private RecyclerView recyclerView;
    private TourSecondaryListAdapter mAdapter;
    private Intent navigationIntent;
    private String comingFrom, tourId;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private Util util;
    private RelativeLayout noResultsLayout;
    private String language;
    private QMDatabase qmDatabase;
    TourDetailsTableEnglish tourDetailsTableEnglish;
    TourDetailsTableArabic tourDetailsTableArabic;
    private String[] splitArray;
    private String startTime, endTime;
    private long startTimeStamp, endTimeStamp;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_secondary_list);
        language = LocaleManager.getLanguage(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        tourId = intent.getStringExtra("ID");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarBack = (ImageView) findViewById(R.id.toolbar_back);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        title = (TextView) findViewById(R.id.main_title);
        shortDescription = (TextView) findViewById(R.id.short_description);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout);
        noResultsLayout = findViewById(R.id.no_result_layout);
        retryButton = (Button) findViewById(R.id.retry_btn);
        recyclerView = (RecyclerView) findViewById(R.id.tour_recycler_view);
        qmDatabase = QMDatabase.getInstance(TourSecondaryListActivity.this);
        util = new Util();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAdapter = new TourSecondaryListAdapter(this, tourDetailsList, position -> {
            navigationIntent = new Intent(TourSecondaryListActivity.this, DetailsActivity.class);
            if (tourDetailsList.get(position).getTourImage().size() > 0) {
                navigationIntent.putExtra("HEADER_IMAGE", tourDetailsList.get(position).getTourImage().get(0));
            }
            navigationIntent.putExtra("MAIN_TITLE", tourDetailsList.get(position).getTourTitle());
            navigationIntent.putExtra("DESCRIPTION", tourDetailsList.get(position).getTourBody());
            navigationIntent.putExtra("DATE", tourDetailsList.get(position).getTourDate());
            navigationIntent.putExtra("ID", tourDetailsList.get(position).getTourEventId());
            navigationIntent.putExtra("COMING_FROM", comingFrom);
            navigationIntent.putExtra("CONTACT_PHONE", tourDetailsList.get(position).getTourContactPhone());
            navigationIntent.putExtra("CONTACT_MAIL", tourDetailsList.get(position).getTourContactEmail());
            navigationIntent.putExtra("REGISTER", tourDetailsList.get(position).getTourRegister());
            navigationIntent.putExtra("REGISTERED", tourDetailsList.get(position).getTourRegistered());
            navigationIntent.putExtra("LONGITUDE", tourDetailsList.get(position).getTourLongtitude());
            navigationIntent.putExtra("LATITUDE", tourDetailsList.get(position).getTourLatitude());
            navigationIntent.putExtra("NID", tourDetailsList.get(position).getnId());
            navigationIntent.putExtra("RESPONSE", tourDetailsList);
            startActivity(navigationIntent);
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            getTourDetailFromAPI(tourId);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        toolbarBack.setOnClickListener(v -> onBackPressed());
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarBack.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarBack.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        toolbarTitle.setText(mainTitle);
        if (util.isNetworkAvailable(this))
            getTourDetailFromAPI(tourId);
        else
            getTourDetailsFromDatabase(tourId);
    }

    public void getTourDetailFromAPI(String id) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<TourDetailsModel>> call = apiService.getTourDetails(language, id);
        call.enqueue(new Callback<ArrayList<TourDetailsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TourDetailsModel>> call, Response<ArrayList<TourDetailsModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tourDetailsList.addAll(response.body());
                        removeHtmlTags(tourDetailsList);
                        for (int i = 0; i < tourDetailsList.size(); i++) {
                            splitArray = tourDetailsList.get(i).getTourDate().split("-");
                            startTime = splitArray[0].concat(splitArray[1].trim());
                            startTimeStamp = util.getTimeStamp(startTime);
                            if (splitArray.length > 2) {
                                endTime = splitArray[0].concat(splitArray[2].trim());
                                endTimeStamp = util.getTimeStamp(endTime);
                                tourDetailsList.get(i).setStartTimeStamp((startTimeStamp));
                                tourDetailsList.get(i).setEndTimeStamp((endTimeStamp));
                                tourDetailsList.get(i).setEventTimeStampDiff(String.valueOf(endTimeStamp - startTimeStamp));
                            }
                        }
                        Collections.sort(tourDetailsList);
                        mAdapter.notifyDataSetChanged();
                        new TourDetailsRowCount(TourSecondaryListActivity.this, language, id).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<TourDetailsModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void removeHtmlTags(ArrayList<TourDetailsModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setTourTitle(util.html2string(models.get(i).getTourTitle()));
            models.get(i).setTourDate(util.html2string(models.get(i).getTourDate()));
            models.get(i).setTourBody(util.html2string(models.get(i).getTourBody()));

        }
    }

    public void getTourDetailsFromDatabase(String id) {
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            progressBar.setVisibility(View.VISIBLE);
            new RetriveTourDetailsEnglish(TourSecondaryListActivity.this, id).execute();
        } else {
            new RetriveTourDetailsArabic(TourSecondaryListActivity.this, id).execute();
        }
    }

    public static class TourDetailsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<TourSecondaryListActivity> activityReference;
        String language, tourId;


        TourDetailsRowCount(TourSecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int tourDetailsRowCount = integer;
            if (tourDetailsRowCount > 0) {
                new CheckTourDetailsRowExist(activityReference.get(), language, tourId).execute();
            } else {
                new InsertDatabaseTask(activityReference.get(),
                        activityReference.get().tourDetailsTableEnglish,
                        activityReference.get().tourDetailsTableArabic, language, tourId).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getTourDetailsTaleDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getTourDetailsTaleDao().getNumberOfRowsArabic();
            }
        }
    }

    public static class CheckTourDetailsRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        String language, tourId;

        CheckTourDetailsRowExist(TourSecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().tourDetailsList.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkEnglishIdExist(
                            tourId);
                    if (n > 0) {
                        new DeleteEventsTableRow(activityReference.get(), language,
                                tourId).execute();
                    } else {
                        new InsertDatabaseTask(activityReference.get(),
                                activityReference.get().tourDetailsTableEnglish,
                                activityReference.get().tourDetailsTableArabic, language, tourId).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkArabicIdExist(
                            tourId);
                    if (n > 0) {
                        new DeleteEventsTableRow(activityReference.get(), language,
                                tourId).execute();
                    } else {
                        new InsertDatabaseTask(activityReference.get(),
                                activityReference.get().tourDetailsTableEnglish,
                                activityReference.get().tourDetailsTableArabic, language, tourId).execute();
                    }
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        private TourDetailsTableEnglish tourDetailsTableEnglish;
        private TourDetailsTableArabic tourDetailsTableArabic;
        String language;
        String tourId;

        InsertDatabaseTask(TourSecondaryListActivity context, TourDetailsTableEnglish tourDetailsTableEnglish,
                           TourDetailsTableArabic tourDetailsTableArabic, String lan, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourDetailsTableEnglish = tourDetailsTableEnglish;
            this.tourDetailsTableArabic = tourDetailsTableArabic;
            language = lan;
            this.tourId = tourId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().tourDetailsList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().tourDetailsList.size(); i++) {
                        Convertor converters = new Convertor();
                        tourDetailsTableEnglish = new TourDetailsTableEnglish(
                                activityReference.get().tourDetailsList.get(i).getTourTitle(),
                                converters.fromArrayList(activityReference.get().tourDetailsList.get(i).getTourImage()),
                                activityReference.get().tourDetailsList.get(i).getTourDate(),
                                activityReference.get().tourDetailsList.get(i).getTourEventId(),
                                activityReference.get().tourDetailsList.get(i).getTourContactEmail(),
                                activityReference.get().tourDetailsList.get(i).getTourContactPhone(),
                                activityReference.get().tourDetailsList.get(i).getTourLatitude(),
                                activityReference.get().tourDetailsList.get(i).getTourLongtitude(),
                                activityReference.get().tourDetailsList.get(i).getTourSortId(),
                                activityReference.get().tourDetailsList.get(i).getTourBody(),
                                activityReference.get().tourDetailsList.get(i).getTourRegistered(),
                                activityReference.get().tourDetailsList.get(i).getTourSpeakerName(),
                                activityReference.get().tourDetailsList.get(i).getTourSpeakerInfo(),
                                activityReference.get().tourDetailsList.get(i).getSeatsRemaining()

                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < activityReference.get().tourDetailsList.size(); i++) {

                        Convertor converters = new Convertor();
                        tourDetailsTableArabic = new TourDetailsTableArabic(
                                activityReference.get().tourDetailsList.get(i).getTourTitle(),
                                converters.fromArrayList(activityReference.get().tourDetailsList.get(i).getTourImage()),
                                activityReference.get().tourDetailsList.get(i).getTourDate(),
                                activityReference.get().tourDetailsList.get(i).getTourEventId(),
                                activityReference.get().tourDetailsList.get(i).getTourContactEmail(),
                                activityReference.get().tourDetailsList.get(i).getTourContactPhone(),
                                activityReference.get().tourDetailsList.get(i).getTourLatitude(),
                                activityReference.get().tourDetailsList.get(i).getTourLongtitude(),
                                activityReference.get().tourDetailsList.get(i).getTourSortId(),
                                activityReference.get().tourDetailsList.get(i).getTourBody(),
                                activityReference.get().tourDetailsList.get(i).getTourRegistered(),
                                activityReference.get().tourDetailsList.get(i).getSeatsRemaining()
                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableArabic);

                    }
                }
            }
            return true;
        }
    }

    public static class DeleteEventsTableRow extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        String language, tourId;

        DeleteEventsTableRow(TourSecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                activityReference.get().qmDatabase.getTourDetailsTaleDao().deleteEnglishTourDetailsWithId(
                        tourId
                );

            } else {
                activityReference.get().qmDatabase.getTourDetailsTaleDao().deleteArabicTourDetailsWithId(
                        tourId
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new InsertDatabaseTask(activityReference.get(),
                    activityReference.get().tourDetailsTableEnglish,
                    activityReference.get().tourDetailsTableArabic, language, tourId).execute();

        }
    }

    public static class RetriveTourDetailsEnglish extends AsyncTask<Void, Void, List<TourDetailsTableEnglish>> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        String tourId;

        RetriveTourDetailsEnglish(TourSecondaryListActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<TourDetailsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getTourDetailsWithIdEnglish(tourId);
        }

        @Override
        protected void onPostExecute(List<TourDetailsTableEnglish> tourDetailsTableEnglishList) {
            activityReference.get().tourDetailsList.clear();
            Convertor converters = new Convertor();
            if (tourDetailsTableEnglishList.size() > 0) {
                for (int i = 0; i < tourDetailsTableEnglishList.size(); i++) {
                    TourDetailsModel tourDetailsModel = new TourDetailsModel(
                            tourDetailsTableEnglishList.get(i).getTour_title(),
                            converters.fromString(tourDetailsTableEnglishList.get(i).getTour_images()),
                            tourDetailsTableEnglishList.get(i).getTour_date(),
                            tourDetailsTableEnglishList.get(i).getTour_id(),
                            tourDetailsTableEnglishList.get(i).getTour_contact_email(),
                            tourDetailsTableEnglishList.get(i).getTour_contact_phone(),
                            tourDetailsTableEnglishList.get(i).getTour_latitude(),
                            tourDetailsTableEnglishList.get(i).getTour_longtitude(),
                            tourDetailsTableEnglishList.get(i).getTour_sort_id(),
                            tourDetailsTableEnglishList.get(i).getTour_body(),
                            tourDetailsTableEnglishList.get(i).getTour_registered(),
                            tourDetailsTableEnglishList.get(i).getSeats_remaining()
                    );
                    activityReference.get().tourDetailsList.add(i, tourDetailsModel);
                }
                Collections.sort(activityReference.get().tourDetailsList);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }
    }

    public static class RetriveTourDetailsArabic extends AsyncTask<Void, Void, List<TourDetailsTableArabic>> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        String tourId;

        RetriveTourDetailsArabic(TourSecondaryListActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<TourDetailsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getTourDetailsWithIdArabic(tourId);

        }

        @Override
        protected void onPostExecute(List<TourDetailsTableArabic> tourDetailsTableArabicList) {
            activityReference.get().tourDetailsList.clear();
            Convertor converters = new Convertor();
            if (tourDetailsTableArabicList.size() > 0) {
                for (int i = 0; i < tourDetailsTableArabicList.size(); i++) {
                    TourDetailsModel tourDetailsModel = new TourDetailsModel(
                            tourDetailsTableArabicList.get(i).getTour_title(),
                            converters.fromString(tourDetailsTableArabicList.get(i).getTour_images()),
                            tourDetailsTableArabicList.get(i).getTour_date(),
                            tourDetailsTableArabicList.get(i).getTour_id(),
                            tourDetailsTableArabicList.get(i).getTour_contact_email(),
                            tourDetailsTableArabicList.get(i).getTour_contact_phone(),
                            tourDetailsTableArabicList.get(i).getTour_latitude(),
                            tourDetailsTableArabicList.get(i).getTour_longtitude(),
                            tourDetailsTableArabicList.get(i).getTour_sort_id(),
                            tourDetailsTableArabicList.get(i).getTour_body(),
                            tourDetailsTableArabicList.get(i).getTour_registered(),
                            tourDetailsTableArabicList.get(i).getSeats_remaining()
                    );
                    activityReference.get().tourDetailsList.add(i, tourDetailsModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (comingFrom != null && !comingFrom.equals("")) {
            if (comingFrom.equals(getString(R.string.museum_tours)))
                mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.tour_secondary_page), null);
            else
                mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.activities_secondary_page), null);
        }
    }
}
