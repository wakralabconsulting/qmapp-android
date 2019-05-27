package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Converter;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTable;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserRegistrationDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilitiesDetailModel;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTable;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.museum.GlideLoaderForMuseum;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTable;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.NMoQParkListDetails;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.profile.Model;
import com.qatarmuseums.qatarmuseumsapp.profile.Und;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.utils.DeCryptor;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebViewActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.Page;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static cn.lightsky.infiniteindicator.IndicatorConfiguration.LEFT;
import static cn.lightsky.infiniteindicator.IndicatorConfiguration.RIGHT;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS;

public class DetailsActivity extends AppCompatActivity implements IPullZoom, OnMapReadyCallback,
        NumberPicker.OnValueChangeListener {

    ImageView headerImageView;
    View toolbarClose, favIcon, shareIcon;
    String headerImage;
    String mainTitle, comingFrom;
    boolean isFavourite;
    Toolbar toolbar;
    TextView title, subTitle, shortDescription, longDescription, secondTitle, secondTitleDescription,
            timingTitle, timingDetails, locationDetails, contactPhone, contactEmail;
    private Util util;
    private Animation zoomOutAnimation;
    private View zoomView;
    ArrayList<String> imageList = new ArrayList<>();
    private int headerOffSetSize;
    String appLanguage;
    private LinearLayout secondTitleLayout, timingLayout, contactLayout, retryLayout, videoLayout;
    private String latitude, longitude, id;
    Intent intent;
    String token, startTime, endTime;
    long start, end;
    String user_uid, field_first_name_, field_nmoq_last_name, time_zone;
    int field_membership_number;
    QMDatabase qmDatabase;
    MuseumAboutTable museumAboutTable;
    int publicArtsTableRowCount, heritageTableRowCount,
            exhibitionRowCount, museumAboutRowCount;
    SharedPreferences qmPreferences;
    LinearLayout commonContentLayout, dateLocationLayout;
    TextView noResultFoundTxt;
    ArrayList<PublicArtModel> publicArtModel = new ArrayList<>();
    ArrayList<MuseumAboutModel> museumAboutModels = new ArrayList<>();
    ArrayList<HeritageOrExhibitionDetailModel> heritageOrExhibitionDetailModel = new ArrayList<>();
    boolean fromMuseumAbout = false;
    String first_description;
    String long_description;
    MapView mapDetails;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap gMap, gValue;
    FrameLayout mapView;
    ImageView mapImageView, direction;
    int iconView = 0;
    ProgressBar progressBar;
    ImageView imagePlaceHolder;
    JzvdStd jzvdStd;
    FrameLayout frameLayout;
    View retryButton;
    private InfiniteIndicator circleIndicator;
    private ArrayList ads;
    private LinearLayout eventDateLayout;
    private TextView eventDateTxt;
    private TextView registerUnregisterCount;
    private ConstraintLayout interestLayout;
    private LinearLayout locationLayout;
    private LinearLayout offerLayout;
    private View claimButton;
    private String description;
    private String contactNumber, contactMail;
    private String promotionCode;
    private TextView offerCode;
    private String claimOfferURL;
    private Intent navigation_intent;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private String eventDate, nid;
    String registrationId;
    RegistrationDetailsModel registrationDetailsModel;
    private UserRegistrationDetailsTable registrationDetails;
    private HashMap<String, UserRegistrationDetailsTable> registrationDetailsMap;
    private HashMap<String, TourDetailsModel> tourDetailsMap;
    private TourDetailsModel tourDetailsModel;
    private String currentEventTimeStampDiff;
    private Long currentEventStartTimeStamp;
    private Long currentEventEndTimeStamp;
    private UserRegistrationDetailsTable userRegistrationDetailsTable;
    private RelativeLayout registrationLoader;
    private Button registerButton;
    private ContentResolver contentResolver;
    private ContentValues contentValues;
    private Dialog dialog;
    private LayoutInflater layoutInflater;
    private ImageView closeBtn;
    private Button dialogActionButton;
    private TextView dialogContent;
    int MY_PERMISSIONS_REQUEST_CALENDAR = 100;
    String seatsRemaining = "0";
    private int seatsCount;
    private FirebaseAnalytics mFireBaseAnalytics;
    private String pageName = null;
    private ArrayList<FacilitiesDetailModel> facilitiesDetailModels = new ArrayList<>();
    private ArrayList<NMoQParkListDetails> nMoQParkListDetails = new ArrayList<>();
    private ArrayList<DiningDetailModel> diningDetailModels = new ArrayList<>();
    FacilityDetailTable facilityDetailTable;
    private NMoQParkListDetailsTable nMoQParkListDetailsTable;
    private LinearLayout diningContent;
    private View downloadText;

    public DetailsActivity() {
    }

    @Override

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        comingFrom = intent.getStringExtra("COMING_FROM");
        if (comingFrom.equals(getString(R.string.side_menu_dining_text))) {
            setContentView(R.layout.activity_dining);
            diningContent = findViewById(R.id.dining_content);
        } else
            setContentView(R.layout.activity_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = LocaleManager.getLanguage(this);

        try {
            token = new DeCryptor().decryptData(QM_ALIAS, this);
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            e.printStackTrace();
        }
        user_uid = qmPreferences.getString("UID", null);
        field_nmoq_last_name = qmPreferences.getString("LAST_NAME", null);
        field_first_name_ = qmPreferences.getString("FIRST_NAME", null);
        field_membership_number = qmPreferences.getInt("MEMBERSHIP", 0);
        time_zone = qmPreferences.getString("TIMEZONE", null);
        progressBar = findViewById(R.id.progressBarLoading);
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        headerImage = intent.getStringExtra("HEADER_IMAGE");
        description = intent.getStringExtra("LONG_DESC");
        promotionCode = intent.getStringExtra("PROMOTION_CODE");
        claimOfferURL = intent.getStringExtra("CLAIM_OFFER");
        contactNumber = intent.getStringExtra("CONTACT_PHONE");
        contactMail = intent.getStringExtra("CONTACT_MAIL");
        id = intent.getStringExtra("ID");
        isFavourite = intent.getBooleanExtra("IS_FAVOURITE", false);

        registrationDetailsMap = new HashMap<>();
        tourDetailsMap = new HashMap<>();

        if (comingFrom.equals(getString(R.string.museum_tours)) ||
                comingFrom.equals(getString(R.string.museum_discussion))) {
            description = intent.getStringExtra("DESCRIPTION");
            eventDate = intent.getStringExtra("DATE");
            contactNumber = intent.getStringExtra("CONTACT_PHONE");
            contactMail = intent.getStringExtra("CONTACT_MAIL");
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");
            nid = intent.getStringExtra("NID");
            tourDetailsList = intent.getParcelableArrayListExtra("RESPONSE");
            tourDetailsMap.clear();
            for (int i = 0; i < tourDetailsList.size(); i++) {
                tourDetailsModel = tourDetailsList.get(i);
                if (!Objects.equals(tourDetailsModel.getNid(), nid))
                    tourDetailsMap.put(tourDetailsModel.getNid(), tourDetailsModel);
                else {
                    currentEventTimeStampDiff = tourDetailsModel.getEventTimeStampDiff();
                    currentEventStartTimeStamp = tourDetailsModel.getStartTimeStamp();
                    currentEventEndTimeStamp = tourDetailsModel.getEndTimeStamp();
                    if (tourDetailsModel.getSeatsRemaining() != null)
                        seatsRemaining = tourDetailsModel.getSeatsRemaining();
                }
            }
        }

        if (comingFrom.equals(getString(R.string.facility_sublist))) {
            mainTitle = intent.getStringExtra("MAIN_TITLE");
            description = intent.getStringExtra("DESCRIPTION");
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");

        }
        qmDatabase = QMDatabase.getInstance(DetailsActivity.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = findViewById(R.id.toolbar_close);
        headerImageView = findViewById(R.id.header_img);
        title = findViewById(R.id.main_title);

        shortDescription = findViewById(R.id.short_description);
        longDescription = findViewById(R.id.long_description);

        timingTitle = findViewById(R.id.timing_title);
        timingDetails = findViewById(R.id.timing_info);
        locationDetails = findViewById(R.id.location_info);
        mapImageView = findViewById(R.id.map_view);
        direction = findViewById(R.id.direction);
        mapView = findViewById(R.id.map_layout);
        mapDetails = findViewById(R.id.map_info);
        favIcon = findViewById(R.id.favourite);
        shareIcon = findViewById(R.id.share);
        noResultFoundTxt = findViewById(R.id.noResultFoundTxt);
        retryButton = findViewById(R.id.retry_btn);
        circleIndicator = findViewById(R.id.carousel_indicator);
        locationLayout = findViewById(R.id.location_layout);
        timingLayout = findViewById(R.id.timing_layout);
        retryLayout = findViewById(R.id.retry_layout);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapDetails.onCreate(mapViewBundle);
        mapDetails.getMapAsync(this);
        if (!comingFrom.equals(getString(R.string.side_menu_dining_text))) {
            subTitle = findViewById(R.id.sub_title);
            retryLayout = findViewById(R.id.retry_layout_about);
            secondTitle = findViewById(R.id.second_title);
            secondTitleDescription = findViewById(R.id.second_short_description);
            jzvdStd = findViewById(R.id.videoplayer);
            imagePlaceHolder = findViewById(R.id.video_place_holder);
            contactPhone = findViewById(R.id.contact_phone);
            contactEmail = findViewById(R.id.contact_mail);
            secondTitleLayout = findViewById(R.id.second_title_layout);
            contactLayout = findViewById(R.id.contact_layout);
            commonContentLayout = findViewById(R.id.common_content_layout);
            dateLocationLayout = findViewById(R.id.date_and_location_layout);
            registrationLoader = findViewById(R.id.registration_loader);
            eventDateLayout = findViewById(R.id.event_date_layout);
            eventDateTxt = findViewById(R.id.event_date);
            videoLayout = findViewById(R.id.video_layout);
            downloadText = findViewById(R.id.download_text);
            interestLayout = findViewById(R.id.interest_layout);
            registerUnregisterCount = findViewById(R.id.register_unregister_label);
            registerButton = findViewById(R.id.register_button);
            offerLayout = findViewById(R.id.offer_layout);
            offerCode = findViewById(R.id.offer_code);
            claimButton = findViewById(R.id.claim_offer_button);

            downloadText.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downloadText.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            });
            registrationLoader.setOnClickListener(v -> {
            });
            claimButton.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        claimButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            });
            claimButton.setOnClickListener(v -> {
                Timber.i("Claim button clicked");
                if (util.isNetworkAvailable(this)) {
                    if (!claimOfferURL.equals("")) {
                        Timber.i("Opening web view");
                        navigation_intent = new Intent(DetailsActivity.this, WebViewActivity.class);
                        navigation_intent.putExtra("url", claimOfferURL);
                        startActivity(navigation_intent);
                    } else {
                        Timber.w("URL Error");
                        util.showComingSoonDialog(DetailsActivity.this, R.string.coming_soon_content);
                    }
                } else
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());
            });

            downloadText.setOnClickListener(v ->
                    downloadAction());
            registerButton.setOnClickListener(v -> {
                Timber.i("%s Button clicked", registerButton.getText());
                if (registerButton.getText().toString().equals(getResources().getString(R.string.interested))) {
                    // Overlapping check commented temporarily

//                if (registrationDetailsMap.size() > 0 && tourDetailsMap.size() > 0) {
//                    myVeryOwnIterator = tourDetailsMap.keySet().iterator();
//                    isTimeSlotAvailable = true;
//                    while (myVeryOwnIterator.hasNext()) {
//                        final String key = myVeryOwnIterator.next();
//                        if (registrationDetailsMap.get(key) != null) {
//                            if (tourDetailsMap.get(key).getEventTimeStampDiff().equals(currentEventTimeStampDiff))
//                                isTimeSlotAvailable = false;
//                            else if (!((tourDetailsMap.get(key).getStartTimeStamp() >= currentEventEndTimeStamp) ||
//                                    (tourDetailsMap.get(key).getEndTimeStamp() <= currentEventStartTimeStamp))) {
//                                isTimeSlotAvailable = false;
//                            }
//                        }
//                    }
//                    if (isTimeSlotAvailable) {
//                        showNumberPicker();
//                    } else {
//                        Timber.i("%s",getResources().getString(R.string.time_slot_not_available));
//                        util.showNormalDialog(DetailsActivity.this, R.string.time_slot_not_available);
//                    }
//                } else
                    showNumberPicker();

                } else
                    showDeclineDialog();
            });

        }
        util = new Util();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        title.setText(mainTitle);
        getData();

        GlideApp.with(this)
                .load(headerImage)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(headerImageView);

        if (isFavourite)
            ((ImageView) favIcon).setImageResource(R.drawable.heart_fill);
        else
            ((ImageView) favIcon).setImageResource(R.drawable.heart_empty);

        toolbarClose.setOnClickListener(v -> {
                    Timber.i("Toolbar close clicked");
                    onBackPressed();
                }
        );
        favIcon.setOnClickListener(v -> {
            if (util.checkImageResource(DetailsActivity.this, (ImageView) favIcon, R.drawable.heart_fill)) {
                ((ImageView) favIcon).setImageResource(R.drawable.heart_empty);
            } else
                ((ImageView) favIcon).setImageResource(R.drawable.heart_fill);
        });

        initViews();

        zoomOutAnimation = AnimationUtils.loadAnimation(

                getApplicationContext(),

                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getData();
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
        mapImageView.setOnClickListener(view -> {
            Timber.i("Change MAP TYPE button clicked");
            if (iconView == 0) {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(DetailsActivity.this);
                } else {
                    Timber.i("MAP TYPE changed to HYBRID");
                    iconView = 1;
                    mapImageView.setImageResource(R.drawable.ic_map);
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DetailsActivity.this, R.raw.map_style));

                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            } else {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(DetailsActivity.this);
                } else {
                    Timber.i("MAP TYPE changed to NORMAL");
                    iconView = 0;
                    mapImageView.setImageResource(R.drawable.ic_satellite);
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DetailsActivity.this, R.raw.map_style));

                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            }
        });

        direction.setOnClickListener(view -> {
            Timber.i("Direction button clicked");
            if (latitude == null || latitude.equals("")) {
                util.showLocationAlertDialog(DetailsActivity.this);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&basemap=satellite"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    private void getFacilityDetailsFromAPI(String id) {
        Timber.i("getFacilityDetailsFromAPI(id: %s, language: %s)", id, appLanguage);
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        interestLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<FacilitiesDetailModel>> call = apiService.getFacilityDetails(appLanguage, id);
        call.enqueue(new Callback<ArrayList<FacilitiesDetailModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<FacilitiesDetailModel>> call,
                                   @NonNull Response<ArrayList<FacilitiesDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("Setting Facility details with for id: %s", id);
                        facilitiesDetailModels.addAll(response.body());
                        removeHtmlTagsForFacilities(facilitiesDetailModels);
                        if (!DetailsActivity.this.isFinishing())
                            GlideApp.with(DetailsActivity.this)
                                    .load(facilitiesDetailModels.get(0).getFacilityImage().get(0))
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(headerImageView);
                        mainTitle = facilitiesDetailModels.get(0).getFacilitiesTitle();
                        timingTitle.setText(facilitiesDetailModels.get(0).getFacilityTitleTiming());
                        loadData(null, facilitiesDetailModels.get(0).getFacilityDescription(),
                                null, null, null,
                                facilitiesDetailModels.get(0).getFacilitiesTiming(), null, null, null,
                                facilitiesDetailModels.get(0).getLatitude(), facilitiesDetailModels.get(0).getLongitude(),
                                true, null);
                        new FacilityRowCount(DetailsActivity.this, appLanguage).execute();

                    } else {
                        Timber.i("Facility details have no data");
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("Facility details response is not successful");
                    commonContentLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<FacilitiesDetailModel>> call, @NonNull Throwable t) {
                Timber.e("getFacilityDetailsFromAPI() - onFailure: %s", t.getMessage());
                commonContentLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void getFacilityDetailsFromDataBase(String id) {
        Timber.i("getFacilityDetailsFromDataBase(id: %s, language: %s)", id, appLanguage);
        new RetrieveFacilityData(DetailsActivity.this, Integer.valueOf(id)).execute();
    }

    public void registerButtonAction(String registrationCount) {
        Timber.i("registerButtonAction() with count: %s", registrationCount);
        if (util.isNetworkAvailable(DetailsActivity.this)) {
            entryJsonArray(registrationCount);
            if (registrationDetailsModel != null) {
                entityRegistration(token, registrationDetailsModel);
            }
        } else {
            Timber.i("%s", getResources().getString(R.string.check_network));
            util.showToast(getResources().getString(R.string.check_network), getApplicationContext());
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        Timber.i("Number picked: %d", numberPicker.getValue());
        if (numberPicker.getValue() <= Integer.parseInt(seatsRemaining)) {
            registerButtonAction(String.valueOf(numberPicker.getValue()));
        } else {
            Timber.i("%s", getResources().getString(R.string.warning_selection_not_available));
            showRegistrationWarningDialog(R.string.warning_selection_not_available);
        }
    }

    public void showNumberPicker() {
        Timber.i("showNumberPicker()");
        if (eventDate.split("-").length > 2) {
            NumberPickerDialog newFragment = new NumberPickerDialog();
            newFragment.setValueChangeListener(this);
            newFragment.show(getSupportFragmentManager(), "number picker");
        } else {
            Timber.i("%s", getResources().getString(R.string.warning_no_end_time));
            showRegistrationWarningDialog(R.string.warning_no_end_time);
        }
    }

    public void entryJsonArray(String registrationCount) {
        ArrayList<Und> values = new ArrayList<>();
        Und attendanceValue = new Und("1");
        values.clear();
        values.add(attendanceValue);
        Model attendanceModel = new Model(values);
        ArrayList<Und> nValues = new ArrayList<>();
        Und numberOfAttendanceValue = new Und("2");
        nValues.add(numberOfAttendanceValue);
        Model numberOfAttendanceModel = new Model(nValues);
        ArrayList<Und> firstNameValues = new ArrayList<>();
        Und firstNameValue = new Und(field_first_name_, null, field_first_name_);
        firstNameValues.add(firstNameValue);
        Model firstNameModel = new Model(firstNameValues);
        ArrayList<Und> lvalues = new ArrayList<>();
        Und lastNameValue = new Und(field_nmoq_last_name, null, field_nmoq_last_name);
        lvalues.add(lastNameValue);
        Model lastNameModel = new Model(lvalues);
        ArrayList<Und> membershipValues = new ArrayList<>();
        Und membershipValue = new Und(String.valueOf(field_membership_number));
        membershipValues.add(membershipValue);
        Model membershipModel = new Model(membershipValues);
        if (comingFrom.equals(getString(R.string.museum_discussion))) {
            eventDate = tourDetailsList.get(0).getTourDate();
        }
        String[] splitArray = eventDate.split("-");
        startTime = splitArray[0].concat(splitArray[1].trim());
        start = util.getTimeStamp(startTime);
        if (splitArray.length > 2) {
            endTime = splitArray[0].concat(splitArray[2]);
            end = util.getTimeStamp(endTime);
            ArrayList<Und> registrationValues = new ArrayList<>();
            Und membershipRegistrationValue = new Und(String.valueOf(start / 1000), String.valueOf(end / 1000), time_zone, "10800", "10800", time_zone, "datestamp");
            registrationValues.add(membershipRegistrationValue);
            Model membershipRegistrationModel = new Model(registrationValues);
            registrationDetailsModel =
                    new RegistrationDetailsModel("nmoq_event_registration", nid, "node",
                            user_uid, registrationCount, user_uid, "pending",
                            String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),
                            String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),
                            attendanceModel, numberOfAttendanceModel, firstNameModel,
                            lastNameModel, membershipModel, membershipRegistrationModel);
        } else {
            showRegistrationWarningDialog(R.string.warning_no_end_time);
        }

    }

    protected void showRegistrationWarningDialog(int message) {
        Timber.i("showRegistrationWarningDialog()");
        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.vip_pop_up, null);
        dialog.setContentView(view);

        View line = view.findViewById(R.id.view);
        Button yes = view.findViewById(R.id.acceptbtn);
        Button no = view.findViewById(R.id.accept_later_btn);
        no.setVisibility(View.GONE);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        line.setVisibility(View.GONE);
        dialogTitle.setVisibility(View.GONE);
        yes.setText(getResources().getString(R.string.ok));
        dialogContent.setText(message);
        yes.setOnClickListener(v -> {
            Timber.i("Yes button clicked");
            dialog.dismiss();
        });
        dialog.show();
    }

    protected void showDeclineDialog() {
        Timber.i("showDeclineDialog()");
        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.vip_pop_up, null);
        dialog.setContentView(view);

        View line = view.findViewById(R.id.view);
        Button yes = view.findViewById(R.id.acceptbtn);
        Button no = view.findViewById(R.id.accept_later_btn);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        line.setVisibility(View.GONE);
        dialogTitle.setVisibility(View.GONE);
        yes.setText(getResources().getString(R.string.yes));
        no.setText(getResources().getString(R.string.no));
        dialogContent.setText(getResources().getString(R.string.decline_content_tour));

        yes.setOnClickListener(view1 -> {
            Timber.i("Yes button clicked");
            registrationLoader.setVisibility(View.VISIBLE);
            if (comingFrom.equals(getString(R.string.museum_discussion))) {
                new RetrieveRegistrationId(DetailsActivity.this, nid).execute();
            } else {
                new RetrieveRegistrationId(DetailsActivity.this, nid).execute();
            }
            dialog.dismiss();
        });
        no.setOnClickListener(v -> {
            Timber.i("No button clicked");
            dialog.dismiss();
        });

        dialog.show();
    }


    private void downloadAction() {
        Timber.i("Download button clicked");
        if (util.isNetworkAvailable(this)) {
            if (museumAboutModels.size() > 0 && museumAboutModels.get(0).getDownloadable().size() > 0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(museumAboutModels.get(0).getDownloadable().get(0)));
                startActivity(browserIntent);
            } else
                util.showNormalDialog(this, R.string.location_alert_txt);
        } else
            Toast.makeText(this, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapDetails.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapDetails.onResume();
        if (pageName != null)
            mFireBaseAnalytics.setCurrentScreen(this, pageName + getString(R.string.details_page), null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapDetails.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapDetails.onStop();
    }

    @Override
    protected void onPause() {
        mapDetails.onPause();
        Jzvd.releaseAllVideos();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapDetails.onDestroy();
        gMap = null;
        gValue = null;
        super.onDestroy();
    }

    public void getData() {
        Timber.i("getData() for %s Details", comingFrom);
        if (comingFrom.equals(getString(R.string.side_menu_exhibition_text))) {
            pageName = comingFrom;
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, "Exhibition_detail_Page.json");
            } else {
                getExhibitionAPIDataFromDatabase(id);
            }
        } else if (comingFrom.equals(getString(R.string.side_menu_heritage_text))) {
            pageName = comingFrom.replaceAll(" ", "");
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, "heritage_detail_Page.json");
            } else {
                getHeritageAPIDataFromDatabase(id);
            }
        } else if (comingFrom.equals(getString(R.string.side_menu_public_arts_text))) {
            pageName = comingFrom.replaceAll(" ", "");
            if (util.isNetworkAvailable(DetailsActivity.this))
                getPublicArtDetailsFromAPI(id);
            else
                getCommonListAPIDataFromDatabase(id);

        } else if (comingFrom.equals(getString(R.string.museum_about_text))) {
            videoLayout.setVisibility(View.VISIBLE);
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                if (id.equals("13376"))
                    getMuseumAboutDetailsFromAPI(id, true);
                else
                    getMuseumAboutDetailsFromAPI(id, false);
            } else {
                if (id.equals("13376"))
                    getMuseumAboutDetailsFromDatabase(id, true);
                else
                    getMuseumAboutDetailsFromDatabase(id, false);
            }
        } else if (comingFrom.equals(getString(R.string.facilities_txt))) {
            pageName = getString(R.string.nmoq) + comingFrom;
            if (util.isNetworkAvailable(DetailsActivity.this))
                getFacilityDetailsFromAPI(id);
            else
                getFacilityDetailsFromDataBase(id);
        } else if (comingFrom.equals(getString(R.string.museum_travel))) {
            pageName = getString(R.string.nmoq) + comingFrom;
            getTravelsDetails();
        } else if (comingFrom.equals(getString(R.string.museum_tours))) {
            pageName = getString(R.string.nmoq) + comingFrom;
            setTourDetailsData();
        } else if (comingFrom.equals(getString(R.string.facility_sublist))) {
            setFacilitiesDetails();
        } else if (comingFrom.equals(getString(R.string.museum_discussion))) {
            pageName = getString(R.string.nmoq) + comingFrom;
            setSpecialEventDetailsData();
        } else if (comingFrom.equals(getString(R.string.side_menu_parks_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getNMoQParkListDetailsFromAPI(intent.getStringExtra("NID"));
            else
                getNMoQParkListDetailsFromDataBase(intent.getStringExtra("NID"));
        } else if (comingFrom.equals(getString(R.string.side_menu_dining_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getDiningDetailsFromAPI(id);
            else
                getDiningDetailsFromDatabase(id);
        }
    }

    public void getDiningDetailsFromAPI(String id) {
        Timber.i("getDiningDetailsFromAPI(id: %s, language: %s)", id, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        diningContent.setVisibility(View.INVISIBLE);
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<DiningDetailModel>> call = apiService.getDiningDetails(appLanguage, id);
        call.enqueue(new Callback<ArrayList<DiningDetailModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DiningDetailModel>> call,
                                   @NonNull Response<ArrayList<DiningDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("Setting %s Details", comingFrom);
                        diningContent.setVisibility(View.VISIBLE);
                        diningDetailModels = response.body();
                        removeDiningDetailsHtmlTags(diningDetailModels);
                        for (int i = 0; i < diningDetailModels.get(0).getImages().size(); i++) {
                            imageList.add(i, diningDetailModels.get(0).getImages().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator();
                        }
                        loadData(diningDetailModels.get(0).getDescription(),
                                diningDetailModels.get(0).getOpeningTime(),
                                diningDetailModels.get(0).getClosingTime(),
                                diningDetailModels.get(0).getLocation(),
                                diningDetailModels.get(0).getLatitude(),
                                diningDetailModels.get(0).getLongitude());
                        new DiningRowCount(DetailsActivity.this).execute();

                    } else {
                        Timber.i("%s details response have no data", comingFrom);
                        diningContent.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s details response is not successful", comingFrom);
                    diningContent.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DiningDetailModel>> call, @NonNull Throwable t) {
                Timber.e("get%sDetailsFromAPI() - onFailure: %s", comingFrom, t.getMessage());
                diningContent.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void removeDiningDetailsHtmlTags(ArrayList<DiningDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setDescription(new Util().html2string(models.get(i).getDescription()));
        }
    }

    private void getDiningDetailsFromDatabase(String id) {
        Timber.i("get%sDetailsFromDatabase(id: %s)", comingFrom, id);
        new RetrieveDiningData(DetailsActivity.this, id).execute();
    }

    public void loadData(String longDescription, String openingTime,
                         String closingTime, String locationInfo, String latitudefromApi,
                         String longitudefromApi) {
        Timber.i("loadData()");
        this.shortDescription.setText(longDescription);
        latitude = latitudefromApi;
        longitude = longitudefromApi;
        if (openingTime != null) {
            this.timingLayout.setVisibility(View.VISIBLE);
            String time = getResources().getString(R.string.everyday_from) + " " +
                    openingTime + " " + getResources().getString(R.string.to) + " " +
                    closingTime;
            this.timingDetails.setText(time);
        }
        if (locationInfo != null) {
            this.locationLayout.setVisibility(View.VISIBLE);
            this.locationDetails.setText(locationInfo);
        }

        if (latitude != null && !latitude.equals("") && latitude.contains("°")) {
            latitude = convertDegreeToDecimalMeasure(latitude);
            longitude = convertDegreeToDecimalMeasure(longitude);
        }
        if (latitude != null && !latitude.equals("")) {
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


    public static class DiningRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;

        DiningRowCount(DetailsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int diningTableRowCount = integer;
            if (diningTableRowCount > 0) {
                if (activityReference.get().diningDetailModels.size() > 0) {
                    Timber.i("Count: %d", integer);
                    new CheckDiningDBRowExist(activityReference.get(),
                            activityReference.get().appLanguage).execute();
                }
            } else {
                Timber.i("%s Table have no data", activityReference.get().comingFrom);
                activityReference.get().diningContent.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getDiningTableDao()
                    .getNumberOfRows(activityReference.get().appLanguage);
        }
    }

    public static class CheckDiningDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckDiningDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (activityReference.get().diningDetailModels.size() > 0) {
                for (int i = 0; i < activityReference.get().diningDetailModels.size(); i++) {
                    int n = activityReference.get().qmDatabase.getDiningTableDao()
                            .checkIdExist(Integer.parseInt(activityReference.get()
                                    .diningDetailModels.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().diningDetailModels.get(i).getId());
                        new UpdateDiningTable(activityReference.get(), language, i).execute();
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateDiningTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateDiningTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().diningDetailModels.get(0).getId());
            activityReference.get().qmDatabase.getDiningTableDao().updateDiningDetails(
                    activityReference.get().diningDetailModels.get(position).getName(),
                    activityReference.get().diningDetailModels.get(position).getImage(),
                    activityReference.get().diningDetailModels.get(position).getDescription(),
                    activityReference.get().diningDetailModels.get(position).getOpeningTime(),
                    activityReference.get().diningDetailModels.get(position).getClosingTime(),
                    activityReference.get().diningDetailModels.get(position).getLatitude(),
                    activityReference.get().diningDetailModels.get(position).getLongitude(),
                    activityReference.get().diningDetailModels.get(position).getId(),
                    activityReference.get().diningDetailModels.get(position).getSortId(),
                    language
            );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class RetrieveDiningData extends AsyncTask<Void, Void, List<DiningTable>> {
        private WeakReference<DetailsActivity> activityReference;
        String diningId;

        RetrieveDiningData(DetailsActivity context, String diningId) {
            activityReference = new WeakReference<>(context);
            this.diningId = diningId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<DiningTable> diningTableList) {
            if (diningTableList.get(0).getDescription() != null) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        diningTableList.get(0).getDining_id());
                activityReference.get().loadData(diningTableList.get(0).getDescription(),
                        diningTableList.get(0).getOpening_time(),
                        diningTableList.get(0).getClosing_time(),
                        diningTableList.get(0).getLocation(),
                        diningTableList.get(0).getLatitude(),
                        diningTableList.get(0).getLongitude());
            } else {
                Timber.i("Have no data in database");
                activityReference.get().diningContent.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<DiningTable> doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %s", activityReference.get().comingFrom,
                    activityReference.get().appLanguage, diningId);
            return activityReference.get().qmDatabase.getDiningTableDao()
                    .getDiningDetails(Integer.parseInt(diningId), activityReference.get().appLanguage);
        }
    }


    private void getNMoQParkListDetailsFromAPI(String nid) {
        Timber.i("getNMoQParkListDetailsFromAPI(id: %s, language: %s)", nid, appLanguage);
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQParkListDetails>> call = apiService.getNMoQParkListDetails(appLanguage, nid);
        call.enqueue(new Callback<ArrayList<NMoQParkListDetails>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQParkListDetails>> call,
                                   @NonNull Response<ArrayList<NMoQParkListDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("Setting %s Details", comingFrom);
                        nMoQParkListDetails.addAll(response.body());
                        removeHtmlTagsForParkDetails(nMoQParkListDetails);
                        if (!DetailsActivity.this.isFinishing())
                            GlideApp.with(DetailsActivity.this)
                                    .load(nMoQParkListDetails.get(0).getImages().get(0))
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(headerImageView);
                        mainTitle = nMoQParkListDetails.get(0).getMainTitle();
                        commonContentLayout.setVisibility(View.VISIBLE);
                        dateLocationLayout.setVisibility(View.GONE);
                        shortDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        loadData(null, nMoQParkListDetails.get(0).getDescription(),
                                null, null, null,
                                null, null, null,
                                null, null, null,
                                true, null);

                        // Commented for Nid issue in park list details API
//                        new NMoQParkListDetailsRowCount(DetailsActivity.this, appLanguage).execute();

                    } else {
                        Timber.i("%s details response have no data", comingFrom);
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("%s details response is not successful", comingFrom);
                    commonContentLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQParkListDetails>> call, @NonNull Throwable t) {
                Timber.e("get%sDetailsFromAPI() - onFailure: %s", comingFrom, t.getMessage());
                commonContentLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    /*
        public static class NMoQParkListDetailsRowCount extends AsyncTask<Void, Void, Integer> {
            private WeakReference<DetailsActivity> activityReference;
            String language;

            NMoQParkListDetailsRowCount(DetailsActivity context, String language) {
                this.activityReference = new WeakReference<>(context);
                this.language = language;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
                return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao()
                        .getNumberOfRows(language);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer > 0) {
                    Timber.i("Count: %d", integer);
                    new CheckNMoQParkListDetailsDBRowExist(activityReference.get(), language).execute();
                } else {
                    Timber.i("%s Table have no data", activityReference.get().comingFrom);
                    new InsertNMoQParkListDetailsDataToDataBase(
                            activityReference.get(),
                            activityReference.get().nMoQParkListDetailsTable, language).execute();
                }
            }
        }

        public static class CheckNMoQParkListDetailsDBRowExist extends AsyncTask<Void, Void, Void> {
            private WeakReference<DetailsActivity> activityReference;
            String language;
            private NMoQParkListDetailsTable nMoQParkListDetailsTable;

            CheckNMoQParkListDetailsDBRowExist(DetailsActivity context, String apiLanguage) {
                activityReference = new WeakReference<>(context);
                language = apiLanguage;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (activityReference.get().nMoQParkListDetails.size() > 0) {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().checkIdExist(
                                Integer.parseInt(activityReference.get().nMoQParkListDetails.get(i).getNid()),
                                language);
                        if (n > 0) {
                            Timber.i("Row exist in database(language: %s) for id: %s", language,
                                    activityReference.get().diningDetailModels.get(i).getId());
                            new UpdateNMoQParkListDetailsTable(activityReference.get(), language).execute();
                        } else {
                            Timber.i("Inserting %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                                    language, activityReference.get().nMoQParkListDetails.get(i).getNid());
                            nMoQParkListDetailsTable = new NMoQParkListDetailsTable(
                                    activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                    activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                    activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                    activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                    activityReference.get().nMoQParkListDetails.get(i).getDescription(),
                                    language);

                            activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                    insertTable(nMoQParkListDetailsTable);
                        }
                    }
                }
                return null;
            }
        }

        public static class UpdateNMoQParkListDetailsTable extends AsyncTask<Void, Void, Void> {
            private WeakReference<DetailsActivity> activityReference;
            String language;

            UpdateNMoQParkListDetailsTable(DetailsActivity context, String apiLanguage) {
                activityReference = new WeakReference<>(context);
                language = apiLanguage;

            }

            @Override
            protected Void doInBackground(Void... voids) {
                Timber.i("Updating %s Table(language: %s) with id: %s", activityReference.get().comingFrom,
                        language, activityReference.get().nMoQParkListDetails.get(0).getNid());
                activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().updateNMoQParkListDetails(
                        activityReference.get().nMoQParkListDetails.get(0).getMainTitle(),
                        activityReference.get().nMoQParkListDetails.get(0).getImages(),
                        activityReference.get().nMoQParkListDetails.get(0).getSortId(),
                        activityReference.get().nMoQParkListDetails.get(0).getNid(),
                        activityReference.get().nMoQParkListDetails.get(0).getDescription(),
                        language
                );

                return null;
            }
        }

        public static class InsertNMoQParkListDetailsDataToDataBase extends AsyncTask<Void, Void, Boolean> {

            private WeakReference<DetailsActivity> activityReference;
            private NMoQParkListDetailsTable nMoQParkListDetailsTable;
            String language;

            InsertNMoQParkListDetailsDataToDataBase(DetailsActivity context,
                                                    NMoQParkListDetailsTable nMoQParkListDetailsTable,
                                                    String apiLanguage) {
                activityReference = new WeakReference<>(context);
                this.nMoQParkListDetailsTable = nMoQParkListDetailsTable;
                this.language = apiLanguage;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (activityReference.get().nMoQParkListDetails != null &&
                        activityReference.get().nMoQParkListDetails.size() > 0) {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        Timber.i("Inserting %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                                language, activityReference.get().nMoQParkListDetails.get(i).getNid());
                        nMoQParkListDetailsTable = new NMoQParkListDetailsTable(
                                activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                activityReference.get().nMoQParkListDetails.get(i).getDescription(),
                                language);
                        activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                insertTable(nMoQParkListDetailsTable);
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }
        }
    */
    public void getNMoQParkListDetailsFromDataBase(String nid) {
        Timber.i("get%sListDetailsFromDatabase()", comingFrom);
        new RetrieveNMoQParkListDetailsData(DetailsActivity.this, nid).execute();
    }

    public static class RetrieveNMoQParkListDetailsData extends AsyncTask<Void, Void,
            List<NMoQParkListDetailsTable>> {
        private WeakReference<DetailsActivity> activityReference;
        private int nid;

        RetrieveNMoQParkListDetailsData(DetailsActivity context, String nid) {
            this.activityReference = new WeakReference<>(context);
            this.nid = Integer.parseInt(nid);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListDetailsTable> doInBackground(Void... voids) {
            Timber.i("get%sDetailsData(id: %s, language: %s)", activityReference.get().comingFrom,
                    nid, activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                    getNMoQParkListDetailsTable(nid, activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<NMoQParkListDetailsTable> nMoQParkListDetailsTables) {
            if (nMoQParkListDetailsTables.size() > 0) {
                Timber.i("Setting %s list details from database with id: %s", activityReference.get().comingFrom,
                        nid);
                if (!activityReference.get().isFinishing())
                    GlideApp.with(activityReference.get())
                            .load(nMoQParkListDetailsTables.get(0).getParkImages())
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(activityReference.get().headerImageView);
                activityReference.get().mainTitle = nMoQParkListDetailsTables.get(0).getParkTitle();
                activityReference.get().commonContentLayout.setVisibility(View.VISIBLE);
                activityReference.get().dateLocationLayout.setVisibility(View.GONE);
                activityReference.get().shortDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                activityReference.get().loadData(null, nMoQParkListDetailsTables.get(0).getParkDescription(),
                        null, null, null,
                        null, null, null,
                        null, null, null,
                        true, null);
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public void setTourDetailsData() {
        Timber.i("setTourDetailsData()");
        new RetrieveRegistrationDetails(DetailsActivity.this, nid, true).execute();
        commonContentLayout.setVisibility(View.VISIBLE);
        interestLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        timingTitle.setText(R.string.date);
        shortDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        loadData(null, description,
                null, null, null,
                eventDate, null, contactNumber, contactMail, latitude, longitude,
                true, null);
    }

    public void setFacilitiesDetails() {
        Timber.i("setTourDetailsData()");
        commonContentLayout.setVisibility(View.VISIBLE);
        interestLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        timingTitle.setText(intent.getStringExtra("TITLE_TIMING"));
        loadData(null, description,
                null, null, null,
                intent.getStringExtra("TIMING"), null, null, null, latitude, longitude,
                true, null);
    }

    public void setSpecialEventDetailsData() {
        Timber.i("setSpecialEventDetailsData()");
        commonContentLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        timingTitle.setText(R.string.date);
        shortDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        loadData(null, description,
                null, null, null,
                eventDate, null, contactNumber, contactMail, latitude, longitude,
                true, null);
    }

    public static class RetrieveRegistrationId extends AsyncTask<Void, Void, String> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;

        RetrieveRegistrationId(DetailsActivity context, String eventID) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Timber.i("getRegistrationIdFromTable(id: %s)", eventID);
            return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getRegistrationIdFromTable(eventID);
        }

        @Override
        protected void onPostExecute(String rId) {
            activityReference.get().deleteEventRegistration(rId, activityReference.get().token,
                    eventID, activityReference.get().seatsRemaining);
        }
    }

    public static class InsertRegisteredEventToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private UserRegistrationDetailsTable userRegistrationDetailsTable;

        InsertRegisteredEventToDataBase(DetailsActivity context,
                                        UserRegistrationDetailsTable userRegistrationDetailsTable) {
            activityReference = new WeakReference<>(context);
            this.userRegistrationDetailsTable = userRegistrationDetailsTable;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Inserting RegisteredEventToDataBase with id: %s",
                    activityReference.get().nid);
            userRegistrationDetailsTable = new UserRegistrationDetailsTable(
                    activityReference.get().registrationDetailsModel.getRegistrationID(),
                    activityReference.get().nid,
                    activityReference.get().mainTitle,
                    activityReference.get().registrationDetailsModel.getRegistrationCount());
            activityReference.get().qmDatabase.getUserRegistrationTaleDao()
                    .insertUserRegistrationTable(userRegistrationDetailsTable);
            return true;
        }

    }

    public static class deleteEventFromDataBase extends AsyncTask<Void, Void, Integer> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;

        deleteEventFromDataBase(DetailsActivity context, String eventID) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("Deleting RegisteredEvent from DataBase with id: %s",
                    eventID);
            return activityReference.get().qmDatabase.getUserRegistrationTaleDao().deleteEventFromTable(eventID);
        }

    }

    public static class RetrieveRegistrationDetails extends AsyncTask<Void, Void, List<UserRegistrationDetailsTable>> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;
        Boolean isTour;

        RetrieveRegistrationDetails(DetailsActivity context, String eventID, Boolean isTour) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
            this.isTour = isTour;
        }

        @Override
        protected List<UserRegistrationDetailsTable> doInBackground(Void... voids) {
            if (isTour) {
                Timber.i("getAllDataFromTable()");
                return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getAllDataFromTable();
            } else {
                Timber.i("getEventFromTable(id: %s)", eventID);
                return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getEventFromTable(eventID);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(List<UserRegistrationDetailsTable> userRegistrationDetailsTableList) {
            int count = 0;
            Timber.i("Setting Registration details from database");
            if (isTour) {
                if (userRegistrationDetailsTableList.size() > 0) {
                    activityReference.get().registrationDetailsMap.clear();
                    for (int i = 0; i < userRegistrationDetailsTableList.size(); i++) {
                        activityReference.get().registrationDetails = userRegistrationDetailsTableList.get(i);
                        activityReference.get().registrationDetailsMap.put(
                                activityReference.get().registrationDetails.getEventId(),
                                activityReference.get().registrationDetails);
                    }
                    if (activityReference.get().registrationDetailsMap.get(eventID) != null) {
                        if (activityReference.get().registrationDetailsMap.get(eventID).
                                getNumberOfReservations() != null)
                            count = Integer.parseInt(activityReference.get().registrationDetailsMap.get(eventID).
                                    getNumberOfReservations());
                        activityReference.get().registerButton.setBackground(
                                activityReference.get().getResources().getDrawable(R.drawable.round_corner_red));
                        activityReference.get().registerButton.setText(R.string.not_interested);
                        if (count == 0) {
                            activityReference.get().registerUnregisterCount.setText(
                                    activityReference.get().getResources().getString(R.string.warning_no_seat_available));
                            activityReference.get().registerButton.setEnabled(false);
                            activityReference.get().registerButton.setText(R.string.interested);
                            activityReference.get().registerButton.setBackground(
                                    activityReference.get().getResources().getDrawable(R.drawable.rounded_corner_grey));
                        } else if (count > 1)
                            activityReference.get().registerUnregisterCount.setText(
                                    activityReference.get().getResources().getString(R.string.you_have_registered) + count +
                                            activityReference.get().getResources().getString(R.string.seats_for_this_tour));
                        else
                            activityReference.get().registerUnregisterCount.setText(
                                    activityReference.get().getResources().getString(R.string.you_have_registered) + count +
                                            activityReference.get().getResources().getString(R.string.seat_for_this_tour));


                    } else {
                        if (activityReference.get().seatsRemaining != null)
                            count = Integer.parseInt(activityReference.get().seatsRemaining);
                        activityReference.get().registerButton.setBackground(
                                activityReference.get().getResources().getDrawable(R.drawable.round_corner_green));
                        activityReference.get().registerButton.setText(R.string.interested);
                        if (count == 0) {
                            activityReference.get().registerUnregisterCount.setText(
                                    activityReference.get().getResources().getString(R.string.warning_no_seat_available));
                            activityReference.get().registerButton.setEnabled(false);
                            activityReference.get().registerButton.setBackground(
                                    activityReference.get().getResources().getDrawable(R.drawable.rounded_corner_grey));
                        } else if (count > 1)
                            activityReference.get().registerUnregisterCount.setText(
                                    count + activityReference.get().getResources().getString(R.string.spaces_available));
                        else
                            activityReference.get().registerUnregisterCount.setText(count +
                                    activityReference.get().getResources().getString(R.string.space_available));
                    }
                } else {
                    if (activityReference.get().seatsRemaining != null)
                        count = Integer.parseInt(activityReference.get().seatsRemaining);
                    activityReference.get().registerButton.setBackground(
                            activityReference.get().getResources().getDrawable(R.drawable.round_corner_green));
                    activityReference.get().registerButton.setText(R.string.interested);
                    if (count == 0) {
                        activityReference.get().registerUnregisterCount.setText(
                                activityReference.get().getResources().getString(R.string.warning_no_seat_available));
                        activityReference.get().registerButton.setEnabled(false);
                        activityReference.get().registerButton.setBackground(
                                activityReference.get().getResources().getDrawable(R.drawable.rounded_corner_grey));
                    } else if (count > 1)
                        activityReference.get().registerUnregisterCount.setText(count +
                                activityReference.get().getResources().getString(R.string.spaces_available));
                    else
                        activityReference.get().registerUnregisterCount.setText(count +
                                activityReference.get().getResources().getString(R.string.space_available));
                }
            } else {
                if (userRegistrationDetailsTableList.size() > 0) {
                    if (activityReference.get().registrationDetailsMap.get(eventID).
                            getNumberOfReservations() != null)
                        count = Integer.parseInt(activityReference.get().registrationDetailsMap.get(eventID).
                                getNumberOfReservations());
                    activityReference.get().registerButton.setBackground(
                            activityReference.get().getResources().getDrawable(R.drawable.round_corner_red));
                    activityReference.get().registerButton.setText(R.string.not_interested);
                    if (count == 0) {
                        activityReference.get().registerUnregisterCount.setText(
                                activityReference.get().getResources().getString(R.string.warning_no_seat_available));
                        activityReference.get().registerButton.setEnabled(false);
                        activityReference.get().registerButton.setText(R.string.interested);
                        activityReference.get().registerButton.setBackground(
                                activityReference.get().getResources().getDrawable(R.drawable.rounded_corner_grey));
                    } else if (count > 1)
                        activityReference.get().registerUnregisterCount.setText(
                                activityReference.get().getResources().getString(R.string.you_have_registered) + count +
                                        activityReference.get().getResources().getString(R.string.seats_for_this_tour));
                    else
                        activityReference.get().registerUnregisterCount.setText(
                                activityReference.get().getResources().getString(R.string.you_have_registered) + count +
                                        activityReference.get().getResources().getString(R.string.seat_for_this_tour));

                } else {
                    if (activityReference.get().seatsRemaining != null)
                        count = Integer.parseInt(activityReference.get().seatsRemaining);
                    activityReference.get().registerButton.setBackground(
                            activityReference.get().getResources().getDrawable(R.drawable.round_corner_green));
                    activityReference.get().registerButton.setText(R.string.interested);
                    if (count == 0) {
                        activityReference.get().registerUnregisterCount.setText(
                                activityReference.get().getResources().getString(R.string.warning_no_seat_available));
                        activityReference.get().registerButton.setEnabled(false);
                        activityReference.get().registerButton.setBackground(
                                activityReference.get().getResources().getDrawable(R.drawable.rounded_corner_grey));
                    } else if (count > 1)
                        activityReference.get().registerUnregisterCount.setText(count +
                                activityReference.get().getResources().getString(R.string.spaces_available));
                    else
                        activityReference.get().registerUnregisterCount.setText(count +
                                activityReference.get().getResources().getString(R.string.space_available));
                }
            }
        }
    }

    private void getTravelsDetails() {
        Timber.i("getTravelsDetails()");
        videoLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        offerLayout.setVisibility(View.VISIBLE);
        if (promotionCode != null && !promotionCode.equals("")) {
            offerCode.setVisibility(View.VISIBLE);
            offerCode.setText(promotionCode);
        }
        shortDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        loadData(null, description, null, null,
                null, null, null,
                contactNumber, contactMail, null, null,
                false, null);
    }

    private void getCommonListAPIDataFromDatabase(String id) {
        Timber.i("getCommonListAPIDataFromDatabase(id: %s, language: %s)", id, appLanguage);
        new RetrievePublicArtsData(DetailsActivity.this, id).execute();
    }

    private void getHeritageAPIDataFromDatabase(String id) {
        Timber.i("getHeritageAPIDataFromDatabase(id: %s, language: %s)", id, appLanguage);
        new RetrieveHeritageData(DetailsActivity.this, id).execute();
    }

    private void getExhibitionAPIDataFromDatabase(String id) {
        Timber.i("getExhibitionAPIDataFromDatabase(id: %s, language: %s)", id, appLanguage);
        new RetrieveExhibitionData(DetailsActivity.this, id).execute();
    }

    private String convertDegreeToDecimalMeasure(String degreeValue) {
        String value = degreeValue.trim();
        String[] latParts = value.split("°");
        float degree = Float.parseFloat(latParts[0]);
        if (degree == 0.0) {
            return null;
        }
        value = latParts[1].trim();
        latParts = value.split("'");
        float min = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("\"");
        float sec = Float.parseFloat(latParts[0]);
        String result;
        result = String.valueOf(degree + (min / 60) + (sec / 3600));
        return result;
    }

    private void initViews() {
        Timber.i("init PullToZoomCoordinatorLayout");
        frameLayout = findViewById(R.id.fragment_frame);
        PullToZoomCoordinatorLayout coordinatorLayout = findViewById(R.id.main_content);
        zoomView = findViewById(R.id.header_img);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);

        coordinatorLayout.setPullZoom(zoomView, PixelUtil.dp2px(this, 232),
                PixelUtil.dp2px(this, 332), this);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> headerOffSetSize = verticalOffset);
    }

    public void showCarouselView() {
        Timber.i("showCarouselView()");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imageList", imageList);
        HorizontalLayoutFragment fragment = new HorizontalLayoutFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_frame, fragment);
        transaction.commit();
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

    public void loadData(String subTitle, String shortDescription, String longDescription,
                         String secondTitle, String secondTitleDescription, String openingTime,
                         String locationInfo, String contactNumber, String contactMail,
                         String latitudeFromApi, String longitudeFromApi, boolean museumAboutStatus, String videoFile) {
        Timber.i("load data for %s Details", comingFrom);
        if (shortDescription != null) {
            commonContentLayout.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            this.title.setText(mainTitle);
            if (museumAboutStatus) {
                latitude = latitudeFromApi;
                longitude = longitudeFromApi;
            } else {
                latitude = intent.getStringExtra("LATITUDE");
                longitude = intent.getStringExtra("LONGITUDE");
            }
            if (videoFile != null) {

                jzvdStd.setUp(videoFile, "", Jzvd.SCREEN_WINDOW_NORMAL);
                jzvdStd.setVisibility(View.VISIBLE);
                imagePlaceHolder.setVisibility(View.GONE);
            } else {
                imagePlaceHolder.setVisibility(View.VISIBLE);
                jzvdStd.setVisibility(View.GONE);
                imagePlaceHolder.setImageResource(R.drawable.placeholder_video);
            }
            if (subTitle != null) {
                this.subTitle.setVisibility(View.VISIBLE);
                this.subTitle.setText(subTitle);
            }
            this.shortDescription.setText(shortDescription);
            if (longDescription != null) {
                this.longDescription.setMovementMethod(LinkMovementMethod.getInstance());
                this.longDescription.setText(Html.fromHtml(longDescription));
            } else {
                this.longDescription.setVisibility(View.GONE);

            }
            if (secondTitle != null) {
                this.secondTitleLayout.setVisibility(View.VISIBLE);
                this.secondTitle.setText(secondTitle);
                this.secondTitleDescription.setText(secondTitleDescription);
            }
            if (comingFrom.equals(getString(R.string.museum_tours)) ||
                    comingFrom.equals(getString(R.string.museum_discussion))) {
                timingTitle.setText(R.string.date);
            } else if (comingFrom.equals(getString(R.string.facility_sublist)) ||
                    comingFrom.equals(getString(R.string.facilities_txt))) {
                timingTitle.setText(R.string.opening_timings);
                registerButton.setVisibility(View.GONE);
            } else
                timingTitle.setText(R.string.museum_timings);

            if (openingTime != null) {
                this.timingLayout.setVisibility(View.VISIBLE);
                this.timingDetails.setText(openingTime);
            }
            if (locationInfo != null) {
                this.locationDetails.setVisibility(View.VISIBLE);
                this.locationDetails.setText(locationInfo);
            }
            if (contactNumber != null && !(contactNumber.isEmpty()) && !contactNumber.equals("\n")) {
                this.contactLayout.setVisibility(View.VISIBLE);
                this.contactPhone.setVisibility(View.VISIBLE);
                this.contactPhone.setText(contactNumber, TextView.BufferType.SPANNABLE);
                if (contactPhone != null)
                    util.removeUnderlines((Spannable) contactPhone.getText());
            }
            if (contactMail != null && !(contactMail.isEmpty()) && !contactMail.equals("\n")) {
                this.contactLayout.setVisibility(View.VISIBLE);
                this.contactEmail.setVisibility(View.VISIBLE);
                this.contactEmail.setText(contactMail);
            }
            if (latitude != null) {
                if (latitude.contains("°")) {
                    latitude = convertDegreeToDecimalMeasure(latitude);
                    longitude = convertDegreeToDecimalMeasure(longitude);
                }
            }
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
        } else {
            progressBar.setVisibility(View.GONE);
            commonContentLayout.setVisibility(View.INVISIBLE);
            if (util.isNetworkAvailable(this))
                noResultFoundTxt.setVisibility(View.VISIBLE);
            else
                retryLayout.setVisibility(View.VISIBLE);
        }

    }


    public void loadDataForHeritageOrExhibitionDetails(String subTitle, String shortDescription,
                                                       String longDescription, String secondTitle,
                                                       String secondTitleDescription, String timingInfo,
                                                       String locationInfo, String contactMail,
                                                       String latitudefromApi, String longitudefromApi,
                                                       String openingTime, String closingtime,
                                                       String videoFile) {
        Timber.i("load data for %s Details", comingFrom);
        if (shortDescription != null) {
            commonContentLayout.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            this.title.setText(mainTitle);
            latitude = latitudefromApi;
            longitude = longitudefromApi;
            if (subTitle != null) {
                this.subTitle.setVisibility(View.VISIBLE);
                this.subTitle.setText(subTitle);
            }
            this.shortDescription.setText(shortDescription);
            this.longDescription.setText(longDescription);
            if (secondTitle != null) {
                this.secondTitleLayout.setVisibility(View.VISIBLE);
                this.secondTitle.setText(secondTitle);
                this.secondTitleDescription.setText(secondTitleDescription);
            }
            if (timingInfo != null) {
                timingTitle.setText(R.string.exhibition_timings);
                this.timingLayout.setVisibility(View.VISIBLE);
                this.timingDetails.setText(timingInfo);
            }
            if (openingTime != null) {
                timingTitle.setText(R.string.exhibition_timings);
                this.timingLayout.setVisibility(View.VISIBLE);
                String time = " " +
                        openingTime + " " + getResources().getString(R.string.to) + " " +
                        closingtime;
                this.timingDetails.setText(time);
            }
            if (locationInfo != null) {
                this.locationDetails.setVisibility(View.VISIBLE);
                this.locationDetails.setText(locationInfo);
            }

            if (videoFile != null) {

                jzvdStd.setUp(videoFile, "", Jzvd.SCREEN_WINDOW_NORMAL);
                jzvdStd.setVisibility(View.VISIBLE);
                imagePlaceHolder.setVisibility(View.GONE);

            } else {
                imagePlaceHolder.setVisibility(View.VISIBLE);
                jzvdStd.setVisibility(View.GONE);
                imagePlaceHolder.setImageResource(R.drawable.placeholder_video);
            }
            if (contactMail != null && !(contactMail.isEmpty())) {
                this.contactLayout.setVisibility(View.VISIBLE);
                this.contactEmail.setVisibility(View.VISIBLE);
                this.contactEmail.setText(contactMail);
            }
            if (latitude != null) {
                if (latitude.contains("°")) {
                    latitude = convertDegreeToDecimalMeasure(latitude);
                    longitude = convertDegreeToDecimalMeasure(longitude);

                }
            }
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

        } else {
            commonContentLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
            if (util.isNetworkAvailable(this))
                noResultFoundTxt.setVisibility(View.VISIBLE);
            else
                retryLayout.setVisibility(View.VISIBLE);
        }
    }

    public void entityRegistration(String token, RegistrationDetailsModel myRegistration) {
        Timber.i("entityRegistration()");
        registrationLoader.setVisibility(View.VISIBLE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APIInterface apiService = retrofit.create(APIInterface.class);

        Call<RegistrationDetailsModel> call = apiService.eventRegistration("application/json", token, myRegistration);
        call.enqueue(new Callback<RegistrationDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<RegistrationDetailsModel> call,
                                   @NonNull Response<RegistrationDetailsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("entityRegistration() - isSuccessful");
                        registrationDetailsModel = response.body();
                        registrationId = registrationDetailsModel.getRegistrationID();
                        registrationDetailsModel.setRegistrationState("completed");
                        entityRegistrationCompletion(token, registrationDetailsModel, registrationId);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationDetailsModel> call, @NonNull Throwable t) {
                Timber.e("entityRegistration() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });
    }


    public void entityRegistrationCompletion(String token, RegistrationDetailsModel myRegistration, String registrationId) {
        Timber.i("entityRegistrationCompletion()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APIInterface apiService = retrofit.create(APIInterface.class);

        Call<RegistrationDetailsModel> call = apiService.eventRegistrationCompletion("application/json", token, registrationId, myRegistration);
        call.enqueue(new Callback<RegistrationDetailsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<RegistrationDetailsModel> call,
                                   @NonNull Response<RegistrationDetailsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("entityRegistrationCompletion() - isSuccessful");
                        registrationDetailsModel = response.body();
                        seatsCount = Integer.parseInt(registrationDetailsModel.getRegistrationCount());
                        if (seatsCount > 1)
                            registerUnregisterCount.setText(
                                    getResources().getString(R.string.you_have_registered) + seatsCount +
                                            getResources().getString(R.string.seats_for_this_tour));
                        else
                            registerUnregisterCount.setText(
                                    getResources().getString(R.string.you_have_registered) + seatsCount +
                                            getResources().getString(R.string.seat_for_this_tour));

                        registerButton.setBackground(getResources().getDrawable(R.drawable.round_corner_red));
                        registerButton.setText(R.string.not_interested);
                        showAddToCalendarDialog(mainTitle, description, eventDate);
                        new InsertRegisteredEventToDataBase(DetailsActivity.this,
                                userRegistrationDetailsTable).execute();
                    }
                }
                registrationLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationDetailsModel> call, @NonNull Throwable t) {
                Timber.e("entityRegistrationCompletion() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });

    }

    protected void showAddToCalendarDialog(final String title, final String details, String eventDate) {
        Timber.i("showAddToCalendarDialog()");
        dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.registration_success_popup, null);
        dialog.setContentView(view);
        closeBtn = view.findViewById(R.id.close_dialog);
        dialogActionButton = view.findViewById(R.id.doneBtn);
        dialogContent = view.findViewById(R.id.dialog_content);

        dialogActionButton.setText(getResources().getString(R.string.add_to_calendar));
        dialogContent.setText(getResources().getString(R.string.thankyou_interest));

        dialogActionButton.setOnClickListener(view1 -> {
            Timber.i("%s button clicked", dialogActionButton.getText());
            addToCalendar(title, details, eventDate);
            dialog.dismiss();

        });
        closeBtn.setOnClickListener(view12 -> {
            Timber.i("Close button clicked");
            dialog.dismiss();
        });
        dialog.show();
    }


    private void addToCalendar(String title, String details, String eventDate) {
        Timber.i("addToCalendar() - Setting calendar data for %s", title);
        String[] dateTimeArray = eventDate.trim().split("-");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date startDate = null, endDate = null;
        try {
            startDate = formatter.parse(dateTimeArray[0].trim() + " " + dateTimeArray[1].trim());
            endDate = formatter.parse(dateTimeArray[0].trim() + " " + dateTimeArray[2].trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        contentResolver = getContentResolver();
        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DESCRIPTION, details);
        contentValues.put(CalendarContract.Events.DTSTART, startDate.getTime());
        contentValues.put(CalendarContract.Events.DTEND, endDate.getTime());
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.i("Requesting CALENDAR READ & WRITE permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_CALENDAR);

            } else {
                util.insertEventToCalendar(this, contentValues);
            }
        } else
            util.insertEventToCalendar(this, contentValues);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("onRequestPermissionsResult() - PERMISSION_GRANTED");
                    util.insertEventToCalendar(this, contentValues);
                } else {
                    Timber.i("onRequestPermissionsResult() - PERMISSION_NOT_GRANTED ");
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    }
                    if (!showRationale) {
                        util.showNavigationDialog(this,
                                getString(R.string.permission_required), getString(R.string.runtime_permission));
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            Timber.i("onActivityResult() - PERMISSION_GRANTED");
            util.insertEventToCalendar(this, contentValues);

        }
    }

    public void deleteEventRegistration(String id, String token, String myNid, String seatsRemaining) {
        Timber.i("deleteEventRegistration()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APIInterface apiService = retrofit.create(APIInterface.class);

        Call<String> call = apiService.deleteEventRegistration(id, token);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Timber.i("Deleting EventRegistration for id: %s", id);
                    new deleteEventFromDataBase(DetailsActivity.this, myNid).execute();
                    registerButton.setBackground(getResources().getDrawable(R.drawable.round_corner_green));
                    registerButton.setText(R.string.interested);
                    seatsCount = Integer.parseInt(seatsRemaining);
                    if (seatsCount == 0) {
                        registerUnregisterCount.setText(
                                getResources().getString(R.string.warning_no_seat_available));
                        registerButton.setEnabled(false);
                        registerButton.setBackground(getResources().getDrawable(R.drawable.rounded_corner_grey));
                    } else if (seatsCount > 1)
                        registerUnregisterCount.setText(seatsCount +
                                getResources().getString(R.string.spaces_available));
                    else
                        registerUnregisterCount.setText(seatsCount +
                                getResources().getString(R.string.space_available));

                }
                registrationLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Timber.e("deleteEventRegistration() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });
    }

    public void getHeritageOrExhibitionDetailsFromAPI(String id, final String pageName) {
        Timber.i("get%sDetailsFromAPI(id: %s, language: %s)", comingFrom, id, appLanguage);
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HeritageOrExhibitionDetailModel>> call = apiService.getHeritageOrExhibitionDetails(appLanguage,
                pageName, id);
        call.enqueue(new Callback<ArrayList<HeritageOrExhibitionDetailModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HeritageOrExhibitionDetailModel>> call,
                                   @NonNull Response<ArrayList<HeritageOrExhibitionDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("get%sDetailsFromAPI() - isSuccessful", comingFrom);
                        commonContentLayout.setVisibility(View.VISIBLE);
                        heritageOrExhibitionDetailModel = response.body();
                        removeHtmlTags(heritageOrExhibitionDetailModel);
                        for (int i = 0; i < heritageOrExhibitionDetailModel.get(0).getImages().size(); i++) {
                            imageList.add(i, heritageOrExhibitionDetailModel.get(0).getImages().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator();
                        }

                        if (pageName.equals("heritage_detail_Page.json")) {
                            loadDataForHeritageOrExhibitionDetails(
                                    null,
                                    heritageOrExhibitionDetailModel.get(0).getShortDescription(),
                                    heritageOrExhibitionDetailModel.get(0).getLongDescription(),
                                    null,
                                    null,
                                    null,
                                    heritageOrExhibitionDetailModel.get(0).getLocation(),
                                    null,
                                    heritageOrExhibitionDetailModel.get(0).getLatitude(),
                                    heritageOrExhibitionDetailModel.get(0).getLongitude(),
                                    null, null, null);
                            new HeritageRowCount(DetailsActivity.this, appLanguage).execute();
                        } else {
                            loadDataForHeritageOrExhibitionDetails(null,
                                    heritageOrExhibitionDetailModel.get(0).getShortDescription(),
                                    heritageOrExhibitionDetailModel.get(0).getLongDescription(),
                                    null, null,
                                    null,
                                    null,
                                    getResources().getString(R.string.contact_mail),
                                    heritageOrExhibitionDetailModel.get(0).getLatitude(),
                                    heritageOrExhibitionDetailModel.get(0).getLongitude(),
                                    heritageOrExhibitionDetailModel.get(0).getStartDate(),
                                    heritageOrExhibitionDetailModel.get(0).getEndDate(), null);
                            new ExhibitionDetailRowCount(DetailsActivity.this, appLanguage).execute();
                        }

                    } else {
                        Timber.i("get%sDetailsFromAPI() - have no data", comingFrom);
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("get%sDetailsFromAPI() - response not successful", comingFrom);
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HeritageOrExhibitionDetailModel>> call, @NonNull Throwable t) {
                Timber.e("get%sDetailsFromAPI() - onFailure: %s", comingFrom, t.getMessage());
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showIndicator() {
        Timber.i("showIndicator()");
        circleIndicator.setVisibility(View.VISIBLE);
        ads = new ArrayList<>();
        for (int i = 0; i < (imageList.size() > 3 ? 3 : imageList.size()); i++) {
            ads.add(new Page("", imageList.get(0),
                    null));
        }
        GlideLoaderForMuseum glideLoader = new GlideLoaderForMuseum();
        IndicatorConfiguration configuration;
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH))
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(glideLoader)
                    .direction(LEFT)
                    .position(IndicatorConfiguration.IndicatorPosition.Center)
                    .build();
        else
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(glideLoader)
                    .direction(RIGHT)
                    .position(IndicatorConfiguration.IndicatorPosition.Center)
                    .build();
        circleIndicator.init(configuration);
        circleIndicator.notifyDataChange(ads);
        circleIndicator.stop();
    }

    public void removeHtmlTags(ArrayList<HeritageOrExhibitionDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setStartDate(util.html2string(models.get(i).getStartDate()));
            models.get(i).setEndDate(util.html2string(models.get(i).getEndDate()));
            models.get(i).setLongDescription(util.html2string(models.get(i).getLongDescription()));
        }
    }

    public void removeHtmlTagsForFacilities(ArrayList<FacilitiesDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setFacilitiesSubtitle(util.html2string(models.get(i).getFacilitiesSubtitle()));
            models.get(i).setFacilitiesTitle(util.html2string(models.get(i).getFacilitiesTitle()));
            models.get(i).setFacilityDescription(util.html2string(models.get(i).getFacilityDescription()));
            models.get(i).setFacilitiesTiming(util.html2string(models.get(i).getFacilitiesTiming()));


        }
    }

    public void removeHtmlTagsForParkDetails(ArrayList<NMoQParkListDetails> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));
            models.get(i).setDescription(util.html2string(models.get(i).getDescription()));
        }
    }

    public void removeHtmlTagsPublicArts(ArrayList<PublicArtModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setLongDescription(util.html2string(models.get(i).getLongDescription()));
        }
    }

    public void getItemPosition() {
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


    public static class ExhibitionDetailRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        ExhibitionDetailRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().exhibitionRowCount = integer;
            if (activityReference.get().exhibitionRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckExhibitionDetailDBRowExist(activityReference.get(), language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
            return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRows(language);
        }
    }

    public static class CheckExhibitionDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckExhibitionDetailDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().heritageOrExhibitionDetailModel.size() > 0) {
                for (int i = 0; i < activityReference.get().heritageOrExhibitionDetailModel.size(); i++) {
                    int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().heritageOrExhibitionDetailModel.get(i).getId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().heritageOrExhibitionDetailModel.get(i).getId());
                        new UpdateExhibitionDetailTable(activityReference.get(), language, i).execute();
                    }
                }

            }
            return null;
        }
    }

    public static class UpdateExhibitionDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateExhibitionDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().heritageOrExhibitionDetailModel.get(position).getId());
            Converter converters = new Converter();
            activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionDetail(
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getStartDate(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getEndDate(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getImage(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getLatitude(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getLongitude(),
                    converters.fromArrayList(activityReference.get().heritageOrExhibitionDetailModel.get(position).getImages()),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getId(),
                    language
            );
            return null;
        }
    }

    public static class RetrieveExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTable>> {

        private WeakReference<DetailsActivity> activityReference;
        String exhibitionId;

        RetrieveExhibitionData(DetailsActivity context, String id) {
            activityReference = new WeakReference<>(context);
            exhibitionId = id;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override

        protected void onPostExecute(List<ExhibitionListTable> exhibitionListTables) {
            if (exhibitionListTables.size() > 0) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        exhibitionId);
                activityReference.get().commonContentLayout.setVisibility(View.VISIBLE);
                Converter convertor = new Converter();
                if (exhibitionListTables.get(0).getExhibition_latest_image().contains("[")) {
                    ArrayList<String> list = convertor.fromString(exhibitionListTables.get(0).getExhibition_latest_image());
                    for (int i = 0; i < list.size(); i++) {
                        activityReference.get().imageList.add(i, list.get(i));
                    }
                }
                if (activityReference.get().imageList.size() > 0) {
                    activityReference.get().zoomView.setOnClickListener(view -> activityReference.get().showCarouselView());
                    activityReference.get().showIndicator();
                }

                activityReference.get().loadDataForHeritageOrExhibitionDetails(null,
                        exhibitionListTables.get(0).getExhibition_short_description(),
                        exhibitionListTables.get(0).getExhibition_long_description(),
                        null, null,
                        null,
                        exhibitionListTables.get(0).getExhibition_location(),
                        activityReference.get().getResources().getString(R.string.contact_mail),
                        exhibitionListTables.get(0).getExhibition_latitude(),
                        exhibitionListTables.get(0).getExhibition_longitude(),
                        exhibitionListTables.get(0).getExhibition_start_date(),
                        exhibitionListTables.get(0).getExhibition_end_date(), null);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().commonContentLayout.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ExhibitionListTable> doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %s", activityReference.get().comingFrom,
                    activityReference.get().appLanguage, exhibitionId);
            return activityReference.get().qmDatabase.getExhibitionTableDao()
                    .getExhibitionDetails(Integer.parseInt(exhibitionId), activityReference.get().appLanguage);
        }
    }

    public static class HeritageRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        HeritageRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().heritageTableRowCount = integer;
            if (activityReference.get().heritageTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckHeritageDetailDBRowExist(activityReference.get(), language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
            return activityReference.get().qmDatabase.getHeritageListTableDao()
                    .getNumberOfRows(language);
        }
    }

    public static class CheckHeritageDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckHeritageDetailDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().heritageOrExhibitionDetailModel.size() > 0) {
                for (int i = 0; i < activityReference.get().heritageOrExhibitionDetailModel.size(); i++) {
                    int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().heritageOrExhibitionDetailModel.get(i).getId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().heritageOrExhibitionDetailModel.get(i).getId());
                        new UpdateHeritageDetailTable(activityReference.get(), language, i).execute();

                    }
                }
            }
            return null;
        }
    }

    public static class UpdateHeritageDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateHeritageDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().heritageOrExhibitionDetailModel.get(position).getId());
            Converter converter = new Converter();
            activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageDetails(
                    activityReference.get().latitude, activityReference.get().longitude,
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getImage(),
                    converter.fromArrayList(activityReference.get().heritageOrExhibitionDetailModel.get(position).getImages()),
                    activityReference.get().heritageOrExhibitionDetailModel.get(position).getId(),
                    language
            );
            return null;
        }
    }

    public static class RetrieveHeritageData extends AsyncTask<Void, Void, List<HeritageListTable>> {

        private WeakReference<DetailsActivity> activityReference;
        String heritageId;

        RetrieveHeritageData(DetailsActivity context, String id) {
            activityReference = new WeakReference<>(context);
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<HeritageListTable> heritageListTables) {
            if (heritageListTables.size() > 0) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        heritageId);
                activityReference.get().commonContentLayout.setVisibility(View.VISIBLE);
                Converter convertor = new Converter();
                if (heritageListTables.get(0).getHeritage_images() != null &&
                        heritageListTables.get(0).getHeritage_images().contains("[")) {
                    ArrayList<String> list = convertor.fromString(heritageListTables.get(0).getHeritage_images());
                    for (int i = 0; i < list.size(); i++) {
                        activityReference.get().imageList.add(i, list.get(i));
                    }
                }
                if (activityReference.get().imageList.size() > 0) {
                    activityReference.get().zoomView.setOnClickListener(view -> activityReference.get().showCarouselView());
                    activityReference.get().showIndicator();
                }

                activityReference.get().loadDataForHeritageOrExhibitionDetails(null,
                        heritageListTables.get(0).getHeritage_short_description(),
                        heritageListTables.get(0).getHeritage_long_description(),
                        null, null,
                        null,
                        heritageListTables.get(0).getLocation(),
                        null, heritageListTables.get(0).getLatitude(),
                        heritageListTables.get(0).getLongitude(), null, null, null);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().commonContentLayout.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<HeritageListTable> doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %s", activityReference.get().comingFrom,
                    activityReference.get().appLanguage, heritageId);
            return activityReference.get().qmDatabase.getHeritageListTableDao()
                    .getHeritageDetails(Integer.parseInt(heritageId), activityReference.get().appLanguage);
        }
    }

    private void getPublicArtDetailsFromAPI(String id) {
        Timber.i("getPublicArtDetailsFromAPI(id: %s, language: %s)", id, appLanguage);
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<PublicArtModel>> call = apiService.getPublicArtsDetails(appLanguage, id);
        call.enqueue(new Callback<ArrayList<PublicArtModel>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<PublicArtModel>> call,
                                   @NonNull Response<ArrayList<PublicArtModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("getPublicArtDetailsFromAPI() - isSuccessful");
                        commonContentLayout.setVisibility(View.VISIBLE);
                        publicArtModel = response.body();
                        removeHtmlTagsPublicArts(publicArtModel);
                        for (int i = 0; i < publicArtModel.get(0).getImage().size(); i++) {
                            imageList.add(i, publicArtModel.get(0).getImage().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator();
                        }

                        fromMuseumAbout = false;
                        loadData(null,
                                publicArtModel.get(0).getShortDescription(),
                                publicArtModel.get(0).getLongDescription(),
                                null, null, null,
                                null, null, null, publicArtModel.get(0).getLatitude(),
                                publicArtModel.get(0).getLatitude(), fromMuseumAbout, null);
                        new PublicArtsRowCount(DetailsActivity.this, appLanguage).execute();
                    } else {
                        Timber.i("get%sDetailsFromAPI() - have no data", comingFrom);
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("get%sDetailsFromAPI() - response not successful", comingFrom);
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<PublicArtModel>> call, @NonNull Throwable t) {
                Timber.e("get%sDetailsFromAPI() - onFailure: %s", comingFrom, t.getMessage());
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        PublicArtsRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().publicArtsTableRowCount = integer;
            if (activityReference.get().publicArtsTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckPublicArtsDetailDBRowExist(activityReference.get(), language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRows(language);
        }
    }

    public static class CheckPublicArtsDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckPublicArtsDetailDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().publicArtModel.size() > 0) {
                for (int i = 0; i < activityReference.get().publicArtModel.size(); i++) {
                    int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().publicArtModel.get(i).getId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().publicArtModel.get(i).getId());
                        new UpdatePublicArtsDetailTable(activityReference.get(), language, i).execute();

                    }
                }
            }

            return null;
        }
    }

    public static class UpdatePublicArtsDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdatePublicArtsDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Converter converters = new Converter();
            Timber.i("Updating %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().publicArtModel.get(position).getId());
            activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsDetail(
                    activityReference.get().publicArtModel.get(position).getName(),
                    activityReference.get().latitude, activityReference.get().longitude,
                    activityReference.get().publicArtModel.get(position).getShortDescription(),
                    activityReference.get().publicArtModel.get(position).getLongDescription(),
                    converters.fromArrayList(activityReference.get().publicArtModel.get(position).getImage()),
                    activityReference.get().publicArtModel.get(position).getId(),
                    language
            );
            return null;
        }
    }

    public static class RetrievePublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTable>> {

        private WeakReference<DetailsActivity> activityReference;
        String publicArtsId;

        RetrievePublicArtsData(DetailsActivity context, String id) {
            activityReference = new WeakReference<>(context);
            publicArtsId = id;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTable> publicArtsTables) {
            if (publicArtsTables.size() > 0) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        publicArtsId);
                Converter converters = new Converter();
                if (publicArtsTables.get(0).getPublic_arts_images() != null &&
                        publicArtsTables.get(0).getPublic_arts_images().contains("[")) {
                    ArrayList<String> list = converters.fromString(publicArtsTables.get(0).getPublic_arts_images());
                    for (int l = 0; l < list.size(); l++) {
                        activityReference.get().imageList.add(l, list.get(l));
                    }
                }
                if (activityReference.get().imageList.size() > 0) {
                    activityReference.get().zoomView.setOnClickListener(view -> activityReference.get().showCarouselView());
                    activityReference.get().showIndicator();
                }

                for (int i = 0; i < publicArtsTables.size(); i++) {
                    activityReference.get().fromMuseumAbout = false;
                    activityReference.get().loadData(null,
                            publicArtsTables.get(i).getShort_description(),
                            publicArtsTables.get(i).getDescription(),
                            null, null, null,
                            null, null, null,
                            publicArtsTables.get(i).getLatitude(),
                            publicArtsTables.get(i).getLongitude(), activityReference.get().fromMuseumAbout, null);
                }
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().commonContentLayout.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTable> doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %s", activityReference.get().comingFrom,
                    activityReference.get().appLanguage, publicArtsId);
            return activityReference.get().qmDatabase.getPublicArtsTableDao()
                    .getPublicArtsDetails(publicArtsId, activityReference.get().appLanguage);

        }
    }

    public void getMuseumAboutDetailsFromAPI(String id, boolean isLaunchEvent) {
        Timber.i("getMuseumAboutDetailsFromAPI(id: %s, language: %s)", id, appLanguage);
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Call<ArrayList<MuseumAboutModel>> call;

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        if (isLaunchEvent && !(Objects.equals(id, "66") || Objects.equals(id, "638")))
            call = apiService.getLaunchMuseumAboutDetails(appLanguage, id);
        else
            call = apiService.getMuseumAboutDetails(appLanguage, id);
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<MuseumAboutModel>> call,
                                   @NonNull Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getMuseumAboutDetailsFromAPI() - isSuccessful");
                        commonContentLayout.setVisibility(View.VISIBLE);
                        museumAboutModels = response.body();
                        if (isLaunchEvent) {
                            eventDateLayout.setVisibility(View.VISIBLE);
                            eventDateTxt.setText(museumAboutModels.get(0).getEventDate());
                            videoLayout.setVisibility(View.GONE);
                            downloadText.setVisibility(View.VISIBLE);
                            mainTitle = museumAboutModels.get(0).getName();
                        } else {
                            timingTitle.setText(R.string.museum_timings);
                        }
                        if (museumAboutModels.get(0).getImageList().size() > 0)
                            headerImage = museumAboutModels.get(0).getImageList().get(0);
                        for (int i = 0; i < museumAboutModels.get(0).getImageList().size(); i++) {
                            imageList.add(i, museumAboutModels.get(0).getImageList().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator();
                        }

                        if (!DetailsActivity.this.isFinishing()) {
                            GlideApp.with(DetailsActivity.this)
                                    .load(headerImage)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(headerImageView);
                        }

                        first_description = "";
                        if (museumAboutModels.get(0).getDescriptionList().size() > 0)
                            first_description = museumAboutModels.get(0).getDescriptionList().get(0);
                        long_description = "";
                        for (int i = 1; i < museumAboutModels.get(0).getDescriptionList().size(); i++) {
                            if (isLaunchEvent) {
                                if (i == museumAboutModels.get(0).getDescriptionList().size() - 1) {
                                    String str[] = museumAboutModels.get(0).getDescriptionList().get(i).
                                            trim().split(" ");
                                    for (int j = 0; j < str.length; j++) {
                                        if (j == str.length - 2) {
                                            long_description = long_description +
                                                    "<a href='http://visitqatar.qa/'> " + str[j];
                                            long_description = long_description + " ";
                                        } else if (j == str.length - 1) {
                                            long_description = long_description + str[j];
                                            long_description = long_description + " </a>";
                                        } else {
                                            long_description = long_description + str[j];
                                            long_description = long_description + " ";
                                        }
                                    }
                                } else {
                                    long_description = String.format("%s%s", long_description, museumAboutModels.get(0).getDescriptionList().get(i));
                                    long_description = long_description + "<br/><br/><br/>";
                                }
                            } else {
                                long_description = long_description + museumAboutModels.get(0).getDescriptionList().get(i);
                                long_description = long_description + "\n\n";
                            }
                        }
                        fromMuseumAbout = true;
                        String file;
                        if (museumAboutModels.get(0).getVideoInfo() == null || museumAboutModels.get(0).getVideoInfo().size() == 0) {
                            file = null;
                        } else {
                            file = museumAboutModels.get(0).getVideoInfo().get(0);
                        }
                        if (!isLaunchEvent)
                            loadData(null,
                                    first_description,
                                    null,
                                    museumAboutModels.get(0).getSubTitle(),
                                    long_description,
                                    museumAboutModels.get(0).getTimingInfo(),
                                    null,
                                    museumAboutModels.get(0).getContactNumber(),
                                    museumAboutModels.get(0).getContactEmail(),
                                    museumAboutModels.get(0).getLatitude(),
                                    museumAboutModels.get(0).getLongitude(),
                                    fromMuseumAbout,
                                    file);
                        else
                            loadData(null,
                                    first_description,
                                    long_description,
                                    museumAboutModels.get(0).getSubTitle(),
                                    null,
                                    museumAboutModels.get(0).getTimingInfo(),
                                    null,
                                    museumAboutModels.get(0).getContactNumber(),
                                    museumAboutModels.get(0).getContactEmail(),
                                    museumAboutModels.get(0).getLatitude(),
                                    museumAboutModels.get(0).getLongitude(),
                                    fromMuseumAbout,
                                    file);
                        new MuseumAboutRowCount(DetailsActivity.this, appLanguage).execute();

                    } else {
                        Timber.i("get%sDetailsFromAPI() - have no data", comingFrom);
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("get%sDetailsFromAPI() - response not successful", comingFrom);
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<MuseumAboutModel>> call, @NonNull Throwable t) {
                Timber.e("get%sDetailsFromAPI() - onFailure: %s", comingFrom, t.getMessage());
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumAboutDetailsFromDatabase(String id, boolean isLaunchEvent) {
        Timber.i("getMuseumAboutDetailsFromDatabase()");
        progressBar.setVisibility(View.VISIBLE);
        new RetrieveMuseumAboutData(DetailsActivity.this, id, isLaunchEvent).execute();

    }

    public static class MuseumAboutRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        MuseumAboutRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().museumAboutRowCount = integer;
            if (activityReference.get().museumAboutRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckMuseumAboutDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().comingFrom);
                new InsertMuseumAboutDatabaseTask(activityReference.get(), activityReference.get().museumAboutTable,
                        language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
            return activityReference.get().qmDatabase.getMuseumAboutDao()
                    .getNumberOfRows(language);
        }
    }

    public static class CheckMuseumAboutDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTable museumAboutTable;
        String language;

        CheckMuseumAboutDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().museumAboutModels.size() > 0) {
                for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                    int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkIdExist(
                            Integer.parseInt(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().museumAboutModels.get(i).getMuseumId());
                        new UpdateMuseumAboutDetailTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language: %s) with id: %s", activityReference.get().comingFrom,
                                language, activityReference.get().museumAboutModels.get(i).getMuseumId());
                        museumAboutTable = new MuseumAboutTable(
                                activityReference.get().museumAboutModels.get(i).getName(),
                                Long.parseLong(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                                activityReference.get().museumAboutModels.get(i).getTourGuideAvailable(),
                                activityReference.get().first_description,
                                activityReference.get().long_description,
                                activityReference.get().museumAboutModels.get(i).getImageList().get(0),
                                activityReference.get().museumAboutModels.get(i).getContactNumber(),
                                activityReference.get().museumAboutModels.get(i).getContactEmail(),
                                activityReference.get().museumAboutModels.get(i).getLatitude(),
                                activityReference.get().museumAboutModels.get(i).getLongitude(),
                                activityReference.get().museumAboutModels.get(i).getTourGuideAvailability(),
                                activityReference.get().museumAboutModels.get(i).getSubTitle(),
                                activityReference.get().museumAboutModels.get(i).getTimingInfo(),
                                activityReference.get().museumAboutModels.get(i).getEventDate(),
                                language);
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTable);
                    }
                }
            }

            return null;
        }
    }

    public static class InsertMuseumAboutDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTable museumAboutTable;
        String language;

        InsertMuseumAboutDatabaseTask(DetailsActivity context, MuseumAboutTable museumAboutTable,
                                      String lan) {
            activityReference = new WeakReference<>(context);
            this.museumAboutTable = museumAboutTable;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().museumAboutModels != null) {
                Converter converters = new Converter();
                for (int i = 0; i < activityReference.get().museumAboutModels.size(); i++) {
                    Timber.i("Inserting %s Table(language: %s) with id: %s", activityReference.get().comingFrom,
                            language, activityReference.get().museumAboutModels.get(i).getMuseumId());
                    museumAboutTable = new MuseumAboutTable(
                            activityReference.get().museumAboutModels.get(i).getName(),
                            Long.parseLong(activityReference.get().museumAboutModels.get(i).getMuseumId()),
                            activityReference.get().museumAboutModels.get(i).getTourGuideAvailable(),
                            activityReference.get().first_description,
                            activityReference.get().long_description,
                            converters.fromArrayList(activityReference.get().museumAboutModels.get(i).getImageList()),
                            activityReference.get().museumAboutModels.get(i).getContactNumber(),
                            activityReference.get().museumAboutModels.get(i).getContactEmail(),
                            activityReference.get().museumAboutModels.get(i).getLatitude(),
                            activityReference.get().museumAboutModels.get(i).getLongitude(),
                            activityReference.get().museumAboutModels.get(i).getTourGuideAvailability(),
                            activityReference.get().museumAboutModels.get(i).getSubTitle(),
                            activityReference.get().museumAboutModels.get(i).getTimingInfo(),
                            activityReference.get().museumAboutModels.get(i).getEventDate(),
                            language);
                    activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class UpdateMuseumAboutDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateMuseumAboutDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().museumAboutModels.get(position).getMuseumId());
            Converter converters = new Converter();
            activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutData(
                    activityReference.get().museumAboutModels.get(position).getName(),
                    activityReference.get().museumAboutModels.get(position).getTourGuideAvailable(),
                    activityReference.get().first_description,
                    activityReference.get().long_description,
                    converters.fromArrayList(activityReference.get().museumAboutModels.get(position).getImageList()),
                    activityReference.get().museumAboutModels.get(position).getContactNumber(),
                    activityReference.get().museumAboutModels.get(position).getContactEmail(),
                    activityReference.get().museumAboutModels.get(position).getLatitude(),
                    activityReference.get().museumAboutModels.get(position).getLongitude(),
                    activityReference.get().museumAboutModels.get(position).getTourGuideAvailability(),
                    activityReference.get().museumAboutModels.get(position).getSubTitle(),
                    activityReference.get().museumAboutModels.get(position).getTimingInfo(),
                    Long.parseLong(activityReference.get().museumAboutModels.get(position).getMuseumId()),
                    language);

            return null;
        }
    }

    public static class RetrieveMuseumAboutData extends AsyncTask<Void, Void, MuseumAboutTable> {
        private WeakReference<DetailsActivity> activityReference;
        String museumId;
        boolean isLaunchEvent;

        RetrieveMuseumAboutData(DetailsActivity context, String museumId, boolean isLaunchEvent) {
            activityReference = new WeakReference<>(context);
            this.museumId = museumId;
            this.isLaunchEvent = isLaunchEvent;
        }

        @Override
        protected MuseumAboutTable doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %s", activityReference.get().appLanguage,
                    activityReference.get().comingFrom, museumId);
            return activityReference.get().qmDatabase.getMuseumAboutDao()
                    .getMuseumAboutData(Integer.parseInt(museumId), activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(MuseumAboutTable museumAboutTable) {
            if (museumAboutTable != null &&
                    !museumAboutTable.getShort_description().equals("")) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        museumId);
                Converter converters = new Converter();
                activityReference.get().commonContentLayout.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
                if (museumAboutTable.getMuseum_image().contains("[")) {
                    ArrayList<String> list = converters.fromString(museumAboutTable.getMuseum_image());
                    for (int i = 0; i < list.size(); i++) {
                        activityReference.get().imageList.add(i, list.get(i));
                    }
                }
                if (!activityReference.get().isFinishing())
                    GlideApp.with(activityReference.get())
                            .load(activityReference.get().imageList.get(0))
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(activityReference.get().headerImageView);

                if (activityReference.get().imageList.size() > 0) {
                    activityReference.get().zoomView.setOnClickListener(view -> activityReference.get().showCarouselView());
                    activityReference.get().showIndicator();
                }

                String description1 = museumAboutTable.getShort_description();
                String description2 = museumAboutTable.getLong_description();

                if (isLaunchEvent) {
                    activityReference.get().eventDateLayout.setVisibility(View.VISIBLE);
                    activityReference.get().eventDateTxt.setText(museumAboutTable.getEvent_date());
                    activityReference.get().videoLayout.setVisibility(View.GONE);
                    activityReference.get().downloadText.setVisibility(View.VISIBLE);
                } else {
                    activityReference.get().timingTitle.setText(R.string.museum_timings);
                }
                if (!isLaunchEvent)
                    activityReference.get().loadData(null, description1,
                            null,
                            museumAboutTable.getMuseum_subtitle(), description2,
                            museumAboutTable.getMuseum_opening_time(),
                            null, museumAboutTable.getMuseum_contact_number(),
                            museumAboutTable.getMuseum_contact_email(),
                            museumAboutTable.getMuseum_latitude(),
                            museumAboutTable.getMuseum_longitude(),
                            true, null);
                else
                    activityReference.get().loadData(null, description1,
                            description2,
                            museumAboutTable.getMuseum_subtitle(), null,
                            museumAboutTable.getMuseum_opening_time(),
                            null, museumAboutTable.getMuseum_contact_number(),
                            museumAboutTable.getMuseum_contact_email(),
                            museumAboutTable.getMuseum_latitude(),
                            museumAboutTable.getMuseum_longitude(),
                            true, null);

            } else {
                Timber.i("Have no data in database");
                activityReference.get().commonContentLayout.setVisibility(View.INVISIBLE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }
    }

    public void imageValue(int value) {
        Timber.i("This activity was clicked: %d", value);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Timber.i("onBackPressed()");
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }


    public static class FacilityRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<DetailsActivity> activityReference;
        String language;

        FacilityRowCount(DetailsActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language: %s)", activityReference.get().comingFrom, language);
            return activityReference.get().qmDatabase.getFacilitiesDetailTableDao()
                    .getNumberOfRows(language);

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckFacilityDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().comingFrom);
                new InsertFacilityDataToDataBase(activityReference.get(), activityReference.get().facilityDetailTable,
                        language).execute();
            }
        }
    }

    public static class CheckFacilityDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        private FacilityDetailTable facilityDetailTable;
        String language;

        CheckFacilityDBRowExist(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().facilitiesDetailModels.size() > 0) {
                for (int i = 0; i < activityReference.get().facilitiesDetailModels.size(); i++) {
                    int n = activityReference.get().qmDatabase.getFacilitiesDetailTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId());
                        new UpdateFacilityTable(activityReference.get(), language).execute();
                    } else {
                        Timber.i("Inserting %s Table(language: %s) with id: %s", activityReference.get().comingFrom,
                                language,
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId());
                        facilityDetailTable = new FacilityDetailTable(
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId(),
                                "",
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesTitle(),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilityImage().get(0),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesSubtitle(),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilityDescription(),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesTiming(),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilityTitleTiming(),
                                activityReference.get().facilitiesDetailModels.get(i).getLongitude(),
                                activityReference.get().facilitiesDetailModels.get(i).getFacilitiesCategoryId(),
                                activityReference.get().facilitiesDetailModels.get(i).getLatitude(),
                                activityReference.get().facilitiesDetailModels.get(i).getLocationTitle(),
                                language);

                        activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertData(facilityDetailTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateFacilityTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        String language;

        UpdateFacilityTable(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s details table(language: %s) with id: %s", activityReference.get().comingFrom,
                    language, activityReference.get().facilitiesDetailModels.get(0).getFacilitiesId())
            ;
            activityReference.get().qmDatabase.getFacilitiesDetailTableDao().updateFacilityDetail(
                    "",
                    activityReference.get().facilitiesDetailModels.get(0).getFacilitiesTitle(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilityImage().get(0),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilitiesId(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilityDescription(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilitiesTiming(),
                    activityReference.get().facilitiesDetailModels.get(0).getLongitude(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilitiesCategoryId(),
                    activityReference.get().facilitiesDetailModels.get(0).getLatitude(),
                    activityReference.get().facilitiesDetailModels.get(0).getLocationTitle(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilityTitleTiming(),
                    activityReference.get().facilitiesDetailModels.get(0).getFacilitiesId(),
                    language
            );
            return null;
        }
    }

    public static class InsertFacilityDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<DetailsActivity> activityReference;
        private FacilityDetailTable facilityDetailTable;
        String language;

        InsertFacilityDataToDataBase(DetailsActivity context, FacilityDetailTable facilityDetailTable,
                                     String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.facilityDetailTable = facilityDetailTable;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().facilitiesDetailModels != null &&
                    activityReference.get().facilitiesDetailModels.size() > 0) {
                for (int i = 0; i < activityReference.get().facilitiesDetailModels.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().comingFrom,
                            language,
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId());
                    facilityDetailTable = new FacilityDetailTable(
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesId(),
                            "",
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesTitle(),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilityImage().get(0),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesSubtitle(),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilityDescription(),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesTiming(),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilityTitleTiming(),
                            activityReference.get().facilitiesDetailModels.get(i).getLongitude(),
                            activityReference.get().facilitiesDetailModels.get(i).getFacilitiesCategoryId(),
                            activityReference.get().facilitiesDetailModels.get(i).getLatitude(),
                            activityReference.get().facilitiesDetailModels.get(i).getLocationTitle(),
                            language);
                    activityReference.get().qmDatabase.getFacilitiesDetailTableDao().insertData(facilityDetailTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class RetrieveFacilityData extends AsyncTask<Void, Void, List<FacilityDetailTable>> {
        private WeakReference<DetailsActivity> activityReference;

        int id;

        RetrieveFacilityData(DetailsActivity context, int id) {
            this.activityReference = new WeakReference<>(context);
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityDetailTable> doInBackground(Void... voids) {
            Timber.i("get%sData(language: %s) for id: %d", activityReference.get().comingFrom,
                    activityReference.get().appLanguage, id);
            return activityReference.get().qmDatabase.getFacilitiesDetailTableDao()
                    .getFacilityDetail(id, activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<FacilityDetailTable> facilityDetailTables) {
            activityReference.get().facilitiesDetailModels.clear();
            if (facilityDetailTables.size() > 0) {
                Timber.i("Setting %s details from database with id: %s", activityReference.get().comingFrom,
                        id);
                for (int i = 0; i < facilityDetailTables.size(); i++) {

                    activityReference.get().loadData(facilityDetailTables.get(i).getFacilitySubtitle(),
                            facilityDetailTables.get(i).getFacilityDescription(),
                            null, null, null,
                            facilityDetailTables.get(i).getFacilityTiming(),
                            null, null, null,
                            facilityDetailTables.get(i).getFacilityLatitude(),
                            facilityDetailTables.get(i).getFacilityLongitude(),
                            true, null);
                    activityReference.get().videoLayout.setVisibility(View.GONE);

                }
                activityReference.get().progressBar.setVisibility(View.GONE);


            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
