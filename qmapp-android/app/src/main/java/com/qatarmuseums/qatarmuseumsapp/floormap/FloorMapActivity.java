package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FloorMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnGroundOverlayClickListener {

    private static final int TRANSPARENCY_MAX = 100;
    private int normalMapIconWidth = 53;
    private int normalMapIconHeight = 67;
    private int largeMapIconWidth = 75;
    private int largeMapIconHeight = 93;

    //    private static final LatLng QM = new LatLng(25.295033, 51.538970);
    private static final LatLng QM = new LatLng(25.294730, 51.539021);
    private static final LatLng QM_CENTER = new LatLng(25.295300, 51.539195);

    LatLng G6_SC2 = new LatLng(25.295193, 51.539105);
    LatLng G6_SC16 = new LatLng(25.295132, 51.539102);
    LatLng G6_14 = new LatLng(25.295093, 51.539125);
    LatLng G10 = new LatLng(25.295245, 51.539210);

    // Science Tour
    LatLng L2_G1_SC3 = new LatLng(25.295141, 51.539185);
    LatLng L2_G8 = new LatLng(25.295500, 51.538855);
    LatLng L2_G8_SC1 = new LatLng(25.295468, 51.538905);
    LatLng L2_G8_SC6_1 = new LatLng(25.295510, 51.538803);
    LatLng L2_G8_SC6_2 = new LatLng(25.295450, 51.538830);
    LatLng L2_G8_SC5 = new LatLng(25.295540, 51.538835);
    LatLng L2_G8_SC4_1 = new LatLng(25.295571, 51.538840);
    LatLng L2_G8_SC4_2 = new LatLng(25.295558, 51.538841);
    LatLng L2_G9_SC7 = new LatLng(25.295643, 51.538895);
    LatLng L2_G9_SC5_1 = new LatLng(25.295654, 51.538918);
    LatLng L2_G9_SC5_2 = new LatLng(25.295652, 51.538927);
    LatLng L2_G5_SC6 = new LatLng(25.295686, 51.539265);
    LatLng L2_G3_SC13 = new LatLng(25.295566, 51.539429);

    LatLng L3_G10_SC1_1 = new LatLng(25.295230, 51.539170);
    LatLng L3_G10_SC1_2 = new LatLng(25.295245, 51.539210);
    LatLng L3_G11_WR15 = new LatLng(25.295330, 51.539414);
    LatLng L3_G13_5 = new LatLng(25.295664, 51.539330);
    LatLng L3_G13_7 = new LatLng(25.295628, 51.539360);
    LatLng L3_G17_3 = new LatLng(25.295505, 51.538905);

    // Highlight Tour
    LatLng L2_G1_SC2 = new LatLng(25.295195, 51.539160);
    LatLng L2_G1_SC7 = new LatLng(25.295215, 51.539395);
    LatLng L2_G1_SC8 = new LatLng(25.295268, 51.539373);
    LatLng L2_G1_SC13 = new LatLng(25.295180, 51.539248);
    LatLng L2_G1_SC14 = new LatLng(25.295205, 51.539319);
    LatLng L2_G2_2 = new LatLng(25.295220, 51.539450);
    LatLng L2_G3_SC14_1 = new LatLng(25.295548, 51.539406);
    LatLng L2_G3_SC14_2 = new LatLng(25.295580, 51.539392);
    LatLng L2_G3_WR4 = new LatLng(25.295540, 51.539470);
    LatLng L2_G4_SC5 = new LatLng(25.295690, 51.539312);
    LatLng L2_G3_SC3 = new LatLng(25.295715, 51.539348);
    LatLng L2_G5_SC5 = new LatLng(25.295715, 51.539205);
    LatLng L2_G5_SC11 = new LatLng(25.295735, 51.539225);
    LatLng L2_G7_SC13 = new LatLng(25.295395, 51.538915);
    LatLng L2_G7_SC8 = new LatLng(25.295345, 51.538880);
    LatLng L2_G7_SC4 = new LatLng(25.295450, 51.538908);

    LatLng L3_G10_WR2_1 = new LatLng(25.295130, 51.539217);
    LatLng L3_G10_WR2_2 = new LatLng(25.295138, 51.539240);
    LatLng L3_G10_PODIUM14 = new LatLng(25.295188, 51.539240);
    LatLng L3_G10_PODIUM9 = new LatLng(25.295222, 51.539333);
    LatLng L3_G11_14 = new LatLng(25.295392, 51.539495);
    LatLng L3_G12_11 = new LatLng(25.295530, 51.539390);
    LatLng L3_G12_12 = new LatLng(25.295492, 51.539405);
    LatLng L3_G12_17 = new LatLng(25.295480, 51.539440);
    LatLng L3_G12_WR5 = new LatLng(25.295540, 51.539470);
    LatLng L3_G13_2 = new LatLng(25.295690, 51.539402);
    LatLng L3_G13_15 = new LatLng(25.295660, 51.539375);
    LatLng L3_G14_7 = new LatLng(25.295693, 51.539270);
    LatLng L3_G14_13 = new LatLng(25.295723, 51.539225);
    LatLng L3_G15_13 = new LatLng(25.295150, 51.539135);
    LatLng L3_G16_WR5 = new LatLng(25.295444, 51.538955);
    LatLng L3_G17_8 = new LatLng(25.295504, 51.538880);
    LatLng L3_G17_9 = new LatLng(25.295490, 51.538850);
    LatLng L3_G18_1 = new LatLng(25.295555, 51.538892);
    LatLng L3_G18_2 = new LatLng(25.295557, 51.538906);
    LatLng L3_G18_11 = new LatLng(25.295613, 51.538914);

    String markerTitle = "";

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GroundOverlay mGroundOverlay;

    private GroundOverlay mGroundOverlayRotated;
    Marker selectedMarker;


    private int mCurrentEntry = 0;
    private CameraPosition position;
    GoogleMap googleMap;
    private LinearLayout level2, level1, levelG;
    private LinearLayout levelPicker;
    private Marker l2_g1_sc3,/* l2_g3_sc14,*/
            l2_g3_sc13, l2_g5_sc6, l2_g8, l2_g8_sc1, l2_g8_sc4_1, l2_g8_sc4_2, l2_g8_sc5,
            l2_g8_sc6_1, l2_g8_sc6_2, l2_g9_sc5_1, l2_g9_sc5_2, l2_g9_sc7, g10, l3_g10_sc1_1,
            l3_g10_sc1_2, l3_g11_wr15, l3_g13_5, l3_g13_7, l3_g17_3;
    private int selectedLevel = 2;

    // BottomSheetBehavior variable
    BottomSheetBehavior mBottomSheetBehavior;
    TextView viewDetails, popupTitle, popupProduction, popupProductionDate, popupPeriodStyle,
            noDataTxt, maiTtitle, shortDescription, image1Description, historyTitle, historyDescription, image2Description;
    String name;
    ImageView numberPad, popupImage, image1, image2, image3, image4;
    private boolean pickerInAboveState = false;
    ImageView qrCode;
    View bottomSheet;
    LinearLayout popupShortlayout, popupLongLayout;
    private Handler mHandler;
    private Runnable mRunnable;

    private RelativeLayout levelPickerRelative;
    private RelativeLayout.LayoutParams params;
    private ImageView imageToZoom;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    private APIInterface apiService;
    private String language;
    ProgressBar progressBar;
    private HashMap<String, ArtifactDetails> artifactDetailsMap;
    private Util utils;
    private ArtifactDetails artifactDetails;
    private CoordinatorLayout floormapLayout;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_map);
        floormapLayout = findViewById(R.id.floor_map_activity);
        levelPicker = (LinearLayout) findViewById(R.id.level_picker);
        level2 = (LinearLayout) findViewById(R.id.level_3);
        level1 = (LinearLayout) findViewById(R.id.level_2);
        levelG = (LinearLayout) findViewById(R.id.level_1);
        levelPickerRelative = (RelativeLayout) findViewById(R.id.level_picker_relative);
        popupLongLayout = (LinearLayout) findViewById(R.id.details_popup_long);
        popupShortlayout = (LinearLayout) findViewById(R.id.details_popup_short);
        popupTitle = findViewById(R.id.popup_title);
        popupImage = findViewById(R.id.popup_image);
        popupProduction = findViewById(R.id.production_txt);
        popupProductionDate = findViewById(R.id.production_date);
        popupPeriodStyle = findViewById(R.id.period_style);
        maiTtitle = findViewById(R.id.title);
        shortDescription = findViewById(R.id.short_description);
        image1 = findViewById(R.id.image_1);
        image1Description = findViewById(R.id.image_desc1);
        historyTitle = findViewById(R.id.history_title);
        historyDescription = findViewById(R.id.history_description);
        image2 = findViewById(R.id.image_2);
        image3 = findViewById(R.id.image_3);
        image4 = findViewById(R.id.image_4);
        image2Description = findViewById(R.id.image_desc2);
        noDataTxt = findViewById(R.id.no_data_text);
        viewDetails = (TextView) findViewById(R.id.view_details_text);
        imageToZoom = (ImageView) findViewById(R.id.image_to_zoom);
        bottomSheet = findViewById(R.id.bottomSheetLayout);
        numberPad = (ImageView) findViewById(R.id.number_pad);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numberPad = (ImageView) findViewById(R.id.number_pad);
        qrCode = (ImageView) findViewById(R.id.scanner);
        progressBar = findViewById(R.id.progress_bar_loading);
        utils = new Util();

        numberPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FloorMapActivity.this, ObjectSearchActivity.class);
                startActivity(intent);
                Intent numberPadIntent = new Intent(FloorMapActivity.this, ObjectSearchActivity.class);
                startActivity(numberPadIntent);
            }
        });
        imageToZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForZoomingImage();
            }
        });

        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrCodeIntent = new Intent(FloorMapActivity.this, BarCodeCaptureActivity.class);
                startActivity(qrCodeIntent);
            }
        });


        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(dpToPx(160));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                int n = newState;
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        disableLevelPicker();
                        popupShortlayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        disableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        popupShortlayout.setVisibility(View.GONE);
                        disableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        checkMarkerStatus();
                        markerTitle = "";
                        enableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        disableLevelPicker();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        popupShortlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = 2;
                position = googleMap.getCameraPosition();
                if (position.zoom > 19)
                    showLevel3();
                hideLevel2();
                level2.setBackgroundColor(getResources().getColor(R.color.white));
                level1.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                levelG.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_3));
            }
        });
        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = 1;
                position = googleMap.getCameraPosition();
                if (position.zoom > 19) {
                    showLevel2();
                }
                hideLevel3();
                level2.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                level1.setBackgroundColor(getResources().getColor(R.color.white));
                levelG.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_2));
            }
        });
        levelG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = 0;
                hideLevel2();
                hideLevel3();
                level2.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                level1.setBackgroundColor(getResources().getColor(R.color.floor_map_buttonbg));
                levelG.setBackgroundColor(getResources().getColor(R.color.white));
                mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_1));
            }
        });

        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        artifactList = new ArrayList<ArtifactDetails>();
        fetchArtifactsFromAPI();
    }

    ArrayList<ArtifactDetails> artifactList;

    public void fetchArtifactsFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        Call<ArrayList<ArtifactDetails>> call = apiService.getArtifactList(language);
        call.enqueue(new Callback<ArrayList<ArtifactDetails>>() {

            @Override
            public void onResponse(Call<ArrayList<ArtifactDetails>> call, Response<ArrayList<ArtifactDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        artifactList.addAll(response.body());
                        artifactDetailsMap = new HashMap<String, ArtifactDetails>();
                        for (int i = 0; i < artifactList.size(); i++) {
                            ArtifactDetails artifactDetails = artifactList.get(i);
                            artifactDetailsMap.put(artifactDetails.getArtifactPosition(), artifactDetails);
                        }

                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ArtifactDetails>> call, Throwable t) {
                if (t instanceof IOException) {
                    utils.showToast(getResources().getString(R.string.check_network), getApplicationContext());
                } else {
                    // error due to mapping issues
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showLevel2() {
        l2_g1_sc3.setVisible(true);
        //l2_g3_sc14.setVisible(true);
        l2_g3_sc13.setVisible(true);
        l2_g5_sc6.setVisible(true);
        l2_g8.setVisible(true);
        l2_g8_sc1.setVisible(true);
        l2_g8_sc4_1.setVisible(true);
        l2_g8_sc4_2.setVisible(true);
        l2_g8_sc5.setVisible(true);
        l2_g8_sc6_1.setVisible(true);
        l2_g8_sc6_2.setVisible(true);
        l2_g9_sc5_1.setVisible(true);
        l2_g9_sc5_2.setVisible(true);
        l2_g9_sc7.setVisible(true);
    }

    public void hideLevel2() {
        l2_g1_sc3.setVisible(false);
        //l2_g3_sc14.setVisible(false);
        l2_g3_sc13.setVisible(false);
        l2_g5_sc6.setVisible(false);
        l2_g8.setVisible(false);
        l2_g8_sc1.setVisible(false);
        l2_g8_sc4_1.setVisible(false);
        l2_g8_sc4_2.setVisible(false);
        l2_g8_sc5.setVisible(false);
        l2_g8_sc6_1.setVisible(false);
        l2_g8_sc6_2.setVisible(false);
        l2_g9_sc5_1.setVisible(false);
        l2_g9_sc5_2.setVisible(false);
        l2_g9_sc7.setVisible(false);
    }

    public void showLevel3() {
        l3_g10_sc1_1.setVisible(true);
        l3_g10_sc1_2.setVisible(true);
        l3_g11_wr15.setVisible(true);
        l3_g13_5.setVisible(true);
        l3_g13_7.setVisible(true);
        l3_g17_3.setVisible(true);
    }

    public void hideLevel3() {
        l3_g10_sc1_1.setVisible(false);
        l3_g10_sc1_2.setVisible(false);
        l3_g11_wr15.setVisible(false);
        l3_g13_5.setVisible(false);
        l3_g13_7.setVisible(false);
        l3_g17_3.setVisible(false);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Register a listener to respond to clicks on GroundOverlays.
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QM_CENTER, 18));
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(FloorMapActivity.this, R.raw.map_style));
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                position = googleMap.getCameraPosition();
                if (position.zoom <= 17.0) {
                    mGroundOverlay.setVisible(false);
                    levelPicker.setVisibility(View.GONE);
                } else {
                    mGroundOverlay.setVisible(true);
                    levelPicker.setVisibility(View.VISIBLE);

                }
                if (position.zoom <= 19.0 && selectedLevel == 1) {
                    hideLevel2();
                } else if (position.zoom > 19 && selectedLevel == 1) {
                    showLevel2();
                } else if (position.zoom <= 19.0 && selectedLevel == 2) {
                    hideLevel3();
                } else if (position.zoom > 19 && selectedLevel == 2) {
                    showLevel3();
                }

            }
        });

        googleMap.setOnGroundOverlayClickListener(this);
        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_1));
        mGroundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(QM, 93f, 106f)
                .bearing(-22));

        l2_g1_sc3 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC3)
                .title("SI.5")
                .snippet("l2_g1_sc3")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("si_5", normalMapIconWidth, normalMapIconHeight))));

//        l2_g3_sc14 = googleMap.addMarker(new MarkerOptions()
//                .position(L2_G3_SC14)
//                .title("MW.634")
//                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_634", normalMapIconWidth, normalMapIconHeight))));
        l2_g3_sc13 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_SC13)
                .title("MW.634")
                .snippet("l2_g3_sc13")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_634", normalMapIconWidth, normalMapIconHeight))));

        l2_g5_sc6 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G5_SC6)
                .title("MW.56")
                .snippet("l2_g5_sc6")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_56", normalMapIconWidth, normalMapIconHeight))));
        l2_g8 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8)
                .title("MS.523")
                .snippet("l2_g8")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_523", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC1)
                .title("MW.548")
                .snippet("l2_g8_sc1")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_548", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc4_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_1)
                .title("MS.650")
                .snippet("l2_g8_sc4_1")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_650", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc4_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_2)
                .title("MS.688")
                .snippet("l2_g8_sc4_2")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_688", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC5)
                .title("MW.361.2007")
                .snippet("l2_g8_sc5")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_361_2007", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc6_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_1)
                .title("MS.709.2010-1")
                .snippet("l2_g8_sc6_1")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_709_2010_1", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc6_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_2)
                .title("MS.709.2010-2")
                .snippet("l2_g8_sc6_2")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_709_2010_2", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc5_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_1)
                .title("MW.340")
                .snippet("l2_g9_sc5_1")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_340", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc5_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_2)
                .title("MS.794")
                .snippet("l2_g9_sc5_2")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_794", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc7 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC7)
                .title("MW.146")
                .snippet("l2_g9_sc7")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mw_146", normalMapIconWidth, normalMapIconHeight))));

        l3_g10_sc1_1 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_1)
                .title("PO.297")
                .snippet("l3_g10_sc1_1")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("po_297", normalMapIconWidth, normalMapIconHeight))));
        l3_g10_sc1_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_2)
                .title("PO.308")
                .snippet("l3_g10_sc1_2")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("po_308", normalMapIconWidth, normalMapIconHeight))));
        l3_g11_wr15 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G11_WR15)
                .title("MS.647.A.59")
                .snippet("l3_g11_wr15")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ms_647_a_59", normalMapIconWidth, normalMapIconHeight))));
        l3_g13_5 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_5)
                .title("GL.322")
                .snippet("l3_g13_5")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("gl_322", normalMapIconWidth, normalMapIconHeight))));
        l3_g13_7 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_7)
                .title("HS.32")
                .snippet("l3_g13_7")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("hs_32", normalMapIconWidth, normalMapIconHeight))));
        l3_g17_3 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G17_3)
                .title("IV.61")
                .snippet("l3_g17_3")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("iv_61", normalMapIconWidth, normalMapIconHeight))));

        levelG.performClick();

        googleMap.setContentDescription("Google Map with ground overlay.");
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (markerTitle.equals(marker.getTitle())) {
//                    Toast.makeText(FloorMapActivity.this, "same Name", Toast.LENGTH_SHORT).show();
                } else {
                    markerTitle = marker.getTitle();
                    if (selectedMarker != null) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(name, normalMapIconWidth, normalMapIconHeight)));
                        selectedMarker = null;
                    }
                    historyTitle.setVisibility(View.GONE);
                    image1.setVisibility(View.GONE);
                    image2.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                    image4.setVisibility(View.GONE);
                    historyDescription.setVisibility(View.GONE);
                    image2Description.setVisibility(View.GONE);
                    if (artifactDetailsMap != null)
                        artifactDetails = artifactDetailsMap.get(marker.getSnippet());
                    if (artifactDetails != null) {
                        noDataTxt.setVisibility(View.GONE);
                        popupLongLayout.setVisibility(View.VISIBLE);
                        popupTitle.setText(utils.html2string(artifactDetails.getTitle()));
                        popupProduction.setText(artifactDetails.getProduction());
                        popupProductionDate.setText(artifactDetails.getProductionDates());
                        popupPeriodStyle.setText(artifactDetails.getPeriodStyle());
                        GlideApp.with(FloorMapActivity.this)
                                .load(artifactDetails.getImage())
                                .placeholder(R.drawable.placeholder_portrait)
                                .centerCrop()
                                .into(popupImage);
                        maiTtitle.setText(utils.html2string(artifactDetails.getMainTitle()));
                        shortDescription.setText(artifactDetails.getCuratorialDescription());
                        GlideApp.with(FloorMapActivity.this)
                                .load(artifactDetails.getImage())
                                .placeholder(R.drawable.placeholder_portrait)
                                .centerInside()
                                .into(imageToZoom);
                        if (artifactDetails.getObjectHistory() != null &&
                                !artifactDetails.getObjectHistory().equals("")) {
                            historyTitle.setVisibility(View.VISIBLE);
                            historyDescription.setVisibility(View.VISIBLE);
                            historyDescription.setText(artifactDetails.getObjectHistory());
                            if (artifactDetails.getObjectENGSummary() != null) {
                                image2Description.setText(utils.html2string(artifactDetails.getObjectENGSummary()));
                                image2Description.setVisibility(View.VISIBLE);
                            }

                        }
                        if (artifactDetails.getImages().size() > 0) {
                            switch (artifactDetails.getImages().size()) {
                                case 1:
                                    setImage1();
                                    break;
                                case 2:
                                    setImage1();
                                    setImage2();
                                    break;
                                case 3:
                                    setImage1();
                                    setImage2();
                                    setImage3();
                                    break;
                                case 4:
                                    setImage1();
                                    setImage2();
                                    setImage3();
                                    setImage4();
                                    break;
                            }
                        }
                    } else {
                        noDataTxt.setVisibility(View.VISIBLE);
                        popupLongLayout.setVisibility(View.GONE);
                    }

                    String temp = marker.getTitle();
                    switch (temp) {
                        case "SI.5":
                            name = "si_5";
                            break;
                        case "MW.634":
                            name = "mw_634";
                            break;
                        case "MW.56":
                            name = "mw_56";
                            break;
                        case "MW.548":
                            name = "mw_548";
                            break;
                        case "MS.523":
                            name = "ms_523";
                            break;
                        case "MS.709.2010-1":
                            name = "ms_709_2010_1";
                            break;
                        case "MS.709.2010-2":
                            name = "ms_709_2010_2";
                            break;
                        case "MW.361.2007":
                            name = "mw_361_2007";
                            break;

                        case "MS.650":
                            name = "ms_650";
                            break;
                        case "MS.688":
                            name = "ms_688";
                            break;
                        case "MW.146":
                            name = "mw_146";
                            break;
                        case "MW.340":
                            name = "mw_340";
                            break;
                        case "MS.794":
                            name = "ms_794";
                            break;
                        case "MS.647.A.59":
                            name = "ms_647_a_59";
                            break;
                        case "HS.32":
                            name = "hs_32";
                            break;
                        case "GL.322":
                            name = "gl_322";
                            break;
                        case "IV.61":
                            name = "iv_61";
                            break;
                        case "PO.297":
                            name = "po_297";
                            break;
                        case "PO.308":
                            name = "po_308";
                            break;
                    }
                    if (noDataTxt.getVisibility() != View.VISIBLE) {
                        if (snackbar != null && snackbar.isShown())
                            snackbar.dismiss();
                        selectedMarker = marker;
                        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(name, largeMapIconWidth, largeMapIconHeight)));
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    } else {
                        snackbar = Snackbar
                                .make(floormapLayout, R.string.no_results_text, Snackbar.LENGTH_SHORT);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.BLACK);
                        sbView.setBackgroundColor(Color.WHITE);
                        snackbar.show();
                    }
                }
                return true;
            }
        });
        LatLngBounds QM = new LatLngBounds(
                new LatLng(25.294616, 51.538288), new LatLng(25.296323, 51.540369));
        googleMap.setLatLngBoundsForCameraTarget(QM);
        googleMap.setMinZoomPreference(19);
        googleMap.getUiSettings().

                setMapToolbarEnabled(false);

    }

    public void setImage1() {
        GlideApp.with(this)
                .load(artifactDetails.getImages().get(0))
                .into(image1);
        image1.setVisibility(View.VISIBLE);
    }

    public void setImage2() {
        GlideApp.with(this)
                .load(artifactDetails.getImages().get(1))
                .into(image2);
        image2.setVisibility(View.VISIBLE);
    }

    public void setImage3() {
        GlideApp.with(this)
                .load(artifactDetails.getImages().get(2))
                .into(image3);
        image3.setVisibility(View.VISIBLE);
    }

    public void setImage4() {
        GlideApp.with(this)
                .load(artifactDetails.getImages().get(3))
                .into(image4);
        image4.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(name, normalMapIconWidth, normalMapIconHeight)));
            selectedMarker = null;
        } else
            super.onBackPressed();

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        // Toggle transparency value between 0.0f and 0.5f. Initial default value is 0.0f.
        mGroundOverlayRotated.setTransparency(0.5f - mGroundOverlayRotated.getTransparency());

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (selectedMarker != null) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                    Rect outRect = new Rect();
                    bottomSheet.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(name, normalMapIconWidth, normalMapIconHeight)));
                        selectedMarker = null;
                    }
                    checkMarkerStatus();
                }


            }


        }

        return super.dispatchTouchEvent(event);

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void disableLevelPicker() {
        levelG.setEnabled(false);
        level1.setEnabled(false);
        level2.setEnabled(false);
    }

    public void enableLevelPicker() {
        levelG.setEnabled(true);
        level1.setEnabled(true);
        level2.setEnabled(true);
    }

    public void checkMarkerStatus() {
        popupShortlayout.setVisibility(View.VISIBLE);
        mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                if (selectedMarker != null) {
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(name, normalMapIconWidth, normalMapIconHeight)));
                        selectedMarker.hideInfoWindow();
                        selectedMarker = null;
                    }
                }
            }
        };
        mHandler.postDelayed(mRunnable, 200);


    }


    public void openDialogForZoomingImage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FloorMapActivity.this/*,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen*/);
        View mView = getLayoutInflater().inflate(R.layout.zooming_layout, null);
        PhotoView photoView = mView.findViewById(R.id.imageView);
        photoView.setImageURI(Uri.parse(artifactDetails.getImage()));
        GlideApp.with(FloorMapActivity.this)
                .load(artifactDetails.getImage())
                .placeholder(R.drawable.placeholder_portrait)
                .into(photoView);

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setCancelable(true);
    }


}
