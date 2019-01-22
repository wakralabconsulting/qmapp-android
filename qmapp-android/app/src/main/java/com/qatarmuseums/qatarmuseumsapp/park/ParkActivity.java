package com.qatarmuseums.qatarmuseumsapp.park;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkActivity extends AppCompatActivity implements IPullZoom {

    private List<ParkList> parkLists = new ArrayList<>();
    private ParkListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView headerImageView, favIcon, shareIcon, toolbarClose;
    private Toolbar toolbar;
    private Animation zoomOutAnimation;
    ProgressBar progressBar;
    RelativeLayout noResultFoundLayout;
    LinearLayout mainLayout, retryLayout;
    QMDatabase qmDatabase;
    ParkTableEnglish parkTableEnglish;
    ParkTableArabic parkTableArabic;
    int parkTableRowCount;
    Util utilObject;
    private PullToZoomCoordinatorLayout coordinatorLayout;
    private View zoomView;
    private AppBarLayout appBarLayout;
    private int headerOffSetSize;
    Button retryButton;
    private String appLanguage;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        utilObject = new Util();
        qmDatabase = QMDatabase.getInstance(ParkActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        recyclerView = (RecyclerView) findViewById(R.id.park_recycler_view);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);

        mAdapter = new ParkListAdapter(this, parkLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        appLanguage = LocaleManager.getLanguage(this);
        if (utilObject.isNetworkAvailable(this)) {
            // fetch data from api
            getParkDetailsFromAPI();
        } else {
            // fetch data from database
            getParkDetailsFromDataBase();
        }
        toolbarClose.setOnClickListener(v -> onBackPressed());

        favIcon.setOnClickListener(v -> {
            if (utilObject.checkImageResource(ParkActivity.this, favIcon, R.drawable.heart_fill)) {
                favIcon.setImageResource(R.drawable.heart_empty);
            } else
                favIcon.setImageResource(R.drawable.heart_fill);
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            getParkDetailsFromAPI();
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
        toolbarClose.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        favIcon.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    favIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        shareIcon.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shareIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        initViews();
    }

    private void initViews() {
        coordinatorLayout = (PullToZoomCoordinatorLayout) findViewById(R.id.main_content);
        zoomView = findViewById(R.id.header_img);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        coordinatorLayout.setPullZoom(zoomView, PixelUtil.dp2px(this, 232),
                PixelUtil.dp2px(this, 332), this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });
    }

    public void getParkDetailsFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ParkList>> call = apiService.getParkDetails(appLanguage);
        call.enqueue(new Callback<ArrayList<ParkList>>() {
            @Override
            public void onResponse(Call<ArrayList<ParkList>> call, Response<ArrayList<ParkList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        mainLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        parkLists.addAll(response.body());
                        if (!ParkActivity.this.isFinishing()) {
                            GlideApp.with(ParkActivity.this)
                                    .load(parkLists.get(0).getImage())
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(headerImageView);
                        }
                        mAdapter.notifyDataSetChanged();
                        new RowCount(ParkActivity.this, appLanguage).execute();
                    } else {
                        mainLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ParkList>> call, Throwable t) {
                mainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getParkDetailsFromDataBase() {
        progressBar.setVisibility(View.VISIBLE);
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH))
            new RetriveEnglishTableData(ParkActivity.this).execute();
        else
            new RetriveArabicTableData(ParkActivity.this).execute();

    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    @Override
    public void onPullZooming(int newScrollValue) {

    }

    @Override
    public void onPullZoomEnd() {

    }


    public static class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<ParkActivity> activityReference;
        String language;

        RowCount(ParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getParkTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getParkTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().parkTableRowCount = integer;
            if (activityReference.get().parkTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(activityReference.get(), language).execute();

            } else {
                //create databse
                new InsertDatabaseTask(activityReference.get(), activityReference.get().parkTableEnglish,
                        activityReference.get().parkTableArabic, language).execute();

            }

        }
    }

    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<ParkActivity> activityReference;
        private ParkTableEnglish parkTableEnglish;
        private ParkTableArabic parkTableArabic;
        String language;

        CheckDBRowExist(ParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().parkLists.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getParkTableDao().checkIdExistEnglish(
                                Integer.parseInt(activityReference.get().parkLists.get(i).getSortId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            parkTableEnglish = new ParkTableEnglish(activityReference.get().parkLists.get(i).getMainTitle(),
                                    activityReference.get().parkLists.get(i).getShortDescription(),
                                    activityReference.get().parkLists.get(i).getImage(),
                                    Long.parseLong(activityReference.get().parkLists.get(i).getSortId()),
                                    activityReference.get().parkLists.get(i).getLatitude(),
                                    activityReference.get().parkLists.get(i).getLongitude(),
                                    activityReference.get().parkLists.get(i).getTimingInfo());
                            activityReference.get().qmDatabase.getParkTableDao().insertEnglishTable(parkTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getParkTableDao().checkIdExistArabic(
                                Integer.parseInt(activityReference.get().parkLists.get(i).getSortId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            parkTableArabic = new ParkTableArabic(activityReference.get().parkLists.get(i).getMainTitle(),
                                    activityReference.get().parkLists.get(i).getShortDescription(),
                                    activityReference.get().parkLists.get(i).getImage(),
                                    Long.parseLong(activityReference.get().parkLists.get(i).getSortId()),
                                    activityReference.get().parkLists.get(i).getLatitude(),
                                    activityReference.get().parkLists.get(i).getLongitude(),
                                    activityReference.get().parkLists.get(i).getTimingInfo());
                            activityReference.get().qmDatabase.getParkTableDao().insertArabicTable(parkTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<ParkActivity> activityReference;
        private ParkTableEnglish parkTableEnglish;
        private ParkTableArabic parkTableArabic;
        String language;

        InsertDatabaseTask(ParkActivity context, ParkTableEnglish parkTableEnglish,
                           ParkTableArabic parkTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.parkTableEnglish = parkTableEnglish;
            this.parkTableArabic = parkTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().parkLists != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        parkTableEnglish = new ParkTableEnglish(
                                activityReference.get().parkLists.get(i).getMainTitle(),
                                activityReference.get().parkLists.get(i).getShortDescription(),
                                activityReference.get().parkLists.get(i).getImage(),
                                Long.parseLong(activityReference.get().parkLists.get(i).getSortId()),
                                activityReference.get().parkLists.get(i).getLatitude(),
                                activityReference.get().parkLists.get(i).getLongitude(),
                                activityReference.get().parkLists.get(i).getTimingInfo());
                        activityReference.get().qmDatabase.getParkTableDao().insertEnglishTable(parkTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        parkTableArabic = new ParkTableArabic(
                                activityReference.get().parkLists.get(i).getMainTitle(),
                                activityReference.get().parkLists.get(i).getShortDescription(),
                                activityReference.get().parkLists.get(i).getImage(),
                                Long.parseLong(activityReference.get().parkLists.get(i).getSortId()),
                                activityReference.get().parkLists.get(i).getLatitude(),
                                activityReference.get().parkLists.get(i).getLongitude(),
                                activityReference.get().parkLists.get(i).getTimingInfo());
                        activityReference.get().qmDatabase.getParkTableDao().insertArabicTable(parkTableArabic);

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
        private WeakReference<ParkActivity> activityReference;
        String language;
        int position;

        UpdateHomePageTable(ParkActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getParkTableDao().updateParkEnglish(
                        activityReference.get().parkLists.get(position).getMainTitle(),
                        activityReference.get().parkLists.get(position).getShortDescription(),
                        activityReference.get().parkLists.get(position).getImage(),
                        activityReference.get().parkLists.get(position).getLatitude(),
                        activityReference.get().parkLists.get(position).getLongitude(),
                        activityReference.get().parkLists.get(position).getTimingInfo(),
                        Long.parseLong(activityReference.get().parkLists.get(position).getSortId())
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getParkTableDao().updateParkArabic(
                        activityReference.get().parkLists.get(position).getMainTitle(),
                        activityReference.get().parkLists.get(position).getShortDescription(),
                        activityReference.get().parkLists.get(position).getImage(),
                        activityReference.get().parkLists.get(position).getLatitude(),
                        activityReference.get().parkLists.get(position).getLongitude(),
                        activityReference.get().parkLists.get(position).getTimingInfo(),
                        Long.parseLong(activityReference.get().parkLists.get(position).getSortId())
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }


    public static class RetriveEnglishTableData extends AsyncTask<Void, Void, List<ParkTableEnglish>> {
        private WeakReference<ParkActivity> activityReference;

        RetriveEnglishTableData(ParkActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<ParkTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getParkTableDao().getAllDataFromParkEnglishTable();

        }

        @Override
        protected void onPostExecute(List<ParkTableEnglish> parkTableEnglishes) {
            if (parkTableEnglishes.size() > 0) {
                activityReference.get().parkLists.clear();
                for (int i = 0; i < parkTableEnglishes.size(); i++) {
                    ParkList parkObject = new ParkList(parkTableEnglishes.get(i).getMainTitle(),
                            parkTableEnglishes.get(i).getShortDescription(),
                            parkTableEnglishes.get(i).getImage(),
                            parkTableEnglishes.get(i).getLatitude(), parkTableEnglishes.get(i).getLongitude(),
                            parkTableEnglishes.get(i).getTimingInfo(),
                            String.valueOf(parkTableEnglishes.get(i).getSortId()));
                    activityReference.get().parkLists.add(i, parkObject);
                }


                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }


    public static class RetriveArabicTableData extends AsyncTask<Void, Void, List<ParkTableArabic>> {
        private WeakReference<ParkActivity> activityReference;

        RetriveArabicTableData(ParkActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<ParkTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getParkTableDao().getAllDataFromParkArabicTable();

        }

        @Override
        protected void onPostExecute(List<ParkTableArabic> parkTableArabics) {
            if (parkTableArabics.size() > 0) {
                activityReference.get().parkLists.clear();
                for (int i = 0; i < parkTableArabics.size(); i++) {
                    ParkList parkObject = new ParkList(parkTableArabics.get(i).getMainTitle(),
                            parkTableArabics.get(i).getShortDescription(),
                            parkTableArabics.get(i).getImage(),
                            parkTableArabics.get(i).getLatitude(), parkTableArabics.get(i).getLongitude(),
                            parkTableArabics.get(i).getTimingInfo(),
                            String.valueOf(parkTableArabics.get(i).getSortId()));
                    activityReference.get().parkLists.add(i, parkObject);
                }


                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

}
