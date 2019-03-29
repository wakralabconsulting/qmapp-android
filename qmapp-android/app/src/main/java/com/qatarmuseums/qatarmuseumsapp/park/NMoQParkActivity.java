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
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableEnglish;
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
    private NMoQParkTableArabic nMoQParkTableArabic;
    private NMoQParkTableEnglish nMoQParkTableEnglish;
    private NMoQParkListTableArabic nMoQParkListTableArabic;
    private NMoQParkListTableEnglish nMoQParkListTableEnglish;

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
        toolbarBack.setOnClickListener(view -> finish());
        if (util.isNetworkAvailable(this)) {
            getNMoQParkFromAPI();
            getNMoQParkListFromAPI();
        } else {
            getNMoQParkFromDataBase();
            getNMoQParkListFromDataBase();
        }
        mAdapter = new NMoQParkListAdapter(this, parkLists, position -> {
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
            if (iconView == 0) {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(NMoQParkActivity.this);
                } else {
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
            getNMoQParkFromAPI();
            getNMoQParkListFromAPI();
            retryLayoutPark.setVisibility(View.GONE);
        });


    }

    private void setData(NMoQPark nmoqPark) {
        toolbarTitle.setText(nmoqPark.getMainTitle());
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
                        detailsLayout.setVisibility(View.VISIBLE);
                        nMoQPark.addAll(response.body());
                        removeHtmlTags(nMoQPark);
                        setData(nMoQPark.get(0));
                        new NMoQParkRowCount(NMoQParkActivity.this, language).execute();
                    } else {
                        detailsLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    detailsLayout.setVisibility(View.GONE);
                    retryLayoutPark.setVisibility(View.VISIBLE);
                }
                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQPark>> call, @NonNull Throwable t) {
                detailsLayout.setVisibility(View.GONE);
                retryLayoutPark.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.GONE);
            }
        });

    }

    public void getNMoQParkListFromAPI() {
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQParkList>> call = apiService.getNMoQParkList(language);
        call.enqueue(new Callback<ArrayList<NMoQParkList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQParkList>> call,
                                   @NonNull Response<ArrayList<NMoQParkList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
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
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishNMoQParkData(NMoQParkActivity.this).execute();
        } else {
            new RetrieveArabicNMoQParkData(NMoQParkActivity.this).execute();
        }
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getNMoQParkTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getNMoQParkTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                new CheckNMoQParkDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertNMoQParkDataToDataBase(activityReference.get(), activityReference.get().nMoQParkTableEnglish,
                        activityReference.get().nMoQParkTableArabic, language).execute();
            }
        }
    }

    public static class CheckNMoQParkDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkTableArabic nMoQParkTableArabic;
        String language;
        private NMoQParkTableEnglish nMoQParkTableEnglish;

        CheckNMoQParkDBRowExist(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().nMoQPark.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().nMoQPark.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkTableDao().checkIdExistEnglish(
                                Integer.parseInt(activityReference.get().nMoQPark.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkTable(activityReference.get(), language).execute();
                        } else {
                            nMoQParkTableEnglish = new NMoQParkTableEnglish(
                                    activityReference.get().nMoQPark.get(i).getNid(),
                                    activityReference.get().nMoQPark.get(i).getMainTitle(),
                                    activityReference.get().nMoQPark.get(i).getMainDescription(),
                                    activityReference.get().nMoQPark.get(i).getParkTitle(),
                                    activityReference.get().nMoQPark.get(i).getParkDescription(),
                                    activityReference.get().nMoQPark.get(i).getParkHoursTitle(),
                                    activityReference.get().nMoQPark.get(i).getParksHoursDescription(),
                                    activityReference.get().nMoQPark.get(i).getLocationTitle(),
                                    activityReference.get().nMoQPark.get(i).getLatitudeNMoQ(),
                                    activityReference.get().nMoQPark.get(i).getLongitudeNMoQ());

                            activityReference.get().qmDatabase.getNMoQParkTableDao().insertEnglishTable(nMoQParkTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().nMoQPark.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkTableDao().checkIdExistArabic(
                                Integer.parseInt(activityReference.get().nMoQPark.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkTable(activityReference.get(), language).execute();
                        } else {

                            nMoQParkTableArabic = new NMoQParkTableArabic(
                                    activityReference.get().nMoQPark.get(i).getNid(),
                                    activityReference.get().nMoQPark.get(i).getMainTitle(),
                                    activityReference.get().nMoQPark.get(i).getMainDescription(),
                                    activityReference.get().nMoQPark.get(i).getParkTitle(),
                                    activityReference.get().nMoQPark.get(i).getParkDescription(),
                                    activityReference.get().nMoQPark.get(i).getParkHoursTitle(),
                                    activityReference.get().nMoQPark.get(i).getParksHoursDescription(),
                                    activityReference.get().nMoQPark.get(i).getLocationTitle(),
                                    activityReference.get().nMoQPark.get(i).getLatitudeNMoQ(),
                                    activityReference.get().nMoQPark.get(i).getLongitudeNMoQ());

                            activityReference.get().qmDatabase.getNMoQParkTableDao().insertArabicTable(nMoQParkTableArabic);
                        }
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                activityReference.get().qmDatabase.getNMoQParkTableDao().updateNMoQParkEnglish(
                        activityReference.get().nMoQPark.get(0).getMainTitle(),
                        activityReference.get().nMoQPark.get(0).getMainDescription(),
                        activityReference.get().nMoQPark.get(0).getParkTitle(),
                        activityReference.get().nMoQPark.get(0).getParkDescription(),
                        activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                        activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                        activityReference.get().nMoQPark.get(0).getLocationTitle(),
                        activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                        activityReference.get().nMoQPark.get(0).getLongitudeNMoQ(),
                        Long.parseLong(activityReference.get().nMoQPark.get(0).getNid())
                );

            } else {
                activityReference.get().qmDatabase.getNMoQParkTableDao().updateNMoQParkArabic(
                        activityReference.get().nMoQPark.get(0).getMainTitle(),
                        activityReference.get().nMoQPark.get(0).getMainDescription(),
                        activityReference.get().nMoQPark.get(0).getParkTitle(),
                        activityReference.get().nMoQPark.get(0).getParkDescription(),
                        activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                        activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                        activityReference.get().nMoQPark.get(0).getLocationTitle(),
                        activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                        activityReference.get().nMoQPark.get(0).getLongitudeNMoQ(),
                        Long.parseLong(activityReference.get().nMoQPark.get(0).getNid())
                );

            }
            return null;
        }
    }

    public static class InsertNMoQParkDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkTableEnglish nMoQParkTableEnglish;
        private NMoQParkTableArabic nMoQParkTableArabic;
        String language;

        InsertNMoQParkDataToDataBase(NMoQParkActivity context, NMoQParkTableEnglish nMoQParkTableEnglish,
                                     NMoQParkTableArabic nMoQParkTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkTableEnglish = nMoQParkTableEnglish;
            this.nMoQParkTableArabic = nMoQParkTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().nMoQPark != null && activityReference.get().nMoQPark.size() > 0) {
                    nMoQParkTableEnglish = new NMoQParkTableEnglish(
                            activityReference.get().nMoQPark.get(0).getNid(),
                            activityReference.get().nMoQPark.get(0).getMainTitle(),
                            activityReference.get().nMoQPark.get(0).getMainDescription(),
                            activityReference.get().nMoQPark.get(0).getParkTitle(),
                            activityReference.get().nMoQPark.get(0).getParkDescription(),
                            activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                            activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                            activityReference.get().nMoQPark.get(0).getLocationTitle(),
                            activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                            activityReference.get().nMoQPark.get(0).getLongitudeNMoQ());
                    activityReference.get().qmDatabase.getNMoQParkTableDao().insertEnglishTable(nMoQParkTableEnglish);
                }
            } else {
                if (activityReference.get().nMoQPark != null && activityReference.get().nMoQPark.size() > 0) {
                    nMoQParkTableArabic = new NMoQParkTableArabic(
                            activityReference.get().nMoQPark.get(0).getNid(),
                            activityReference.get().nMoQPark.get(0).getMainTitle(),
                            activityReference.get().nMoQPark.get(0).getMainDescription(),
                            activityReference.get().nMoQPark.get(0).getParkTitle(),
                            activityReference.get().nMoQPark.get(0).getParkDescription(),
                            activityReference.get().nMoQPark.get(0).getParkHoursTitle(),
                            activityReference.get().nMoQPark.get(0).getParksHoursDescription(),
                            activityReference.get().nMoQPark.get(0).getLocationTitle(),
                            activityReference.get().nMoQPark.get(0).getLatitudeNMoQ(),
                            activityReference.get().nMoQPark.get(0).getLongitudeNMoQ());
                    activityReference.get().qmDatabase.getNMoQParkTableDao().insertArabicTable(nMoQParkTableArabic);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveEnglishNMoQParkData extends AsyncTask<Void, Void, List<NMoQParkTableEnglish>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveEnglishNMoQParkData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkTableDao().getAllDataFromNMoQParkEnglishTable();
        }

        @Override
        protected void onPostExecute(List<NMoQParkTableEnglish> nMoQParkTableEnglishes) {
            NMoQPark nMoQPark;
            if (nMoQParkTableEnglishes.size() > 0) {
                nMoQPark = new NMoQPark(
                        nMoQParkTableEnglishes.get(0).getParkNid(),
                        nMoQParkTableEnglishes.get(0).getParkToolbarTitle(),
                        nMoQParkTableEnglishes.get(0).getMainDescription(),
                        nMoQParkTableEnglishes.get(0).getParkTitle(),
                        nMoQParkTableEnglishes.get(0).getParkTitleDescription(),
                        nMoQParkTableEnglishes.get(0).getParkHoursTitle(),
                        nMoQParkTableEnglishes.get(0).getParksHoursDescription(),
                        nMoQParkTableEnglishes.get(0).getParkLocationTitle(),
                        nMoQParkTableEnglishes.get(0).getParkLatitude(),
                        nMoQParkTableEnglishes.get(0).getParkLongitude());
                activityReference.get().detailsLayout.setVisibility(View.VISIBLE);
                activityReference.get().setData(nMoQPark);
                activityReference.get().progressLoading.setVisibility(View.GONE);
            } else {
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class RetrieveArabicNMoQParkData extends AsyncTask<Void, Void, List<NMoQParkTableArabic>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveArabicNMoQParkData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkTableDao().getAllDataFromNMoQParkArabicTable();
        }

        @Override
        protected void onPostExecute(List<NMoQParkTableArabic> nMoQParkTableArabics) {
            NMoQPark nMoQPark;
            if (nMoQParkTableArabics.size() > 0) {
                nMoQPark = new NMoQPark(
                        nMoQParkTableArabics.get(0).getParkNid(),
                        nMoQParkTableArabics.get(0).getParkToolbarTitle(),
                        nMoQParkTableArabics.get(0).getMainDescription(),
                        nMoQParkTableArabics.get(0).getParkTitle(),
                        nMoQParkTableArabics.get(0).getParkTitleDescription(),
                        nMoQParkTableArabics.get(0).getParkHoursTitle(),
                        nMoQParkTableArabics.get(0).getParksHoursDescription(),
                        nMoQParkTableArabics.get(0).getParkLocationTitle(),
                        nMoQParkTableArabics.get(0).getParkLatitude(),
                        nMoQParkTableArabics.get(0).getParkLongitude());

                activityReference.get().detailsLayout.setVisibility(View.VISIBLE);
                activityReference.get().setData(nMoQPark);
                activityReference.get().progressLoading.setVisibility(View.GONE);
            } else {
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    public void getNMoQParkListFromDataBase() {
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishNMoQParkListData(NMoQParkActivity.this).execute();
        } else {
            new RetrieveArabicNMoQParkListData(NMoQParkActivity.this).execute();
        }
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getNMoQParkListTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getNMoQParkListTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                new CheckNMoQParkListDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertNMoQParkListDataToDataBase(activityReference.get(), activityReference.get().nMoQParkListTableEnglish,
                        activityReference.get().nMoQParkListTableArabic, language).execute();
            }
        }
    }

    public static class CheckNMoQParkListDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkListTableArabic nMoQParkListTableArabic;
        String language;
        private NMoQParkListTableEnglish nMoQParkListTableEnglish;

        CheckNMoQParkListDBRowExist(NMoQParkActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().parkLists.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkListTableDao().checkIdExistEnglish(
                                Integer.parseInt(activityReference.get().parkLists.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkListTable(activityReference.get(), language).execute();
                        } else {
                            nMoQParkListTableEnglish = new NMoQParkListTableEnglish(
                                    activityReference.get().parkLists.get(i).getNid(),
                                    activityReference.get().parkLists.get(i).getMainTitle(),
                                    activityReference.get().parkLists.get(i).getSortId(),
                                    activityReference.get().parkLists.get(i).getImage().get(0));

                            activityReference.get().qmDatabase.getNMoQParkListTableDao().insertEnglishTable(nMoQParkListTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkListTableDao().checkIdExistArabic(
                                Integer.parseInt(activityReference.get().parkLists.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkListTable(activityReference.get(), language).execute();
                        } else {

                            nMoQParkListTableArabic = new NMoQParkListTableArabic(
                                    activityReference.get().parkLists.get(i).getNid(),
                                    activityReference.get().parkLists.get(i).getMainTitle(),
                                    activityReference.get().parkLists.get(i).getSortId(),
                                    activityReference.get().parkLists.get(i).getImage().get(0));

                            activityReference.get().qmDatabase.getNMoQParkListTableDao().insertArabicTable(nMoQParkListTableArabic);
                        }
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                activityReference.get().qmDatabase.getNMoQParkListTableDao().updateNMoQParkListEnglish(
                        activityReference.get().parkLists.get(0).getMainTitle(),
                        activityReference.get().parkLists.get(0).getImage(),
                        activityReference.get().parkLists.get(0).getSortId(),
                        activityReference.get().parkLists.get(0).getNid()
                );

            } else {
                activityReference.get().qmDatabase.getNMoQParkListTableDao().updateNMoQParkListArabic(
                        activityReference.get().parkLists.get(0).getMainTitle(),
                        activityReference.get().parkLists.get(0).getImage(),
                        activityReference.get().parkLists.get(0).getSortId(),
                        activityReference.get().parkLists.get(0).getNid()
                );

            }
            return null;
        }
    }

    public static class InsertNMoQParkListDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<NMoQParkActivity> activityReference;
        private NMoQParkListTableEnglish nMoQParkListTableEnglish;
        private NMoQParkListTableArabic nMoQParkListTableArabic;
        String language;

        InsertNMoQParkListDataToDataBase(NMoQParkActivity context, NMoQParkListTableEnglish nMoQParkListTableEnglish,
                                         NMoQParkListTableArabic nMoQParkListTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkListTableEnglish = nMoQParkListTableEnglish;
            this.nMoQParkListTableArabic = nMoQParkListTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().parkLists != null && activityReference.get().parkLists.size() > 0) {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        nMoQParkListTableEnglish = new NMoQParkListTableEnglish(
                                activityReference.get().parkLists.get(i).getNid(),
                                activityReference.get().parkLists.get(i).getMainTitle(),
                                activityReference.get().parkLists.get(i).getSortId(),
                                activityReference.get().parkLists.get(i).getImage().get(0));
                        activityReference.get().qmDatabase.getNMoQParkListTableDao().insertEnglishTable(nMoQParkListTableEnglish);
                    }
                }
            } else {
                if (activityReference.get().parkLists != null && activityReference.get().parkLists.size() > 0) {
                    for (int i = 0; i < activityReference.get().parkLists.size(); i++) {
                        nMoQParkListTableArabic = new NMoQParkListTableArabic(
                                activityReference.get().parkLists.get(i).getNid(),
                                activityReference.get().parkLists.get(i).getMainTitle(),
                                activityReference.get().parkLists.get(i).getSortId(),
                                activityReference.get().parkLists.get(i).getImage().get(0));
                        activityReference.get().qmDatabase.getNMoQParkListTableDao().insertArabicTable(nMoQParkListTableArabic);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveEnglishNMoQParkListData extends AsyncTask<Void, Void, List<NMoQParkListTableEnglish>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveEnglishNMoQParkListData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkListTableDao().getAllDataFromNMoQParkListEnglishTable();
        }

        @Override
        protected void onPostExecute(List<NMoQParkListTableEnglish> nMoQParkListTableEnglishes) {
            NMoQParkList nMoQParkList;
            if (nMoQParkListTableEnglishes.size() > 0) {
                for (int i = 0; i < nMoQParkListTableEnglishes.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListTableEnglishes.get(i).getParkImages());

                    nMoQParkList = new NMoQParkList(
                            nMoQParkListTableEnglishes.get(i).getParkNid(),
                            nMoQParkListTableEnglishes.get(i).getParkTitle(),
                            nMoQParkListTableEnglishes.get(i).getParkSortId(),
                            image);
                    activityReference.get().parkLists.add(i, nMoQParkList);
                }
                activityReference.get().parksRecyclerView.setVisibility(View.VISIBLE);
                activityReference.get().progressLoading.setVisibility(View.GONE);
                Collections.sort(activityReference.get().parkLists);
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class RetrieveArabicNMoQParkListData extends AsyncTask<Void, Void, List<NMoQParkListTableArabic>> {
        private WeakReference<NMoQParkActivity> activityReference;

        RetrieveArabicNMoQParkListData(NMoQParkActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkListTableDao().getAllDataFromNMoQParkListArabicTable();
        }

        @Override
        protected void onPostExecute(List<NMoQParkListTableArabic> nMoQParkListTableArabics) {
            NMoQParkList nMoQParkList;
            if (nMoQParkListTableArabics.size() > 0) {
                for (int i = 0; i < nMoQParkListTableArabics.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListTableArabics.get(i).getParkImages());
                    nMoQParkList = new NMoQParkList(
                            nMoQParkListTableArabics.get(i).getParkNid(),
                            nMoQParkListTableArabics.get(i).getParkTitle(),
                            nMoQParkListTableArabics.get(i).getParkSortId(),
                            image);
                    activityReference.get().parkLists.add(i, nMoQParkList);
                }
                activityReference.get().parksRecyclerView.setVisibility(View.VISIBLE);
                Collections.sort(activityReference.get().parkLists);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressLoading.setVisibility(View.GONE);
            } else {
                activityReference.get().progressLoading.setVisibility(View.GONE);
                activityReference.get().retryLayoutPark.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        parkMapInfo.onDestroy();
        gMap = null;
        gValue = null;
        super.onDestroy();
    }

}
