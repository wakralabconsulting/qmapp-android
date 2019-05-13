package com.qatarmuseums.qatarmuseumsapp.park;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NMoQParkActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    View toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.details_layout)
    NestedScrollView detailsLayout;
    @BindView(R.id.progress_loading)
    ProgressBar progressLoading;
    @BindView(R.id.no_result_found_txt)
    TextView noResultFoundTxt;
    @BindView(R.id.main_description)
    TextView mainDescription;
    @BindView(R.id.parks_recycler_view)
    RecyclerView parksRecyclerView;
    @BindView(R.id.retry_layout_park)
    LinearLayout retryLayoutPark;
    @BindView(R.id.retry_btn)
    View retryButton;
    @BindView(R.id.about_park_layout)
    ConstraintLayout aboutParkLayout;
    @BindView(R.id.park_title_label)
    TextView parkTitleLabel;
    @BindView(R.id.park_description)
    TextView parkDescription;
    @BindView(R.id.park_timing_layout)
    ConstraintLayout parkTimingLayout;
    @BindView(R.id.park_timing_label)
    TextView parkTimingLabel;
    @BindView(R.id.park_timings)
    TextView parkTimings;
    @BindView(R.id.park_location_title)
    TextView parkLocationTitle;
    @BindView(R.id.park_map_info)
    MapView parkMapInfo;
    @BindView(R.id.map_view)
    ImageView mapViewToggle;
    @BindView(R.id.direction)
    ImageView mapDirection;
    private List<NMoQParkList> parkLists = new ArrayList<>();
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap gMap, gValue;
    private String latitude, longitude;
    private Util util;
    int iconView = 0;
    private Intent navigationIntent;
    private List<NMoQPark> nMoQPark = new ArrayList<>();
    private String language;
    private NMoQParkListAdapter mAdapter;
    private QMDatabase qmDatabase;
    private NMoQParkTable nMoQParkTable;
    private NMoQParkListTable nMoQParkListTable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmoq_park);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        util = new Util();
        language = LocaleManager.getLanguage(this);
        Animation zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        qmDatabase = QMDatabase.getInstance(NMoQParkActivity.this);

        toolbarBack.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarBack.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarBack.setOnClickListener(view -> {
            Timber.i("Back button clicked");
            finish();
        });
        if (util.isNetworkAvailable(this)) {
            getNMoQParkFromAPI();
            getNMoQParkListFromAPI();
        } else {
            getNMoQParkFromDataBase();
            getNMoQParkListFromDataBase();
        }
        mAdapter = new NMoQParkListAdapter(this, parkLists, position -> {
            Timber.i("%s is clicked with ID: %s from %s",
                    parkLists.get(position).getMainTitle().toUpperCase(),
                    parkLists.get(position).getNid(),
                    toolbarTitle);
            if (parkLists.get(position).getNid().equals("15616") ||
                    parkLists.get(position).getNid().equals("15851")) {
                navigationIntent = new Intent(NMoQParkActivity.this, CollectionDetailsActivity.class);
            } else {
                navigationIntent = new Intent(NMoQParkActivity.this, DetailsActivity.class);
            }
            navigationIntent.putExtra("MAIN_TITLE", parkLists.get(position).getMainTitle());
            navigationIntent.putExtra("COMING_FROM", getIntent().getStringExtra(getString(R.string.toolbar_title_key)));
            navigationIntent.putExtra("NID", parkLists.get(position).getNid());
            startActivity(navigationIntent);
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        parksRecyclerView.setLayoutManager(mLayoutManager);
        parksRecyclerView.setItemAnimator(new DefaultItemAnimator());
        parksRecyclerView.setAdapter(mAdapter);
        parksRecyclerView.setNestedScrollingEnabled(false);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        parkMapInfo.onCreate(mapViewBundle);
        parkMapInfo.getMapAsync(this);
        mapViewToggle.setOnClickListener(view -> {
            Timber.i("Change MAP TYPE button clicked");
            if (iconView == 0) {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(NMoQParkActivity.this);
                } else {
                    Timber.i("MAP TYPE changed to HYBRID");
                    iconView = 1;
                    mapViewToggle.setImageResource(R.drawable.ic_map);
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(NMoQParkActivity.this, R.raw.map_style));

                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            } else {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(NMoQParkActivity.this);
                } else {
                    Timber.i("MAP TYPE changed to NORMAL");
                    iconView = 0;
                    mapViewToggle.setImageResource(R.drawable.ic_satellite);
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(NMoQParkActivity.this, R.raw.map_style));

                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            }
        });

        mapDirection.setOnClickListener(view -> {
            Timber.i("Direction button clicked");
            if (latitude == null || latitude.equals("")) {
                util.showLocationAlertDialog(NMoQParkActivity.this);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&basemap=satellite"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getNMoQParkFromAPI();
            getNMoQParkListFromAPI();
            retryLayoutPark.setVisibility(View.GONE);
        });


    }

    private void setData(NMoQPark nmoqPark) {
        Timber.i("Setting data");
        toolbarTitle.setText(nmoqPark.getMainTitle());
        if (nmoqPark.getMainDescription().trim().equals(""))
            mainDescription.setVisibility(View.GONE);
        else
            mainDescription.setText(nmoqPark.getMainDescription());
        if (nmoqPark.getParkTitle().trim().equals(""))
            aboutParkLayout.setVisibility(View.GONE);
        else {
            parkTitleLabel.setText(nmoqPark.getParkTitle());
            parkDescription.setText(nmoqPark.getParkDescription());
        }
        if (nmoqPark.getParkHoursTitle().trim().equals(""))
            parkTimingLayout.setVisibility(View.GONE);
        else {
            parkTimingLabel.setText(nmoqPark.getParkHoursTitle());
            parkTimings.setText(nmoqPark.getParksHoursDescription());
        }
        parkLocationTitle.setText(nmoqPark.getLocationTitle());
        latitude = nmoqPark.getLatitudeNMoQ();
        longitude = nmoqPark.getLongitudeNMoQ();
        if (latitude != null && !latitude.equals("") && gValue != null) {
            gMap = gValue;
            gMap.setMinZoomPreference(12);
            LatLng ny = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            UiSettings uiSettings = gMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ny);
            gMap.addMarker(markerOptions);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        }

    }

    public void getNMoQParkFromAPI() {
        Timber.i("getNMoQParkFromAPI(language: %s)", language);
        progressLoading.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQPark>> call = apiService.getNMoQPark(language);
        call.enqueue(new Callback<ArrayList<NMoQPark>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQPark>> call,
                                   @NonNull Response<ArrayList<NMoQPark>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getNMoQParkFromAPI() - isSuccessful");
                        detailsLayout.setVisibility(View.VISIBLE);
                        nMoQPark.addAll(response.body());
                        removeHtmlTags(nMoQPark);
                        setData(nMoQPark.get(0));
                        new NMoQParkRowCount(NMoQParkActivity.this, language).execute();
                    } else {
                        Timber.i("Response have no data");
                        detailsLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("Response not successful");
                    detailsLayout.setVisibility(View.GONE);
                    retryLayoutPark.setVisibility(View.VISIBLE);
                }
                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQPark>> call, @NonNull Throwable t) {
                Timber.e("getNMoQParkFromAPI() - onFailure: %s", t.getMessage());
                detailsLayout.setVisibility(View.GONE);
                retryLayoutPark.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.GONE);
            }
        });

    }

    public void getNMoQParkListFromAPI() {
        Timber.i("getNMoQParkListFromAPI(language: %s)", language);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQParkList>> call = apiService.getNMoQParkList(language);
        call.enqueue(new Callback<ArrayList<NMoQParkList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQParkList>> call,
                                   @NonNull Response<ArrayList<NMoQParkList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getNMoQParkListFromAPI() - isSuccessful with size: %s", response.body().size());
                        detailsLayout.setVisibility(View.VISIBLE);
                        parksRecyclerView.setVisibility(View.VISIBLE);
                        parkLists.addAll(response.body());
                        removeListHtmlTags(parkLists);
                        Collections.sort(parkLists);
                        mAdapter.notifyDataSetChanged();
                        new NMoQParkListRowCount(NMoQParkActivity.this, language).execute();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQParkList>> call, @NonNull Throwable t) {
                Timber.e("getNMoQParkListFromAPI() - onFailure: %s", t.getMessage());
            }
        });

    }

    private void removeListHtmlTags(List<NMoQParkList> parkLists) {
        for (int i = 0; i < parkLists.size(); i++) {
            parkLists.get(i).setMainTitle(util.html2string(parkLists.get(i).getMainTitle()));
        }

    }

    private void removeHtmlTags(List<NMoQPark> nMoQParks) {
        for (int i = 0; i < nMoQParks.size(); i++) {
            nMoQParks.get(i).setMainTitle(util.html2string(nMoQParks.get(i).getMainTitle()));
            nMoQParks.get(i).setMainDescription(util.html2string(nMoQParks.get(i).getMainDescription()));
            nMoQParks.get(i).setParkDescription(util.html2string(nMoQParks.get(i).getParkDescription()));
            nMoQParks.get(i).setParkTitle(util.html2string(nMoQParks.get(i).getParkTitle()));
        }

    }

    public void getNMoQParkFromDataBase() {
        Timber.i("getNMoQParkFromDataBase(language: %s)", language);
        new RetrieveNMoQParkData(NMoQParkActivity.this).execute();
    }

    public static class NMoQParkRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;

        NMoQParkRowCount(NMoQParkActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language: %s)", language);
            return activityReference.get().qmDatabase.getNMoQParkTableDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckNMoQParkDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("Database table have no data");
                new InsertNMoQParkDataToDataBase(activityReference.get(),
                        activityReference.get().nMoQParkTable, language).execute();
            }
        }
    }

    public static class CheckNMoQParkDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;
        private NMoQParkTable nMoQParkTable;

        CheckNMoQParkDBRowExist(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().nMoQPark.size() > 0) {
                for (int i = 0; i < activityReference.get().nMoQPark.size(); i++) {
                    int n = activityReference.get().qmDatabase.getNMoQParkTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().nMoQPark.get(i).getNid()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().nMoQPark.get(i).getNid());
                        new UpdateNMoQParkTable(activityReference.get(), language).execute();
                    } else {
                        Timber.i("Inserting data to Table(language :%s) with id: %s",
                                language, activityReference.get().nMoQPark.get(i).getNid());
                        nMoQParkTable = new NMoQParkTable(
                                activityReference.get().nMoQPark.get(i).getNid(),
                                activityReference.get().nMoQPark.get(i).getMainTitle(),
                                activityReference.get().nMoQPark.get(i).getMainDescription(),
                                activityReference.get().nMoQPark.get(i).getParkTitle(),
                                activityReference.get().nMoQPark.get(i).getParkDescription(),
                                activityReference.get().nMoQPark.get(i).getParkHoursTitle(),
                                activityReference.get().nMoQPark.get(i).getParksHoursDescription(),
                                activityReference.get().nMoQPark.get(i).getLocationTitle(),
                                activityReference.get().nMoQPark.get(i).getLatitudeNMoQ(),
                                activityReference.get().nMoQPark.get(i).getLongitudeNMoQ(),
                                language);
                        activityReference.get().qmDatabase.getNMoQParkTableDao().insertData(nMoQParkTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateNMoQParkTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;

        UpdateNMoQParkTable(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating data on Table(language :%s) for id: %s",
                    language, activityReference.get().nMoQPark.get(0).getNid());
            activityReference.get().qmDatabase.getNMoQParkTableDao().updateNMoQPark(
                    activityReference.get().nMoQPark.get(0).getMainTitle(),
                    activityReference.get().nMoQPark.get(0).getMainDescription(),
                    activityReference.get().nMoQPark.get(0).getParkTitle(),
                    activityReference.get().nMoQPark.get(0).getParkDescription(),
                    activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                    activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                    activityReference.get().nMoQPark.get(0).getLocationTitle(),
                    activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                    activityReference.get().nMoQPark.get(0).getLongitudeNMoQ(),
                    Long.parseLong(activityReference.get().nMoQPark.get(0).getNid()),
                    language
            );

            return null;
        }
    }

    public static class InsertNMoQParkDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkTable nMoQParkTable;
        String language;

        InsertNMoQParkDataToDataBase(NMoQParkActivity context, NMoQParkTable nMoQParkTable,
                                     String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkTable = nMoQParkTable;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().nMoQPark != null && activityReference.get().nMoQPark.size() > 0) {
                Timber.i("Inserting data to Table(language :%s) with id: %s",
                        language, activityReference.get().nMoQPark.get(0).getNid());
                nMoQParkTable = new NMoQParkTable(
                        activityReference.get().nMoQPark.get(0).getNid(),
                        activityReference.get().nMoQPark.get(0).getMainTitle(),
                        activityReference.get().nMoQPark.get(0).getMainDescription(),
                        activityReference.get().nMoQPark.get(0).getParkTitle(),
                        activityReference.get().nMoQPark.get(0).getParkDescription(),
                        activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                        activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                        activityReference.get().nMoQPark.get(0).getLocationTitle(),
                        activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                        activityReference.get().nMoQPark.get(0).getLongitudeNMoQ(),
                        language);
                activityReference.get().qmDatabase.getNMoQParkTableDao().insertData(nMoQParkTable);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveNMoQParkData extends AsyncTask<Void, Void, List<NMoQParkTable>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveNMoQParkData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromNMoQParkTable(language :%s)", activityReference.get().language);
            return activityReference.get().qmDatabase.getNMoQParkTableDao()
                    .getAllDataFromNMoQParkTable(activityReference.get().language);
        }

        @Override
        protected void onPostExecute(List<NMoQParkTable> nMoQParkTables) {
            NMoQPark nMoQPark;
            if (nMoQParkTables.size() > 0) {
                Timber.i("Setting data from database with id: %s",
                        nMoQParkTables.get(0).getParkNid());
                nMoQPark = new NMoQPark(
                        nMoQParkTables.get(0).getParkNid(),
                        nMoQParkTables.get(0).getParkToolbarTitle(),
                        nMoQParkTables.get(0).getMainDescription(),
                        nMoQParkTables.get(0).getParkTitle(),
                        nMoQParkTables.get(0).getParkTitleDescription(),
                        nMoQParkTables.get(0).getParkHoursTitle(),
                        nMoQParkTables.get(0).getParksHoursDescription(),
                        nMoQParkTables.get(0).getParkLocationTitle(),
                        nMoQParkTables.get(0).getParkLatitude(),
                        nMoQParkTables.get(0).getParkLongitude());
                activityReference.get().detailsLayout.setVisibility(View.VISIBLE);
                activityReference.get().setData(nMoQPark);
                activityReference.get().progressLoading.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    public void getNMoQParkListFromDataBase() {
        Timber.i("getNMoQParkListFromDataBase(language :%s)", language);
        new RetrieveNMoQParkListData(NMoQParkActivity.this).execute();
    }

    public static class NMoQParkListRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;

        NMoQParkListRowCount(NMoQParkActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language :%s)", language);
            return activityReference.get().qmDatabase.getNMoQParkListTableDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckNMoQParkListDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("Database table have no data");
                new InsertNMoQParkListDataToDataBase(activityReference.get(),
                        activityReference.get().nMoQParkListTable, language).execute();
            }
        }
    }

    public static class CheckNMoQParkListDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;
        private NMoQParkListTable nMoQParkListTable;

        CheckNMoQParkListDBRowExist(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().parkLists.size() > 0) {
                for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                    int n = activityReference.get().qmDatabase.getNMoQParkListTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().parkLists.get(i).getNid()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().parkLists.get(i).getNid());
                        new UpdateNMoQParkListTable(activityReference.get(), language).execute();
                    } else {
                        Timber.i("Inserting data to Table(language :%s) with id: %s",
                                language, activityReference.get().parkLists.get(i).getNid());
                        nMoQParkListTable = new NMoQParkListTable(
                                activityReference.get().parkLists.get(i).getNid(),
                                activityReference.get().parkLists.get(i).getMainTitle(),
                                activityReference.get().parkLists.get(i).getSortId(),
                                activityReference.get().parkLists.get(i).getImage().get(0),
                                language);

                        activityReference.get().qmDatabase.getNMoQParkListTableDao().insertData(nMoQParkListTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateNMoQParkListTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        String language;

        UpdateNMoQParkListTable(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating data on Table(language :%s) for id: %s",
                    language, activityReference.get().parkLists.get(0).getNid());
            activityReference.get().qmDatabase.getNMoQParkListTableDao().updateNMoQParkList(
                    activityReference.get().parkLists.get(0).getMainTitle(),
                    activityReference.get().parkLists.get(0).getImage(),
                    activityReference.get().parkLists.get(0).getSortId(),
                    activityReference.get().parkLists.get(0).getNid(),
                    language
            );
            return null;
        }
    }

    public static class InsertNMoQParkListDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkListTable nMoQParkListTable;
        String language;

        InsertNMoQParkListDataToDataBase(NMoQParkActivity context, NMoQParkListTable nMoQParkListTable,
                                         String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkListTable = nMoQParkListTable;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().parkLists != null && activityReference.get().parkLists.size() > 0) {
                for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                    Timber.i("Inserting data to Table(language :%s) with id: %s",
                            language, activityReference.get().parkLists.get(i).getNid());
                    nMoQParkListTable = new NMoQParkListTable(
                            activityReference.get().parkLists.get(i).getNid(),
                            activityReference.get().parkLists.get(i).getMainTitle(),
                            activityReference.get().parkLists.get(i).getSortId(),
                            activityReference.get().parkLists.get(i).getImage().get(0),
                            language);
                    activityReference.get().qmDatabase.getNMoQParkListTableDao().insertData(nMoQParkListTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveNMoQParkListData extends AsyncTask<Void, Void, List<NMoQParkListTable>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveNMoQParkListData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromNMoQParkListTable(language: %s)", activityReference.get().language);
            return activityReference.get().qmDatabase.getNMoQParkListTableDao()
                    .getAllDataFromNMoQParkListTable(activityReference.get().language);
        }

        @Override
        protected void onPostExecute(List<NMoQParkListTable> nMoQParkListTables) {
            NMoQParkList nMoQParkList;
            if (nMoQParkListTables.size() > 0) {
                for (int i = 0; i < nMoQParkListTables.size(); i++) {
                    Timber.i("Setting data from database with id: %s",
                            nMoQParkListTables.get(i).getParkNid());
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListTables.get(i).getParkImages());

                    nMoQParkList = new NMoQParkList(
                            nMoQParkListTables.get(i).getParkNid(),
                            nMoQParkListTables.get(i).getParkTitle(),
                            nMoQParkListTables.get(i).getParkSortId(),
                            image);
                    activityReference.get().parkLists.add(i, nMoQParkList);
                }
                activityReference.get().parksRecyclerView.setVisibility(View.VISIBLE);
                activityReference.get().progressLoading.setVisibility(View.GONE);
                Collections.sort(activityReference.get().parkLists);
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.i("onMapReady()");
        gValue = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (latitude != null && !latitude.equals("") && gValue != null) {
            gMap = gValue;
            gMap.setMinZoomPreference(12);
            LatLng ny = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            UiSettings uiSettings = gMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ny);
            gMap.addMarker(markerOptions);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        parkMapInfo.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        parkMapInfo.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        parkMapInfo.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        parkMapInfo.onStop();
    }

    @Override
    protected void onPause() {
        parkMapInfo.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy()");
        parkMapInfo.onDestroy();
        gMap = null;
        gValue = null;
        super.onDestroy();
    }

}
