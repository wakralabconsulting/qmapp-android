package com.qatarmuseums.qatarmuseumsapp.park;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
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
    SharedPreferences qmPreferences;
    ProgressBar progressBar;
    RelativeLayout noResultFoundLayout;
    LinearLayout mainLayout;
    QMDatabase qmDatabase;
    ParkTableEnglish parkTableEnglish;
    ParkTableArabic parkTableArabic;
    int parkTableRowCount;
    Util utilObject;
    private PullToZoomCoordinatorLayout coordinatorLayout;
    private View zoomView;
    private AppBarLayout appBarLayout;
    private int headerOffSetSize;

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
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (utilObject.isNetworkAvailable(this)) {
            // fetch data from api
            getParkDetailsFromAPI(appLanguage);
        } else {
            // fetch data from database
            getParkDetailsFromDataBase(appLanguage);
        }
        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilObject.checkImageResource(ParkActivity.this, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                } else
                    favIcon.setImageResource(R.drawable.heart_fill);
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        toolbarClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        toolbarClose.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        favIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        favIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
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

    public void getParkDetailsFromAPI(int lan) {
        final String language;
        if (lan == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ParkList>> call = apiService.getParkDetails(language);
        call.enqueue(new Callback<ArrayList<ParkList>>() {
            @Override
            public void onResponse(Call<ArrayList<ParkList>> call, Response<ArrayList<ParkList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        parkLists.addAll(response.body());
                        GlideApp.with(ParkActivity.this)
                                .load(parkLists.get(0).getImage())
                                .centerCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(headerImageView);
                        mAdapter.notifyDataSetChanged();
                        new RowCount(ParkActivity.this, language).execute();
                    } else {
                        mainLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainLayout.setVisibility(View.GONE);
                    noResultFoundLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ParkList>> call, Throwable t) {
                if (t instanceof IOException) {
                    utilObject.showToast(getResources().getString(R.string.check_network), getApplicationContext());
                } else {
                    // due to mapping issues
                }
                mainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getParkDetailsFromDataBase(int language) {
        progressBar.setVisibility(View.VISIBLE);
        if (language == 1)
            new RetriveEnglishTableData(ParkActivity.this, language).execute();
        else
            new RetriveArabicTableData(ParkActivity.this, language).execute();

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


    public class RowCount extends AsyncTask<Void, Void, Integer> {
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
            parkTableRowCount = integer;
            if (parkTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(ParkActivity.this, language).execute();

            } else {
                //create databse
                new InsertDatabaseTask(ParkActivity.this, parkTableEnglish,
                        parkTableArabic, language).execute();

            }

        }
    }

    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
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
            if (parkLists.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getParkTableDao().checkIdExistEnglish(
                                Integer.parseInt(parkLists.get(i).getSortId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(ParkActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            parkTableEnglish = new ParkTableEnglish(parkLists.get(i).getMainTitle(), parkLists.get(i).getShortDescription(),
                                    parkLists.get(i).getImage(), Long.parseLong(parkLists.get(i).getSortId()),
                                    parkLists.get(i).getLatitude(), parkLists.get(i).getLongitude(),
                                    parkLists.get(i).getTimingInfo());
                            activityReference.get().qmDatabase.getParkTableDao().insertEnglishTable(parkTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getParkTableDao().checkIdExistArabic(
                                Integer.parseInt(parkLists.get(i).getSortId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(ParkActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            parkTableArabic = new ParkTableArabic(parkLists.get(i).getMainTitle(), parkLists.get(i).getShortDescription(),
                                    parkLists.get(i).getImage(), Long.parseLong(parkLists.get(i).getSortId()),
                                    parkLists.get(i).getLatitude(), parkLists.get(i).getLongitude(),
                                    parkLists.get(i).getTimingInfo());
                            activityReference.get().qmDatabase.getParkTableDao().insertArabicTable(parkTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
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
            if (parkLists != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < parkLists.size(); i++) {
                        parkTableEnglish = new ParkTableEnglish(parkLists.get(i).getMainTitle(), parkLists.get(i).getShortDescription(),
                                parkLists.get(i).getImage(), Long.parseLong(parkLists.get(i).getSortId()),
                                parkLists.get(i).getLatitude(), parkLists.get(i).getLongitude(),
                                parkLists.get(i).getTimingInfo());
                        activityReference.get().qmDatabase.getParkTableDao().insertEnglishTable(parkTableEnglish);
                    }
                } else {
                    for (int i = 0; i < parkLists.size(); i++) {
                        parkTableArabic = new ParkTableArabic(parkLists.get(i).getMainTitle(), parkLists.get(i).getShortDescription(),
                                parkLists.get(i).getImage(), Long.parseLong(parkLists.get(i).getSortId()),
                                parkLists.get(i).getLatitude(), parkLists.get(i).getLongitude(),
                                parkLists.get(i).getTimingInfo());
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

    public class UpdateHomePageTable extends AsyncTask<Void, Void, Void> {
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
                        parkLists.get(position).getMainTitle(), parkLists.get(position).getShortDescription(),
                        parkLists.get(position).getImage(),
                        parkLists.get(position).getLatitude(), parkLists.get(position).getLongitude(),
                        parkLists.get(position).getTimingInfo(), Long.parseLong(parkLists.get(position).getSortId())
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getParkTableDao().updateParkArabic(
                        parkLists.get(position).getMainTitle(), parkLists.get(position).getShortDescription(),
                        parkLists.get(position).getImage(),
                        parkLists.get(position).getLatitude(), parkLists.get(position).getLongitude(),
                        parkLists.get(position).getTimingInfo(), Long.parseLong(parkLists.get(position).getSortId())
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }


    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<ParkTableEnglish>> {
        private WeakReference<ParkActivity> activityReference;
        int language;

        RetriveEnglishTableData(ParkActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected List<ParkTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getParkTableDao().getAllDataFromParkEnglishTable();

        }

        @Override
        protected void onPostExecute(List<ParkTableEnglish> parkTableEnglishes) {
            if (parkTableEnglishes.size()>0) {
                parkLists.clear();
                for (int i = 0; i < parkTableEnglishes.size(); i++) {
                    ParkList parkObject = new ParkList(parkTableEnglishes.get(i).getMainTitle(),
                            parkTableEnglishes.get(i).getShortDescription(),
                            parkTableEnglishes.get(i).getImage(),
                            parkTableEnglishes.get(i).getLatitude(), parkTableEnglishes.get(i).getLongitude(),
                            parkTableEnglishes.get(i).getTimingInfo(),
                            String.valueOf(parkTableEnglishes.get(i).getSortId()));
                    parkLists.add(i, parkObject);
                }


                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }


        }
    }


    public class RetriveArabicTableData extends AsyncTask<Void, Void, List<ParkTableArabic>> {
        private WeakReference<ParkActivity> activityReference;
        int language;

        RetriveArabicTableData(ParkActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected List<ParkTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getParkTableDao().getAllDataFromParkArabicTable();

        }

        @Override
        protected void onPostExecute(List<ParkTableArabic> parkTableArabics) {
            if (parkTableArabics.size()>0) {
                parkLists.clear();
                for (int i = 0; i < parkTableArabics.size(); i++) {
                    ParkList parkObject = new ParkList(parkTableArabics.get(i).getMainTitle(),
                            parkTableArabics.get(i).getShortDescription(),
                            parkTableArabics.get(i).getImage(),
                            parkTableArabics.get(i).getLatitude(), parkTableArabics.get(i).getLongitude(),
                            parkTableArabics.get(i).getTimingInfo(),
                            String.valueOf(parkTableArabics.get(i).getSortId()));
                    parkLists.add(i, parkObject);
                }


                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }


        }
    }

}
