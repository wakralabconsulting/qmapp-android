package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.booking.rtlviewpager.RtlViewPager;
import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTableArabic;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ObjectPreviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView backBtn, shareBtn, locationBtn;
    RecyclerView stepIndicatorRecyclerView;
    private StepIndicatorAdapter stepIndicatorAdapter;
    private List<CurrentIndicatorPosition> currentIndicatorPositionList = new ArrayList<>();
    Animation zoomOutAnimation;
    ProgressBar progressBar;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;
    Intent intent;
    String tourId;
    int language;
    SharedPreferences qmPreferences;
    private Util util;
    ViewPager pager;
    ArrayList<ArtifactDetails> artifactList = new ArrayList<>();
    private int currentPosition;
    private LinearLayout rootLayout;
    private QMDatabase qmDatabase;
    ArtifactTableEnglish artifactTableEnglish;
    ArtifactTableArabic artifactTableArabic;
    int artifactTableRowCount;
    private Convertor converters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview);
        setSupportActionBar(toolbar);
        intent = getIntent();

        util = new Util();
        tourId = intent.getStringExtra("TOUR_ID");
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.back_btn);
        shareBtn = findViewById(R.id.share_btn);
        locationBtn = findViewById(R.id.location_btn);
        rootLayout = findViewById(R.id.layout_root);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        commonContentLayout = (LinearLayout) findViewById(R.id.main_content_layout);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        pager = (RtlViewPager) findViewById(R.id.pager);
        assert pager != null;
        stepIndicatorRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);

        RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
        stepIndicatorRecyclerView.addOnItemTouchListener(disabler);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        qmDatabase = QMDatabase.getInstance(ObjectPreviewActivity.this);
        if (new Util().isNetworkAvailable(this))
            getObjectPreviewDetailsFromAPI(tourId, language);
        else
            getObjectPreviewDetailsFromDB(tourId, language);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        backBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        shareBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (artifactList.size() > 0) {
                    String position = artifactList.get(currentPosition).getArtifactPosition();
                    String floorLevel = artifactList.get(currentPosition).getFloorLevel();
                    if (position != null && floorLevel != null && !position.equals("") && !floorLevel.equals("")) {
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
            }
        });
        locationBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        locationBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        converters = new Convertor();
    }

    public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

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

    public void getObjectPreviewDetailsFromAPI(String id, int appLanguage) {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ArtifactDetails>> call = apiService.getObjectPreviewDetails(language, id);
        call.enqueue(new Callback<ArrayList<ArtifactDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<ArtifactDetails>> call, Response<ArrayList<ArtifactDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        artifactList = response.body();
                        new RowCount(ObjectPreviewActivity.this, language).execute();
                        setupAdapter();
                    } else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ArtifactDetails>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void setupAdapter() {
        commonContentLayout.setVisibility(View.VISIBLE);
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

    public class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        String language;

        RowCount(ObjectPreviewActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getArtifactTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getArtifactTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            artifactTableRowCount = integer;
            if (artifactTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(ObjectPreviewActivity.this, language).execute();

            } else {
                //create databse
                new InsertDatabaseTask(ObjectPreviewActivity.this, artifactTableEnglish,
                        artifactTableArabic, language).execute();

            }

        }
    }

    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private ArtifactTableEnglish artifactTableEnglish;
        private ArtifactTableArabic artifactTableArabic;
        String language;

        CheckDBRowExist(ObjectPreviewActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (artifactList.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < artifactList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getArtifactTableDao().checkNidExistEnglish(
                                Integer.parseInt(artifactList.get(i).getNid()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateArtifactTable(ObjectPreviewActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            artifactTableEnglish = new ArtifactTableEnglish(Long.parseLong(artifactList.get(i).getNid()),
                                    artifactList.get(i).getTitle(),
                                    artifactList.get(i).getAccessionNumber(),
                                    artifactList.get(i).getTourGuideId(),
                                    artifactList.get(i).getMainTitle(),
                                    artifactList.get(i).getImage(),
                                    artifactList.get(i).getArtifactPosition(),
                                    artifactList.get(i).getAudioFile(),
                                    artifactList.get(i).getAudioDescriptif(),
                                    artifactList.get(i).getCuratorialDescription(),
                                    converters.fromArrayList(artifactList.get(i).getImages()),
                                    artifactList.get(i).getFloorLevel(),
                                    artifactList.get(i).getGalleryNumber(),
                                    artifactList.get(i).getObjectHistory(),
                                    artifactList.get(i).getProduction(),
                                    artifactList.get(i).getProductionDates(),
                                    artifactList.get(i).getPeriodStyle(),
                                    artifactList.get(i).getArtistCreatorAuthor(),
                                    artifactList.get(i).getTechniqueMaterials(),
                                    artifactList.get(i).getArtifactNumber(),
                                    artifactList.get(i).getDimensions(),
                                    artifactList.get(i).getSortId());
                            activityReference.get().qmDatabase.getArtifactTableDao().insertEnglishTable(artifactTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < artifactList.size(); i++) {
                        int n = activityReference.get().qmDatabase.getArtifactTableDao().checkNidExistArabic(
                                Integer.parseInt(artifactList.get(i).getNid()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateArtifactTable(ObjectPreviewActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            artifactTableArabic = new ArtifactTableArabic(Long.parseLong(artifactList.get(i).getNid()),
                                    artifactList.get(i).getTitle(),
                                    artifactList.get(i).getAccessionNumber(),
                                    artifactList.get(i).getTourGuideId(),
                                    artifactList.get(i).getMainTitle(),
                                    artifactList.get(i).getImage(),
                                    artifactList.get(i).getArtifactPosition(),
                                    artifactList.get(i).getAudioFile(),
                                    artifactList.get(i).getAudioDescriptif(),
                                    artifactList.get(i).getCuratorialDescription(),
                                    converters.fromArrayList(artifactList.get(i).getImages()),
                                    artifactList.get(i).getFloorLevel(),
                                    artifactList.get(i).getGalleryNumber(),
                                    artifactList.get(i).getObjectHistory(),
                                    artifactList.get(i).getProduction(),
                                    artifactList.get(i).getProductionDates(),
                                    artifactList.get(i).getPeriodStyle(),
                                    artifactList.get(i).getArtistCreatorAuthor(),
                                    artifactList.get(i).getTechniqueMaterials(),
                                    artifactList.get(i).getArtifactNumber(),
                                    artifactList.get(i).getDimensions(),
                                    artifactList.get(i).getSortId());
                            activityReference.get().qmDatabase.getArtifactTableDao().insertArabicTable(artifactTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        private ArtifactTableEnglish artifactTableEnglish;
        private ArtifactTableArabic artifactTableArabic;
        String language;

        InsertDatabaseTask(ObjectPreviewActivity context, ArtifactTableEnglish artifactTableEnglish,
                           ArtifactTableArabic artifactTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.artifactTableEnglish = artifactTableEnglish;
            this.artifactTableArabic = artifactTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (artifactList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < artifactList.size(); i++) {
                        artifactTableEnglish = new ArtifactTableEnglish(Long.parseLong(artifactList.get(i).getNid()),
                                artifactList.get(i).getTitle(),
                                artifactList.get(i).getAccessionNumber(),
                                artifactList.get(i).getTourGuideId(),
                                artifactList.get(i).getMainTitle(),
                                artifactList.get(i).getImage(),
                                artifactList.get(i).getArtifactPosition(),
                                artifactList.get(i).getAudioFile(),
                                artifactList.get(i).getAudioDescriptif(),
                                artifactList.get(i).getCuratorialDescription(),
                                converters.fromArrayList(artifactList.get(i).getImages()),
                                artifactList.get(i).getFloorLevel(),
                                artifactList.get(i).getGalleryNumber(),
                                artifactList.get(i).getObjectHistory(),
                                artifactList.get(i).getProduction(),
                                artifactList.get(i).getProductionDates(),
                                artifactList.get(i).getPeriodStyle(),
                                artifactList.get(i).getArtistCreatorAuthor(),
                                artifactList.get(i).getTechniqueMaterials(),
                                artifactList.get(i).getArtifactNumber(),
                                artifactList.get(i).getDimensions(),
                                artifactList.get(i).getSortId());
                        activityReference.get().qmDatabase.getArtifactTableDao().insertEnglishTable(artifactTableEnglish);
                    }
                } else {
                    for (int i = 0; i < artifactList.size(); i++) {
                        artifactTableArabic = new ArtifactTableArabic(Long.parseLong(artifactList.get(i).getNid()),
                                artifactList.get(i).getTitle(),
                                artifactList.get(i).getAccessionNumber(),
                                artifactList.get(i).getTourGuideId(),
                                artifactList.get(i).getMainTitle(),
                                artifactList.get(i).getImage(),
                                artifactList.get(i).getArtifactPosition(),
                                artifactList.get(i).getAudioFile(),
                                artifactList.get(i).getAudioDescriptif(),
                                artifactList.get(i).getCuratorialDescription(),
                                converters.fromArrayList(artifactList.get(i).getImages()),
                                artifactList.get(i).getFloorLevel(),
                                artifactList.get(i).getGalleryNumber(),
                                artifactList.get(i).getObjectHistory(),
                                artifactList.get(i).getProduction(),
                                artifactList.get(i).getProductionDates(),
                                artifactList.get(i).getPeriodStyle(),
                                artifactList.get(i).getArtistCreatorAuthor(),
                                artifactList.get(i).getTechniqueMaterials(),
                                artifactList.get(i).getArtifactNumber(),
                                artifactList.get(i).getDimensions(),
                                artifactList.get(i).getSortId());
                        activityReference.get().qmDatabase.getArtifactTableDao().insertArabicTable(artifactTableArabic);

                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


    public class UpdateArtifactTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        String language;
        int position;

        UpdateArtifactTable(ObjectPreviewActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getArtifactTableDao().updateArtifactEnglish(
                        artifactList.get(position).getNid(),
                        artifactList.get(position).getTitle(),
                        artifactList.get(position).getAccessionNumber(),
                        artifactList.get(position).getTourGuideId(),
                        artifactList.get(position).getMainTitle(),
                        artifactList.get(position).getImage(),
                        artifactList.get(position).getArtifactPosition(),
                        artifactList.get(position).getAudioFile(),
                        artifactList.get(position).getAudioDescriptif(),
                        artifactList.get(position).getCuratorialDescription(),
                        converters.fromArrayList(artifactList.get(position).getImages()),
                        artifactList.get(position).getFloorLevel(),
                        artifactList.get(position).getGalleryNumber(),
                        artifactList.get(position).getObjectHistory(),
                        artifactList.get(position).getProduction(),
                        artifactList.get(position).getProductionDates(),
                        artifactList.get(position).getPeriodStyle(),
                        artifactList.get(position).getArtistCreatorAuthor(),
                        artifactList.get(position).getTechniqueMaterials(),
                        artifactList.get(position).getArtifactNumber(),
                        artifactList.get(position).getDimensions(),
                        artifactList.get(position).getSortId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getArtifactTableDao().updateArtifactArabic(
                        artifactList.get(position).getNid(),
                        artifactList.get(position).getTitle(),
                        artifactList.get(position).getAccessionNumber(),
                        artifactList.get(position).getTourGuideId(),
                        artifactList.get(position).getMainTitle(),
                        artifactList.get(position).getImage(),
                        artifactList.get(position).getArtifactPosition(),
                        artifactList.get(position).getAudioFile(),
                        artifactList.get(position).getAudioDescriptif(),
                        artifactList.get(position).getCuratorialDescription(),
                        converters.fromArrayList(artifactList.get(position).getImages()),
                        artifactList.get(position).getFloorLevel(),
                        artifactList.get(position).getGalleryNumber(),
                        artifactList.get(position).getObjectHistory(),
                        artifactList.get(position).getProduction(),
                        artifactList.get(position).getProductionDates(),
                        artifactList.get(position).getPeriodStyle(),
                        artifactList.get(position).getArtistCreatorAuthor(),
                        artifactList.get(position).getTechniqueMaterials(),
                        artifactList.get(position).getArtifactNumber(),
                        artifactList.get(position).getDimensions(),
                        artifactList.get(position).getSortId()
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }

    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<ArtifactTableEnglish>> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        int language;
        String tourId;

        RetriveEnglishTableData(ObjectPreviewActivity context, int appLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.tourId = tourId;
        }

        @Override
        protected List<ArtifactTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getArtifactTableDao().getDataFromArtifactEnglishTable(tourId);

        }

        @Override
        protected void onPostExecute(List<ArtifactTableEnglish> artifactDetailsList) {
            if (artifactDetailsList.size() > 0) {
                artifactList.clear();
                for (int i = 0; i < artifactDetailsList.size(); i++) {
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
                            artifactDetailsList.get(i).getSortId());
                    artifactList.add(i, artifactDetails);
                }
                if (artifactList.size() > 0) {
                    setupAdapter();
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
            }


        }
    }

    public class RetriveArabicTableData extends AsyncTask<Void, Void,
            List<ArtifactTableArabic>> {
        private WeakReference<ObjectPreviewActivity> activityReference;
        int language;
        String tourId;

        RetriveArabicTableData(ObjectPreviewActivity context, int appLanguage, String tourId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.tourId = tourId;
        }


        @Override
        protected List<ArtifactTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getArtifactTableDao().getDataFromArtifactArabicTable(tourId);

        }

        @Override
        protected void onPostExecute(List<ArtifactTableArabic> artifactDetailsList) {
            if (artifactDetailsList.size() > 0) {
                artifactList.clear();
                for (int i = 0; i < artifactDetailsList.size(); i++) {
                    ArtifactDetails artifactDetails = new ArtifactDetails(artifactDetailsList.get(i).getNid(),
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
                            artifactDetailsList.get(i).getSortId());
                    artifactList.add(i, artifactDetails);
                }
                if (artifactList.size() > 0) {
                    setupAdapter();
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getObjectPreviewDetailsFromDB(String tourId, int language) {
        progressBar.setVisibility(View.VISIBLE);
        if (language == 1)
            new RetriveEnglishTableData(ObjectPreviewActivity.this, language, tourId).execute();
        else
            new RetriveArabicTableData(ObjectPreviewActivity.this, language, tourId).execute();
    }

}
