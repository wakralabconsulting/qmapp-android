package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.util.ArrayList;
import java.util.List;

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
    LatLng L2_G3_SC14 = new LatLng(25.295566, 51.539397);

    LatLng L3_G10_SC1_1 = new LatLng(25.295230, 51.539170);
    LatLng L3_G10_SC1_2 = new LatLng(25.295245, 51.539210);
    LatLng L3_G11_WR15 = new LatLng(25.295330, 51.539414);
    LatLng L3_G13_5 = new LatLng(25.295664, 51.539330);
    LatLng L3_G13_7 = new LatLng(25.295628, 51.539360);
    LatLng L3_G17_3 = new LatLng(25.295505, 51.538905);

    LatLng G10 = new LatLng(25.295245, 51.539210);


    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GroundOverlay mGroundOverlay;

    private GroundOverlay mGroundOverlayRotated;
    Marker selectedMarker;


    private int mCurrentEntry = 0;
    private CameraPosition position;
    GoogleMap googleMap;
    private LinearLayout level2, level1, levelG;
    private LinearLayout levelPicker;
    private Marker l2_g1_sc3, l2_g3_sc14, l2_g5_sc6, l2_g8, l2_g8_sc1, l2_g8_sc4_1, l2_g8_sc4_2, l2_g8_sc5,
            l2_g8_sc6_1, l2_g8_sc6_2, l2_g9_sc5_1, l2_g9_sc5_2, l2_g9_sc7, g10, l3_g10_sc1_1,
            l3_g10_sc1_2, l3_g11_wr15, l3_g13_5, l3_g13_7, l3_g17_3;
    private int selectedLevel = 2;

    // BottomSheetBehavior variable
    BottomSheetBehavior mBottomSheetBehavior;
    ImageView detailLyout;
    LinearLayout bottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_map);
        levelPicker = (LinearLayout) findViewById(R.id.level_picker);
        level2 = (LinearLayout) findViewById(R.id.level_3);
        level1 = (LinearLayout) findViewById(R.id.level_2);
        levelG = (LinearLayout) findViewById(R.id.level_1);
        detailLyout = (ImageView) findViewById(R.id.detail_layout);
        bottomSheet = (LinearLayout) findViewById(R.id.bottomSheetLayout);


        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        mBottomSheetBehavior.setPeekHeight(dpToPx(170));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                int n = newState;
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        disableLevelPicker();
                        detailLyout.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        disableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        disableLevelPicker();
                        detailLyout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (selectedMarker != null) {
                            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight)));
                            selectedMarker.hideInfoWindow();
                            selectedMarker = null;
                            }
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


    }


    public void showLevel2() {
        l2_g1_sc3.setVisible(true);
        l2_g3_sc14.setVisible(true);
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
        l2_g3_sc14.setVisible(false);
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
                .title("S1.5")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g3_sc14 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_SC14)
                .title("MW.634")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g5_sc6 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G5_SC6)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g8 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC1)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
        l2_g8_sc4_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_1)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc4_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_2)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC5)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g8_sc6_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_1)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
        l2_g8_sc6_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_2)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc5_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_1)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc5_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_2)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l2_g9_sc7 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC7)
                .title("MW.56")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));

        l3_g10_sc1_1 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_1)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l3_g10_sc1_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_2)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l3_g11_wr15 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G11_WR15)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l3_g13_5 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_5)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l3_g13_7 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_7)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));
        l3_g17_3 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G17_3)
                .title("G10")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight))));

        levelG.performClick();

        googleMap.setContentDescription("Google Map with ground overlay.");
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight)));
                    selectedMarker = null;
                }
                selectedMarker = marker;
                selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", largeMapIconWidth, largeMapIconHeight)));
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return false;
            }
        });
        LatLngBounds QM = new LatLngBounds(
                new LatLng(25.294616, 51.538288), new LatLng(25.296323, 51.540369));
        googleMap.setLatLngBoundsForCameraTarget(QM);
        googleMap.setMinZoomPreference(19);
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (selectedMarker != null) {
                        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", normalMapIconWidth, normalMapIconHeight)));
                        selectedMarker.hideInfoWindow();
                        selectedMarker = null;
                    }
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                }
            }
        }
        return super.dispatchTouchEvent(event);

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public void disableLevelPicker(){
        levelG.setEnabled(false);
        level1.setEnabled(false);
        level2.setEnabled(false);
    }
    public void enableLevelPicker(){
        levelG.setEnabled(true);
        level1.setEnabled(true);
        level2.setEnabled(true);
    }
}
