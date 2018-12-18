package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserRegistrationDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.home.UserRegistrationModel;
import com.qatarmuseums.qatarmuseumsapp.museum.GlideLoaderForMuseum;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.profile.Model;
import com.qatarmuseums.qatarmuseumsapp.profile.Und;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import org.json.JSONStringer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import static cn.lightsky.infiniteindicator.IndicatorConfiguration.LEFT;
import static cn.lightsky.infiniteindicator.IndicatorConfiguration.RIGHT;

public class DetailsActivity extends AppCompatActivity implements IPullZoom, OnMapReadyCallback {

    ImageView headerImageView, toolbarClose, favIcon, shareIcon;
    String headerImage;
    String mainTitle, comingFrom;
    boolean isFavourite;
    Toolbar toolbar;
    TextView title, subTitle, shortDescription, longDescription, secondTitle, secondTitleDescription,
            timingTitle, timingDetails, locationDetails, contactDetails;
    private Util util;
    private Animation zoomOutAnimation;
    private PullToZoomCoordinatorLayout coordinatorLayout;
    //    private FrameLayout frameLayout;
    private View zoomView;
    ArrayList<String> imageList = new ArrayList<String>();
    private AppBarLayout appBarLayout;
    private int headerOffSetSize, appLanguage;
    private LinearLayout secondTitleLayout, timingLayout, contactLayout, retryLayout, videoLayout,
            downloadLayout;
    private String latitude, longitude, id;
    Intent intent;
    int language;
    String token, startTime, endTime;
    long start, end;
    String type, entity_id, entity_type, anon_mail, user_uid, count, author_uid, state, created,
            updated, field_confirm_attendance, field_number_of_attendees, field_first_name_,
            field_nmoq_last_name, field_qma_edu_reg_date, time_zone;
    int field_membership_number;
    JSONStringer jsonStringer = null;
    JSONStringer completionjsonStringer = null;
    QMDatabase qmDatabase;
    PublicArtsTableEnglish publicArtsTableEnglish;
    PublicArtsTableArabic publicArtsTableArabic;
    MuseumAboutTableEnglish museumAboutTableEnglish;
    MuseumAboutTableArabic museumAboutTableArabic;
    int publicArtsTableRowCount, heritageTableRowCount,
            exhibitionRowCount, museumAboutRowCount;
    SharedPreferences qmPreferences;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;
    ArrayList<PublicArtModel> publicArtModel = new ArrayList<>();
    ArrayList<MuseumAboutModel> museumAboutModels = new ArrayList<>();
    ArrayList<HeritageOrExhibitionDetailModel> heritageOrExhibitionDetailModel = new ArrayList<>();
    boolean fromMuseumAbout = false;
    String first_description;
    String long_description;
    int imageItemPosition;
    MapView mapDetails;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap gmap, gvalue;
    LinearLayout mapView;
    ImageView mapImageView, direction;
    int iconView = 0;
    ProgressBar progressBar;
    ImageView imagePlaceHolder;
    JzvdStd jzvdStd;
    FrameLayout frameLayout;
    Button retryButton;
    private InfiniteIndicator circleIndicator;
    private ArrayList ads;
    private GlideLoaderForMuseum glideLoader;
    private IndicatorConfiguration configuration;
    private LinearLayout eventDateLayout;
    private TextView eventDateTxt;
    private TextView downloadText, speakerName, speakerInfo, locationTitle;
    private View downloadButton;
    private LinearLayout interestLayout;
    private SwitchCompat interestToggle;
    private Boolean interested = false;
    private LinearLayout locationLayout;
    private LinearLayout offerLayout;
    private View claimButton;
    private LinearLayout speakerLayout;
    private ImageView speakerImage;
    private float curveRadius = 30F;
    private String description;
    private String contactInfo;
    private String promotionCode;
    private TextView offerCode;
    private String claimOfferURL;
    private Intent navigation_intent;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private String register, registered;
    private String eventDate, nid;
    TourDetailsTableEnglish tourDetailsTableEnglish;
    TourDetailsTableArabic tourDetailsTableArabic;
    ArrayList<RegistrationDetailsModel> registrationDetailsModels = new ArrayList<>();
    String registrationId;
    RegistrationDetailsModel registrationDetailsModel;
    private UserRegistrationDetailsTable registrationDetails;
    private HashMap<String, UserRegistrationDetailsTable> registrationDetailsMap;
    private HashMap<String, TourDetailsModel> tourDetailsMap;
    private TourDetailsModel tourDetailsModel;
    private Iterator<String> myVeryOwnIterator;
    private String currentEventTimeStampDiff;
    private boolean isTimeSlotAvailable;
    private UserRegistrationDetailsTable userRegistrationDetailsTable;
    private RelativeLayout registrationLoader;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        token = qmPreferences.getString("TOKEN", null);
        user_uid = qmPreferences.getString("UID", null);
        field_nmoq_last_name = qmPreferences.getString("LAST_NAME", null);
        field_first_name_ = qmPreferences.getString("FIRST_NAME", null);
        field_membership_number = qmPreferences.getInt("MEMBERSHIP", 0);
        time_zone = qmPreferences.getString("TIMEZONE", null);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        headerImage = intent.getStringExtra("HEADER_IMAGE");
        description = intent.getStringExtra("LONG_DESC");
        promotionCode = intent.getStringExtra("PROMOTION_CODE");
        claimOfferURL = intent.getStringExtra("CLAIM_OFFER");
        contactInfo = intent.getStringExtra("CONTACT");
        id = intent.getStringExtra("ID");
        isFavourite = intent.getBooleanExtra("IS_FAVOURITE", false);

        registrationDetailsMap = new HashMap<String, UserRegistrationDetailsTable>();
        tourDetailsMap = new HashMap<String, TourDetailsModel>();

        if (comingFrom.equals(getString(R.string.museum_tours))) {
            description = intent.getStringExtra("DESCRIPTION");
            eventDate = intent.getStringExtra("DATE");
            contactInfo = intent.getStringExtra("CONTACT");
            register = intent.getStringExtra("REGISTER");
            registered = intent.getStringExtra("REGISTERED");
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");
            nid = intent.getStringExtra("NID");
            tourDetailsList = intent.getParcelableArrayListExtra("RESPONSE");
            tourDetailsMap.clear();
            for (int i = 0; i < tourDetailsList.size(); i++) {
                tourDetailsModel = tourDetailsList.get(i);
                if (!Objects.equals(tourDetailsModel.getnId(), nid))
                    tourDetailsMap.put(tourDetailsModel.getnId(), tourDetailsModel);
                else
                    currentEventTimeStampDiff = tourDetailsModel.getEventTimeStampDiff();
            }

        }
        qmDatabase = QMDatabase.getInstance(DetailsActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        title = (TextView) findViewById(R.id.main_title);
        subTitle = (TextView) findViewById(R.id.sub_title);
        shortDescription = (TextView) findViewById(R.id.short_description);
        longDescription = (TextView) findViewById(R.id.long_description);
        secondTitle = (TextView) findViewById(R.id.second_title);
        secondTitleDescription = (TextView) findViewById(R.id.second_short_description);
        timingTitle = (TextView) findViewById(R.id.timing_title);
        timingDetails = (TextView) findViewById(R.id.timing_info);
        locationDetails = (TextView) findViewById(R.id.location_info);
        mapImageView = findViewById(R.id.map_view);
        direction = findViewById(R.id.direction);
        imagePlaceHolder = (ImageView) findViewById(R.id.video_place_holder);
        jzvdStd = (JzvdStd) findViewById(R.id.videoplayer);
        mapView = (LinearLayout) findViewById(R.id.map_layout);
        mapDetails = (MapView) findViewById(R.id.map_info);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapDetails.onCreate(mapViewBundle);
        mapDetails.getMapAsync(this);
        contactDetails = (TextView) findViewById(R.id.contact_info);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);
        secondTitleLayout = (LinearLayout) findViewById(R.id.second_title_layout);
        timingLayout = (LinearLayout) findViewById(R.id.timing_layout);
        contactLayout = (LinearLayout) findViewById(R.id.contact_layout);
        commonContentLayout = (LinearLayout) findViewById(R.id.common_content_layout);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout_about);
        retryButton = (Button) findViewById(R.id.retry_btn);
        circleIndicator = (InfiniteIndicator) findViewById(R.id.carousel_indicator);
        registrationLoader = (RelativeLayout) findViewById(R.id.registration_loader);
        eventDateLayout = findViewById(R.id.event_date_layout);
        eventDateTxt = findViewById(R.id.event_date);
        videoLayout = findViewById(R.id.video_layout);
        downloadLayout = findViewById(R.id.download_layout);
        downloadText = findViewById(R.id.download_text);
        downloadButton = findViewById(R.id.download_button);
        interestLayout = findViewById(R.id.interest_layout);
        interestToggle = findViewById(R.id.interest_toggle_button);
        locationLayout = findViewById(R.id.location_layout);
        offerLayout = findViewById(R.id.offer_layout);
        offerCode = findViewById(R.id.offer_code);
        claimButton = findViewById(R.id.claim_offer_button);
        speakerLayout = findViewById(R.id.speaker_layout);
        speakerImage = findViewById(R.id.speaker_img);
        speakerName = findViewById(R.id.name_of_the_speaker);
        speakerInfo = findViewById(R.id.information_of_the_speaker);
        locationTitle = findViewById(R.id.location_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speakerImage.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(),
                            (int) (view.getHeight() + curveRadius), curveRadius);
                }
            });
            speakerImage.setClipToOutline(true);
        }
        util = new Util();
        title.setText(mainTitle);
        getData();
        if (comingFrom.equals(getString(R.string.museum_discussion)) ||
                comingFrom.equals(getString(R.string.museum_travel))) {
            title.setAllCaps(false);
            title.setTextSize(33);
        }

        GlideApp.with(this)
                .load(headerImage)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(headerImageView);

        if (isFavourite)
            favIcon.setImageResource(R.drawable.heart_fill);
        else
            favIcon.setImageResource(R.drawable.heart_empty);

        toolbarClose.setOnClickListener(v ->

                onBackPressed());
        favIcon.setOnClickListener(v ->

        {
            if (util.checkImageResource(DetailsActivity.this, favIcon, R.drawable.heart_fill)) {
                favIcon.setImageResource(R.drawable.heart_empty);
            } else
                favIcon.setImageResource(R.drawable.heart_fill);
        });

        initViews();

        zoomOutAnimation = AnimationUtils.loadAnimation(

                getApplicationContext(),

                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v ->

        {
            getData();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener((v, event) ->

        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnTouchListener((v, event) ->

        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        favIcon.setOnTouchListener((v, event) ->

        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    favIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        shareIcon.setOnTouchListener((v, event) ->

        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shareIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        downloadText.setOnTouchListener((v, event) ->

        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downloadButton.startAnimation(zoomOutAnimation);
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
        downloadButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downloadButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        claimButton.setOnClickListener(v -> {
            if (!claimOfferURL.equals("")) {
                navigation_intent = new Intent(DetailsActivity.this, WebviewActivity.class);
                navigation_intent.putExtra("url", claimOfferURL);
                startActivity(navigation_intent);
            } else
                util.showComingSoonDialog(DetailsActivity.this, R.string.coming_soon_content);
        });
        mapImageView.setOnClickListener(view -> {
            if (iconView == 0) {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(DetailsActivity.this);
                } else {
                    iconView = 1;
                    mapImageView.setImageResource(R.drawable.ic_map);
                    gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DetailsActivity.this, R.raw.map_style));

                    gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            } else {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(DetailsActivity.this);
                } else {
                    iconView = 0;
                    mapImageView.setImageResource(R.drawable.ic_satellite);
                    gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DetailsActivity.this, R.raw.map_style));

                    gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            }
        });

        direction.setOnClickListener(view -> {
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
        downloadButton.setOnClickListener(v ->

                downloadAction());
        downloadText.setOnClickListener(v ->

                downloadAction());

        interestToggle.setOnTouchListener((v, event) -> {
            if (interestToggle.isChecked()) {
                if (registrationDetailsMap.size() > 0 && tourDetailsMap.size() > 0) {
                    myVeryOwnIterator = tourDetailsMap.keySet().iterator();
                    isTimeSlotAvailable = true;
                    while (myVeryOwnIterator.hasNext()) {
                        final String key = myVeryOwnIterator.next();
                        if (registrationDetailsMap.get(key) != null) {
                            if (tourDetailsMap.get(key).getEventTimeStampDiff().equals(currentEventTimeStampDiff)) {
                                isTimeSlotAvailable = false;
                            }
                        }
                    }
                    if (isTimeSlotAvailable) {
                        entryJsonarray();
                        entityRegistration(token, registrationDetailsModel);
                    } else {
                        util.showNormalDialog(DetailsActivity.this, R.string.time_slot_not_available);
                    }
                } else {
                    entryJsonarray();
                    entityRegistration(token, registrationDetailsModel);
                }
            } else {
                showDeclineDialog();
            }
            return false;
        });
    }

    public void entryJsonarray() {
        ArrayList<Und> values = new ArrayList<>();
        Und attendanceValue = new Und("1");
        values.clear();
        values.add(attendanceValue);
        Model attendancemodel = new Model(values);
        ArrayList<Und> nvalues = new ArrayList<>();
        Und numberofattendanceValue = new Und("2");
        nvalues.add(numberofattendanceValue);
        Model numberofattendancemodel = new Model(nvalues);
        ArrayList<Und> fvalues = new ArrayList<>();
        Und firstnameValue = new Und(field_first_name_, null, field_first_name_);
        fvalues.add(firstnameValue);
        Model firstnamemodel = new Model(fvalues);
        ArrayList<Und> lvalues = new ArrayList<>();
        Und lastnameValue = new Und(field_nmoq_last_name, null, field_nmoq_last_name);
        lvalues.add(lastnameValue);
        Model lastnamemodel = new Model(lvalues);
        ArrayList<Und> mvalues = new ArrayList<>();
        Und membershipValue = new Und(String.valueOf(field_membership_number));
        mvalues.add(membershipValue);
        Model membershipmodel = new Model(mvalues);
        String[] splitArray = eventDate.split("-");
        startTime = splitArray[0].concat(splitArray[1].trim());
        start = util.getTimeStamp(startTime);
        endTime = splitArray[0].concat(splitArray[2]);
        end = util.getTimeStamp(endTime);
        ArrayList<Und> rvalues = new ArrayList<>();
        Und membershipregValue = new Und(String.valueOf(start / 1000), String.valueOf(end / 1000), time_zone, "10800", "10800", time_zone, "datestamp");
        rvalues.add(membershipregValue);
        Model membershipregmodel = new Model(rvalues);
        registrationDetailsModel =
                new RegistrationDetailsModel("nmoq_event_registration", nid, "node",
                        user_uid, "1", user_uid, "pending",
                        String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),
                        String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),
                        attendancemodel, numberofattendancemodel, firstnamemodel,
                        lastnamemodel, membershipmodel, membershipregmodel);
    }

    protected void showDeclineDialog() {

        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getLayoutInflater().inflate(R.layout.vip_pop_up, null);
        dialog.setContentView(view);

        View line = (View) view.findViewById(R.id.view);
        Button yes = (Button) view.findViewById(R.id.acceptbtn);
        Button no = (Button) view.findViewById(R.id.accept_later_btn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        line.setVisibility(View.GONE);
        dialogTitle.setVisibility(View.GONE);
        yes.setText(getResources().getString(R.string.yes));
        no.setText(getResources().getString(R.string.no));
        dialogContent.setText(getResources().getString(R.string.decline_content_tour));

        yes.setOnClickListener(view1 -> {
            registrationLoader.setVisibility(View.VISIBLE);
            if (comingFrom.equals(getString(R.string.museum_discussion))) {
                new RetriveRegistrationId(DetailsActivity.this, id).execute();
            } else {
                new RetriveRegistrationId(DetailsActivity.this, nid).execute();
            }
            dialog.dismiss();
        });
        no.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void downloadAction() {
        util.showComingSoonDialog(this, R.string.coming_soon_content);
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
        super.onDestroy();
    }

    public void getData() {
        if (comingFrom.equals(getString(R.string.sidemenu_exhibition_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, language, "Exhibition_detail_Page.json");
            } else {
                getExhibitionAPIDataFromDatabase(id, language);
            }
        } else if (comingFrom.equals(getString(R.string.sidemenu_heritage_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, language, "heritage_detail_Page.json");
            } else {
                getHeritageAPIDataFromDatabase(id, language);
            }
        } else if (comingFrom.equals(getString(R.string.sidemenu_public_arts_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getPublicArtDetailsFromAPI(id, language);
            else
                getCommonListAPIDataFromDatabase(id, language);

        } else if (comingFrom.equals(getString(R.string.museum_about))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getMuseumAboutDetailsFromAPI(id, language, false);
            else
                getMuseumAboutDetailsFromDatabase(id, language, false);
        } else if (comingFrom.equals(getString(R.string.museum_about_launch))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getMuseumAboutDetailsFromAPI(id, language, true);
            else
                getMuseumAboutDetailsFromDatabase(id, language, true);
        } else if (comingFrom.equals(getString(R.string.museum_travel))) {
            getTravelsDetails();
        } else if (comingFrom.equals(getString(R.string.museum_discussion))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getSpecialEventDetailFromAPI(id, language);
            else
                getTourDetailsFromDatabase(id);
        } else if (comingFrom.equals(getString(R.string.museum_tours))) {
            setTourDetailsData();
        }
    }

    public void setTourDetailsData() {
        new RetriveRegistrationDetails(DetailsActivity.this, nid, true).execute();
        commonContentLayout.setVisibility(View.VISIBLE);
        interestLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        timingTitle.setText(R.string.date);
        loadData(null, description,
                null, null, null,
                eventDate, null, null, contactInfo, latitude, longitude,
                true, null);
    }

    public void getSpecialEventDetailFromAPI(String id, int appLanguage) {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<TourDetailsModel>> call = apiService.getTourDetails(language, id);
        call.enqueue(new Callback<ArrayList<TourDetailsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TourDetailsModel>> call, Response<ArrayList<TourDetailsModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        tourDetailsList.addAll(response.body());
                        new RetriveRegistrationDetails(DetailsActivity.this,
                                tourDetailsList.get(0).getnId(), false).execute();
                        commonContentLayout.setVisibility(View.VISIBLE);
                        interestLayout.setVisibility(View.VISIBLE);
                        videoLayout.setVisibility(View.GONE);
                        speakerLayout.setVisibility(View.VISIBLE);
                        removeTourDetailsHtmlTags(tourDetailsList);
                        if (tourDetailsList.get(0).getTourImage().size() > 1)
                            GlideApp.with(DetailsActivity.this)
                                    .load(tourDetailsList.get(0).getTourImage().get(1))
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(speakerImage);
                        speakerName.setText(tourDetailsList.get(0).getTourSpeakerName());
                        speakerInfo.setText(tourDetailsList.get(0).getTourSpeakerInfo());
                        timingTitle.setText(R.string.date);
                        locationTitle.setText(R.string.venue);
                        loadData(null,
                                tourDetailsList.get(0).getTourBody(),
                                null, null, null,
                                tourDetailsList.get(0).getTourDate(),
                                null, null,
                                tourDetailsList.get(0).getTourContactPhone() + "\n" +
                                        tourDetailsList.get(0).getTourContactEmail(),
                                tourDetailsList.get(0).getTourLatitude(),
                                tourDetailsList.get(0).getTourLongtitude(), true, null);

                        new SpecialEventDetailsRowCount(DetailsActivity.this, id).execute();
                    } else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<TourDetailsModel>> call, Throwable t) {
                commonContentLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private ArrayList<UserRegistrationModel> registeredEventLists = new ArrayList<>();

    public class RetriveRegistrationId extends AsyncTask<Void, Void, String> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;

        RetriveRegistrationId(DetailsActivity context, String eventID) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getRegistrationIdFromTable(eventID);
        }

        @Override
        protected void onPostExecute(String rId) {
            deleteEventRegistration(rId, token);
        }
    }

    public class InsertRegisteredEventToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private UserRegistrationDetailsTable userRegistrationDetailsTable;

        InsertRegisteredEventToDataBase(DetailsActivity context,
                                        UserRegistrationDetailsTable userRegistrationDetailsTable) {
            activityReference = new WeakReference<>(context);
            this.userRegistrationDetailsTable = userRegistrationDetailsTable;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            userRegistrationDetailsTable = new UserRegistrationDetailsTable(
                    registrationDetailsModel.getRegistrationID(),
                    nid,
                    mainTitle);
            activityReference.get().qmDatabase.getUserRegistrationTaleDao()
                    .insertUserRegistrationTable(userRegistrationDetailsTable);
            return true;
        }

    }

    public class deleteEventFromDataBase extends AsyncTask<Void, Void, Integer> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;

        deleteEventFromDataBase(DetailsActivity context, String eventID) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getUserRegistrationTaleDao().deleteEventFromTable(eventID);
        }

    }


    public class RetriveRegistrationDetails extends AsyncTask<Void, Void, List<UserRegistrationDetailsTable>> {
        private WeakReference<DetailsActivity> activityReference;
        String eventID;
        Boolean isTour;

        RetriveRegistrationDetails(DetailsActivity context, String eventID, Boolean isTour) {
            activityReference = new WeakReference<>(context);
            this.eventID = eventID;
            this.isTour = isTour;
        }

        @Override
        protected List<UserRegistrationDetailsTable> doInBackground(Void... voids) {
            if (isTour)
                return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getAllDataFromTable();
            else
                return activityReference.get().qmDatabase.getUserRegistrationTaleDao().getEventFromTable(eventID);

        }

        @Override
        protected void onPostExecute(List<UserRegistrationDetailsTable> userRegistrationDetailsTableList) {
            if (isTour) {
                if (userRegistrationDetailsTableList.size() > 0) {
                    registrationDetailsMap.clear();
                    for (int i = 0; i < userRegistrationDetailsTableList.size(); i++) {
                        registrationDetails = userRegistrationDetailsTableList.get(i);
                        registrationDetailsMap.put(registrationDetails.getEventId(), registrationDetails);
                    }
                    if (registrationDetailsMap.get(eventID) != null)
                        interestToggle.setChecked(false);
                    else
                        interestToggle.setChecked(true);

                } else
                    interestToggle.setChecked(true);
            } else {
                if (userRegistrationDetailsTableList.size() > 0) {
                    interestToggle.setChecked(false);
                } else
                    interestToggle.setChecked(true);
            }
        }
    }

    private void getTravelsDetails() {
        videoLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        offerLayout.setVisibility(View.VISIBLE);
        offerCode.setText(promotionCode);
        loadData(null, description, null, null,
                null, null, null, null,
                contactInfo, null, null, false, null);
    }

    public void getTourDetailsFromDatabase(String id) {
        progressBar.setVisibility(View.VISIBLE);
        new RetriveTourDetailsEnglish(DetailsActivity.this, id).execute();
    }

    public class SpecialEventDetailsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language, tourId;


        SpecialEventDetailsRowCount(DetailsActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int tourDetailsRowCount = integer;
            if (tourDetailsRowCount > 0) {
                new CheckTourDetailsRowExist(DetailsActivity.this, tourId).execute();
            } else {
                new InsertDatabaseTask(DetailsActivity.this, tourDetailsTableEnglish,
                        tourDetailsTableArabic, tourId).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getNumberOfRowsEnglish();
        }
    }

    public class CheckTourDetailsRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        String tourId;

        CheckTourDetailsRowExist(DetailsActivity context, String tourId) {
            activityReference = new WeakReference<>(context);

            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (tourDetailsList.size() > 0) {
                int n = activityReference.get().qmDatabase.getTourDetailsTaleDao().checkEnglishIdExist(
                        tourId);
                if (n > 0) {
                    new DeleteEventsTableRow(DetailsActivity.this,
                            tourId).execute();
                } else {
                    new InsertDatabaseTask(DetailsActivity.this, tourDetailsTableEnglish,
                            tourDetailsTableArabic, tourId).execute();
                }

            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private TourDetailsTableEnglish tourDetailsTableEnglish;
        private TourDetailsTableArabic tourDetailsTableArabic;
        String language;
        String tourId;

        InsertDatabaseTask(DetailsActivity context, TourDetailsTableEnglish tourDetailsTableEnglish,
                           TourDetailsTableArabic tourDetailsTableArabic, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourDetailsTableEnglish = tourDetailsTableEnglish;
            this.tourDetailsTableArabic = tourDetailsTableArabic;

            this.tourId = tourId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (tourDetailsList != null) {
                for (int i = 0; i < tourDetailsList.size(); i++) {
                    Convertor converters = new Convertor();
                    tourDetailsTableEnglish = new TourDetailsTableEnglish(
                            tourDetailsList.get(i).getTourTitle(),
                            converters.fromArrayList(tourDetailsList.get(i).getTourImage()),
                            tourDetailsList.get(i).getTourDate(),
                            tourDetailsList.get(i).getTourEventId(),
                            tourDetailsList.get(i).getTourContactEmail(),
                            tourDetailsList.get(i).getTourContactPhone(),
                            tourDetailsList.get(i).getTourLatitude(),
                            tourDetailsList.get(i).getTourLongtitude(),
                            tourDetailsList.get(i).getTourSortId(),
                            tourDetailsList.get(i).getTourBody(),
                            tourDetailsList.get(i).getTourRegistered(),
                            tourDetailsList.get(i).getTourSpeakerName(),
                            tourDetailsList.get(i).getTourSpeakerInfo()
                    );
                    activityReference.get().qmDatabase.getTourDetailsTaleDao().
                            insert(tourDetailsTableEnglish);
                }
            }
            return true;
        }
    }

    public class DeleteEventsTableRow extends AsyncTask<Void, Void, Void> {
        private WeakReference<DetailsActivity> activityReference;
        String language, tourId;

        DeleteEventsTableRow(DetailsActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            activityReference.get().qmDatabase.getTourDetailsTaleDao().
                    deleteEnglishTourDetailsWithId(tourId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new InsertDatabaseTask(DetailsActivity.this, tourDetailsTableEnglish,
                    tourDetailsTableArabic, tourId).execute();

        }
    }

    public class RetriveTourDetailsEnglish extends AsyncTask<Void, Void, List<TourDetailsTableEnglish>> {
        private WeakReference<DetailsActivity> activityReference;
        String tourId;

        RetriveTourDetailsEnglish(DetailsActivity context, String tourId) {
            activityReference = new WeakReference<>(context);
            this.tourId = tourId;
        }

        @Override
        protected List<TourDetailsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTourDetailsTaleDao().getTourDetailsWithIdEnglish(tourId);
        }

        @Override
        protected void onPostExecute(List<TourDetailsTableEnglish> tourDetailsTableEnglishList) {
            tourDetailsList.clear();
            Convertor converters = new Convertor();
            if (tourDetailsTableEnglishList.size() > 0) {
                interestLayout.setVisibility(View.VISIBLE);
                videoLayout.setVisibility(View.GONE);
                speakerLayout.setVisibility(View.VISIBLE);
                if (tourDetailsTableEnglishList.size() > 1)
                    GlideApp.with(DetailsActivity.this)
                            .load(converters.fromString(tourDetailsTableEnglishList.get(0).getTour_images()).get(1))
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(speakerImage);
                speakerName.setText(tourDetailsTableEnglishList.get(0).getModerator_name());
                speakerInfo.setText(tourDetailsTableEnglishList.get(0).getDescription_for_moderator());
                timingTitle.setText(R.string.date);
                locationTitle.setText(R.string.venue);
                loadData(null,
                        tourDetailsTableEnglishList.get(0).getTour_body(),
                        null, null, null,
                        tourDetailsTableEnglishList.get(0).getTour_date(),
                        null, null,
                        tourDetailsTableEnglishList.get(0).getTour_contact_phone() + "\n" +
                                tourDetailsTableEnglishList.get(0).getTour_contact_email(),
                        tourDetailsTableEnglishList.get(0).getTour_latitude(),
                        tourDetailsTableEnglishList.get(0).getTour_longtitude(), true, null);

                commonContentLayout.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            } else {
                commonContentLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getCommonListAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishPublicArtsData(DetailsActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicPublicArtsData(DetailsActivity.this, appLanguage, id).execute();
        }
    }

    private void getHeritageAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishHeritageData(DetailsActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicHeritageData(DetailsActivity.this, appLanguage, id).execute();
        }
    }

    private void getExhibitionAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishExhibitionData(DetailsActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicExhibitionData(DetailsActivity.this, appLanguage, id).execute();
        }
    }

    private String convertDegreetoDecimalMeasure(String degreeValue) {
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
        frameLayout = (FrameLayout) findViewById(R.id.fragment_frame);
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

    public void showCarouselView() {
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
                         String closingTime, String locationInfo, String contactInfo, String latitudefromApi,
                         String longitudefromApi, boolean museumAboutStatus, String videoFile) {
        if (shortDescription != null) {
            commonContentLayout.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            this.title.setText(mainTitle);
            if (museumAboutStatus) {
                latitude = latitudefromApi;
                longitude = longitudefromApi;
            } else {
                latitude = intent.getStringExtra("LATITUDE");
                longitude = intent.getStringExtra("LONGITUDE");
            }
            if (videoFile != null) {

                String videoPath = videoFile;
                Uri uri = Uri.parse(videoPath);
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
                this.longDescription.setText(longDescription);
            } else {
                this.longDescription.setVisibility(View.GONE);

            }
            if (secondTitle != null) {
                this.secondTitleLayout.setVisibility(View.VISIBLE);
                this.secondTitle.setText(secondTitle);
                this.secondTitleDescription.setText(secondTitleDescription);
            }
            if (comingFrom.equals(getString(R.string.museum_tours)) ||
                    comingFrom.equals(getString(R.string.museum_discussion)))
                timingTitle.setText(R.string.date);
            else
                timingTitle.setText(R.string.museum_timings);
            if (openingTime != null) {
                this.timingLayout.setVisibility(View.VISIBLE);
                String time = /* getResources().getString(R.string.everyday_from) +" " + */
                        openingTime /*+ " " + getResources().getString(R.string.to) + " " +
                    closingTime*/;
                this.timingDetails.setText(time);
            }
            if (locationInfo != null) {
                this.locationDetails.setVisibility(View.VISIBLE);
                this.locationDetails.setText(locationInfo);
            }
            if (contactInfo != null && !(contactInfo.isEmpty()) && !contactInfo.equals("\n")) {
                this.contactLayout.setVisibility(View.VISIBLE);
                this.contactDetails.setText(contactInfo);
            }
            if (latitude != null) {
                if (latitude.contains("°")) {
                    latitude = convertDegreetoDecimalMeasure(latitude);
                    longitude = convertDegreetoDecimalMeasure(longitude);
                }
            }
            if (latitude != null && !latitude.equals("") && gvalue != null) {
                gmap = gvalue;
                gmap.setMinZoomPreference(12);
                LatLng ny = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                UiSettings uiSettings = gmap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(true);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ny);
                gmap.addMarker(markerOptions);
                gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
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


    public void loadDataForHeritageOrExhibitionDetails(String subTitle, String
            shortDescription, String longDescription,
                                                       String secondTitle, String secondTitleDescription, String timingInfo,
                                                       String locationInfo, String contactInfo, String latitudefromApi,
                                                       String longitudefromApi, String openingTime, String closingtime, String videoFile) {
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

                String videoPath = videoFile;
                Uri uri = Uri.parse(videoPath);
                jzvdStd.setUp(videoFile, "", Jzvd.SCREEN_WINDOW_NORMAL);
                jzvdStd.setVisibility(View.VISIBLE);
                imagePlaceHolder.setVisibility(View.GONE);

            } else {
                imagePlaceHolder.setVisibility(View.VISIBLE);
                jzvdStd.setVisibility(View.GONE);
                imagePlaceHolder.setImageResource(R.drawable.placeholder_video);
            }
            if (contactInfo != null && !(contactInfo.isEmpty())) {
                this.contactLayout.setVisibility(View.VISIBLE);
                this.contactDetails.setText(contactInfo);
            }
            if (latitude != null) {
                if (latitude.contains("°")) {
                    latitude = convertDegreetoDecimalMeasure(latitude);
                    longitude = convertDegreetoDecimalMeasure(longitude);

                }
            }
            if (latitude != null && !latitude.equals("")) {
                gmap = gvalue;
                gmap.setMinZoomPreference(12);
                LatLng ny = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                UiSettings uiSettings = gmap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(true);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ny);
                gmap.addMarker(markerOptions);
                gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
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
            public void onResponse(Call<RegistrationDetailsModel> call, Response<RegistrationDetailsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        registrationDetailsModel = response.body();
                        registrationId = registrationDetailsModel.getRegistrationID();
                        registrationDetailsModel.setRegistrationState("completed");
                        entityRegistrationCompletion(token, registrationDetailsModel, registrationId);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistrationDetailsModel> call, Throwable t) {
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });
    }


    public void entityRegistrationCompletion(String token, RegistrationDetailsModel myRegistration, String registrationId) {

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
            @Override
            public void onResponse(Call<RegistrationDetailsModel> call, Response<RegistrationDetailsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        registrationDetailsModel = response.body();
                        interestToggle.setChecked(false);
                        util.showCulturalPassAlertDialog(DetailsActivity.this);
                        new InsertRegisteredEventToDataBase(DetailsActivity.this, userRegistrationDetailsTable).execute();
                    }
                }
                registrationLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RegistrationDetailsModel> call, Throwable t) {
                Log.d("Details", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });

    }

    public void deleteEventRegistration(String id, String token) {

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
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    new deleteEventFromDataBase(DetailsActivity.this, nid).execute();
                    interestToggle.setChecked(true);
                }
                registrationLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("DETAILS", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network), DetailsActivity.this);
                registrationLoader.setVisibility(View.GONE);
            }
        });
    }

    public void getHeritageOrExhibitionDetailsFromAPI(String id, int language,
                                                      final String pageName) {
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final String appLanguage;
        if (language == 1) {
            appLanguage = "en";
        } else {
            appLanguage = "ar";
        }

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HeritageOrExhibitionDetailModel>> call = apiService.getHeritageOrExhebitionDetails(appLanguage,
                pageName, id);
        call.enqueue(new Callback<ArrayList<HeritageOrExhibitionDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<HeritageOrExhibitionDetailModel>> call, Response<ArrayList<HeritageOrExhibitionDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        heritageOrExhibitionDetailModel = response.body();
                        removeHtmlTags(heritageOrExhibitionDetailModel);
                        for (int i = 0; i < heritageOrExhibitionDetailModel.get(0).getImage().size(); i++) {
                            imageList.add(i, heritageOrExhibitionDetailModel.get(0).getImage().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator(language);
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
                                    heritageOrExhibitionDetailModel.get(0).getLocation(),
                                    null, heritageOrExhibitionDetailModel.get(0).getLatitude(),
                                    heritageOrExhibitionDetailModel.get(0).getLongitude(),
                                    heritageOrExhibitionDetailModel.get(0).getStartDate(),
                                    heritageOrExhibitionDetailModel.get(0).getEndDate(), null);
                            new ExhibitionDetailRowCount(DetailsActivity.this, appLanguage).execute();
                        }

                    } else {
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<ArrayList<HeritageOrExhibitionDetailModel>> call, Throwable t) {
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showIndicator(int language) {
        circleIndicator.setVisibility(View.VISIBLE);
        ads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ads.add(new Page("", imageList.get(0),
                    null));
        }
        glideLoader = new GlideLoaderForMuseum();
        if (language == 1)
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

    public void removeTourDetailsHtmlTags(ArrayList<TourDetailsModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setTourTitle(util.html2string(models.get(i).getTourTitle()));
            models.get(i).setTourDate(util.html2string(models.get(i).getTourDate()));
            models.get(i).setTourBody(util.html2string(models.get(i).getTourBody()));

        }
    }

    public void removeHtmlTags(ArrayList<HeritageOrExhibitionDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setStartDate(util.html2string(models.get(i).getStartDate()));
            models.get(i).setEndDate(util.html2string(models.get(i).getEndDate()));
            models.get(i).setLongDescription(util.html2string(models.get(i).getLongDescription()));
        }
    }

    public void removeHtmlTagsPublicArts(ArrayList<PublicArtModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setLongDescription(util.html2string(models.get(i).getLongDescription()));
        }
    }

    public void getItemPosition(int value) {
        int pos = value;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gvalue = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (latitude != null && !latitude.equals("")) {
            gmap = gvalue;
            gmap.setMinZoomPreference(12);
            LatLng ny = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            UiSettings uiSettings = gmap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ny);
            gmap.addMarker(markerOptions);
            gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        }
    }


    public class ExhibitionDetailRowCount extends AsyncTask<Void, Void, Integer> {

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
            exhibitionRowCount = integer;
            if (exhibitionRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckExhibitionDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsArabic();
            }
        }
    }

    public class CheckExhibitionDetailDBRowExist extends AsyncTask<Void, Void, Void> {

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
            if (heritageOrExhibitionDetailModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkEnglishIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkArabicIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateArabicTable same id
                            new UpdateExhibitionDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }
            return null;
        }
    }

    public class UpdateExhibitionDetailTable extends AsyncTask<Void, Void, Void> {

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
            Convertor converters = new Convertor();
            if (language.equals("en")) {
                // updateEnglishTable table with english name

                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionDetailEnglish(
                        heritageOrExhibitionDetailModel.get(position).getStartDate(),
                        heritageOrExhibitionDetailModel.get(position).getEndDate(),
                        converters.fromArrayList(heritageOrExhibitionDetailModel.get(position).getImage()),
                        heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                        heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getLatitude(),
                        heritageOrExhibitionDetailModel.get(position).getLongitude(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );


            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionDetailArabic(
                        heritageOrExhibitionDetailModel.get(position).getStartDate(),
                        heritageOrExhibitionDetailModel.get(position).getEndDate(),
                        converters.fromArrayList(heritageOrExhibitionDetailModel.get(position).getImage()),
                        heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                        heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getLatitude(),
                        heritageOrExhibitionDetailModel.get(position).getLongitude(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveEnglishExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String exhibitionId;

        RetriveEnglishExhibitionData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            exhibitionId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableEnglish> exhibitionListTableEnglish) {
            if (exhibitionListTableEnglish.size() > 0) {
                Convertor convertor = new Convertor();
                if (exhibitionListTableEnglish.get(0).getExhibition_latest_image().contains("[")) {
                    ArrayList<String> list = convertor.fromString(exhibitionListTableEnglish.get(0).getExhibition_latest_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                loadDataForHeritageOrExhibitionDetails(null,
                        exhibitionListTableEnglish.get(0).getExhibition_short_description(),
                        exhibitionListTableEnglish.get(0).getExhibition_long_description(),
                        null, null,
                        null,
                        exhibitionListTableEnglish.get(0).getExhibition_location(),
                        null, exhibitionListTableEnglish.get(0).getExhibition_latitude(),
                        exhibitionListTableEnglish.get(0).getExhibition_longitude(),
                        exhibitionListTableEnglish.get(0).getExhibition_start_date(),
                        exhibitionListTableEnglish.get(0).getExhibition_end_date(), null);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ExhibitionListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionDetailsEnglish(Integer.parseInt(exhibitionId));

        }
    }

    public class RetriveArabicExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveArabicExhibitionData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableArabic> exhibitionListTableArabics) {
            if (exhibitionListTableArabics.size() > 0) {
                Convertor convertor = new Convertor();
                if (exhibitionListTableArabics.get(0).getExhibition_latest_image().contains("[")) {
                    ArrayList<String> list = convertor.fromString(exhibitionListTableArabics.get(0).getExhibition_latest_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                loadDataForHeritageOrExhibitionDetails(null,
                        exhibitionListTableArabics.get(0).getExhibition_short_description(),
                        exhibitionListTableArabics.get(0).getExhibition_long_description(),
                        null, null,
                        null,
                        exhibitionListTableArabics.get(0).getExhibition_location(),
                        null, exhibitionListTableArabics.get(0).getExhibition_latitude(),
                        exhibitionListTableArabics.get(0).getExhibition_longitude(),
                        exhibitionListTableArabics.get(0).getExhibition_start_date(),
                        exhibitionListTableArabics.get(0).getExhibition_end_date(), null);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ExhibitionListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionDetailsArabic(Integer.parseInt(heritageId));
        }
    }

    public class HeritageRowCount extends AsyncTask<Void, Void, Integer> {

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
            heritageTableRowCount = integer;
            if (heritageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckHeritageDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsArabic();
            }
        }
    }

    public class CheckHeritageDetailDBRowExist extends AsyncTask<Void, Void, Void> {

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
            if (heritageOrExhibitionDetailModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkEnglishIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritageDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkArabicIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateArabicTable same id
                            new UpdateHeritageDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }
            return null;
        }
    }

    public class UpdateHeritageDetailTable extends AsyncTask<Void, Void, Void> {

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
            Convertor convertor = new Convertor();
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageDetailEnglish(
                        latitude, longitude, heritageOrExhibitionDetailModel.get(position).getLongDescription()
                        , heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        convertor.fromArrayList(heritageOrExhibitionDetailModel.get(position).getImage()),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageDetailArabic(
                        latitude, longitude, heritageOrExhibitionDetailModel.get(position).getLongDescription()
                        , heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        convertor.fromArrayList(heritageOrExhibitionDetailModel.get(position).getImage()),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveEnglishHeritageData extends AsyncTask<Void, Void, List<HeritageListTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveEnglishHeritageData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<HeritageListTableEnglish> heritageListTableEnglish) {
            if (heritageListTableEnglish.size() > 0) {
                Convertor convertor = new Convertor();
                if (heritageListTableEnglish.get(0).getHeritage_image().contains("[")) {
                    ArrayList<String> list = convertor.fromString(heritageListTableEnglish.get(0).getHeritage_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                loadDataForHeritageOrExhibitionDetails(null, heritageListTableEnglish.get(0).getHeritage_short_description(),
                        heritageListTableEnglish.get(0).getHeritage_long_description(),
                        null, null,
                        null,
                        heritageListTableEnglish.get(0).getLocation(),
                        null, heritageListTableEnglish.get(0).getLatitude(),
                        heritageListTableEnglish.get(0).getLongitude(), null, null, null);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<HeritageListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getHeritageDetailsEnglish(Integer.parseInt(heritageId));
        }
    }

    public class RetriveArabicHeritageData extends AsyncTask<Void, Void, List<HeritageListTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveArabicHeritageData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<HeritageListTableArabic> heritageListTableArabic) {

            if (heritageListTableArabic.size() > 0) {
                Convertor convertor = new Convertor();
                if (heritageListTableArabic.get(0).getHeritage_image().contains("[")) {
                    ArrayList<String> list = convertor.fromString(heritageListTableArabic.get(0).getHeritage_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                loadDataForHeritageOrExhibitionDetails(null, heritageListTableArabic.get(0).getHeritage_short_description(),
                        heritageListTableArabic.get(0).getHeritage_long_description(),
                        null, null,
                        null,
                        heritageListTableArabic.get(0).getLocation(),
                        null, heritageListTableArabic.get(0).getLatitude(),
                        heritageListTableArabic.get(0).getLongitude(), null, null, null);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected List<HeritageListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getHeritageDetailsArabic(Integer.parseInt(heritageId));
        }
    }

    private void getPublicArtDetailsFromAPI(String id, int appLanguage) {
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<PublicArtModel>> call = apiService.getPublicArtsDetails(language, id);
        call.enqueue(new Callback<ArrayList<PublicArtModel>>() {

            @Override
            public void onResponse(Call<ArrayList<PublicArtModel>> call, Response<ArrayList<PublicArtModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        publicArtModel = response.body();
                        removeHtmlTagsPublicArts(publicArtModel);
                        for (int i = 0; i < publicArtModel.get(0).getImage().size(); i++) {
                            imageList.add(i, publicArtModel.get(0).getImage().get(i));
                        }
                        if (imageList.size() > 0) {
                            zoomView.setOnClickListener(view -> showCarouselView());
                            showIndicator(appLanguage);
                        }

                        fromMuseumAbout = false;
                        loadData(null,
                                publicArtModel.get(0).getShortDescription(),
                                publicArtModel.get(0).getLongDescription(),
                                null, null, null, null,
                                null, null, publicArtModel.get(0).getLatitude(),
                                publicArtModel.get(0).getLatitude(), fromMuseumAbout, null);
                        new PublicArtsRowCount(DetailsActivity.this, language).execute();
                    } else {
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<PublicArtModel>> call, Throwable t) {
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

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
            publicArtsTableRowCount = integer;
            if (publicArtsTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckPublicArtsDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsArabic();
            }


        }
    }

    public class CheckPublicArtsDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        private PublicArtsTableEnglish publicArtsTableEnglish;
        private PublicArtsTableArabic publicArtsTableArabic;
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
            if (publicArtModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < publicArtModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkEnglishIdExist(
                                Integer.parseInt(publicArtModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < publicArtModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkArabicIdExist(
                                Integer.parseInt(publicArtModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }

            return null;
        }
    }

    public class UpdatePublicArtsDetailTable extends AsyncTask<Void, Void, Void> {

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
            Convertor converters = new Convertor();
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsDetailEnglish(
                        publicArtModel.get(position).getName(),
                        latitude, longitude, publicArtModel.get(position).getShortDescription()
                        , publicArtModel.get(position).getLongDescription(),
                        converters.fromArrayList(publicArtModel.get(position).getImage()),
                        publicArtModel.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsDetailArabic(
                        publicArtModel.get(position).getName(),
                        latitude, longitude, publicArtModel.get(position).getShortDescription(),
                        publicArtModel.get(position).getLongDescription(),
                        converters.fromArrayList(publicArtModel.get(position).getImage()),
                        publicArtModel.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveEnglishPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String publicArtsId;

        RetriveEnglishPublicArtsData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            publicArtsId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableEnglish> publicArtsTableEnglish) {
            if (publicArtsTableEnglish.size() > 0) {
                Convertor converters = new Convertor();
                if (publicArtsTableEnglish.get(0).getPublic_arts_image().contains("[")) {
                    ArrayList<String> list = converters.fromString(publicArtsTableEnglish.get(0).getPublic_arts_image());
                    for (int l = 0; l < list.size(); l++) {
                        imageList.add(l, list.get(l));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                for (int i = 0; i < publicArtsTableEnglish.size(); i++) {
                    fromMuseumAbout = false;
                    loadData(null,
                            publicArtsTableEnglish.get(i).getShort_description(),
                            publicArtsTableEnglish.get(i).getDescription(),
                            null, null, null,
                            null, null, null,
                            publicArtsTableEnglish.get(i).getLatitude(),
                            publicArtsTableEnglish.get(i).getLongitude(), fromMuseumAbout, null);
                }
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllDataEnglish(publicArtsId);

        }
    }

    public class RetriveArabicPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String publicArtsId;

        RetriveArabicPublicArtsData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            publicArtsId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableArabic> publicArtsTableArabic) {
            if (publicArtsTableArabic.size() > 0) {
                Convertor converters = new Convertor();
                if (publicArtsTableArabic.get(0).getPublic_arts_image().contains("[")) {
                    ArrayList<String> list = converters.fromString(publicArtsTableArabic.get(0).getPublic_arts_image());
                    for (int l = 0; l < list.size(); l++) {
                        imageList.add(l, list.get(l));
                    }
                }
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                for (int i = 0; i < publicArtsTableArabic.size(); i++) {
                    fromMuseumAbout = false;
                    loadData(null,
                            publicArtsTableArabic.get(i).getShort_description(),
                            publicArtsTableArabic.get(i).getDescription(),
                            null, null, null,
                            null, null, null,
                            publicArtsTableArabic.get(i).getLatitude(),
                            publicArtsTableArabic.get(i).getLongitude(), fromMuseumAbout, null);
                }
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllDataArabic(publicArtsId);
        }
    }

    public void getMuseumAboutDetailsFromAPI(String id, int appLanguage, boolean isLaunchEvent) {
        commonContentLayout.setVisibility(View.INVISIBLE);
        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Call<ArrayList<MuseumAboutModel>> call;
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        if (isLaunchEvent)
            call = apiService.getLaunchMuseumAboutDetails(language, id);
        else
            call = apiService.getMuseumAboutDetails(language, id);
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MuseumAboutModel>> call, Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        museumAboutModels = response.body();
                        if (isLaunchEvent) {
                            eventDateLayout.setVisibility(View.VISIBLE);
                            eventDateTxt.setText(museumAboutModels.get(0).getEventDate());
                            videoLayout.setVisibility(View.GONE);
                            downloadLayout.setVisibility(View.VISIBLE);
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
                            showIndicator(appLanguage);
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
                            long_description = long_description + museumAboutModels.get(0).getDescriptionList().get(i);
                            long_description = long_description + "\n\n";

                        }

                        fromMuseumAbout = true;
                        String file;
                        if (museumAboutModels.get(0).getVideoInfo() == null || museumAboutModels.get(0).getVideoInfo().size() == 0) {
                            file = null;
                        } else {
                            file = museumAboutModels.get(0).getVideoInfo().get(0);
                        }
                        loadData(null, first_description,
                                null,
                                museumAboutModels.get(0).getSubTitle(), long_description,
                                museumAboutModels.get(0).getTimingInfo(), "",
                                null,
                                museumAboutModels.get(0).getContactEmail(),
                                museumAboutModels.get(0).getLatitude(),
                                museumAboutModels.get(0).getLongitude(),
                                fromMuseumAbout, file);
                        new MuseumAboutRowCount(DetailsActivity.this, language).execute();

                    } else {
                        commonContentLayout.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<MuseumAboutModel>> call, Throwable t) {
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumAboutDetailsFromDatabase(String id, int language, boolean isLaunchEvent) {
        if (language == 1) {
            progressBar.setVisibility(View.VISIBLE);
            new RetriveMuseumAboutDataEnglish(DetailsActivity.this, language, id, isLaunchEvent).execute();
        } else {
            new RetriveMuseumAboutDataArabic(DetailsActivity.this, language, id, isLaunchEvent).execute();
        }
    }

    public class MuseumAboutRowCount extends AsyncTask<Void, Void, Integer> {

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
            museumAboutRowCount = integer;
            if (museumAboutRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumAboutDBRowExist(DetailsActivity.this, language).execute();
            } else {
                //create databse
                new InsertMuseumAboutDatabaseTask(DetailsActivity.this, museumAboutTableEnglish,
                        museumAboutTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsArabic();
            }


        }
    }

    public class CheckMuseumAboutDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
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
            if (museumAboutModels.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkEnglishIdExist(
                                Integer.parseInt(museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumAboutDetailTable(DetailsActivity.this, language, i).execute();

                        } else {
                            museumAboutTableEnglish = new MuseumAboutTableEnglish(museumAboutModels.get(i).getName(), Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                    museumAboutModels.get(i).getTourGuideAvailable(),
                                    first_description, long_description,
                                    museumAboutModels.get(i).getImageList().get(0),
                                    museumAboutModels.get(i).getContactNumber(), museumAboutModels.get(i).getContactEmail(),
                                    museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude(),
                                    museumAboutModels.get(i).getTourGuideAvailability(),
                                    museumAboutModels.get(i).getSubTitle(),
                                    museumAboutModels.get(i).getTimingInfo(),
                                    museumAboutModels.get(i).getEventDate());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkArabicIdExist(
                                Integer.parseInt(museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumAboutDetailTable(DetailsActivity.this, language, i).execute();

                        } else {
                            museumAboutTableArabic = new MuseumAboutTableArabic(museumAboutModels.get(i).getName(), Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                    museumAboutModels.get(i).getTourGuideAvailable(),
                                    first_description, long_description,
                                    museumAboutModels.get(i).getImageList().get(0),
                                    museumAboutModels.get(i).getContactNumber(), museumAboutModels.get(i).getContactEmail(),
                                    museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude(),
                                    museumAboutModels.get(i).getTourGuideAvailability(),
                                    museumAboutModels.get(i).getSubTitle(),
                                    museumAboutModels.get(i).getTimingInfo(),
                                    museumAboutModels.get(i).getEventDate());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                        }
                    }
                }
            }

            return null;
        }
    }

    public class InsertMuseumAboutDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
        String language;

        InsertMuseumAboutDatabaseTask(DetailsActivity context, MuseumAboutTableEnglish museumAboutTableEnglish,
                                      MuseumAboutTableArabic museumAboutTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.museumAboutTableEnglish = museumAboutTableEnglish;
            this.museumAboutTableArabic = museumAboutTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (museumAboutModels != null) {
                Convertor converters = new Convertor();
                if (language.equals("en")) {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        museumAboutTableEnglish = new MuseumAboutTableEnglish(museumAboutModels.get(i).getName(), Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                museumAboutModels.get(i).getTourGuideAvailable(),
                                first_description, long_description,
                                converters.fromArrayList(museumAboutModels.get(i).getImageList()),
                                museumAboutModels.get(i).getContactNumber(), museumAboutModels.get(i).getContactEmail(),
                                museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude(),
                                museumAboutModels.get(i).getTourGuideAvailability(),
                                museumAboutModels.get(i).getSubTitle(),
                                museumAboutModels.get(i).getTimingInfo(),
                                museumAboutModels.get(i).getEventDate());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                    }
                } else {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        museumAboutTableArabic = new MuseumAboutTableArabic(museumAboutModels.get(i).getName(), Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                museumAboutModels.get(i).getTourGuideAvailable(),
                                first_description, long_description,
                                converters.fromArrayList(museumAboutModels.get(i).getImageList()),
                                museumAboutModels.get(i).getContactNumber(), museumAboutModels.get(i).getContactEmail(),
                                museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude(),
                                museumAboutModels.get(i).getTourGuideAvailability(),
                                museumAboutModels.get(i).getSubTitle(),
                                museumAboutModels.get(i).getTimingInfo(),
                                museumAboutModels.get(i).getEventDate());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public class UpdateMuseumAboutDetailTable extends AsyncTask<Void, Void, Void> {

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
            Convertor converters = new Convertor();
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataEnglish(
                        museumAboutModels.get(position).getName(),
                        museumAboutModels.get(position).getTourGuideAvailable(),
                        first_description, long_description,
                        converters.fromArrayList(museumAboutModels.get(position).getImageList()),
                        museumAboutModels.get(position).getContactNumber(),
                        museumAboutModels.get(position).getContactEmail(),
                        museumAboutModels.get(position).getLatitude(),
                        museumAboutModels.get(position).getLongitude(),
                        museumAboutModels.get(position).getTourGuideAvailability(),
                        museumAboutModels.get(position).getSubTitle(),
                        museumAboutModels.get(position).getTimingInfo(),
                        Long.parseLong(museumAboutModels.get(position).getMuseumId()));
            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataArabic(museumAboutModels.get(position).getName(),
                        museumAboutModels.get(position).getTourGuideAvailable(),
                        first_description, long_description,
                        converters.fromArrayList(museumAboutModels.get(position).getImageList()),
                        museumAboutModels.get(position).getContactNumber(), museumAboutModels.get(position).getContactEmail(),
                        museumAboutModels.get(position).getLatitude(), museumAboutModels.get(position).getLongitude(),
                        museumAboutModels.get(position).getTourGuideAvailability(),
                        museumAboutModels.get(position).getSubTitle(),
                        museumAboutModels.get(position).getTimingInfo(),
                        Long.parseLong(museumAboutModels.get(position).getMuseumId()));

            }
            return null;
        }
    }

    public class RetriveMuseumAboutDataEnglish extends AsyncTask<Void, Void, MuseumAboutTableEnglish> {
        private WeakReference<DetailsActivity> activityReference;
        int language;
        String museumId;
        boolean isLaunchEvent;

        RetriveMuseumAboutDataEnglish(DetailsActivity context, int appLanguage, String museumId, boolean isLaunchEvent) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.museumId = museumId;
            this.isLaunchEvent = isLaunchEvent;
        }

        @Override
        protected MuseumAboutTableEnglish doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getMuseumAboutDataEnglish(Integer.parseInt(museumId));
        }

        @Override
        protected void onPostExecute(MuseumAboutTableEnglish museumAboutTableEnglish) {
            if (museumAboutTableEnglish != null) {
                Convertor converters = new Convertor();
                commonContentLayout.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
                if (museumAboutTableEnglish.getMuseum_image().contains("[")) {
                    ArrayList<String> list = converters.fromString(museumAboutTableEnglish.getMuseum_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }

                GlideApp.with(DetailsActivity.this)
                        .load(imageList.get(0))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(headerImageView);

                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                String description1 = museumAboutTableEnglish.getShort_description();
                String description2 = museumAboutTableEnglish.getLong_description();

                if (isLaunchEvent) {
                    eventDateLayout.setVisibility(View.VISIBLE);
                    eventDateTxt.setText(museumAboutTableEnglish.getEvent_date());
                    videoLayout.setVisibility(View.GONE);
                    downloadLayout.setVisibility(View.VISIBLE);
                } else {
                    timingTitle.setText(R.string.museum_timings);
                }
                loadData(null, description1,
                        null,
                        museumAboutTableEnglish.getMuseum_subtitle(), description2,
                        museumAboutTableEnglish.getMuseum_opening_time(), "",
                        null, museumAboutTableEnglish.getMuseum_contact_email(),
                        museumAboutTableEnglish.getMuseum_lattitude(),
                        museumAboutTableEnglish.getMuseum_longitude(),
                        true, null);

            } else {
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetriveMuseumAboutDataArabic extends AsyncTask<Void, Void, MuseumAboutTableArabic> {
        private WeakReference<DetailsActivity> activityReference;
        int language;
        String museumId;
        boolean isLaunchEvent;

        RetriveMuseumAboutDataArabic(DetailsActivity context, int appLanguage, String museumId, boolean isLaunchEvent) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.museumId = museumId;
            this.isLaunchEvent = isLaunchEvent;
        }

        @Override
        protected MuseumAboutTableArabic doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getMuseumAboutDataArabic(Integer.parseInt(museumId));
        }

        @Override
        protected void onPostExecute(MuseumAboutTableArabic museumAboutTableArabic) {
            if (museumAboutTableArabic != null) {
                Convertor converters = new Convertor();
                commonContentLayout.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
                if (museumAboutTableArabic.getMuseum_image().contains("[")) {
                    ArrayList<String> list = converters.fromString(museumAboutTableArabic.getMuseum_image());
                    for (int i = 0; i < list.size(); i++) {
                        imageList.add(i, list.get(i));
                    }
                }
                GlideApp.with(DetailsActivity.this)
                        .load(imageList.get(0))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(headerImageView);
                String description1 = museumAboutTableArabic.getShort_description();
                String description2 = museumAboutTableArabic.getLong_description();
                if (imageList.size() > 0) {
                    zoomView.setOnClickListener(view -> showCarouselView());
                    showIndicator(appLanguage);
                }

                if (isLaunchEvent) {
                    eventDateLayout.setVisibility(View.VISIBLE);
                    eventDateTxt.setText(museumAboutTableArabic.getEvent_date());
                    videoLayout.setVisibility(View.GONE);
                    downloadLayout.setVisibility(View.VISIBLE);
                } else {
                    timingTitle.setText(R.string.museum_timings);
                }
                loadData(null, description1,
                        null,
                        museumAboutTableArabic.getMuseum_subtitle(), description2,
                        museumAboutTableArabic.getMuseum_opening_time(), "",
                        null, museumAboutTableArabic.getMuseum_contact_email(),
                        museumAboutTableArabic.getMuseum_lattitude(),
                        museumAboutTableArabic.getMuseum_longitude(),
                        true, null);
            } else {
                commonContentLayout.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

    }

    public void imageValue(int value) {
        int pos = value;
        Log.i("TAG", "This activity was clicked: " + pos);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
