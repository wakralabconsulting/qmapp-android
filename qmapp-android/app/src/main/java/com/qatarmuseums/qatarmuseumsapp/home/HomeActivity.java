package com.qatarmuseums.qatarmuseumsapp.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.CommonListActivity;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.commonlistsecondary.SecondaryListActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.LoginData;
import com.qatarmuseums.qatarmuseumsapp.culturepass.ReceivedCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserRegistrationDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserResponseDeserializer;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebViewActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class HomeActivity extends BaseActivity {
    View diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation, fadeOutAnimation;
    private RecyclerView recyclerView;
    private HomeListAdapter mAdapter;
    private ArrayList<HomeList> homeLists = new ArrayList<>();
    private ArrayList<HomeList> bannerLists = new ArrayList<>();
    private ArrayList<UserRegistrationModel> registeredEventLists = new ArrayList<>();
    private Intent navigation_intent;
    Util util;
    RelativeLayout noResultFoundLayout;
    private boolean doubleBackToExitPressedOnce;
    ProgressBar progressBar;
    SharedPreferences qmPreferences;
    QMDatabase qmDatabase;
    HomePageBannerTableEnglish homePageBannerTableEnglish;
    HomePageBannerTableArabic homePageBannerTableArabic;
    private Intent navigationIntent;
    private LinearLayout retryLayout;
    private View retryButton;
    private String name;
    private SharedPreferences.Editor editor;
    private int badgeCount;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String notificationMessage;
    private String language;
    private AppBarLayout bannerLayout;
    private ImageView headerImageView;
    private TextView bannerText;
    private String RSVP;
    private Retrofit retrofit;
    private APIInterface apiService;
    private ProfileDetails profileDetails;
    private TextInputLayout mPasswordViewLayout, mUsernameViewLayout;
    private Dialog loginDialog;
    private EditText mUsernameView, mPasswordView;
    private ProgressBar mProgressView;
    private FrameLayout mLoginFormView;
    private View dialogLoginButton;
    private String qatar;
    private LoginData loginData;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle bundleParams, contentBundleParams;

    @SuppressLint("ClickableViewAccessibility")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = findViewById(R.id.progressBarLoading);
        progressBar.setVisibility(View.VISIBLE);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        diningNavigation = findViewById(R.id.dining_layout);
        noResultFoundLayout = findViewById(R.id.no_result_layout);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        giftShopNavigation = findViewById(R.id.gift_shop_layout);
        culturePassNavigation = findViewById(R.id.culture_pass_layout);
        moreNavigation = findViewById(R.id.more_layout);
        recyclerView = findViewById(R.id.recycler_view);
        headerImageView = findViewById(R.id.header_img);
        bannerText = findViewById(R.id.banner_text);
        headerImageView.setOnClickListener(v -> navigateToLaunchPage());
        bannerLayout = findViewById(R.id.banner);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        util = new Util();
        profileDetails = new ProfileDetails();
        qmDatabase = QMDatabase.getInstance(HomeActivity.this);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        name = qmPreferences.getString("NAME", null);
        badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
        if (badgeCount > 0)
            setBadge(badgeCount);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimations();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentBundleParams = new Bundle();

        diningNavigation.setOnClickListener(v -> {
            Timber.i("Bottom bar Dining clicked");
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BOTTOM BAR");
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID,
                    ((TextView) ((RelativeLayout) v).getChildAt(1)).getText().toString());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            navigation_intent = new Intent(HomeActivity.this, CommonListActivity.class);
            navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_dining_text));
            startActivity(navigation_intent);
        });
        giftShopNavigation.setOnClickListener(v -> {
            Timber.i("Bottom bar Gift shop clicked");
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BOTTOM BAR");
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID,
                    ((TextView) ((RelativeLayout) v).getChildAt(1)).getText().toString());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            navigation_intent = new Intent(HomeActivity.this, WebViewActivity.class);
            navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
            startActivity(navigation_intent);
        });
        culturePassNavigation.setOnClickListener(v -> {
            Timber.i("Bottom bar Culture pass clicked");
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BOTTOM BAR");
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID,
                    ((TextView) ((RelativeLayout) v).getChildAt(1)).getText().toString());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);
            name = qmPreferences.getString("NAME", null);
            if (name == null)
                navigation_intent = new Intent(HomeActivity.this, CulturePassActivity.class);
            else
                navigation_intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(navigation_intent);
        });
        moreNavigation.setOnClickListener(v -> {
            handlingDrawer();
            Timber.i("Bottom bar More clicked");
        });

        mAdapter = new HomeListAdapter(this, homeLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                HomeList homeList = homeLists.get(position);
                if (homeList.getId().equals("12181") || homeList.getId().equals("12186")) {
                    Timber.i("Home list Exhibition clicked");
                    navigationIntent = new Intent(HomeActivity.this, CommonListActivity.class);
                    navigationIntent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_exhibition_text));
                    startActivity(navigationIntent);
                } else if (homeList.getName().trim().toUpperCase().contains("QATAR CREATES: EVENTS FOR THE OPENING OF NMOQ") ||
                        homeList.getName().trim().toUpperCase().contains("قطر تبدع: فعاليات افتتاح متحف قطر الوطني")) {
                    Timber.i("Home list %s clicked", homeList.getName().trim().toUpperCase());
                    navigationIntent = new Intent(HomeActivity.this,
                            SecondaryListActivity.class);
                    navigationIntent.putExtra("MAIN_TITLE", homeList.getName());
                    navigationIntent.putExtra("ID", homeList.getId());
                    navigationIntent.putExtra("COMING_FROM", getString(R.string.museum_discussion));
                    startActivity(navigationIntent);
                } else {
                    Timber.i("Home list %s clicked with ID: %s", homeList.getName(), homeList.getId());
                    navigationIntent = new Intent(HomeActivity.this, MuseumActivity.class);
                    navigationIntent.putExtra("MUSEUMTITLE", homeList.getName());
                    navigationIntent.putExtra("MUSEUM_ID", homeList.getId());
                    startActivity(navigationIntent);
                }
                contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "HOME LIST");
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, homeList.getId());
                contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_NAME, homeList.getName());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        /*
        // Temporarley Commenting Login popup for the first Launch
        boolean isFirstLaunch = qmPreferences.getBoolean("FIRST_LAUNCH", true);
        if (isFirstLaunch) {
            Timber.i("First launch login popup");
            showLoginDialog();
            editor = qmPreferences.edit();
            editor.putBoolean("FIRST_LAUNCH", false);
            editor.apply();
        }
        */

        language = LocaleManager.getLanguage(this);
        if (util.isNetworkAvailable(this)) {
            getHomePageAPIData(language);
        } else {
            getDataFromDataBase(language);
        }

        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getHomePageAPIData(language);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            /*
            // Hiding Banner temporary
           RSVP = qmPreferences.getString("RSVP", null);
            if (RSVP != null) {
                if (bannerLayout.getVisibility() != View.VISIBLE)
                    showBanner();
            }
            */
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        diningNavigation.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    diningNavigation.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        giftShopNavigation.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    giftShopNavigation.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        culturePassNavigation.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    culturePassNavigation.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        moreNavigation.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    moreNavigation.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    notificationMessage = intent.getStringExtra("ALERT");
                    if (notificationMessage != null && !notificationMessage.equals(""))
                        updateNotificationDB(notificationMessage);
                    badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
                    badgeCount = badgeCount + 1;
                    editor = qmPreferences.edit();
                    editor.putInt("BADGE_COUNT", badgeCount);
                    editor.apply();
                    if (badgeCount > 0) {
                        setBadge(badgeCount);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        checkForNotification(getIntent());
    }

    public void navigateToLaunchPage() {
        if (bannerLists.size() > 0) {
            navigationIntent = new Intent(HomeActivity.this, MuseumActivity.class);
            navigationIntent.putExtra("MUSEUMTITLE", bannerText.getText());
            navigationIntent.putExtra("MUSEUM_ID", bannerLists.get(0).getId());
            navigationIntent.putExtra("IS_BANNER", true);
            startActivity(navigationIntent);
        }
    }

    public void checkForNotification(Intent intent) {
        String message = intent.getStringExtra("MESSAGE");
        if (message != null && !message.equals("")) {
            updateNotificationDB(message);
            navigation_intent = new Intent(this, NotificationActivity.class);
            startActivity(navigation_intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkForNotification(intent);
    }

    public void updateNotificationDB(String message) {
        insertNotificationRelatedDataToDataBase(message, language);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QMDatabase.cleanUp();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.END)) {
            assert navigationView != null;
            navigationView.startAnimation(fadeOutAnimation);
            assert topBarSideMenu != null;
            topBarSideMenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
            showToolBarOptions();
            drawer.closeDrawer(GravityCompat.END, false);
            assert toolbar != null;
            toolbar.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.touch_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    public void getHomePageAPIData(String language) {
        Timber.i("getHomePageAPIData()");
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getMuseumsList(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HomeList>> call, @NonNull Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("getHomePageAPIData() - isSuccessful");
                        recyclerView.setVisibility(View.VISIBLE);
                        homeLists.addAll(response.body());

                        // Commenting Panel & Talks temporary for this release
//                        AddPanelDiscussion();

                        Collections.sort(homeLists);
                        Timber.i("Setting home cell data from API with size: %d", response.body().size());
                        mAdapter.notifyDataSetChanged();
                        new RowCount(HomeActivity.this, language, homeLists).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HomeList>> call, @NonNull Throwable t) {
                Timber.e("getHomePageAPIData() - onFailure: %s", t.getMessage());
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void AddPanelDiscussion() {
        Boolean isPanelDiscussionAvailable = false;
        for (int i = 0; i < homeLists.size(); i++) {
            if (homeLists.get(i).getName().trim().toUpperCase().contains("QATAR CREATES: EVENTS FOR THE OPENING OF NMoQ") ||
                    homeLists.get(i).getName().trim().toUpperCase().contains("قطر تبدع: فعاليات افتتاح متحف قطر الوطني")) {
                isPanelDiscussionAvailable = true;
            }
        }
        if (!isPanelDiscussionAvailable) {
            HomeList homeList;
            Timber.i("AddPanelDiscussion() Hardcoded value");
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                homeList = new HomeList(
                        "QATAR CREATES: EVENTS FOR THE OPENING OF NMoQ",
                        "13976",
                        "https://www.qm.org.qa/sites/default/files/qlibrary.jpeg",
                        "false",
                        "10");
            } else {
                homeList = new HomeList(
                        "قطر تبدع: فعاليات افتتاح متحف قطر الوطني",
                        "15631",
                        "https://www.qm.org.qa/sites/default/files/qlibrary.jpeg",
                        "false",
                        "10");
            }
            homeLists.add(homeList);
        }
    }

    public void getBannerAPIData() {
        Timber.i("getBannerAPIData()");
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getBannerDetails(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HomeList>> call, @NonNull Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        bannerLists.addAll(response.body());
                        if (bannerLists.size() > 0) {
                            Timber.i("getBannerAPIData() - isSuccessful");
                            bannerLayout.setVisibility(View.VISIBLE);
                            addBannerData(bannerLists);
                            new RowCountBanner(HomeActivity.this, language, bannerLists).execute();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HomeList>> call, @NonNull Throwable t) {
                Timber.e("getBannerAPIData() - onFailure: %s", t.getMessage());
            }
        });
    }

    UserRegistrationDetailsTable userRegistrationDetailsTable;

    public void getUserRegistrationDetails(String userID) {
        Timber.i("getUserRegistrationDetails()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ArrayList<UserRegistrationModel>> call = apiService.getUserRegistrationDetails(LocaleManager.LANGUAGE_ENGLISH, userID);
        call.enqueue(new Callback<ArrayList<UserRegistrationModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<UserRegistrationModel>> call,
                                   @NonNull Response<ArrayList<UserRegistrationModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getUserRegistrationDetails() - isSuccessful");
                        registeredEventLists.addAll(response.body());
                        new InsertRegistrationDatabaseTask(HomeActivity.this,
                                userRegistrationDetailsTable, registeredEventLists).execute();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<UserRegistrationModel>> call, @NonNull Throwable t) {
                Timber.e("getUserRegistrationDetails() - onFailure: %s", t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.home_page), null);
        language = LocaleManager.getLanguage(this);
        if (retryLayout.getVisibility() == View.VISIBLE) {
            getDataFromDataBase(language);
        }
        updateBadge();
        /*
        // Hiding Banner temporary
        RSVP = qmPreferences.getString("RSVP", null);
        if (RSVP != null) {
            if (bannerLayout.getVisibility() != View.VISIBLE)
                showBanner();
        } else {
            bannerLayout.setVisibility(View.GONE);
        }
        */
    }

    public void showBanner() {
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            if (util.isNetworkAvailable(this))
                getBannerAPIData();
            else
                getBannerDataFromDataBase();
        }
    }

    public void addBannerData(List<HomeList> bannerLists) {
        Timber.i("addBannerData()");
        if (bannerLists.size() > 0) {
            if (!HomeActivity.this.isFinishing())
                GlideApp.with(HomeActivity.this)
                        .load(bannerLists.get(0).getImage())
                        .placeholder(R.drawable.placeholder_header)
                        .into(headerImageView);
            bannerText.setText(bannerLists.get(0).getName());
        }
    }

    public void fetchToken() {
        Timber.i("fetchToken()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        builder.addInterceptor(new ReceivedCookiesInterceptor(this));
        client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIInterface.class);
        Call<ProfileDetails> call = apiService.generateToken(language, loginData);
        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(@NonNull Call<ProfileDetails> call, @NonNull final Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("fetchToken() - isSuccessful");
                        performLogin(response.body().getToken());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileDetails> call, @NonNull Throwable t) {
                Timber.e("fetchToken() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network),
                        HomeActivity.this);
                showProgress(false);
            }

        });
    }

    public void performLogin(String token) {
        Timber.i("performLogin()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        builder.addInterceptor(new ReceivedCookiesInterceptor(this));
        client = builder.build();
        Gson userDeserializer = new GsonBuilder().setLenient().registerTypeAdapter(ProfileDetails.class, new UserResponseDeserializer()).create();
        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(userDeserializer))
                .client(client)
                .build();

        apiService = retrofit.create(APIInterface.class);

        Call<ProfileDetails> callLogin = apiService.login(language, token, loginData);
        callLogin.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(@NonNull Call<ProfileDetails> call, @NonNull Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        profileDetails = response.body();

                        // Commenting VIP user check for temporary
//                        checkRSVP(profileDetails.getUser().getuId(), profileDetails.getToken());

                        Timber.i("performLogin() - success with id: %s", profileDetails.getUser().getuId());

                        editor = qmPreferences.edit();
                        editor.putString("TOKEN", profileDetails.getToken());
                        editor.putString("UID", profileDetails.getUser().getuId());
                        editor.putString("MEMBERSHIP_NUMBER", "00" + (Integer.parseInt(profileDetails.getUser().getuId()) + 6000));
                        editor.putString("EMAIL", profileDetails.getUser().getMail());
                        if (!(response.body().getUser().getDateOfBirth() instanceof ArrayList))
                            editor.putString("DOB", ((LinkedTreeMap) ((ArrayList) ((LinkedTreeMap)
                                    response.body().getUser().getDateOfBirth()).get("und")).get(0)).get("value").toString());
                        editor.putString("RESIDENCE", profileDetails.getUser().getCountry().getUnd().get(0).getValue());
                        editor.putString("NATIONALITY", profileDetails.getUser().getNationality().getUnd().get(0).getValue());
                        editor.putString("IMAGE", profileDetails.getUser().getPicture());
                        editor.putString("NAME", profileDetails.getUser().getFirstName().getUnd().get(0).getValue() +
                                " " + profileDetails.getUser().getLastName().getUnd().get(0).getValue());
                        editor.putString("FIRST_NAME", profileDetails.getUser().getFirstName().getUnd().get(0).getValue());
                        editor.putString("LAST_NAME", profileDetails.getUser().getLastName().getUnd().get(0).getValue());
                        editor.putInt("MEMBERSHIP", (Integer.parseInt(profileDetails.getUser().getuId()) + 6000));
                        editor.putString("TIMEZONE", profileDetails.getUser().getTimeZone());
                        editor.apply();
                    }
                } else {
                    Timber.w("performLogin() - not success with code: %d", response.code());
                    if (response.code() == 401)
                        mPasswordViewLayout.setError(getString(R.string.error_incorrect_credentials));
                    else if (response.code() == 406)
                        mPasswordViewLayout.setError(getString(R.string.error_already_logged_in));
                    else
                        mPasswordViewLayout.setError(getString(R.string.error_unexpected));
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileDetails> call, @NonNull Throwable t) {
                Timber.e("performLogin() - onFailure: %s", t.getMessage());
                if (t.getMessage().contains("timeout"))
                    util.showToast(t.getMessage(), HomeActivity.this);
                else
                    util.showToast(getResources().getString(R.string.check_network),
                            HomeActivity.this);
                showProgress(false);

            }
        });

    }

    public void checkRSVP(String uid, String token) {
        Timber.i("checkRSVP()");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<UserData> call = apiService.getRSVP(language, uid, token);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getRsvpAttendance() != null) {
                            Timber.i("RSVP check - isSuccessful for id: %s", uid);
                            RSVP = ((LinkedTreeMap) ((ArrayList) ((LinkedTreeMap)
                                    response.body().getRsvpAttendance()).get("und")).get(0)).get("value").toString();
                            editor = qmPreferences.edit();
                            editor.putString("RSVP", RSVP);
                            editor.apply();
                            showBanner();
                            getUserRegistrationDetails(uid);
                            bundleParams = new Bundle();
                            bundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "VIP USER");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleParams);
                        } else {
                            bundleParams = new Bundle();
                            bundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NORMAL USER");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleParams);
                        }
                    }
                    showProgress(false);
                    loginDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                Timber.e("checkRSVP() - onFailure: %s", t.getMessage());
                new Util().showToast(getResources().getString(R.string.check_network), HomeActivity.this);
                showProgress(false);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void showLoginDialog() {
        Timber.i("showLoginDialog()");
        loginDialog = new Dialog(this, R.style.DialogNoAnimation);
        loginDialog.setCancelable(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.login_popup, null);
        loginDialog.setContentView(view);
        ImageView closeBtn = view.findViewById(R.id.close_dialog);
        dialogLoginButton = view.findViewById(R.id.dialog_login_button);
        dialogLoginButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dialogLoginButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        dialogLoginButton.setOnClickListener(view1 -> {
            attemptLogin();
            hideSoftKeyboard(view1);
        });
        closeBtn.setOnClickListener(view12 -> loginDialog.dismiss());
        mUsernameView = view.findViewById(R.id.username);
        mPasswordView = view.findViewById(R.id.password);
        mPasswordViewLayout = view.findViewById(R.id.password_text_input_layout);
        mUsernameViewLayout = view.findViewById(R.id.username_text_input_layout);
        TextView forgotPassword = view.findViewById(R.id.forgot_password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                hideSoftKeyboard(textView);
                return true;
            }
            return false;
        });
        forgotPassword.setOnClickListener(v -> attemptForgotPassword());
        mUsernameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    mUsernameViewLayout.setError(null);
                } else {

                    mUsernameViewLayout.setError(getString(R.string.error_username_required));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    mPasswordViewLayout.setError(null);
                } else {
                    mPasswordViewLayout.setError(getString(R.string.error_password_required));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        loginDialog.show();
    }

    public void hideSoftKeyboard(View view) {
        Timber.i("hideSoftKeyboard()");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void attemptLogin() {
        Timber.i("attemptLogin()");
        // Reset errors.
        mUsernameViewLayout.setError(null);
        mPasswordViewLayout.setError(null);

        // Store values at the time of the login attempt.
        qatar = mUsernameView.getText().toString();
        String museum = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(qatar)) {
            mUsernameViewLayout.setError(getString(R.string.error_username_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(museum)) {
            mPasswordViewLayout.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String token = qmPreferences.getString("TOKEN", null);
            loginData = new LoginData(qatar, museum);
            if (token != null) {
                performLogin(token);
            } else
                fetchToken();
        }
    }

    private void attemptForgotPassword() {
        Timber.i("attemptForgotPassword()");
        mUsernameViewLayout.setError(null);
        mPasswordViewLayout.setError(null);
        qatar = mUsernameView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(qatar)) {
            mUsernameViewLayout.setError(getString(R.string.error_username_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            loginData = new LoginData(qatar, "");
            fetchTokenForPassword();
        }
    }

    public void fetchTokenForPassword() {
        Timber.i("fetchTokenForPassword()");
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ProfileDetails> call = apiService.generateToken(language, loginData);
        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(@NonNull Call<ProfileDetails> call, @NonNull final Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("fetchTokenForPassword() - isSuccessful");
                        forgotPasswordAction(response.body().getToken());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileDetails> call, @NonNull Throwable t) {
                Timber.e("fetchTokenForPassword() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network),
                        HomeActivity.this);
                showProgress(false);
            }

        });
    }

    public void forgotPasswordAction(String token) {
        Timber.i("forgotPasswordAction()");
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<String>> callLogin = apiService.forgotPassword(language, token, loginData);
        callLogin.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Timber.i("forgotPasswordAction() - isSuccessful");
                        loginDialog.dismiss();
                        util.showNormalDialog(HomeActivity.this, R.string.password_reset_successful);
                    }
                } else {
                    Timber.w("forgotPasswordAction() - not success with code: %d", response.code());
                    if (response.code() == 406)
                        mUsernameViewLayout.setError(getString(R.string.error_incorrect_username));
                    else
                        mUsernameViewLayout.setError(getString(R.string.error_unexpected));

                }
                showProgress(false);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                Timber.e("forgotPasswordAction() - onFailure: %s", t.getMessage());
                util.showToast(getResources().getString(R.string.check_network),
                        HomeActivity.this);
                showProgress(false);

            }
        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        Timber.i("showProgress(%b)", show);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    public static class InsertRegistrationDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private UserRegistrationDetailsTable userRegistrationDetailsTable;
        private ArrayList<UserRegistrationModel> registeredEventLists;

        InsertRegistrationDatabaseTask(HomeActivity context,
                                       UserRegistrationDetailsTable userRegistrationDetailsTable,
                                       ArrayList<UserRegistrationModel> registeredEventLists) {
            activityReference = new WeakReference<>(context);
            this.userRegistrationDetailsTable = userRegistrationDetailsTable;
            this.registeredEventLists = registeredEventLists;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (registeredEventLists != null && registeredEventLists.size() > 0) {
                for (int i = 0; i < registeredEventLists.size(); i++) {
                    Timber.i("insertUserRegistrationTable() with id: %s",
                            registeredEventLists.get(i).getRegID());
                    userRegistrationDetailsTable = new UserRegistrationDetailsTable(
                            registeredEventLists.get(i).getRegID(),
                            registeredEventLists.get(i).getEventID(),
                            registeredEventLists.get(i).getEventTitle(),
                            registeredEventLists.get(i).getNumberOfReservations()
                    );
                    activityReference.get().qmDatabase.getUserRegistrationTaleDao()
                            .insertUserRegistrationTable(userRegistrationDetailsTable);
                }
            }
            return true;
        }
    }

    public static class RowCountBanner extends AsyncTask<Void, Void, Integer> {
        private WeakReference<HomeActivity> activityReference;
        String language;
        private ArrayList<HomeList> bannerLists;


        RowCountBanner(HomeActivity context, String apiLanguage, ArrayList<HomeList> bannerLists) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.bannerLists = bannerLists;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfBannerRows%s()", language.toUpperCase());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getHomePageBannerTableDao().getNumberOfBannerRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHomePageBannerTableDao().getNumberOfBannerRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            int homePageBannerTableRowCount = integer;
            if (homePageBannerTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new UpdateHomePageBannerTable(activityReference.get(), language, bannerLists).execute();

            } else {
                Timber.i("Banner Table have no data");
                new InsertBannerDatabaseTask(activityReference.get(), activityReference.get().homePageBannerTableEnglish,
                        activityReference.get().homePageBannerTableArabic, language, bannerLists).execute();

            }

        }
    }

    public static class UpdateHomePageBannerTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<HomeActivity> activityReference;
        String language;
        private ArrayList<HomeList> bannerLists;

        UpdateHomePageBannerTable(HomeActivity context, String apiLanguage, ArrayList<HomeList> bannerLists) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.bannerLists = bannerLists;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                Timber.i("updateHomePageBannerEnglish() with id: %s", bannerLists.get(0).getId());
                activityReference.get().qmDatabase.getHomePageBannerTableDao().updateHomePageBannerEnglish(
                        bannerLists.get(0).getName(),
                        bannerLists.get(0).getImage(),
                        bannerLists.get(0).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                Timber.i("updateHomePageBannerArabic() with id: %s", bannerLists.get(0).getId());
                activityReference.get().qmDatabase.getHomePageBannerTableDao().updateHomePageBannerArabic(
                        bannerLists.get(0).getName(),
                        bannerLists.get(0).getImage(),
                        bannerLists.get(0).getId()
                );
            }

            return null;
        }

    }

    public static class InsertBannerDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private HomePageBannerTableEnglish homePageBannerTableEnglish;
        private HomePageBannerTableArabic homePageBannerTableArabic;
        String language;
        private ArrayList<HomeList> bannerLists;

        InsertBannerDatabaseTask(HomeActivity context, HomePageBannerTableEnglish homePageBannerTableEnglish,
                                 HomePageBannerTableArabic homePageBannerTableArabic, String lan,
                                 ArrayList<HomeList> bannerLists) {
            activityReference = new WeakReference<>(context);
            this.homePageBannerTableEnglish = homePageBannerTableEnglish;
            this.homePageBannerTableArabic = homePageBannerTableArabic;
            language = lan;
            this.bannerLists = bannerLists;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (bannerLists != null && bannerLists.size() > 0) {
                Timber.i("insertBannerTable%s() with id: %s", language.toUpperCase(),
                        bannerLists.get(0).getId());
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    homePageBannerTableEnglish = new HomePageBannerTableEnglish(
                            Long.parseLong(bannerLists.get(0).getId()),
                            bannerLists.get(0).getName(),
                            bannerLists.get(0).getImage());
                    activityReference.get().qmDatabase.getHomePageBannerTableDao()
                            .insertEnglishBannerTable(homePageBannerTableEnglish);

                } else {
                    homePageBannerTableArabic = new HomePageBannerTableArabic(
                            Long.parseLong(bannerLists.get(0).getId()),
                            bannerLists.get(0).getName(),
                            bannerLists.get(0).getImage());
                    activityReference.get().qmDatabase.getHomePageBannerTableDao()
                            .insertArabicBannerTable(homePageBannerTableArabic);
                }
            }
            return true;
        }
    }

    public static class RetrieveEnglishBannerTableData extends AsyncTask<Void, Void, List<HomePageBannerTableEnglish>> {
        private WeakReference<HomeActivity> activityReference;

        RetrieveEnglishBannerTableData(HomeActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<HomePageBannerTableEnglish> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageBannerEnglishTable()");
            return activityReference.get().qmDatabase.getHomePageBannerTableDao().getAllDataFromHomePageBannerEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageBannerTableEnglish> homePageBannerTableEnglishes) {
            if (homePageBannerTableEnglishes.size() > 0) {
                Timber.i("homePageBannerTableEnglishes have data with id: %d", homePageBannerTableEnglishes.get(0).getQatarmuseum_id());
                activityReference.get().bannerLists.clear();
                HomeList bannerList = new HomeList(homePageBannerTableEnglishes.get(0).getName()
                        , String.valueOf(homePageBannerTableEnglishes.get(0).getQatarmuseum_id()),
                        homePageBannerTableEnglishes.get(0).getImage());
                activityReference.get().bannerLists.add(bannerList);
                if (activityReference.get().bannerLists.size() > 0) {
                    if (!activityReference.get().isFinishing())
                        GlideApp.with(activityReference.get())
                                .load(activityReference.get().bannerLists.get(0).getImage())
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_header)
                                .into(activityReference.get().headerImageView);
                    activityReference.get().bannerText.setText(activityReference.get().bannerLists.get(0).getName());
                }
                activityReference.get().bannerLayout.setVisibility(View.VISIBLE);
            } else {
                Timber.i("homePageBannerTableEnglishes have no data");
                activityReference.get().bannerLayout.setVisibility(View.GONE);
            }
        }
    }

    public static class RetrieveArabicBannerTableData extends AsyncTask<Void, Void,
            List<HomePageBannerTableArabic>> {
        private WeakReference<HomeActivity> activityReference;

        RetrieveArabicBannerTableData(HomeActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected List<HomePageBannerTableArabic> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageBannerArabicTable()");
            return activityReference.get().qmDatabase.getHomePageBannerTableDao().getAllDataFromHomePageBannerArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageBannerTableArabic> homePageBannerTableArabics) {
            if (homePageBannerTableArabics.size() > 0) {
                Timber.i("homePageBannerTableEnglishes have data with id: %d", homePageBannerTableArabics.get(0).getQatarmuseum_id());
                activityReference.get().bannerLists.clear();
                HomeList bannerList = new HomeList(homePageBannerTableArabics.get(0).getName()
                        , String.valueOf(homePageBannerTableArabics.get(0).getQatarmuseum_id()),
                        homePageBannerTableArabics.get(0).getImage());
                activityReference.get().bannerLists.add(bannerList);
                if (activityReference.get().bannerLists.size() > 0) {
                    if (!activityReference.get().isFinishing())
                        GlideApp.with(activityReference.get())
                                .load(activityReference.get().bannerLists.get(0).getImage())
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_header)
                                .into(activityReference.get().headerImageView);
                    activityReference.get().bannerText.setText(activityReference.get().bannerLists.get(0).getName());
                }

                activityReference.get().bannerLayout.setVisibility(View.VISIBLE);
            } else {
                Timber.i("homePageBannerTableEnglishes have no data");
                activityReference.get().bannerLayout.setVisibility(View.GONE);
            }
        }

    }

    public void getBannerDataFromDataBase() {
        Timber.i("getBannerDataFromDataBase()");
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
            new RetrieveEnglishBannerTableData(HomeActivity.this).execute();
        else
            new RetrieveArabicBannerTableData(HomeActivity.this).execute();
    }

    public static class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<HomeActivity> activityReference;
        private WeakReference<ArrayList<HomeList>> homeList;
        String language;
        HomePageTableEnglish homePageTableEnglish;
        HomePageTableArabic homePageTableArabic;


        RowCount(HomeActivity context, String apiLanguage, ArrayList<HomeList> list) {
            activityReference = new WeakReference<>(context);
            homeList = new WeakReference<>(list);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows%s()", language.toUpperCase());
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsArabic();
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int homePageTableRowCount;
            homePageTableRowCount = integer;
            if (homePageTableRowCount > 0) {
                Timber.i("homePageTableRowCount: %d", homePageTableRowCount);
                new CheckDBRowExist(activityReference.get(), language, homeList.get()).execute();

            } else {
                Timber.i("homePageTable have no data");
                new InsertDatabaseTask(activityReference.get(), homePageTableEnglish,
                        homePageTableArabic, language, homeList.get()).execute();

            }

        }
    }

    public static class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<HomeActivity> activityReference;
        private WeakReference<ArrayList<HomeList>> homeLists;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        CheckDBRowExist(HomeActivity context, String apiLanguage, ArrayList<HomeList> list) {
            activityReference = new WeakReference<>(context);
            homeLists = new WeakReference<>(list);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (homeLists.get().size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < homeLists.get().size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistEnglish(
                                Integer.parseInt(homeLists.get().get(i).getId()));
                        if (n > 0) {
                            Timber.i("Row exist in DB for id: %s", homeLists.get().get(i).getId());
                            new UpdateHomePageTable(activityReference.get(), language, i, homeLists.get()).execute();
                        } else {
                            Timber.i("insertEnglishTable with id: %s", homeLists.get().get(i).getId());
                            homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(homeLists.get().get(i).getId()),
                                    homeLists.get().get(i).getName(),
                                    homeLists.get().get(i).getTourGuideAvailable(),
                                    homeLists.get().get(i).getImage(),
                                    homeLists.get().get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < homeLists.get().size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistArabic(
                                Integer.parseInt(homeLists.get().get(i).getId()));
                        if (n > 0) {
                            new UpdateHomePageTable(activityReference.get(), language, i, homeLists.get()).execute();
                        } else {
                            Timber.i("insertArabicTable with id: %s", homeLists.get().get(i).getId());
                            homePageTableArabic = new HomePageTableArabic(Long.parseLong(homeLists.get().get(i).getId()),
                                    homeLists.get().get(i).getName(),
                                    homeLists.get().get(i).getTourGuideAvailable(),
                                    homeLists.get().get(i).getImage(),
                                    homeLists.get().get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private WeakReference<ArrayList<HomeList>> homeLists;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        InsertDatabaseTask(HomeActivity context, HomePageTableEnglish homePageTableEnglish,
                           HomePageTableArabic homePageTableArabic, String lan, ArrayList<HomeList> list) {
            activityReference = new WeakReference<>(context);
            homeLists = new WeakReference<>(list);
            this.homePageTableEnglish = homePageTableEnglish;
            this.homePageTableArabic = homePageTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (homeLists != null && homeLists.get().size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    Timber.i("insertEnglishTable with size: %d", homeLists.get().size());
                    for (int i = 0; i < homeLists.get().size(); i++) {
                        Timber.i("insertEnglishTable with id: %s", homeLists.get().get(i).getId());
                        homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(homeLists.get().get(i).getId()),
                                homeLists.get().get(i).getName(),
                                homeLists.get().get(i).getTourGuideAvailable(),
                                homeLists.get().get(i).getImage(),
                                homeLists.get().get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);
                    }
                } else {
                    Timber.i("insertArabicTable with size: %d", homeLists.get().size());
                    for (int i = 0; i < homeLists.get().size(); i++) {
                        Timber.i("insertArabicTable with id: %s", homeLists.get().get(i).getId());
                        homePageTableArabic = new HomePageTableArabic(Long.parseLong(homeLists.get().get(i).getId()),
                                homeLists.get().get(i).getName(),
                                homeLists.get().get(i).getTourGuideAvailable(),
                                homeLists.get().get(i).getImage(),
                                homeLists.get().get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

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
        private WeakReference<HomeActivity> activityReference;
        private WeakReference<ArrayList<HomeList>> homeLists;
        String language;
        int position;

        UpdateHomePageTable(HomeActivity context, String apiLanguage, int p, ArrayList<HomeList> list) {
            activityReference = new WeakReference<>(context);
            homeLists = new WeakReference<>(list);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                Timber.i("updateEnglishTable with id: %s", homeLists.get().get(position).getId());
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageEnglish(
                        homeLists.get().get(position).getName(), homeLists.get().get(position).getTourGuideAvailable(),
                        homeLists.get().get(position).getImage(), homeLists.get().get(position).getSortId(),
                        homeLists.get().get(position).getId()
                );

            } else {
                Timber.i("updateArabicTable with id: %s", homeLists.get().get(position).getId());
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageArabic(
                        homeLists.get().get(position).getName(), homeLists.get().get(position).getTourGuideAvailable(),
                        homeLists.get().get(position).getImage(), homeLists.get().get(position).getSortId(),
                        homeLists.get().get(position).getId()
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }

    public static class RetrieveEnglishTableData extends AsyncTask<Void, Void, List<HomePageTableEnglish>> {
        private WeakReference<HomeActivity> activityReference;

        RetrieveEnglishTableData(HomeActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<HomePageTableEnglish> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageEnglishTable()");
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableEnglish> homePageTableEnglishes) {
            if (homePageTableEnglishes.size() > 0) {
                Timber.i("HomePageEnglishTable with size: %d", homePageTableEnglishes.size());
                activityReference.get().homeLists.clear();
                for (int i = 0; i < homePageTableEnglishes.size(); i++) {
                    Timber.i("Set home list from DB with id: %d", homePageTableEnglishes.get(i).getQatarmuseum_id());
                    HomeList exhibitionObject = new HomeList(homePageTableEnglishes.get(i).getName()
                            , String.valueOf(homePageTableEnglishes.get(i).getQatarmuseum_id()),
                            homePageTableEnglishes.get(i).getImage(),
                            homePageTableEnglishes.get(i).getTourguide_available(),
                            homePageTableEnglishes.get(i).getSortId());
                    activityReference.get().homeLists.add(i, exhibitionObject);
                }

                Collections.sort(activityReference.get().homeLists);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("HomePageEnglishTable have no data");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    public static class RetrieveArabicTableData extends AsyncTask<Void, Void,
            List<HomePageTableArabic>> {
        private WeakReference<HomeActivity> activityReference;

        RetrieveArabicTableData(HomeActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected List<HomePageTableArabic> doInBackground(Void... voids) {
            Timber.i("getAllDataFromHomePageArabicTable()");
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableArabic> homePageTableArabics) {
            if (homePageTableArabics.size() > 0) {
                Timber.i("HomePageTableArabic with size: %s", homePageTableArabics.size());
                activityReference.get().homeLists.clear();
                for (int i = 0; i < homePageTableArabics.size(); i++) {
                    Timber.i("Set home list from DB with id: %d", homePageTableArabics.get(i).getQatarmuseum_id());
                    HomeList exhibitionObject = new HomeList(homePageTableArabics.get(i).getName()
                            , String.valueOf(homePageTableArabics.get(i).getQatarmuseum_id()),
                            homePageTableArabics.get(i).getImage(),
                            homePageTableArabics.get(i).getTourguide_available(),
                            homePageTableArabics.get(i).getSortId());
                    activityReference.get().homeLists.add(i, exhibitionObject);
                }

                Collections.sort(activityReference.get().homeLists);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("HomePageTableArabic have no data");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getDataFromDataBase(String language) {
        Timber.i("getDataFromDataBase()");
        progressBar.setVisibility(View.VISIBLE);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
            new RetrieveEnglishTableData(HomeActivity.this).execute();
        else
            new RetrieveArabicTableData(HomeActivity.this).execute();
    }

}
