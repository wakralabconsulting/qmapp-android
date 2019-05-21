package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Converter;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class FloorMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnGroundOverlayClickListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private int normalMapIconWidth = 53;
    private int normalMapIconHeight = 67;

    //    private static final LatLng QM = new LatLng(25.295033, 51.538970);
    private static final LatLng QM = new LatLng(25.294730, 51.539021);
    private static final LatLng QM_CENTER = new LatLng(25.295300, 51.539195);

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
    LatLng L2_G4_SC3 = new LatLng(25.295715, 51.539348);
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

    private final List<BitmapDescriptor> mImages = new ArrayList<>();

    private GroundOverlay mGroundOverlay;

    private GroundOverlay mGroundOverlayRotated;
    Marker selectedMarker;


    private CameraPosition position;
    GoogleMap googleMap;
    private LinearLayout level2, level1, levelG;
    private LinearLayout levelPicker;
    private Marker l2_g1_sc3, l2_g3_sc13, l2_g5_sc6, l2_g8, l2_g8_sc1, l2_g8_sc4_1, l2_g8_sc4_2,
            l2_g8_sc5, l2_g8_sc6_1, l2_g8_sc6_2, l2_g9_sc5_1, l2_g9_sc5_2, l2_g9_sc7, l3_g10_sc1_1,
            l3_g10_sc1_2, l3_g11_wr15, l3_g13_5, l3_g13_7, l3_g17_3,
            l2_g1_sc2, l2_g1_sc13, l2_g1_sc14, l2_g1_sc8, l2_g1_sc7, l2_g2_2, l2_g3_wr4, l2_g3_sc14_1,
            l2_g3_sc14_2, l2_g4_sc3, l2_g4_sc5, l2_g5_sc11, l2_g5_sc5, l2_g7_sc4, l2_g7_sc8,
            l2_g7_sc13, l3_g10_podium9, l3_g10_podium14, l3_g10_wr2_1, l3_g10_wr2_2, l3_g11_14,
            l3_g12_11, l3_g12_12, l3_g12_17, l3_g12_wr5, l3_g13_2, l3_g13_15, l3_g14_13, l3_g14_7,
            l3_g15_13, l3_g16_wr5, l3_g18_1, l3_g18_2, l3_g17_8, l3_g17_9, l3_g18_11;

    private int selectedLevel = 2;

    // BottomSheetBehavior variable
    BottomSheetBehavior mBottomSheetBehavior;
    TextView viewDetails, popupTitle, popupProduction, popupProductionDate, popupPeriodStyle,
            noDataTxt, maiTitle, shortDescription, image1Description, historyTitle, historyDescription, image2Description;
    ImageView numberPad, popupImage, image1, image2, image3, image4;
    View bottomSheet;
    LinearLayout popupShortLayout, popupLongLayout, retryLayout;

    private RelativeLayout floorMapRootLayout;
    private ImageView imageToZoom;
    private String language;
    LinearLayout progressBar;
    private HashMap<String, ArtifactDetails> artifactDetailsMap;
    private HashMap<String, Marker> markerHashMap;
    private Util utils;
    private ArtifactDetails artifactDetails;
    private CoordinatorLayout floorMapLayout;
    private Snackbar snackbar;
    private String artifactPosition;
    private ArrayList<ArtifactDetails> artifactList;

    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int lengthOfAudio;
    final String groundFloorAudioURL = "http://www.qm.org.qa/sites/default/files/floors.mp3";
    final String firstFloorAudioURL = "http://www.qm.org.qa/sites/default/files/floor2.mp3";
    final String secondFloorAudioURL = "http://www.qm.org.qa/sites/default/files/floor3.mp3";
    String audioURL;
    private ImageView btn_play;
    private final Handler handler = new Handler();
    private final Runnable r = this::updateSeekProgress;
    private LinearLayout audioLayout, audioLayoutDetails;
    private Iterator<String> myVeryOwnIterator;
    private boolean playPause;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private String tourId;
    private QMDatabase qmDatabase;

    private static Converter converters;
    View retryButton;
    private Animation zoomOutAnimation, zoomOutAnimationMore;
    private Bitmap resizedBitmap;
    private View backArrow;
    private FirebaseAnalytics mFireBaseAnalytics;
    private Bundle contentBundleParams;
    private TextView toolbarTitle;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_map);
        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_title);
        floorMapLayout = findViewById(R.id.floor_map_activity);
        floorMapRootLayout = findViewById(R.id.floor_map_root);
        levelPicker = findViewById(R.id.level_picker);
        level2 = findViewById(R.id.level_3);
        level1 = findViewById(R.id.level_2);
        levelG = findViewById(R.id.level_1);
        popupLongLayout = findViewById(R.id.details_popup_long);
        popupShortLayout = findViewById(R.id.details_popup_short);
        popupTitle = findViewById(R.id.popup_title);
        popupImage = findViewById(R.id.popup_image);
        popupProduction = findViewById(R.id.production_txt);
        popupProductionDate = findViewById(R.id.production_date);
        popupPeriodStyle = findViewById(R.id.period_style);
        maiTitle = findViewById(R.id.title);
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
        viewDetails = findViewById(R.id.view_details_text);
        imageToZoom = findViewById(R.id.image_to_zoom);
        bottomSheet = findViewById(R.id.bottomSheetLayout);
        numberPad = findViewById(R.id.number_pad);
        progressBar = findViewById(R.id.progress_bar_loading);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        utils = new Util();
        qmDatabase = QMDatabase.getInstance(FloorMapActivity.this);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        artifactDetailsMap = new HashMap<>();
        markerHashMap = new HashMap<>();
        language = LocaleManager.getLanguage(this);
        artifactList = new ArrayList<>();
        tourId = getIntent().getStringExtra("TourId");
        if (getIntent().getParcelableArrayListExtra("RESPONSE") != null) {
            if (utils.isNetworkAvailable(this)) {
                fetchComingData();
            } else {
                floorMapRootLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (utils.isNetworkAvailable(this))
                fetchArtifactsFromAPI();
            else
                fetchArtifactsFromDB();
        }
        numberPad.setOnClickListener(view -> {
            Timber.i("Number pad clicked");
            Intent numberPadIntent = new Intent(FloorMapActivity.this, ObjectSearchActivity.class);
            startActivity(numberPadIntent);
        });
        imageToZoom.setOnClickListener(view -> {
            Timber.i("Image clicked");
            openDialogForZoomingImage();
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(dpToPx(160));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Timber.i("BottomSheet STATE_COLLAPSED");
                        disableLevelPicker();
                        popupShortLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        disableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Timber.i("BottomSheet STATE_EXPANDED");
                        popupShortLayout.setVisibility(View.GONE);
                        disableLevelPicker();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Timber.i("BottomSheet STATE_HIDDEN");
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
        popupShortLayout.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));


        level2.setOnClickListener(v -> {
            Timber.i("Floor Level 3 Clicked");
            contentBundleParams = new Bundle();
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, toolbarTitle.getText().toString());
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, "Level 3");
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            stopAudio();
            audioURL = secondFloorAudioURL;
            selectedLevel = 2;
            position = googleMap.getCameraPosition();
            showLevel3();
            hideLevel2();
            level2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            level1.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            levelG.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_3));
        });
        level1.setOnClickListener(v -> {
            Timber.i("Floor Level 2 Clicked");
            contentBundleParams = new Bundle();
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, toolbarTitle.getText().toString());
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, "Level 2");
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            stopAudio();
            audioURL = firstFloorAudioURL;
            selectedLevel = 1;
            position = googleMap.getCameraPosition();
            showLevel2();
            hideLevel3();
            level2.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            level1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            levelG.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_2));
        });
        levelG.setOnClickListener(v -> {
            Timber.i("Floor Level 1 Clicked");
            contentBundleParams = new Bundle();
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, toolbarTitle.getText().toString());
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, "Level 1");
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            selectedLevel = 0;
            stopAudio();
            hideLevel2();
            hideLevel3();
            showAudioControllerFloorMap(groundFloorAudioURL);
            level2.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            level1.setBackgroundColor(getResources().getColor(R.color.colorFloorMapButtonBackground));
            levelG.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            mGroundOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_1));
        });
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            if (getIntent().getParcelableArrayListExtra("RESPONSE") != null) {
                if (utils.isNetworkAvailable(FloorMapActivity.this)) {
                    fetchComingData();
                    checkForHighlight();
                }
            } else
                fetchArtifactsFromAPI();
        });

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        zoomOutAnimationMore = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(zoomOutAnimationMore);
                    break;
            }
            return false;
        });
        backArrow.setOnClickListener(v -> onBackPressed());

        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        progressBar.setOnClickListener(v -> {

        });
        initialize_Controls();
        converters = new Converter();
    }

    public void fetchComingData() {
        Timber.i("fetchComingData()");
        artifactList.clear();
        artifactList = getIntent().getParcelableArrayListExtra("RESPONSE");
        addDataToHashMap();
        floorMapRootLayout.setVisibility(View.VISIBLE);
        retryLayout.setVisibility(View.GONE);
    }

    private void initialize_Controls() {
        audioLayout = findViewById(R.id.audio_control);
        audioLayoutDetails = findViewById(R.id.audio_control_details_page);

        btn_play = new ImageView(this);
        btn_play.setImageDrawable(getDrawable(R.drawable.play_black));

        seekBar = new SeekBar(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        seekBar.setLayoutParams(lp);
        seekBar.setOnSeekBarChangeListener(this);

        audioLayout.addView(btn_play);
        audioLayout.addView(seekBar);

        audioURL = groundFloorAudioURL;

        btn_play.setOnClickListener(v -> {
            if (utils.isNetworkAvailable(getApplicationContext())) {
                if (!playPause) {
                    Timber.i("Play button clicked");
                    btn_play.setImageDrawable(getDrawable(R.drawable.pause_black));
                    if (initialStage) {
                        new Player().execute(audioURL);
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            playAudio();
                            updateSeekProgress();
                        }
                    }
                    playPause = true;
                } else {
                    Timber.i("Pause button clicked");
                    btn_play.setImageDrawable(getDrawable(R.drawable.play_black));
                    if (mediaPlayer.isPlaying()) {
                        pauseAudio();
                    }
                    playPause = false;
                }
            } else {
                Toast.makeText(FloorMapActivity.this, R.string.check_network, Toast.LENGTH_SHORT).show();
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared;
            try {
                Timber.i("Preparing audio");
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                lengthOfAudio = mediaPlayer.getDuration();
                prepared = true;

            } catch (Exception e) {
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            playAudio();
            updateSeekProgress();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Timber.i("Audio completed");
        initialStage = true;
        playPause = false;
        mediaPlayer.stop();
        mediaPlayer.reset();
        btn_play.setImageDrawable(getDrawable(R.drawable.play_black));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo((lengthOfAudio / 100) * seekBar.getProgress());
        }
    }

    private void updateSeekProgress() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));
            handler.postDelayed(r, 1000);
        }
    }

    private void stopAudio() {
        Timber.i("stopAudio()");
        if (mediaPlayer != null) {
            initialStage = true;
            playPause = false;
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        btn_play.setImageDrawable(getDrawable(R.drawable.play_black));
        seekBar.setProgress(0);
    }

    private void pauseAudio() {
        Timber.i("pauseAudio()");
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        btn_play.setImageDrawable(getDrawable(R.drawable.play_black));
    }

    private void playAudio() {
        Timber.i("playAudio()");
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            contentBundleParams = new Bundle();
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "AUDIO PLAYED");
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, artifactDetails.getNid());
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audio != null) {
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                Toast.makeText(this, R.string.increase_volume, Toast.LENGTH_SHORT).show();
        }
        btn_play.setImageDrawable(getDrawable(R.drawable.pause_black));
    }

    public void fetchArtifactsFromAPI() {
        Timber.i("fetchArtifactsFromAPI(language :%s)", language);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);

        // Commented for temporary
//        Call<ArrayList<ArtifactDetails>> call = apiService.getArtifactList(language);
        Call<ArrayList<ArtifactDetails>> call;
        if (language.equals(LocaleManager.LANGUAGE_ARABIC))
            call = apiService.getObjectPreviewDetails(language, "12916");   // Temporary
        else
            call = apiService.getObjectPreviewDetails(language, "12471");   // Temporary
        call.enqueue(new Callback<ArrayList<ArtifactDetails>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<ArtifactDetails>> call,
                                   @NonNull Response<ArrayList<ArtifactDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        Timber.i("fetchArtifactsFromAPI() - isSuccessful with artifact count: %s",
                                response.body().size());
                        artifactList.addAll(response.body());
                        addDataToHashMap();
                        new RowCount(FloorMapActivity.this, language, artifactList).execute();
                    }
                    floorMapRootLayout.setVisibility(View.VISIBLE);
                    retryLayout.setVisibility(View.GONE);
                } else {
                    Timber.w("fetchArtifactsFromAPI() - response not successful");
                    floorMapRootLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ArtifactDetails>> call, @NonNull Throwable t) {
                Timber.e("fetchArtifactsFromAPI() - onFailure: %s", t.getMessage());
                progressBar.setVisibility(View.GONE);
                floorMapRootLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addDataToHashMap() {
        artifactDetailsMap.clear();
        for (int i = 0; i < artifactList.size(); i++) {
            artifactDetails = artifactList.get(i);
            artifactDetailsMap.put(artifactDetails.getArtifactPosition(), artifactDetails);
        }
    }

    public void checkForHighlight() {
        artifactPosition = getIntent().getStringExtra("Position");
        String floorLevel = getIntent().getStringExtra("Level");
        if (artifactPosition != null) {
            if (floorLevel.equals("2"))
                level1.performClick();
            else
                level2.performClick();
            if (markerHashMap.get(artifactPosition) != null)
                markerClick(markerHashMap.get(artifactPosition));
        }

    }

    public void showLevel2() {
        Timber.i("showLevel2()");
        showCorrespondingMarkers("l2");
    }

    public void hideLevel2() {
        Timber.i("hideLevel2()");
        if (tourId != null && (tourId.equals("12216") || tourId.equals("12226"))) {
            l2_g1_sc3.setVisible(false);
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
        } else {
            l2_g1_sc2.setVisible(false);
            l2_g1_sc3.setVisible(false);
            l2_g1_sc7.setVisible(false);
            l2_g1_sc8.setVisible(false);
            l2_g1_sc13.setVisible(false);
            l2_g1_sc14.setVisible(false);
            l2_g2_2.setVisible(false);
            l2_g3_sc14_1.setVisible(false);
            l2_g3_sc14_2.setVisible(false);
            l2_g3_wr4.setVisible(false);
            l2_g4_sc3.setVisible(false);
            l2_g4_sc5.setVisible(false);
            l2_g5_sc5.setVisible(false);
            l2_g5_sc11.setVisible(false);
            l2_g7_sc4.setVisible(false);
            l2_g7_sc8.setVisible(false);
            l2_g7_sc13.setVisible(false);
            l2_g8_sc1.setVisible(false);
            l2_g8_sc5.setVisible(false);
            l2_g9_sc7.setVisible(false);

        }
    }

    public void showLevel3() {
        Timber.i("showLevel3()");
        showCorrespondingMarkers("l3");
    }

    public void hideLevel3() {
        Timber.i("hideLevel3()");
        if (tourId != null && (tourId.equals("12216") || tourId.equals("12226"))) {
            l3_g10_sc1_1.setVisible(false);
            l3_g10_sc1_2.setVisible(false);
            l3_g11_wr15.setVisible(false);
            l3_g13_5.setVisible(false);
            l3_g13_7.setVisible(false);
            l3_g17_3.setVisible(false);
        } else {
            l3_g10_podium9.setVisible(false);
            l3_g10_podium14.setVisible(false);
            l3_g10_wr2_1.setVisible(false);
            l3_g10_wr2_2.setVisible(false);
            l3_g11_14.setVisible(false);
            l3_g12_11.setVisible(false);
            l3_g12_12.setVisible(false);
            l3_g12_17.setVisible(false);
            l3_g12_wr5.setVisible(false);
            l3_g13_2.setVisible(false);
            l3_g13_15.setVisible(false);
            l3_g14_7.setVisible(false);
            l3_g14_13.setVisible(false);
            l3_g15_13.setVisible(false);
            l3_g16_wr5.setVisible(false);
            l3_g18_1.setVisible(false);
            l3_g18_2.setVisible(false);
            l3_g17_8.setVisible(false);
            l3_g17_9.setVisible(false);
            l3_g18_11.setVisible(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Timber.i("onMapReady()");
        // Register a listener to respond to clicks on GroundOverlays.
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QM_CENTER, 18));
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
        googleMap.setOnCameraMoveListener(() -> {
            position = googleMap.getCameraPosition();
            if (position.zoom <= 17.0) {
                mGroundOverlay.setVisible(false);
                levelPicker.setVisibility(View.GONE);
            } else {
                mGroundOverlay.setVisible(true);
                levelPicker.setVisibility(View.VISIBLE);
            }
        });

        googleMap.setOnGroundOverlayClickListener(this);
        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.qm_level_1));
        int mCurrentEntry = 0;
        mGroundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(QM, 93f, 106f)
                .bearing(-22));
        if (tourId != null && (tourId.equals("12216") || tourId.equals("12226")))
            addScienceTourMarkers();
        else
            addHighlightMarkers();
        levelG.performClick();
        checkForHighlight();

        googleMap.setContentDescription("Google Map with ground overlay.");
        googleMap.setOnMarkerClickListener(marker -> {
            Timber.i("Marker clicked: %s", marker.getTitle());
            if (!markerTitle.equals(marker.getTitle())) {
                markerTitle = marker.getTitle();
                if (selectedMarker != null) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                    markerClick(marker);
            }
            return true;
        });
        LatLngBounds QM = new LatLngBounds(
                new LatLng(25.294616, 51.538288), new LatLng(25.296323, 51.540369));
        googleMap.setLatLngBoundsForCameraTarget(QM);
        googleMap.setMinZoomPreference(19);
        googleMap.getUiSettings().

                setMapToolbarEnabled(false);

    }

    public void addScienceTourMarkers() {
        Timber.i("adding ScienceTour Markers");
        l2_g1_sc3 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC3)
                .title("SI.5")
                .snippet("l2_g1_sc3")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc3", l2_g1_sc3);
        l2_g3_sc13 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_SC13)
                .title("MW.634")
                .snippet("l2_g3_sc13")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g3_sc13", l2_g3_sc13);
        l2_g5_sc6 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G5_SC6)
                .title("MW.56")
                .visible(false)
                .snippet("l2_g5_sc6")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g5_sc6", l2_g5_sc6);

        l2_g8 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8)
                .title("MS.523")
                .snippet("l2_g8")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8", l2_g8);
        l2_g8_sc1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC1)
                .title("MW.548")
                .snippet("l2_g8_sc1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc1", l2_g8_sc1);
        l2_g8_sc4_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_1)
                .title("MS.650")
                .snippet("l2_g8_sc4_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc4_1", l2_g8_sc4_1);
        l2_g8_sc4_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC4_2)
                .title("MS.688")
                .snippet("l2_g8_sc4_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc4_2", l2_g8_sc4_2);
        l2_g8_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC5)
                .title("MW.361.2007")
                .snippet("l2_g8_sc5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc5", l2_g8_sc5);
        l2_g8_sc6_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_1)
                .title("MS.709.2010-1")
                .snippet("l2_g8_sc6_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc6_1", l2_g8_sc6_1);
        l2_g8_sc6_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC6_2)
                .title("MS.709.2010-2")
                .snippet("l2_g8_sc6_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc6_2", l2_g8_sc6_2);
        l2_g9_sc5_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_1)
                .title("MW.340")
                .snippet("l2_g9_sc5_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g9_sc5_1", l2_g9_sc5_1);
        l2_g9_sc5_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC5_2)
                .title("MS.794")
                .snippet("l2_g9_sc5_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g9_sc5_2", l2_g9_sc5_2);
        l2_g9_sc7 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC7)
                .title("MW.146")
                .snippet("l2_g9_sc7")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g9_sc7", l2_g9_sc7);

        l3_g10_sc1_1 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_1)
                .title("PO.297")
                .snippet("l3_g10_sc1_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_sc1_1", l3_g10_sc1_1);
        l3_g10_sc1_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_SC1_2)
                .title("PO.308")
                .snippet("l3_g10_sc1_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_sc1_2", l3_g10_sc1_2);
        l3_g11_wr15 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G11_WR15)
                .title("MS.647.A.59")
                .snippet("l3_g11_wr15")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g11_wr15", l3_g11_wr15);
        l3_g13_5 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_5)
                .title("GL.322")
                .snippet("l3_g13_5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g13_5", l3_g13_5);
        l3_g13_7 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_7)
                .title("HS.32")
                .snippet("l3_g13_7")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g13_7", l3_g13_7);
        l3_g17_3 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G17_3)
                .title("IV.61")
                .snippet("l3_g17_3")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g17_3", l3_g17_3);

        if (resizedBitmap != null && !resizedBitmap.isRecycled())
            resizedBitmap.recycle();
    }

    public void addHighlightMarkers() {
        Timber.i("adding HighlightTour Markers()");
        l2_g1_sc2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC2)
                .title("GL.378")
                .snippet("l2_g1_sc2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc2", l2_g1_sc2);
        l2_g1_sc3 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC3)
                .title("SI.5")
                .snippet("l2_g1_sc3")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc3", l2_g1_sc3);
        l2_g1_sc13 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC13)
                .title("MW.7")
                .snippet("l2_g1_sc13")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc13", l2_g1_sc13);
        l2_g1_sc14 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC14)
                .title("PO.24")
                .snippet("l2_g1_sc14")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc14", l2_g1_sc14);
        l2_g1_sc8 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC8)
                .title("JE.170")
                .visible(false)
                .snippet("l2_g1_sc8")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc8", l2_g1_sc8);
        l2_g1_sc7 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G1_SC7)
                .title("JE.85")
                .visible(false)
                .snippet("l2_g1_sc7")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g1_sc7", l2_g1_sc7);

        l2_g2_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G2_2)
                .title("MS.621")
                .snippet("l2_g2_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g2_2", l2_g2_2);
        l2_g3_wr4 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_WR4)
                .title("SW.59")
                .snippet("l2_g3_wr4")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g3_wr4", l2_g3_wr4);
        l2_g3_sc14_1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_SC14_1)
                .title("MW.221")
                .snippet("l2_g3_sc14_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g3_sc14_1", l2_g3_sc14_1);
        l2_g3_sc14_2 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G3_SC14_2)
                .title("WW.2")
                .snippet("l2_g3_sc14_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g3_sc14_2", l2_g3_sc14_2);
        l2_g4_sc3 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G4_SC3)
                .title("PO.124")
                .snippet("l2_g4_sc3")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g4_sc3", l2_g4_sc3);
        l2_g4_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G4_SC5)
                .title("PO.228")
                .snippet("l2_g4_sc5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g4_sc5", l2_g4_sc5);
        l2_g5_sc11 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G5_SC11)
                .title("PO.788")
                .snippet("l2_g5_sc11")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g5_sc11", l2_g5_sc11);

        l2_g5_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G5_SC5)
                .title("UNKNOWN")
                .snippet("l2_g5_sc5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g5_sc5", l2_g5_sc5);

        l2_g7_sc4 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G7_SC4)
                .title("JE.210")
                .snippet("l2_g7_sc4")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g7_sc4", l2_g7_sc4);
        l2_g7_sc8 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G7_SC8)
                .title("TI.199")
                .snippet("l2_g7_sc8")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g7_sc8", l2_g7_sc8);
        l2_g7_sc13 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G7_SC13)
                .title("MS.196")
                .snippet("l2_g7_sc13")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g7_sc13", l2_g7_sc13);
        l2_g8_sc1 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC1)
                .title("MW.548")
                .snippet("l2_g8_sc1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc1", l2_g8_sc1);
        l2_g8_sc5 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G8_SC5)
                .title("MW.361")
                .snippet("l2_g8_sc5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g8_sc5", l2_g8_sc5);
        l2_g9_sc7 = googleMap.addMarker(new MarkerOptions()
                .position(L2_G9_SC7)
                .title("MW.146")
                .snippet("l2_g9_sc7")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l2_g9_sc7", l2_g9_sc7);

        l3_g10_podium9 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_PODIUM9)
                .title("TE.7")
                .snippet("l3_g10_podium9")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_podium9", l3_g10_podium9);
        l3_g10_podium14 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_PODIUM14)
                .title("CA.22")
                .snippet("l3_g10_podium14")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_podium14", l3_g10_podium14);
        l3_g10_wr2_1 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_WR2_1)
                .title("ww.15")
                .snippet("l3_g10_wr2_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_wr2_1", l3_g10_wr2_1);
        l3_g10_wr2_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G10_WR2_2)
                .title("WW.33")
                .snippet("l3_g10_wr2_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g10_wr2_2", l3_g10_wr2_2);
        l3_g11_14 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G11_14)
                .title("GL.6")
                .snippet("l3_g11_14")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g11_14", l3_g11_14);
        l3_g12_11 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G12_11)
                .title("MW.469")
                .snippet("l3_g12_11")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g12_11", l3_g12_11);
        l3_g12_12 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G12_12)
                .title("SW.74")
                .snippet("l3_g12_12")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g12_12", l3_g12_12);
        l3_g12_17 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G12_17)
                .title("MW.122")
                .snippet("l3_g12_17")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g12_17", l3_g12_17);
        l3_g12_wr5 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G12_WR5)
                .title("CA.77")
                .snippet("l3_g12_wr5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g12_wr5", l3_g12_wr5);
        l3_g13_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_2)
                .title("GL.108")
                .snippet("l3_g13_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g13_2", l3_g13_2);
        l3_g13_15 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G13_15)
                .title("SW.151")
                .snippet("l3_g13_15")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g13_15", l3_g13_15);
        l3_g14_13 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G14_13)
                .title("PO.53")
                .snippet("l3_g14_13")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g14_13", l3_g14_13);
        l3_g14_7 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G14_7)
                .title("PO.215")
                .snippet("l3_g14_7")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g14_7", l3_g14_7);
        l3_g15_13 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G15_13)
                .title("MW.6")
                .snippet("l3_g15_13")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g15_13", l3_g15_13);
        l3_g16_wr5 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G16_WR5)
                .title("C.1810")
                .snippet("l3_g16_wr5")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g16_wr5", l3_g16_wr5);
        l3_g18_1 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G18_1)
                .title("C.1570")
                .snippet("l3_g18_1")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g18_1", l3_g18_1);
        l3_g18_2 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G18_2)
                .title("PO.265")
                .snippet("l3_g18_2")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g18_2", l3_g18_2);
        l3_g17_8 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G17_8)
                .title("JE.180")
                .snippet("l3_g17_8")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g17_8", l3_g17_8);
        l3_g17_9 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G17_9)
                .title("JE.69")
                .snippet("l3_g17_9")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g17_9", l3_g17_9);
        l3_g18_11 = googleMap.addMarker(new MarkerOptions()
                .position(L3_G18_11)
                .title("AA.39")
                .snippet("l3_g18_11")
                .visible(false)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("default_map_marker", normalMapIconWidth, normalMapIconHeight))));
        markerHashMap.put("l3_g18_11", l3_g18_11);
        if (resizedBitmap != null && !resizedBitmap.isRecycled())
            resizedBitmap.recycle();
    }

    public void showCorrespondingMarkers(String level) {
        myVeryOwnIterator = artifactDetailsMap.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            final String key = myVeryOwnIterator.next();
            if (key.contains(level) && markerHashMap.get(key) != null &&
                    !artifactDetailsMap.get(key).getThumbImage().equals("")) {
                markerHashMap.get(key).setVisible(true);
                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .placeholder(R.drawable.default_map_marker)
                        .centerCrop()
                        .load(artifactDetailsMap.get(key).getThumbImage())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<? super Bitmap> transition) {
                                markerHashMap.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(resource)));
                            }
                        });
                if (resizedBitmap != null && !resizedBitmap.isRecycled())
                    resizedBitmap.recycle();
            }
        }
    }

    public void showAudioControllerDetailsPage() {
        audioLayout.removeView(btn_play);
        audioLayout.removeView(seekBar);
        audioLayoutDetails.setPadding(75, 30, 75, 30);
        audioLayoutDetails.removeView(btn_play);
        audioLayoutDetails.removeView(seekBar);
        audioLayoutDetails.addView(btn_play);
        audioLayoutDetails.addView(seekBar);
        stopAudio();
    }

    public void showAudioControllerFloorMap(String audio) {
        audioLayoutDetails.removeView(btn_play);
        audioLayoutDetails.removeView(seekBar);
        audioLayoutDetails.setPadding(0, 0, 0, 0);
        audioLayout.removeView(btn_play);
        audioLayout.removeView(seekBar);
        audioLayout.addView(btn_play);
        audioLayout.addView(seekBar);
        stopAudio();
        audioURL = audio;
    }

    public void markerClick(Marker marker) {
        if (artifactPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 21));
            artifactPosition = null;
        }
        artifactDetails = artifactDetailsMap.get(marker.getSnippet());
        if (artifactDetails != null) {
            contentBundleParams = new Bundle();
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CLICKED ARTIFACTS");
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, artifactDetails.getNid());
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
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
            if (artifactDetails.getAudioFile() != null && !artifactDetails.getAudioFile().equals("")) {
                showAudioControllerDetailsPage();
                audioURL = artifactDetails.getAudioFile();
            }
            maiTitle.setText(utils.html2string(artifactDetails.getMainTitle()));
            shortDescription.setText(artifactDetails.getCuratorialDescription());
            GlideApp.with(FloorMapActivity.this)
                    .load(artifactDetails.getImage())
                    .placeholder(R.drawable.placeholder_portrait)
                    .centerInside()
                    .into(imageToZoom);
            if (artifactDetails.getObjectENGSummary() != null) {
                image2Description.setText(utils.html2string(artifactDetails.getObjectENGSummary()));
                image2Description.setVisibility(View.VISIBLE);
            }
            if (artifactDetails.getObjectHistory() != null &&
                    !artifactDetails.getObjectHistory().equals("")) {
                historyTitle.setVisibility(View.VISIBLE);
                historyDescription.setVisibility(View.VISIBLE);
                historyDescription.setText(artifactDetails.getObjectHistory());

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

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        if (noDataTxt.getVisibility() != View.VISIBLE) {
            if (snackbar != null && snackbar.isShown())
                snackbar.dismiss();
            selectedMarker = marker;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (utils.isNetworkAvailable(this)) {
            snackbar = Snackbar.make(floorMapLayout, R.string.coming_soon_txt, Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
            sbView.setBackgroundColor(Color.WHITE);
            snackbar.show();
        }
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
        Timber.i("onBackPressed()");
        stopAudio();
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            if (selectedLevel == 0)
                showAudioControllerFloorMap(groundFloorAudioURL);
            else if (selectedLevel == 1)
                showAudioControllerFloorMap(firstFloorAudioURL);
            else
                showAudioControllerFloorMap(secondFloorAudioURL);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            selectedMarker = null;
        } else
            super.onBackPressed();

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        if (resizedBitmap != null && !resizedBitmap.isRecycled())
            resizedBitmap.recycle();
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        imageBitmap.recycle();
        return resizedBitmap;
    }

    public Bitmap resizeMapIcons(Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        if (resizedBitmap != null && !resizedBitmap.isRecycled())
            resizedBitmap.recycle();
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, (originalWidth * 3), (originalHeight * 3), false);
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
                        selectedMarker = null;
                        if (audioLayout.getChildCount() == 0) {
                            if (selectedLevel == 0)
                                showAudioControllerFloorMap(groundFloorAudioURL);
                            else if (selectedLevel == 1)
                                showAudioControllerFloorMap(firstFloorAudioURL);
                            else
                                showAudioControllerFloorMap(secondFloorAudioURL);
                        }
                        stopAudio();
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
        popupShortLayout.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        Runnable mRunnable = () -> {
            if (selectedMarker != null) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    selectedMarker.hideInfoWindow();
                    selectedMarker = null;
                }
            }
        };
        mHandler.postDelayed(mRunnable, 200);

    }

    public void openDialogForZoomingImage() {
        Timber.i("openDialogForZoomingImage()");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FloorMapActivity.this/*,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen*/);
        @SuppressLint("InflateParams")
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

    public static class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<FloorMapActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        String language;

        RowCount(FloorMapActivity context, String apiLanguage, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            artifactList = new WeakReference<>(list);
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getArtifactTableDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int artifactTableRowCount;
            artifactTableRowCount = integer;
            if (artifactTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckDBRowExist(activityReference.get(), language, artifactList.get()).execute();

            } else {
                Timber.i("Artifact Table have no data");
                ArtifactTable artifactTable = null;
                new InsertDatabaseTask(activityReference.get(), artifactTable,
                        language, artifactList.get()).execute();
            }

        }
    }

    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<FloorMapActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        private ArtifactTable artifactTable;
        String language;

        CheckDBRowExist(FloorMapActivity context, String apiLanguage, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (artifactList.get().size() > 0) {
                for (int i = 0; i < artifactList.get().size(); i++) {
                    int n = activityReference.get().qmDatabase.getArtifactTableDao().checkNidExist(
                            Integer.parseInt(artifactList.get().get(i).getNid()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().artifactList.get(i).getNid());
                        new UpdateArtifactTable(activityReference.get(), language, i, artifactList.get()).execute();

                    } else {
                        Timber.i("Inserting Artifact Table(language :%s) with id: %s",
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
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<FloorMapActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        private ArtifactTable artifactTable;
        String language;

        InsertDatabaseTask(FloorMapActivity context, ArtifactTable artifactTable,
                           String lan, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            this.artifactTable = artifactTable;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (artifactList != null) {
                for (int i = 0; i < artifactList.get().size(); i++) {
                    Timber.i("Inserting Artifact Table(language :%s) with id: %s",
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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


    public static class UpdateArtifactTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<FloorMapActivity> activityReference;
        private WeakReference<ArrayList<ArtifactDetails>> artifactList;
        String language;
        int position;

        UpdateArtifactTable(FloorMapActivity context, String apiLanguage, int p, ArrayList<ArtifactDetails> list) {
            activityReference = new WeakReference<>(context);
            artifactList = new WeakReference<>(list);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating Artifact Table(language :%s) with id: %s",
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }

    public static class RetrieveTableData extends AsyncTask<Void, Void, List<ArtifactTable>> {
        private WeakReference<FloorMapActivity> activityReference;

        RetrieveTableData(FloorMapActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<ArtifactTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromArtifactTable(language :%s)", activityReference.get().language);
            return activityReference.get().qmDatabase.getArtifactTableDao()
                    .getAllDataFromArtifactTable(activityReference.get().language);
        }

        @Override
        protected void onPostExecute(List<ArtifactTable> artifactDetailsList) {
            if (artifactDetailsList.size() > 0) {
                Timber.i("Set Artifact list from database with size: %d",
                        artifactDetailsList.size());
                activityReference.get().artifactList.clear();
                for (int i = 0; i < artifactDetailsList.size(); i++) {
                    Timber.i("Setting Artifact list from database with id: %s",
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
                activityReference.get().addDataToHashMap();

                activityReference.get().floorMapRootLayout.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().floorMapRootLayout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    public void fetchArtifactsFromDB() {
        Timber.i("fetchArtifactsFromDB()");
        progressBar.setVisibility(View.VISIBLE);
        new RetrieveTableData(FloorMapActivity.this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAudio();
        playPause = false;
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy()");
        markerHashMap.clear();
        artifactDetailsMap.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAnalytics.setCurrentScreen(this, getString(R.string.floor_map_page), null);
    }
}
