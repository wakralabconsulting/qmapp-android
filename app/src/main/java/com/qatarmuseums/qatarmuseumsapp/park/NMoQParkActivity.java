package com.qatarmuseums.qatarmuseumsapp.park;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;
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
    @BindView(R.id.park_title_label)
    TextView parkTitleLabel;
    @BindView(R.id.park_description)
    TextView parkDescription;
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
    private List<NMoQPark> nmoqPark = new ArrayList<>();
    private String language;
    private NMoQParkListAdapter mAdapter;

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
        }
        mAdapter = new NMoQParkListAdapter(this, parkLists, position -> {
            if (parkLists.get(position).getNid().equals("15616") ||
                    parkLists.get(position).getNid().equals("15616")) {
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


    }

    private void setData(NMoQPark nmoqPark) {
        toolbarTitle.setText(nmoqPark.getMainTitle());
        mainDescription.setText(nmoqPark.getMainDescription());
        parkTitleLabel.setText(nmoqPark.getParkTitle());
        parkDescription.setText(nmoqPark.getParkDescription());
        parkTimingLabel.setText(nmoqPark.getParkHoursTitle());
        parkTimings.setText(nmoqPark.getParksHoursDescription());
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
                        nmoqPark.addAll(response.body());
                        removeHtmlTags(nmoqPark);
                        setData(nmoqPark.get(0));
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
                        mAdapter.notifyDataSetChanged();
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
