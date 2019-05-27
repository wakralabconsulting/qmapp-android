package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.booking.rtlviewpager.RtlViewPager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Converter;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTable;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class ObjectPreviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    View backBtn, shareBtn, locationBtn;
    RecyclerView stepIndicatorRecyclerView;
    private StepIndicatorAdapter stepIndicatorAdapter;
    private List<CurrentIndicatorPosition> currentIndicatorPositionList = new ArrayList<>();
    Animation zoomOutAnimation;
    ProgressBar progressBar;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;
    Intent intent;
    String tourId, museumId;
    String language;
    ViewPager pager;
    ArrayList<ArtifactDetails> artifactList = new ArrayList<>();
    private int currentPosition;
    private LinearLayout rootLayout;
    private QMDatabase qmDatabase;
    private static AsyncTask row;
    private static AsyncTask check;
    private static AsyncTask insert;
    private static AsyncTask update;


    private static Converter converters;
    LinearLayout retryLayout;
    View retryButton;
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
        setContentView(R.layout.activity_object_preview);
        setSupportActionBar(toolbar);
        intent = getIntent();

        Util util = new Util();
        tourId = intent.getStringExtra("TOUR_ID");
        museumId = intent.getStringExtra("MUSEUM_ID");
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.back_btn);
        shareBtn = findViewById(R.id.share_btn);
        locationBtn = findViewById(R.id.location_btn);
        rootLayout = findViewById(R.id.layout_root);
        progressBar = findViewById(R.id.progressBarLoading);
        commonContentLayout = findViewById(R.id.main_content_layout);
        noResultFoundTxt = findViewById(R.id.noResultFoundTxt);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        language = LocaleManager.getLanguage(this);
        pager = (RtlViewPager) findViewById(R.id.pager);
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        assert pager != null;
        stepIndicatorRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);

        RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisablre();
        stepIndicatorRecyclerView.addOnItemTouchListener(disabler);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        qmDatabase = QMDatabase.getInstance(ObjectPreviewActivity.this);
        if (util.isNetworkAvailable(this))
            getObjectPreviewDetailsFromAPI(tourId);
        else
            getObjectPreviewDetailsFromDB(tourId);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                currentIndicatorPositionList.clear();
                CurrentIndicatorPosition c = new CurrentIndicatorPosition(position);
                currentIndicatorPositionList.add(c);
                stepIndicatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        backBtn.setOnClickListener(view -> {
            Timber.i("Back button clicked");
            finish();
        });

        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getObjectPreviewDetailsFromAPI(tourId);
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
        backBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        shareBtn.setOnClickListener(view -> {

        });
        shareBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shareBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        locationBtn.setOnClickListener(view -> {
            Timber.i("Location button clicked");
            if (artifactList.size() > 0) {
                String position = artifactList.get(currentPosition).getArtifactPosition();
                String floorLevel = artifactList.get(currentPosition).getFloorLevel();
                if (position != null && floorLevel != null && !position.equals("") && !floorLevel.equals("")) {
                    contentBundleParams = new Bundle();
                    contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OBJECT PREVIEW LOCATION");
                    contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID,
                            artifactList.get(currentPosition).getNid());
                    mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
                    Intent i = new Intent(ObjectPreviewActivity.this, FloorMapActivity.class);
                    i.putExtra("Position", position);
                    i.putExtra("Level", floorLevel);
                    i.putExtra("TourId", tourId);
                    i.putExtra("RESPONSE", artifactList);
                    startActivity(i);
                    overridePendingTransition(R.anim.flipfadein, R.anim.flipfadeout);
                } else {
                    Snackbar.make(rootLayout, R.string.no_location_data, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        locationBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    locationBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        converters = new Converter();
    }

    public class RecyclerViewDisablre implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width / 5;
        return width;
    }

    public void getObjectPreviewDetailsFromAPI(String id) {
        Timber.i("getObjectPreviewDetailsFromAPI(id: %s, language: %s)", id, language);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ArtifactDetails>> call = apiService.getObjectPreviewDetails(language, id);
        call.enqueue(new Callback<ArrayList<ArtifactDetails>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ArtifactDetails>> call,
                                   @NonNull Response<ArrayList<ArtifactDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getObjectPreviewDetailsFromAPI() - isSuccessful with size: %s", response.body().size());
                        artifactList = response.body();
                        row = new RowCount(ObjectPreviewActivity.this, language, artifactList).execute();
                        setupAdapter();
                    } else {
                        Timber.i("Response have no data");
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("Response not successful");
                    commonContentLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ArtifactDetails>> call, @NonNull Throwable t) {
                Timber.e("getObjectPreviewDetailsFromAPI() - onFailure: %s", t.getMessage());
                commonContentLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void setupAdapter() {
        commonContentLayout.setVisibility(View.VISIBLE);
        if (museumId.equals("63") || museumId.equals("96"))
            locationBtn.setVisibility(View.VISIBLE);
        if (artifactList.size() == 1)
            stepIndicatorRecyclerView.setVisibility(View.GONE);
        if (artifactList.size() < 6)
            stepIndicatorAdapter = new StepIndicatorAdapter(currentIndicatorPositionList, artifactList.size(), getScreenWidth(), artifactList.size());
        else
            stepIndicatorAdapter = new StepIndicatorAdapter(currentIndicatorPositionList, 5, getScreenWidth(), artifactList.size());
        currentIndicatorPositionList.clear();
        CurrentIndicatorPosition c = new CurrentIndicatorPosition(0);
        currentIndicatorPositionList.add(c);
        stepIndicatorAdapter.notifyDataSetChanged();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ObjectPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        stepIndicatorRecyclerView.setLayoutManager(horizontalLayoutManager);
        stepIndicatorRecyclerView.setAdapter(stepIndicatorAdapter);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), artifactList.size(), artifactList));
    }

    public static class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        String language;
        ArtifactTable artifactTable;

        RowCount(ObjectPreviewActivity context, String apiLanguage, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (!(row.isCancelled())) {
                Timber.i("getNumberOfRows(language: %s)", language);
                return activityReference.get().qmDatabase.getArtifactTableDao()
                        .getNumberOfRows(language);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int artifactTableRowCount;
            artifactTableRowCount = integer;
            if (artifactTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                check = new CheckDBRowExist(activityReference.get(), language, artifactList.get()).execute();

            } else {
                Timber.i("Database table have no data");
                insert = new InsertDatabaseTask(activityReference.get(), artifactTable,
                        language, artifactList.get()).execute();

            }

        }
    }

    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        private ArtifactTable artifactTable;
        String language;

        CheckDBRowExist(ObjectPreviewActivity context, String apiLanguage, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!(check.isCancelled())) {
                if (artifactList.get().size() > 0) {
                    for (int i = 0; i < artifactList.get().size(); i++) {
                        int n = activityReference.get().qmDatabase.getArtifactTableDao().checkNidExist(
                                Integer.parseInt(artifactList.get().get(i).getNid()), language);
                        if (n > 0) {
                            Timber.i("Row exist in database(language :%s) for id: %s", language,
                                    activityReference.get().artifactList.get(i).getNid());
                            update = new UpdateArtifactTable(activityReference.get(), language, i, artifactList.get()).execute();

                        } else {
                            Timber.i("Inserting data to Table(language: %s) with id: %s",
                                    language, activityReference.get().artifactList.get(i).getNid());
                            artifactTable = new ArtifactTable(Long.parseLong(artifactList.get().get(i).getNid()),
                                    artifactList.get().get(i).getTitle(),
                                    artifactList.get().get(i).getAccessionNumber(),
                                    artifactList.get().get(i).getTourGuideId(),
                                    artifactList.get().get(i).getMainTitle(),
                                    artifactList.get().get(i).getImage(),
                                    artifactList.get().get(i).getArtifactPosition(),
                                    artifactList.get().get(i).getAudioFile(),
                                    artifactList.get().get(i).getAudioDescriptif(),
                                    artifactList.get().get(i).getCuratorialDescription(),
                                    converters.fromArrayList(artifactList.get().get(i).getImages()),
                                    artifactList.get().get(i).getFloorLevel(),
                                    artifactList.get().get(i).getGalleryNumber(),
                                    artifactList.get().get(i).getObjectHistory(),
                                    artifactList.get().get(i).getProduction(),
                                    artifactList.get().get(i).getProductionDates(),
                                    artifactList.get().get(i).getPeriodStyle(),
                                    artifactList.get().get(i).getArtistCreatorAuthor(),
                                    artifactList.get().get(i).getTechniqueMaterials(),
                                    artifactList.get().get(i).getArtifactNumber(),
                                    artifactList.get().get(i).getDimensions(),
                                    artifactList.get().get(i).getSortId(),
                                    artifactList.get().get(i).getThumbImage(),
                                    language);
                            activityReference.get().qmDatabase.getArtifactTableDao().insertData(artifactTable);

                        }
                    }
                }
            }
            return null;
        }
    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        private ArtifactTable artifactTable;
        String language;

        InsertDatabaseTask(ObjectPreviewActivity context, ArtifactTable artifactTable,
                           String lan, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            this.artifactTable = artifactTable;
            language = lan;
            artifactList = new WeakReference<>(list);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (!(insert.isCancelled())) {
                if (artifactList != null && artifactList.get().size() > 0) {
                    for (int i = 0; i < artifactList.get().size(); i++) {
                        Timber.i("Inserting data to Table(language: %s) with id: %s",
                                language, activityReference.get().artifactList.get(i).getNid());
                        artifactTable = new ArtifactTable(Long.parseLong(artifactList.get().get(i).getNid()),
                                artifactList.get().get(i).getTitle(),
                                artifactList.get().get(i).getAccessionNumber(),
                                artifactList.get().get(i).getTourGuideId(),
                                artifactList.get().get(i).getMainTitle(),
                                artifactList.get().get(i).getImage(),
                                artifactList.get().get(i).getArtifactPosition(),
                                artifactList.get().get(i).getAudioFile(),
                                artifactList.get().get(i).getAudioDescriptif(),
                                artifactList.get().get(i).getCuratorialDescription(),
                                converters.fromArrayList(artifactList.get().get(i).getImages()),
                                artifactList.get().get(i).getFloorLevel(),
                                artifactList.get().get(i).getGalleryNumber(),
                                artifactList.get().get(i).getObjectHistory(),
                                artifactList.get().get(i).getProduction(),
                                artifactList.get().get(i).getProductionDates(),
                                artifactList.get().get(i).getPeriodStyle(),
                                artifactList.get().get(i).getArtistCreatorAuthor(),
                                artifactList.get().get(i).getTechniqueMaterials(),
                                artifactList.get().get(i).getArtifactNumber(),
                                artifactList.get().get(i).getDimensions(),
                                artifactList.get().get(i).getSortId(),
                                artifactList.get().get(i).getThumbImage(),
                                language);
                        activityReference.get().qmDatabase.getArtifactTableDao().insertData(artifactTable);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class UpdateArtifactTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        String language;
        int position;

        UpdateArtifactTable(ObjectPreviewActivity context, String apiLanguage, int p, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!(update.isCancelled())) {
                Timber.i("Updating data on Table(language: %s) for id: %s",
                        language, activityReference.get().artifactList.get(position).getNid());
                activityReference.get().qmDatabase.getArtifactTableDao().updateArtifact(
                        artifactList.get().get(position).getNid(),
                        artifactList.get().get(position).getTitle(),
                        artifactList.get().get(position).getAccessionNumber(),
                        artifactList.get().get(position).getTourGuideId(),
                        artifactList.get().get(position).getMainTitle(),
                        artifactList.get().get(position).getImage(),
                        artifactList.get().get(position).getArtifactPosition(),
                        artifactList.get().get(position).getAudioFile(),
                        artifactList.get().get(position).getAudioDescriptif(),
                        artifactList.get().get(position).getCuratorialDescription(),
                        converters.fromArrayList(artifactList.get().get(position).getImages()),
                        artifactList.get().get(position).getFloorLevel(),
                        artifactList.get().get(position).getGalleryNumber(),
                        artifactList.get().get(position).getObjectHistory(),
                        artifactList.get().get(position).getProduction(),
                        artifactList.get().get(position).getProductionDates(),
                        artifactList.get().get(position).getPeriodStyle(),
                        artifactList.get().get(position).getArtistCreatorAuthor(),
                        artifactList.get().get(position).getTechniqueMaterials(),
                        artifactList.get().get(position).getArtifactNumber(),
                        artifactList.get().get(position).getDimensions(),
                        artifactList.get().get(position).getSortId(),
                        artifactList.get().get(position).getThumbImage(),
                        language
                );
            }
            return null;
        }

    }

    public static class RetrieveArtifactTableData extends AsyncTask<Void, Void, List<ArtifactTable>> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        String tourId;

        RetrieveArtifactTableData(ObjectPreviewActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<ArtifactTable> doInBackground(Void... voids) {
            Timber.i("getDataFromArtifactTableWithId(id: %s, language: %s)", tourId,
                    activityReference.get().language);
            return activityReference.get().qmDatabase.getArtifactTableDao()
                    .getDataFromArtifactTableWithId(tourId, activityReference.get().language);

        }

        @Override
        protected void onPostExecute(List<ArtifactTable> artifactDetailsList) {
            if (artifactDetailsList.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        artifactDetailsList.size());
                activityReference.get().artifactList.clear();
                for (int i = 0; i < artifactDetailsList.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            artifactDetailsList.get(i).getNid());
                    ArtifactDetails artifactDetails = new ArtifactDetails(
                            artifactDetailsList.get(i).getNid(),
                            artifactDetailsList.get(i).getTitle(),
                            artifactDetailsList.get(i).getAccessionNumber(),
                            artifactDetailsList.get(i).getTourGuideId(),
                            artifactDetailsList.get(i).getMainTitle(),
                            artifactDetailsList.get(i).getImage(),
                            artifactDetailsList.get(i).getArtifactPosition(),
                            artifactDetailsList.get(i).getAudioFile(),
                            artifactDetailsList.get(i).getAudioDescription(),
                            artifactDetailsList.get(i).getCuratorialDescription(),
                            converters.fromString(artifactDetailsList.get(i).getImages()),
                            artifactDetailsList.get(i).getFloorLevel(),
                            artifactDetailsList.get(i).getGalleryNumber(),
                            artifactDetailsList.get(i).getObjectHistory(),
                            artifactDetailsList.get(i).getProduction(),
                            artifactDetailsList.get(i).getProductionDates(),
                            artifactDetailsList.get(i).getPeriodStyle(),
                            artifactDetailsList.get(i).getArtistCreatorAuthor(),
                            artifactDetailsList.get(i).getTechniqueMaterials(),
                            artifactDetailsList.get(i).getArtifactNumber(),
                            artifactDetailsList.get(i).getDimensions(),
                            artifactDetailsList.get(i).getSortId(),
                            artifactDetailsList.get(i).getThumbImage());
                    activityReference.get().artifactList.add(i, artifactDetails);
                }
                if (activityReference.get().artifactList.size() > 0) {
                    activityReference.get().setupAdapter();
                } else {
                    activityReference.get().commonContentLayout.setVisibility(View.GONE);
                    activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                }
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().commonContentLayout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    public void getObjectPreviewDetailsFromDB(String tourId) {
        Timber.i("getObjectPreviewDetailsFromDB(id: %s)", tourId);
        progressBar.setVisibility(View.VISIBLE);
        new RetrieveArtifactTableData(ObjectPreviewActivity.this, tourId).execute();
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy()");
        super.onDestroy();
        if (row != null)
            row.cancel(true);
        if (check != null)
            check.cancel(true);
        if (insert != null)
            insert.cancel(true);
        if (update != null)
            update.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAnalytics.setCurrentScreen(this, getString(R.string.object_preview_page), null);
    }
}
