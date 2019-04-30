package com.qatarmuseums.qatarmuseumsapp.commonlistsecondary;

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
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilitiesDetailModel;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilitiesSecondaryAdapter;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableEnglish;
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
import timber.log.Timber;

public class SecondaryListActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView toolbarBack;
    private Animation zoomOutAnimation;
    private Button retryButton;
    private LinearLayout retryLayout;
    private RecyclerView recyclerView;
    private SecondaryListAdapter secondaryListAdapter;
    private FacilitiesSecondaryAdapter facilitiesSecondaryAdapter;
    private Intent navigationIntent;
    private String comingFrom, itemId;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private ArrayList<FacilitiesDetailModel> facilitiesDetailList = new ArrayList<>();
    private Util util;
    private RelativeLayout noResultsLayout;
    private String language;
    private QMDatabase qmDatabase;
    TourDetailsTableEnglish tourDetailsTableEnglish;
    TourDetailsTableArabic tourDetailsTableArabic;
    FacilityDetailTableEnglish facilityDetailTableEnglish;
    FacilityDetailTableArabic facilityDetailTableArabic;
    private String[] splitArray;
    private String startTime, endTime;
    private long startTimeStamp, endTimeStamp;
    private String mainTitle;
    private Bundle contentBundleParams;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String screenName = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list_secondary);
        language = LocaleManager.getLanguage(this);
        progressBar = findViewById(R.id.progressBarLoading);
        Intent intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        itemId = intent.getStringExtra("ID");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarBack = findViewById(R.id.toolbar_back);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        retryLayout = findViewById(R.id.retry_layout);
        noResultsLayout = findViewById(R.id.no_result_layout);
        retryButton = findViewById(R.id.retry_btn);
        recyclerView = findViewById(R.id.secondary_list_recycler_view);
        qmDatabase = QMDatabase.getInstance(SecondaryListActivity.this);
        util = new Util();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        screenName = comingFrom + "Secondary";
        if (comingFrom.equals(getString(R.string.facilities_txt))) {
            facilitiesSecondaryAdapter = new FacilitiesSecondaryAdapter(this, facilitiesDetailList, position -> {
                Timber.i("%s is clicked with ID: %s from %s",
                        facilitiesDetailList.get(position).getFacilitiesTitle().toUpperCase(),
                        facilitiesDetailList.get(position).getFacilitiesId(),
                        mainTitle);
                contentBundleParams = new Bundle();
                contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, mainTitle);
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, facilitiesDetailList.get(position).getFacilitiesId());
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_NAME, facilitiesDetailList.get(position).getFacilitiesTitle());

                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
                navigationIntent = new Intent(SecondaryListActivity.this, DetailsActivity.class);
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
        } else
            secondaryListAdapter = new SecondaryListAdapter(this, tourDetailsList, position -> {
                Timber.i("%s is clicked with ID: %s from %s",
                        tourDetailsList.get(position).getTourTitle().toUpperCase(),
                        tourDetailsList.get(position).getNid(),
                        mainTitle);
                contentBundleParams = new Bundle();
                contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, mainTitle);
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, tourDetailsList.get(position).getNid());
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_NAME, tourDetailsList.get(position).getTourTitle());

                navigationIntent = new Intent(SecondaryListActivity.this, DetailsActivity.class);
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
                navigationIntent.putExtra("NID", tourDetailsList.get(position).getNid());
                navigationIntent.putExtra("RESPONSE", tourDetailsList);
                startActivity(navigationIntent);
            });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (comingFrom.equals(getString(R.string.facilities_txt)))
            recyclerView.setAdapter(facilitiesSecondaryAdapter);
        else
            recyclerView.setAdapter(secondaryListAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getData();
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
        getData();
    }

    private void getData() {
        Timber.i("getData() for %s", mainTitle);
        if (comingFrom.equals(getString(R.string.facilities_txt))) {
            if (util.isNetworkAvailable(this))
                getFacilityDetailsFromAPI(itemId);
            else
                getFacilityDetailFromDataBase(itemId);
        } else {
            if (util.isNetworkAvailable(this))
                getTourDetailFromAPI(itemId);
            else
                getTourDetailsFromDatabase(itemId);

        }
    }

    public void getFacilityDetailsFromAPI(String id) {
        Timber.i("get%sListFromAPI()", mainTitle);
        progressBar.setVisibility(View.VISIBLE);
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
                        removeFacilitiesHtmlTags(facilitiesDetailList);
                        Timber.i("Set %s list with size: %d", mainTitle.toUpperCase(),
                                response.body().size());
                        facilitiesSecondaryAdapter.notifyDataSetChanged();
                        new FacilityRowCount(SecondaryListActivity.this, language).execute();

                    } else {
                        Timber.i("%s list have no data", mainTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("%s list response is not successful", mainTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<FacilitiesDetailModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", mainTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getFacilityDetailFromDataBase(String facilityId) {
        Timber.i("get%sListFromDatabase()", mainTitle);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishFacilityData(SecondaryListActivity.this, facilityId).execute();
        } else {
            new RetrieveArabicFacilityData(SecondaryListActivity.this, facilityId).execute();
        }
    }

    public void removeFacilitiesHtmlTags(ArrayList<FacilitiesDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setFacilitiesSubtitle(util.html2string(models.get(i).getFacilitiesSubtitle()));
            models.get(i).setFacilitiesTitle(util.html2string(models.get(i).getFacilitiesTitle()));
            models.get(i).setFacilityDescription(util.html2string(models.get(i).getFacilityDescription()));
            models.get(i).setFacilitiesTiming(util.html2string(models.get(i).getFacilitiesTiming()));
        }
    }

    public static class FacilityRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<SecondaryListActivity> activityReference;
        String language;

        public FacilityRowCount(SecondaryListActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sDetailsRows%s()", activityReference.get().comingFrom, language.toUpperCase());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getFacilitiesDetailTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getFacilitiesDetailTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckFacilityDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s details table have no data", activityReference.get().comingFrom);
                new InsertFacilityDataToDataBase(activityReference.get(), activityReference.get().facilityDetailTableEnglish,
                        activityReference.get().facilityDetailTableArabic, language).execute();
            }
        }
    }

    public static class CheckFacilityDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<SecondaryListActivity> activityReference;
        private FacilityDetailTableEnglish facilityDetailTableEnglish;
        private FacilityDetailTableArabic facilityDetailTableArabic;
        String language;

        CheckFacilityDBRowExist(SecondaryListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().facilitiesDetailList.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getFacilitiesDetailTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId()));
                        if (n > 0) {
                            Timber.i("Row exist in details database(%s) for id: %s", language.toUpperCase(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                            new UpdateFacilityTable(activityReference.get(), language).execute();
                        } else {
                            Timber.i("Inserting %s details Table(%s) with id: %s", activityReference.get().comingFrom,
                                    language.toUpperCase(), activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                            facilityDetailTableEnglish = new FacilityDetailTableEnglish(
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

                            activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertEnglish(facilityDetailTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getFacilitiesDetailTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId()));
                        if (n > 0) {
                            Timber.i("Row exist in details database(%s) for id: %s", language.toUpperCase(),
                                    activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                            new UpdateFacilityTable(activityReference.get(), language).execute();
                        } else {
                            Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().mainTitle,
                                    language.toUpperCase(), activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                            facilityDetailTableArabic = new FacilityDetailTableArabic(
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

                            activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertArabic(facilityDetailTableArabic);
                        }
                    }

                }
            }
            return null;
        }
    }

    public static class UpdateFacilityTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<SecondaryListActivity> activityReference;
        String language;

        UpdateFacilityTable(SecondaryListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s details table(%s) with id: %s", activityReference.get().comingFrom,
                    language.toUpperCase(), activityReference.get().facilitiesDetailList.get(0).getFacilitiesId());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {

                activityReference.get().qmDatabase.getFacilitiesDetailTableDao().updateFacilityDetailEnglish(
                        "",
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityImage().get(0),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesId(),
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
                activityReference.get().qmDatabase.getFacilitiesDetailTableDao().updateFacilityDetailArabic(
                        "",
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesTitle(),
                        activityReference.get().facilitiesDetailList.get(0).getFacilityImage().get(0),
                        activityReference.get().facilitiesDetailList.get(0).getFacilitiesId(),
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

        private WeakReference<SecondaryListActivity> activityReference;
        private FacilityDetailTableEnglish facilityDetailTableEnglish;
        private FacilityDetailTableArabic facilityDetailTableArabic;
        String language;

        InsertFacilityDataToDataBase(SecondaryListActivity context, FacilityDetailTableEnglish facilityDetailTableEnglish,
                                     FacilityDetailTableArabic facilityDetailTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.facilityDetailTableEnglish = facilityDetailTableEnglish;
            this.facilityDetailTableArabic = facilityDetailTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Insert %s details table(%s) with size: %d", activityReference.get().comingFrom,
                    language.toUpperCase(), activityReference.get().facilitiesDetailList.size());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().facilitiesDetailList != null && activityReference.get().facilitiesDetailList.size() > 0) {
                    for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                        Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                                language.toUpperCase(), activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                        facilityDetailTableEnglish = new FacilityDetailTableEnglish(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
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
                        activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertEnglish(facilityDetailTableEnglish);
                    }
                }
            } else {
                for (int i = 0; i < activityReference.get().facilitiesDetailList.size(); i++) {
                    Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                            language.toUpperCase(), activityReference.get().facilitiesDetailList.get(i).getFacilitiesId());
                    facilityDetailTableArabic = new FacilityDetailTableArabic(activityReference.get().facilitiesDetailList.get(i).getFacilitiesId(),
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
                    activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertArabic(facilityDetailTableArabic);
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveEnglishFacilityData extends AsyncTask<Void, Void, List<FacilityDetailTableEnglish>> {
        private WeakReference<SecondaryListActivity> activityReference;
        int id;

        public RetrieveEnglishFacilityData(SecondaryListActivity context, String id) {
            this.activityReference = new WeakReference<>(context);
            this.id = Integer.valueOf(id);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityDetailTableEnglish> doInBackground(Void... voids) {
            Timber.i("getAll%sEnglishData() for id: %d", activityReference.get().mainTitle, id);
            return activityReference.get().qmDatabase.getFacilitiesDetailTableDao().getFacilityDetailEnglish(id);
        }

        @Override
        protected void onPostExecute(List<FacilityDetailTableEnglish> facilityDetailTableEnglishes) {
            FacilitiesDetailModel facilitiesDetailModel;
            activityReference.get().facilitiesDetailList.clear();
            if (facilityDetailTableEnglishes.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().mainTitle,
                        facilityDetailTableEnglishes.size());
                for (int i = 0; i < facilityDetailTableEnglishes.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().mainTitle,
                            facilityDetailTableEnglishes.get(i).getFacilityNid());
                    ArrayList<String> image = new ArrayList<>();
                    image.add(facilityDetailTableEnglishes.get(i).getFacilityImage());

                    facilitiesDetailModel = new FacilitiesDetailModel(
                            facilityDetailTableEnglishes.get(i).getFacilityTitle(),
                            image,
                            facilityDetailTableEnglishes.get(i).getFacilitySubtitle(),
                            facilityDetailTableEnglishes.get(i).getFacilityDescription(),
                            facilityDetailTableEnglishes.get(i).getFacilityTiming(),
                            facilityDetailTableEnglishes.get(i).getFacilityTitleTiming(),
                            facilityDetailTableEnglishes.get(i).getFacilityNid(),
                            facilityDetailTableEnglishes.get(i).getFacilityLongitude(),
                            facilityDetailTableEnglishes.get(i).getFacilityCategoryId(),
                            facilityDetailTableEnglishes.get(i).getFacilityLatitude(),
                            facilityDetailTableEnglishes.get(i).getFacilityLocationTitle());
                    activityReference.get().facilitiesDetailList.add(i, facilitiesDetailModel);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().facilitiesSecondaryAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);


            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class RetrieveArabicFacilityData extends AsyncTask<Void, Void, List<FacilityDetailTableArabic>> {
        private WeakReference<SecondaryListActivity> activityReference;

        int id;

        public RetrieveArabicFacilityData(SecondaryListActivity context, String id) {
            this.activityReference = new WeakReference<>(context);
            this.id = Integer.valueOf(id);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityDetailTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getFacilitiesDetailTableDao().getFacilityDetailArabic(id);
        }

        @Override
        protected void onPostExecute(List<FacilityDetailTableArabic> facilityDetailTableArabics) {
            FacilitiesDetailModel facilitiesDetailModel;
            activityReference.get().facilitiesDetailList.clear();
            if (facilityDetailTableArabics.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().mainTitle,
                        facilityDetailTableArabics.size());
                for (int i = 0; i < facilityDetailTableArabics.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().mainTitle,
                            facilityDetailTableArabics.get(i).getFacilityNid());
                    ArrayList<String> image = new ArrayList<>();
                    image.add(facilityDetailTableArabics.get(i).getFacilityImage());

                    facilitiesDetailModel = new FacilitiesDetailModel(
                            facilityDetailTableArabics.get(i).getFacilityTitle(),
                            image,
                            facilityDetailTableArabics.get(i).getFacilitySubtitle(),
                            facilityDetailTableArabics.get(i).getFacilityDescription(),
                            facilityDetailTableArabics.get(i).getFacilityTiming(),
                            facilityDetailTableArabics.get(i).getFacilityTitleTiming(),
                            facilityDetailTableArabics.get(i).getFacilityNid(),
                            facilityDetailTableArabics.get(i).getFacilityLongitude(),
                            facilityDetailTableArabics.get(i).getFacilityCategoryId(),
                            facilityDetailTableArabics.get(i).getFacilityLatitude(),
                            facilityDetailTableArabics.get(i).getFacilityLocationTitle());
                    activityReference.get().facilitiesDetailList.add(i, facilitiesDetailModel);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().facilitiesSecondaryAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);


            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public void getTourDetailFromAPI(String id) {
        Timber.i("get%sListFromAPI()", mainTitle);
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
                        if (comingFrom.equals(getString(R.string.museum_tours))) {
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
                        }
                        Collections.sort(tourDetailsList);
                        Timber.i("Set %s list with size: %d", mainTitle.toUpperCase(),
                                response.body().size());
                        secondaryListAdapter.notifyDataSetChanged();
                        new TourDetailsRowCount(SecondaryListActivity.this, language, id).execute();
                    } else {
                        Timber.i("%s list have no data", mainTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", mainTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<TourDetailsModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", mainTitle, t.getMessage());
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
        Timber.i("get%sListFromDatabase()", mainTitle);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            progressBar.setVisibility(View.VISIBLE);
            new RetrieveTourDetailsEnglish(SecondaryListActivity.this, id).execute();
        } else {
            new RetrieveTourDetailsArabic(SecondaryListActivity.this, id).execute();
        }
    }

    public static class TourDetailsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<SecondaryListActivity> activityReference;
        String language, tourId;


        TourDetailsRowCount(SecondaryListActivity context, String apiLanguage, String tourId) {
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
                Timber.i("Count: %d", integer);
                new CheckTourDetailsRowExist(activityReference.get(), language, tourId).execute();
            } else {
                Timber.i("%s details table have no data", activityReference.get().comingFrom);
                new InsertDatabaseTask(activityReference.get(),
                        activityReference.get().tourDetailsTableEnglish,
                        activityReference.get().tourDetailsTableArabic, language, tourId).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sDetailsRows%s()", activityReference.get().comingFrom, language.toUpperCase());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                return activityReference.get().qmDatabase.getTourDetailsTaleDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getTourDetailsTaleDao().getNumberOfRowsArabic();
            }
        }
    }

    public static class CheckTourDetailsRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<SecondaryListActivity> activityReference;
        String language, tourId;

        CheckTourDetailsRowExist(SecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().tourDetailsList.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkEnglishIdExist(
                            tourId);
                    if (n > 0) {
                        Timber.i("Row exist in details database(%s) for id: %s", language.toUpperCase(),
                                tourId);
                        new DeleteTourDetailsTableRow(activityReference.get(), language,
                                tourId).execute();
                    } else {
                        Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                                language.toUpperCase(), tourId);
                        new InsertDatabaseTask(activityReference.get(),
                                activityReference.get().tourDetailsTableEnglish,
                                activityReference.get().tourDetailsTableArabic, language, tourId).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkArabicIdExist(
                            tourId);
                    if (n > 0) {
                        Timber.i("Row exist in details database(%s) for id: %s", language.toUpperCase(),
                                tourId);
                        new DeleteTourDetailsTableRow(activityReference.get(), language,
                                tourId).execute();
                    } else {
                        Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                                language.toUpperCase(), tourId);
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
        private WeakReference<SecondaryListActivity> activityReference;
        private TourDetailsTableEnglish tourDetailsTableEnglish;
        private TourDetailsTableArabic tourDetailsTableArabic;
        String language;
        String tourId;

        InsertDatabaseTask(SecondaryListActivity context, TourDetailsTableEnglish tourDetailsTableEnglish,
                           TourDetailsTableArabic tourDetailsTableArabic, String lan, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourDetailsTableEnglish = tourDetailsTableEnglish;
            this.tourDetailsTableArabic = tourDetailsTableArabic;
            language = lan;
            this.tourId = tourId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Insert %s details table(%s) with size: %d", activityReference.get().comingFrom,
                    language.toUpperCase(), activityReference.get().tourDetailsList.size());
            if (activityReference.get().tourDetailsList != null) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().tourDetailsList.size(); i++) {
                        Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                                language.toUpperCase(), activityReference.get().tourDetailsList.get(i).getNid());
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
                                activityReference.get().tourDetailsList.get(i).getSeatsRemaining(),
                                Long.parseLong(activityReference.get().tourDetailsList.get(i).getNid())

                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < activityReference.get().tourDetailsList.size(); i++) {
                        Timber.i("Inserting %s details table(%s) with id: %s", activityReference.get().comingFrom,
                                language.toUpperCase(), activityReference.get().tourDetailsList.get(i).getNid());
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
                                activityReference.get().tourDetailsList.get(i).getSeatsRemaining(),
                                Long.parseLong(activityReference.get().tourDetailsList.get(i).getNid())
                        );
                        activityReference.get().qmDatabase.getTourDetailsTaleDao().
                                insert(tourDetailsTableArabic);

                    }
                }
            }
            return true;
        }
    }

    public static class DeleteTourDetailsTableRow extends AsyncTask<Void, Void, Void> {
        private WeakReference<SecondaryListActivity> activityReference;
        String language, tourId;

        DeleteTourDetailsTableRow(SecondaryListActivity context, String apiLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("DeleteTourDetailsTableRow(%s) with id: %s before insertion", language, tourId);
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
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

    public static class RetrieveTourDetailsEnglish extends AsyncTask<Void, Void, List<TourDetailsTableEnglish>> {
        private WeakReference<SecondaryListActivity> activityReference;
        String tourId;

        RetrieveTourDetailsEnglish(SecondaryListActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<TourDetailsTableEnglish> doInBackground(Void... voids) {
            Timber.i("get%sDetailsEnglishData() for id: %s", activityReference.get().comingFrom,
                    tourId);
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getTourDetailsWithIdEnglish(tourId);
        }

        @Override
        protected void onPostExecute(List<TourDetailsTableEnglish> tourDetailsTableEnglishList) {
            activityReference.get().tourDetailsList.clear();
            Convertor converters = new Convertor();
            if (tourDetailsTableEnglishList.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().mainTitle,
                        tourDetailsTableEnglishList.size());
                for (int i = 0; i < tourDetailsTableEnglishList.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().mainTitle,
                            tourDetailsTableEnglishList.get(i).getItem_id());
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
                activityReference.get().secondaryListAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }
    }

    public static class RetrieveTourDetailsArabic extends AsyncTask<Void, Void, List<TourDetailsTableArabic>> {
        private WeakReference<SecondaryListActivity> activityReference;
        String tourId;

        RetrieveTourDetailsArabic(SecondaryListActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<TourDetailsTableArabic> doInBackground(Void... voids) {
            Timber.i("get%sDetailsArabicData() for id: %s", activityReference.get().comingFrom,
                    tourId);
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getTourDetailsWithIdArabic(tourId);

        }

        @Override
        protected void onPostExecute(List<TourDetailsTableArabic> tourDetailsTableArabicList) {
            activityReference.get().tourDetailsList.clear();
            Convertor converters = new Convertor();
            if (tourDetailsTableArabicList.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().mainTitle,
                        tourDetailsTableArabicList.size());
                for (int i = 0; i < tourDetailsTableArabicList.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().mainTitle,
                            tourDetailsTableArabicList.get(i).getItem_id());
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
                activityReference.get().secondaryListAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (screenName != null)
            mFirebaseAnalytics.setCurrentScreen(this, screenName + getString(R.string.page), null);

    }

}
