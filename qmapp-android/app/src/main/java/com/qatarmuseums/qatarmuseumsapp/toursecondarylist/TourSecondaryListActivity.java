package com.qatarmuseums.qatarmuseumsapp.toursecondarylist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourSecondaryListActivity extends AppCompatActivity {

    private SharedPreferences qmPreferences;
    private int appLanguage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_secondary_list);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
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
        mAdapter = new TourSecondaryListAdapter(this, tourDetailsList, position -> {
            navigationIntent = new Intent(TourSecondaryListActivity.this, DetailsActivity.class);
            navigationIntent.putExtra("HEADER_IMAGE", tourDetailsList.get(position).getTourImage().get(0));
            navigationIntent.putExtra("MAIN_TITLE", tourDetailsList.get(position).getTourTitle());
            navigationIntent.putExtra("DESCRIPTION", tourDetailsList.get(position).getTourBody());
            navigationIntent.putExtra("DATE", tourDetailsList.get(position).getTourDate());
            navigationIntent.putExtra("ID", tourDetailsList.get(position).getTourEventId());
            navigationIntent.putExtra("COMING_FROM", comingFrom);
            navigationIntent.putExtra("CONTACT", tourDetailsList.get(position).getTourContactPhone() + "\n" +
                    tourDetailsList.get(position).getTourContactEmail());
            navigationIntent.putExtra("REGISTER", tourDetailsList.get(position).getTourRegister());
            navigationIntent.putExtra("REGISTERED", tourDetailsList.get(position).getTourRegistered());
            navigationIntent.putExtra("LONGITUDE", tourDetailsList.get(position).getTourLongtitude());
            navigationIntent.putExtra("LATITUDE", tourDetailsList.get(position).getTourLatitude());
            startActivity(navigationIntent);
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            getTourDetailFromAPI(language, tourId);
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
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        if (util.isNetworkAvailable(this))
            getTourDetailFromAPI(language, tourId);
        else
            getTourDetailsFromDatabase(language, tourId);
    }

    public void getTourDetailFromAPI(String language, String id) {
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

    public void getTourDetailsFromDatabase(String language, String id) {
        if (language.equals("en")) {
            progressBar.setVisibility(View.VISIBLE);
            new RetriveTourDetailsEnglish(TourSecondaryListActivity.this, id).execute();
        } else {
            new RetriveTourDetailsArabic(TourSecondaryListActivity.this, id).execute();
        }
    }

    public class TourDetailsRowCount extends AsyncTask<Void, Void, Integer> {

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
                new CheckTourDetailsRowExist(TourSecondaryListActivity.this, language, tourId).execute();
            } else {
                new InsertDatabaseTask(TourSecondaryListActivity.this, tourDetailsTableEnglish,
                        tourDetailsTableArabic, language, tourId).execute();
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

    public class CheckTourDetailsRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<TourSecondaryListActivity> activityReference;
        String language, tourId;

        CheckTourDetailsRowExist(TourSecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (tourDetailsList.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkEnglishIdExist(
                            tourId);
                    if (n > 0) {
                        new DeleteEventsTableRow(TourSecondaryListActivity.this, language,
                                tourId).execute();
                    } else {
                        new InsertDatabaseTask(TourSecondaryListActivity.this, tourDetailsTableEnglish,
                                tourDetailsTableArabic, language, tourId).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkArabicIdExist(
                            tourId);
                    if (n > 0) {
                        new DeleteEventsTableRow(TourSecondaryListActivity.this, language,
                                tourId).execute();
                    } else {
                        new InsertDatabaseTask(TourSecondaryListActivity.this, tourDetailsTableEnglish,
                                tourDetailsTableArabic, language, tourId).execute();
                    }
                }
            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
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
            if (tourDetailsList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < tourDetailsList.size(); i++) {
                        Convertor converters = new Convertor();
                        tourDetailsTableEnglish = new TourDetailsTableEnglish(
                                tourDetailsList.get(i).getTourTitle(),
                                converters.fromArrayList(tourDetailsList.get(i).getTourImage()),
                                tourDetailsList.get(i).getTourDate(),
                                tourDetailsList.get(i).getTourEventId(),
                                tourDetailsList.get(i).getTourContactEmail(),
                                tourDetailsList.get(i).getTourContactPhone(),
                                tourDetailsList.get(i).getTourLatitude(),
                                tourDetailsList.get(i).getTourLongtitude(),
                                tourDetailsList.get(i).getTourSortId(),
                                tourDetailsList.get(i).getTourBody(),
                                tourDetailsList.get(i).getTourRegistered(),
                                tourDetailsList.get(i).getTourSpeakerName(),
                                tourDetailsList.get(i).getTourSpeakerInfo()

                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < tourDetailsList.size(); i++) {

                        Convertor converters = new Convertor();
                        tourDetailsTableArabic = new TourDetailsTableArabic(
                                tourDetailsList.get(i).getTourTitle(),
                                converters.fromArrayList(tourDetailsList.get(i).getTourImage()),
                                tourDetailsList.get(i).getTourDate(),
                                tourDetailsList.get(i).getTourEventId(),
                                tourDetailsList.get(i).getTourContactEmail(),
                                tourDetailsList.get(i).getTourContactPhone(),
                                tourDetailsList.get(i).getTourLatitude(),
                                tourDetailsList.get(i).getTourLongtitude(),
                                tourDetailsList.get(i).getTourSortId(),
                                tourDetailsList.get(i).getTourBody(),
                                tourDetailsList.get(i).getTourRegistered()
                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableArabic);

                    }
                }
            }
            return true;
        }
    }

    public class DeleteEventsTableRow extends AsyncTask<Void, Void, Void> {
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
            new InsertDatabaseTask(TourSecondaryListActivity.this, tourDetailsTableEnglish,
                    tourDetailsTableArabic, language, tourId).execute();

        }
    }

    public class RetriveTourDetailsEnglish extends AsyncTask<Void, Void, List<TourDetailsTableEnglish>> {
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
            tourDetailsList.clear();
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
                            tourDetailsTableEnglishList.get(i).getTour_registered()
                    );
                    tourDetailsList.add(i, tourDetailsModel);
                }
                mAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetriveTourDetailsArabic extends AsyncTask<Void, Void, List<TourDetailsTableArabic>> {
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
            tourDetailsList.clear();
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
                            tourDetailsTableArabicList.get(i).getTour_registered()
                    );
                    tourDetailsList.add(i, tourDetailsModel);
                }
                mAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}
