package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacilitiesSecondaryActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private ImageView toolbarBack;
    private Animation zoomOutAnimation;
    private Button retryButton;
    private LinearLayout retryLayout;
    private RecyclerView recyclerView;
    private FacilitiesSecondaryAdapter mAdapter;
    private Intent navigationIntent;
    private String comingFrom, facilityId;
    private ArrayList<FacilitiesDetailModel> facilitiesDetailList = new ArrayList<>();
    private Util util;
    private RelativeLayout noResultsLayout;
    private String language;
    private QMDatabase qmDatabase;
    private String[] splitArray;
    private String startTime, endTime;
    private long startTimeStamp, endTimeStamp;
    FacilityListTableEnglish facilityListTableEnglish;
    FacilityListTableArabic facilityListTableArabic;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_secondary_list);
        language = LocaleManager.getLanguage(this);
        progressBar = findViewById(R.id.progressBarLoading);
        Intent intent = getIntent();
        String mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        facilityId = intent.getStringExtra("ID");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarBack = findViewById(R.id.toolbar_back);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        retryLayout = findViewById(R.id.retry_layout);
        noResultsLayout = findViewById(R.id.no_result_layout);
        retryButton = findViewById(R.id.retry_btn);
        recyclerView = findViewById(R.id.tour_recycler_view);
        qmDatabase = QMDatabase.getInstance(FacilitiesSecondaryActivity.this);
        util = new Util();
        mAdapter = new FacilitiesSecondaryAdapter(this, facilitiesDetailList, position -> {
            navigationIntent = new Intent(FacilitiesSecondaryActivity.this, DetailsActivity.class);
            if (facilitiesDetailList.get(position).getFacilityImage().size() > 0) {
                navigationIntent.putExtra("HEADER_IMAGE", facilitiesDetailList.get(position).getFacilityImage().get(0));
            }
            navigationIntent.putExtra("MAIN_TITLE", facilitiesDetailList.get(position).getFacilitiesTitle());
            navigationIntent.putExtra("SUB_TITLE", facilitiesDetailList.get(position).getFacilitiesSubtitle());
            navigationIntent.putExtra("DESCRIPTION", facilitiesDetailList.get(position).getFacilityDescription());
            navigationIntent.putExtra("TIMING", facilitiesDetailList.get(position).getFacilitiesTiming());
            navigationIntent.putExtra("TITLE_TIMING", facilitiesDetailList.get(position).getFacilityTitleTiming());
            navigationIntent.putExtra("ID", facilitiesDetailList.get(position).getFacilitiesId());
            navigationIntent.putExtra("COMING_FROM", getString(R.string.facility_sublist));
            navigationIntent.putExtra("LONGITUDE", facilitiesDetailList.get(position).getLongitude());
            navigationIntent.putExtra("LATITUDE", facilitiesDetailList.get(position).getLattitude());
            navigationIntent.putExtra("CATEGORY_ID", facilitiesDetailList.get(position).getFacilitiesCategoryId());
            navigationIntent.putExtra("LOCATION_TITLE", facilitiesDetailList.get(position).getLocationTitle());
            startActivity(navigationIntent);
        });


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        retryButton.setOnClickListener(v -> {
            getFacilityDetailsFromAPI(facilityId);
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
            getFacilityDetailsFromAPI(facilityId);
        else
            getFacilityListFromDataBase();


    }


    public void getFacilityDetailsFromAPI(String id) {
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<FacilitiesDetailModel>> call = apiService.getFacilityDetails(language, id);
        call.enqueue(new Callback<ArrayList<FacilitiesDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FacilitiesDetailModel>> call, Response<ArrayList<FacilitiesDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        facilitiesDetailList.addAll(response.body());
                        removeHtmlTags(facilitiesDetailList);
                        mAdapter.notifyDataSetChanged();
                        new FacilityRowCount(FacilitiesSecondaryActivity.this, language).execute();

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
            public void onFailure(Call<ArrayList<FacilitiesDetailModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getFacilityListFromDataBase() {
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishFacilityData(FacilitiesSecondaryActivity.this).execute();
        } else {
            new RetrieveArabicFacilityData(FacilitiesSecondaryActivity.this).execute();
        }
    }

    public void removeHtmlTags(ArrayList<FacilitiesDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setFacilitiesSubtitle(util.html2string(models.get(i).getFacilitiesSubtitle()));
            models.get(i).setFacilitiesTitle(util.html2string(models.get(i).getFacilitiesTitle()));
            models.get(i).setFacilityDescription(util.html2string(models.get(i).getFacilityDescription()));
            models.get(i).setFacilitiesTiming(util.html2string(models.get(i).getFacilitiesTiming()));


        }
    }

    public static class FacilityRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<FacilitiesSecondaryActivity> activityReference;
        String language;

        public FacilityRowCount(FacilitiesSecondaryActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getFacilitiesListTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getFacilitiesListTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                new CheckFacilityDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertFacilityDataToDataBase(activityReference.get(), activityReference.get().facilityListTableEnglish,
                        activityReference.get().facilityListTableArabic, language).execute();
            }
        }
    }

    public static class CheckFacilityDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<FacilitiesSecondaryActivity> activityReference;
        private FacilityListTableEnglish facilityListTableEnglish;
        private FacilityListTableArabic facilityListTableArabic;
        String language;

        CheckFacilityDBRowExist(FacilitiesSecondaryActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().facilitiesDetailList.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getFacilitiesListTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId()));
                        if (n > 0) {
                            new UpdateFacilityTable(activityReference.get(), language).execute();
                        } else {

                            facilityListTableEnglish = new FacilityListTableEnglish(
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
                                    "",
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesTitle(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityImage().get(0),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesSubtitle(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityDescription(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesTiming(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityTitleTiming(),
                                    activityReference.get().facilitiesDetailList.get(i).getLongitude(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesCategoryId(),
                                    activityReference.get().facilitiesDetailList.get(i).getLattitude(),
                                    activityReference.get().facilitiesDetailList.get(i).getLocationTitle());

                            activityReference.get().qmDatabase.getFacilitiesListTableDao().insertEnglish(facilityListTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getFacilitiesListTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId()));
                        if (n > 0) {
                            new UpdateFacilityTable(activityReference.get(), language).execute();
                        } else {

                            facilityListTableArabic = new FacilityListTableArabic(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
                                    "",
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesTitle(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityImage().get(0),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesSubtitle(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityDescription(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesTiming(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilityTitleTiming(),
                                    activityReference.get().facilitiesDetailList.get(i).getLongitude(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesCategoryId(),
                                    activityReference.get().facilitiesDetailList.get(i).getLattitude(),
                                    activityReference.get().facilitiesDetailList.get(i).getLocationTitle());

                            activityReference.get().qmDatabase.getFacilitiesListTableDao().insertArabic(facilityListTableArabic);
                        }
                    }

                }
            }
            return null;
        }
    }

    public static class UpdateFacilityTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<FacilitiesSecondaryActivity> activityReference;
        String language;

        UpdateFacilityTable(FacilitiesSecondaryActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {

                activityReference.get().qmDatabase.getFacilitiesListTableDao().updateFacilityListEnglish(
                        "",
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityImage().get(0),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesSubtitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityDescription(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTiming(),
                        activityReference.get().facilitiesDetailList.get(0).getLongitude(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesCategoryId(),
                        activityReference.get().facilitiesDetailList.get(0).getLattitude(),
                        activityReference.get().facilitiesDetailList.get(0).getLocationTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityTitleTiming(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesId()
                );

            } else {
                activityReference.get().qmDatabase.getFacilitiesListTableDao().updateFacilityListArabic(

                        "",
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityImage().get(0),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesSubtitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityDescription(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTiming(),
                        activityReference.get().facilitiesDetailList.get(0).getLongitude(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesCategoryId(),
                        activityReference.get().facilitiesDetailList.get(0).getLattitude(),
                        activityReference.get().facilitiesDetailList.get(0).getLocationTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityTitleTiming(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesId()
                );

            }
            return null;
        }
    }

    public static class InsertFacilityDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<FacilitiesSecondaryActivity> activityReference;
        private FacilityListTableEnglish facilityListTableEnglish;
        private FacilityListTableArabic facilityListTableArabic;
        String language;

        InsertFacilityDataToDataBase(FacilitiesSecondaryActivity context, FacilityListTableEnglish facilityListTableEnglish,
                                     FacilityListTableArabic facilityListTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.facilityListTableEnglish = facilityListTableEnglish;
            this.facilityListTableArabic = facilityListTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().facilitiesDetailList != null && activityReference.get().facilitiesDetailList.size() > 0) {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {

                        facilityListTableEnglish = new FacilityListTableEnglish(
                                activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
                                "",
                                activityReference.get().facilitiesDetailList.get(i).getFacilitiesTitle(),
                                activityReference.get().facilitiesDetailList.get(i).getFacilityImage().get(0),
                                activityReference.get().facilitiesDetailList.get(i).getFacilitiesSubtitle(),
                                activityReference.get().facilitiesDetailList.get(i).getFacilityDescription(),
                                activityReference.get().facilitiesDetailList.get(i).getFacilitiesTiming(),
                                activityReference.get().facilitiesDetailList.get(i).getFacilityTitleTiming(),
                                activityReference.get().facilitiesDetailList.get(i).getLongitude(),
                                activityReference.get().facilitiesDetailList.get(i).getFacilitiesCategoryId(),
                                activityReference.get().facilitiesDetailList.get(i).getLattitude(),
                                activityReference.get().facilitiesDetailList.get(i).getLocationTitle());
                        activityReference.get().qmDatabase.getFacilitiesListTableDao().insertEnglish(facilityListTableEnglish);
                    }
                }
            } else {
                for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {

                    facilityListTableArabic = new FacilityListTableArabic(
                            activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
                            "",
                            activityReference.get().facilitiesDetailList.get(i).getFacilitiesTitle(),
                            activityReference.get().facilitiesDetailList.get(i).getFacilityImage().get(0),
                            activityReference.get().facilitiesDetailList.get(i).getFacilitiesSubtitle(),
                            activityReference.get().facilitiesDetailList.get(i).getFacilityDescription(),
                            activityReference.get().facilitiesDetailList.get(i).getFacilitiesTiming(),
                            activityReference.get().facilitiesDetailList.get(i).getFacilityTitleTiming(),
                            activityReference.get().facilitiesDetailList.get(i).getLongitude(),
                            activityReference.get().facilitiesDetailList.get(i).getFacilitiesCategoryId(),
                            activityReference.get().facilitiesDetailList.get(i).getLattitude(),
                            activityReference.get().facilitiesDetailList.get(i).getLocationTitle());
                    activityReference.get().qmDatabase.getFacilitiesListTableDao().insertArabic(facilityListTableArabic);
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveEnglishFacilityData extends AsyncTask<Void, Void, List<FacilityListTableEnglish>> {
        private WeakReference<FacilitiesSecondaryActivity> activityReference;

        public RetrieveEnglishFacilityData(FacilitiesSecondaryActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getFacilitiesListTableDao().getAllEnglish();
        }

        @Override
        protected void onPostExecute(List<FacilityListTableEnglish> facilityListTableEnglishes) {
            FacilitiesDetailModel facilitiesDetailModel;
            activityReference.get().facilitiesDetailList.clear();
            if (facilityListTableEnglishes.size() > 0) {
                for (int i = 0; i < facilityListTableEnglishes.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(facilityListTableEnglishes.get(i).getFacilityImage());

                    facilitiesDetailModel = new FacilitiesDetailModel(
                            facilityListTableEnglishes.get(i).getFacilityTitle(),
                            image,
                            facilityListTableEnglishes.get(i).getFacilitySubtitle(),
                            facilityListTableEnglishes.get(i).getFacilityDescription(),
                            facilityListTableEnglishes.get(i).getFacilityTiming(),
                            facilityListTableEnglishes.get(i).getFacilityTitleTiming(),
                            facilityListTableEnglishes.get(i).getFacilityNid(),
                            facilityListTableEnglishes.get(i).getFacilityLongitude(),
                            facilityListTableEnglishes.get(i).getFacilityCategoryId(),
                            facilityListTableEnglishes.get(i).getFacilityLatitude(),
                            facilityListTableEnglishes.get(i).getFacilityLocationTitle());
                    activityReference.get().facilitiesDetailList.add(i, facilitiesDetailModel);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);


            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class RetrieveArabicFacilityData extends AsyncTask<Void, Void, List<FacilityListTableArabic>> {
        private WeakReference<FacilitiesSecondaryActivity> activityReference;

        public RetrieveArabicFacilityData(FacilitiesSecondaryActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getFacilitiesListTableDao().getAllArabic();
        }

        @Override
        protected void onPostExecute(List<FacilityListTableArabic> facilityListTableArabics) {
            FacilitiesDetailModel facilitiesDetailModel;
            activityReference.get().facilitiesDetailList.clear();
            if (facilityListTableArabics.size() > 0) {
                for (int i = 0; i < facilityListTableArabics.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(facilityListTableArabics.get(i).getFacilityImage());

                    facilitiesDetailModel = new FacilitiesDetailModel(
                            facilityListTableArabics.get(i).getFacilityTitle(),
                            image,
                            facilityListTableArabics.get(i).getFacilitySubtitle(),
                            facilityListTableArabics.get(i).getFacilityDescription(),
                            facilityListTableArabics.get(i).getFacilityTiming(),
                            facilityListTableArabics.get(i).getFacilityTitleTiming(),
                            facilityListTableArabics.get(i).getFacilityNid(),
                            facilityListTableArabics.get(i).getFacilityLongitude(),
                            facilityListTableArabics.get(i).getFacilityCategoryId(),
                            facilityListTableArabics.get(i).getFacilityLatitude(),
                            facilityListTableArabics.get(i).getFacilityLocationTitle());
                    activityReference.get().facilitiesDetailList.add(i, facilitiesDetailModel);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);


            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

}
