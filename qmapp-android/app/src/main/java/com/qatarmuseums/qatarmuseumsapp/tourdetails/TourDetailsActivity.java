package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourDetailsActivity extends AppCompatActivity {

    private SharedPreferences qmPreferences;
    private ProgressBar progressBar;
    private Intent intent;
    private String mainTitle, comingFrom, id;
    private Toolbar toolbar;
    private ImageView toolbarClose;
    private LinearLayout retryLayout;
    private TextView noResultFoundTxt;
    private Button retryButton;
    private Util util;
    private Animation zoomOutAnimation;
    private RecyclerView recyclerView;
    private TourDetailsAdapter mAdapter;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private int appLanguage;
    private QMDatabase qmDatabase;
    TourDetailsTableEnglish tourDetailsTableEnglish;
    TourDetailsTableArabic tourDetailsTableArabic;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        id = intent.getStringExtra("ID");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        noResultFoundTxt = (TextView) findViewById(R.id.no_result_view);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout);
        retryButton = (Button) findViewById(R.id.retry_btn);
        recyclerView = (RecyclerView) findViewById(R.id.tour_details_recycler_view);
        qmDatabase = QMDatabase.getInstance(TourDetailsActivity.this);
        mAdapter = new TourDetailsAdapter(this, tourDetailsList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setFocusable(false);
        util = new Util();
        toolbarClose.setOnClickListener(v ->
                onBackPressed());

        zoomOutAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            getTourDetailFromAPI(language);

        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        if (util.isNetworkAvailable(this))
            getTourDetailFromAPI(language);
        else
            getTourDetailsFromDatabase(id, language);
    }

    public void getTourDetailFromAPI(String language) {
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
                        mAdapter.notifyDataSetChanged();
                        new TourDetailsRowCount(TourDetailsActivity.this, language, id).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
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

    public void getTourDetailsFromDatabase(String id, String language) {
        if (language.equals("en")) {
            progressBar.setVisibility(View.VISIBLE);
            new RetriveTourDetailsEnglish(TourDetailsActivity.this, id).execute();
        } else {
            new RetriveTourDetailsArabic(TourDetailsActivity.this, id).execute();
        }
    }

    public static class TourDetailsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<TourDetailsActivity> activityReference;
        String language, tourId;


        TourDetailsRowCount(TourDetailsActivity context, String apiLanguage, String tourId) {
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
                //updateEnglishTable or add row to database
                new CheckTourDetailsRowExist(activityReference.get(), language, tourId).execute();
            } else {
                //create databse
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

    public void removeHtmlTags(ArrayList<TourDetailsModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setTourTitle(util.html2string(models.get(i).getTourTitle()));
            models.get(i).setTourDate(util.html2string(models.get(i).getTourDate()));
            models.get(i).setTourBody(util.html2string(models.get(i).getTourBody()));

        }
    }

    public static class CheckTourDetailsRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourDetailsActivity> activityReference;
        String language, tourId;

        CheckTourDetailsRowExist(TourDetailsActivity context, String apiLanguage, String tourId) {
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
        private WeakReference<TourDetailsActivity> activityReference;
        private TourDetailsTableEnglish tourDetailsTableEnglish;
        private TourDetailsTableArabic tourDetailsTableArabic;
        String language;
        String tourId;

        InsertDatabaseTask(TourDetailsActivity context, TourDetailsTableEnglish tourDetailsTableEnglish,
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
                                activityReference.get().tourDetailsList.get(i).getTourSpeakerInfo()

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
                                activityReference.get().tourDetailsList.get(i).getTourRegistered()
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
        private WeakReference<TourDetailsActivity> activityReference;
        String language, tourId;

        DeleteEventsTableRow(TourDetailsActivity context, String apiLanguage, String tourId) {
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
        private WeakReference<TourDetailsActivity> activityReference;
        String tourId;

        RetriveTourDetailsEnglish(TourDetailsActivity context, String tourId) {
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
                    TourDetailsModel calendarEvents = new TourDetailsModel(
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
                            tourDetailsTableEnglishList.get(i).getTour_registered()
                    );
                    activityReference.get().tourDetailsList.add(i, calendarEvents);
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

    public static class RetriveTourDetailsArabic extends AsyncTask<Void, Void, List<TourDetailsTableArabic>> {
        private WeakReference<TourDetailsActivity> activityReference;
        String tourId;

        RetriveTourDetailsArabic(TourDetailsActivity context, String tourId) {
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
                    TourDetailsModel calendarEvents = new TourDetailsModel(
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
                            tourDetailsTableArabicList.get(i).getTour_registered()
                    );
                    activityReference.get().tourDetailsList.add(i, calendarEvents);
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

}
