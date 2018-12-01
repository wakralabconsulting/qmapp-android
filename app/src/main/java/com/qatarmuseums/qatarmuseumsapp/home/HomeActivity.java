package com.qatarmuseums.qatarmuseumsapp.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.arch.persistence.room.Update;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.LoginData;
import com.qatarmuseums.qatarmuseumsapp.culturepass.ReceivedCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserResponseDeserializer;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends BaseActivity {
    RelativeLayout diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation, fadeOutAnimation;
    private RecyclerView recyclerView;
    private HomeListAdapter mAdapter;
    private ArrayList<HomeList> homeLists = new ArrayList<>();
    private ArrayList<HomeList> bannerLists = new ArrayList<>();
    private Intent navigation_intent;
    Util util;
    RelativeLayout noResultFoundLayout;
    private boolean doubleBackToExitPressedOnce;
    ProgressBar progressBar;
    SharedPreferences qmPreferences;
    QMDatabase qmDatabase;
    HomePageTableEnglish homePageTableEnglish;
    HomePageTableArabic homePageTableArabic;
    HomePageBannerTableEnglish homePageBannerTableEnglish;
    HomePageBannerTableArabic homePageBannerTableArabic;
    int homePageTableRowCount, homePageBannerTableRowCount;
    private Intent navigationIntent;
    private LinearLayout retryLayout;
    private Button retryButton;
    private int appLanguage;
    private String name;
    private SharedPreferences.Editor editor;
    private int badgeCount;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String notificationMessage, message;
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
    private LayoutInflater layoutInflater;
    private ImageView closeBtn;
    private EditText mUsernameView, mPasswordView;
    private ProgressBar mProgressView;
    private FrameLayout mLoginFormView;
    private View dialogLoginButton;
    private TextView forgotPassword;
    private String qatar, museum;
    private LoginData loginData;
    private String token;
    private boolean isFirstLaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        progressBar.setVisibility(View.VISIBLE);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        diningNavigation = (RelativeLayout) findViewById(R.id.dining_layout);
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        giftShopNavigation = (RelativeLayout) findViewById(R.id.gift_shop_layout);
        culturePassNavigation = (RelativeLayout) findViewById(R.id.culture_pass_layout);
        moreNavigation = (RelativeLayout) findViewById(R.id.more_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        bannerText = findViewById(R.id.banner_text);
        headerImageView.setOnClickListener(v -> navigateToLaunchPage());
        bannerLayout = (AppBarLayout) findViewById(R.id.banner);

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
        diningNavigation.setOnClickListener(v -> {
            navigation_intent = new Intent(HomeActivity.this, CommonActivity.class);
            navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_dining_text));
            startActivity(navigation_intent);
        });
        giftShopNavigation.setOnClickListener(v -> {
            navigation_intent = new Intent(HomeActivity.this, WebviewActivity.class);
            navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
            startActivity(navigation_intent);

        });
        culturePassNavigation.setOnClickListener(v -> {
            name = qmPreferences.getString("NAME", null);
            if (name == null)
                navigation_intent = new Intent(HomeActivity.this, CulturePassActivity.class);
            else
                navigation_intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(navigation_intent);
        });
        moreNavigation.setOnClickListener(v -> handlingDrawer());

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
                    navigationIntent = new Intent(HomeActivity.this, CommonActivity.class);
                    navigationIntent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_exhibition_text));
                    startActivity(navigationIntent);
                } else {
                    navigationIntent = new Intent(HomeActivity.this, MuseumActivity.class);
                    navigationIntent.putExtra("MUSEUMTITLE", homeList.getName());
                    navigationIntent.putExtra("MUSEUM_ID", homeList.getId());
                    startActivity(navigationIntent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        isFirstLaunch = qmPreferences.getBoolean("FIRST_LAUNCH", true);
        if (isFirstLaunch) {
            showLoginDialog();
            editor = qmPreferences.edit();
            editor.putBoolean("FIRST_LAUNCH", false);
            editor.commit();
        }
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";

        if (util.isNetworkAvailable(this)) {
            getHomePageAPIData(appLanguage);
        } else {
            getDataFromDataBase(appLanguage);
        }

        retryButton.setOnClickListener(v -> {
            getHomePageAPIData(appLanguage);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            RSVP = qmPreferences.getString("RSVP", null);
            if (RSVP != null) {
                if (bannerLayout.getVisibility() != View.VISIBLE)
                    showBanner();
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

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    notificationMessage = intent.getStringExtra("ALERT");
                    if (notificationMessage != null && !notificationMessage.equals(""))
                        updateNotificationDB(notificationMessage);
                    badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
                    badgeCount = badgeCount + 1;
                    editor = qmPreferences.edit();
                    editor.putInt("BADGE_COUNT", badgeCount);
                    editor.commit();
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
        navigationIntent = new Intent(HomeActivity.this, MuseumActivity.class);
        navigationIntent.putExtra("MUSEUMTITLE", bannerText.getText());
        navigationIntent.putExtra("MUSEUM_ID", bannerLists.get(0).getId());
        startActivity(navigationIntent);
    }

    public void checkForNotification(Intent intent) {
        message = intent.getStringExtra("MESSAGE");
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
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        insertNotificationRelatedDataToDataBase(message, language);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            navigationView.startAnimation(fadeOutAnimation);
            topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
            showToolBarOptions();
            drawer.closeDrawer(GravityCompat.END, false);
            toolbar.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.touch_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void getHomePageAPIData(int lan) {
        int appLanguage = lan;
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getHomepageDetails(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(Call<ArrayList<HomeList>> call, Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        homeLists.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        new RowCount(HomeActivity.this, language).execute();
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
            public void onFailure(Call<ArrayList<HomeList>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getBannerAPIData(int lan) {
        int appLanguage = lan;
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getBannerDetails(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(Call<ArrayList<HomeList>> call, Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        bannerLists.addAll(response.body());
                        addBannerData(bannerLists);
                        new RowCountBanner(HomeActivity.this, language).execute();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<HomeList>> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (retryLayout.getVisibility() == View.VISIBLE) {
            getDataFromDataBase(appLanguage);
        }
        updateBadge();
        RSVP = qmPreferences.getString("RSVP", null);
        if (RSVP != null) {
            if (bannerLayout.getVisibility() != View.VISIBLE)
                showBanner();
        } else {
            bannerLayout.setVisibility(View.GONE);
        }
    }

    public void showBanner() {
        if (util.isNetworkAvailable(this))
            getBannerAPIData(appLanguage);
        else
            getBannerDataFromDataBase(appLanguage);
        bannerLayout.setVisibility(View.VISIBLE);
    }

    public void addBannerData(List<HomeList> bannerLists) {
        if (!HomeActivity.this.isFinishing())
            GlideApp.with(HomeActivity.this)
                    .load(bannerLists.get(0).getImage())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_header)
                    .into(headerImageView);
        bannerText.setText(bannerLists.get(0).getName());

    }

    public void fetchToken() {
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
            public void onResponse(Call<ProfileDetails> call, final Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        performLogin(response.body().getToken());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
                util.showToast(getResources().getString(R.string.check_network),
                        HomeActivity.this);
                showProgress(false);
            }

        });
    }

    public void performLogin(String token) {
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
            public void onResponse(Call<ProfileDetails> call, Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        profileDetails = response.body();
                        checkRSVP(profileDetails.getUser().getuId(), profileDetails.getToken());
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
                        editor.commit();
                    }
                } else {
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
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
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
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getRsvpAttendance() != null) {
                            RSVP = ((LinkedTreeMap) ((ArrayList) ((LinkedTreeMap)
                                    response.body().getRsvpAttendance()).get("und")).get(0)).get("value").toString();
                            editor = qmPreferences.edit();
                            editor.putString("RSVP", RSVP);
                            editor.commit();
                            showBanner();
                        }
                    }
                    showProgress(false);
                    loginDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                new Util().showToast(getResources().getString(R.string.check_network), HomeActivity.this);
                showProgress(false);
            }
        });
    }

    protected void showLoginDialog() {
        loginDialog = new Dialog(this, R.style.DialogNoAnimation);
        loginDialog.setCancelable(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.login_popup, null);
        loginDialog.setContentView(view);
        closeBtn = view.findViewById(R.id.close_dialog);
        dialogLoginButton = view.findViewById(R.id.dialog_login_button);
        dialogLoginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dialogLoginButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
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
        forgotPassword = view.findViewById(R.id.forgot_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    hideSoftKeyboard(textView);
                    return true;
                }
                return false;
            }
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameViewLayout.setError(null);
        mPasswordViewLayout.setError(null);

        // Store values at the time of the login attempt.
        qatar = mUsernameView.getText().toString();
        museum = mPasswordView.getText().toString();

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
            token = qmPreferences.getString("TOKEN", null);
            loginData = new LoginData(qatar, museum);
            if (token != null) {
                performLogin(token);
            } else
                fetchToken();
        }
    }

    private void attemptForgotPassword() {
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
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ProfileDetails> call = apiService.generateToken(language, loginData);
        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(Call<ProfileDetails> call, final Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        forgotPasswordAction(response.body().getToken());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
                util.showToast(getResources().getString(R.string.check_network),
                        HomeActivity.this);
                showProgress(false);
            }

        });
    }

    public void forgotPasswordAction(String token) {
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<String>> callLogin = apiService.forgotPassword(language, token, loginData);
        callLogin.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        loginDialog.dismiss();
                        util.showNormalDialog(HomeActivity.this, R.string.password_reset_successful);
                    }
                } else {
                    if (response.code() == 406)
                        mUsernameViewLayout.setError(getString(R.string.error_incorrect_username));
                    else
                        mUsernameViewLayout.setError(getString(R.string.error_unexpected));

                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
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


    public class RowCountBanner extends AsyncTask<Void, Void, Integer> {
        private WeakReference<HomeActivity> activityReference;
        String language;

        RowCountBanner(HomeActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getHomePageBannerTableDao().getNumberOfBannerRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHomePageBannerTableDao().getNumberOfBannerRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            homePageBannerTableRowCount = integer;
            if (homePageBannerTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new UpdateHomePageBannerTable(HomeActivity.this, language).execute();

            } else {
                //create databse
                new InsertBannerDatabaseTask(HomeActivity.this, homePageBannerTableEnglish,
                        homePageBannerTableArabic, language).execute();

            }

        }
    }

    public class UpdateHomePageBannerTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<HomeActivity> activityReference;
        String language;

        UpdateHomePageBannerTable(HomeActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHomePageBannerTableDao().updateHomePageBannerEnglish(
                        bannerLists.get(0).getName(),
                        bannerLists.get(0).getImage(),
                        bannerLists.get(0).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getHomePageBannerTableDao().updateHomePageBannerArabic(
                        bannerLists.get(0).getName(),
                        bannerLists.get(0).getImage(),
                        bannerLists.get(0).getId()
                );
            }

            return null;
        }

    }

    public class InsertBannerDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private HomePageBannerTableEnglish homePageBannerTableEnglish;
        private HomePageBannerTableArabic homePageBannerTableArabic;
        String language;

        InsertBannerDatabaseTask(HomeActivity context, HomePageBannerTableEnglish homePageBannerTableEnglish,
                                 HomePageBannerTableArabic homePageBannerTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.homePageBannerTableEnglish = homePageBannerTableEnglish;
            this.homePageBannerTableArabic = homePageBannerTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (bannerLists != null) {
                if (language.equals("en")) {
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

    public class RetriveEnglishBannerTableData extends AsyncTask<Void, Void, List<HomePageBannerTableEnglish>> {
        private WeakReference<HomeActivity> activityReference;
        int language;

        RetriveEnglishBannerTableData(HomeActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected List<HomePageBannerTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageBannerTableDao().getAllDataFromHomePageBannerEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageBannerTableEnglish> homePageBannerTableEnglishes) {
            if (homePageBannerTableEnglishes.size() > 0) {
                bannerLists.clear();
                HomeList exhibitonObject = new HomeList(homePageBannerTableEnglishes.get(0).getName()
                        , String.valueOf(homePageBannerTableEnglishes.get(0).getQatarmuseum_id()),
                        homePageBannerTableEnglishes.get(0).getImage());
                bannerLists.add(exhibitonObject);
                addBannerData(bannerLists);
                bannerLayout.setVisibility(View.VISIBLE);
            } else {
                bannerLayout.setVisibility(View.GONE);
            }
        }
    }

    public class RetriveArabicBannerTableData extends AsyncTask<Void, Void,
            List<HomePageBannerTableArabic>> {
        private WeakReference<HomeActivity> activityReference;
        int language;

        RetriveArabicBannerTableData(HomeActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }


        @Override
        protected List<HomePageBannerTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageBannerTableDao().getAllDataFromHomePageBannerArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageBannerTableArabic> homePageBannerTableArabics) {
            if (homePageBannerTableArabics.size() > 0) {
                bannerLists.clear();
                HomeList exhibitonObject = new HomeList(homePageBannerTableArabics.get(0).getName()
                        , String.valueOf(homePageBannerTableArabics.get(0).getQatarmuseum_id()),
                        homePageBannerTableArabics.get(0).getImage());
                bannerLists.add(exhibitonObject);
                addBannerData(bannerLists);
                bannerLayout.setVisibility(View.VISIBLE);
            } else {
                bannerLayout.setVisibility(View.GONE);
            }
        }

    }

    public void getBannerDataFromDataBase(int language) {
        if (language == 1)
            new RetriveEnglishBannerTableData(HomeActivity.this, language).execute();
        else
            new RetriveArabicBannerTableData(HomeActivity.this, language).execute();
    }

    public class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<HomeActivity> activityReference;
        String language;

        RowCount(HomeActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            homePageTableRowCount = integer;
            if (homePageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(HomeActivity.this, language).execute();

            } else {
                //create databse
                new InsertDatabaseTask(HomeActivity.this, homePageTableEnglish,
                        homePageTableArabic, language).execute();

            }

        }
    }


    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<HomeActivity> activityReference;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        CheckDBRowExist(HomeActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (homeLists.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < homeLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistEnglish(
                                Integer.parseInt(homeLists.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(HomeActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(homeLists.get(i).getId()),
                                    homeLists.get(i).getName(),
                                    homeLists.get(i).getTourguideAvailable().toString(),
                                    homeLists.get(i).getImage(),
                                    homeLists.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < homeLists.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHomePageTableDao().checkIdExistArabic(
                                Integer.parseInt(homeLists.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHomePageTable(HomeActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            homePageTableArabic = new HomePageTableArabic(Long.parseLong(homeLists.get(i).getId()),
                                    homeLists.get(i).getName(),
                                    homeLists.get(i).getTourguideAvailable().toString(),
                                    homeLists.get(i).getImage(),
                                    homeLists.get(i).getSortId());
                            activityReference.get().qmDatabase.getHomePageTableDao().insertArabicTable(homePageTableArabic);

                        }
                    }
                }
            }
            return null;
        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private HomePageTableEnglish homePageTableEnglish;
        private HomePageTableArabic homePageTableArabic;
        String language;

        InsertDatabaseTask(HomeActivity context, HomePageTableEnglish homePageTableEnglish,
                           HomePageTableArabic homePageTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.homePageTableEnglish = homePageTableEnglish;
            this.homePageTableArabic = homePageTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (homeLists != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < homeLists.size(); i++) {
                        homePageTableEnglish = new HomePageTableEnglish(Long.parseLong(homeLists.get(i).getId()),
                                homeLists.get(i).getName(),
                                homeLists.get(i).getTourguideAvailable().toString(),
                                homeLists.get(i).getImage(),
                                homeLists.get(i).getSortId());
                        activityReference.get().qmDatabase.getHomePageTableDao().insertEnglishTable(homePageTableEnglish);
                    }
                } else {
                    for (int i = 0; i < homeLists.size(); i++) {
                        homePageTableArabic = new HomePageTableArabic(Long.parseLong(homeLists.get(i).getId()),
                                homeLists.get(i).getName(),
                                homeLists.get(i).getTourguideAvailable().toString(),
                                homeLists.get(i).getImage(),
                                homeLists.get(i).getSortId());
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


    public class UpdateHomePageTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<HomeActivity> activityReference;
        String language;
        int position;

        UpdateHomePageTable(HomeActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageEnglish(
                        homeLists.get(position).getName(), homeLists.get(position).getTourguideAvailable().toString(),
                        homeLists.get(position).getImage(), homeLists.get(position).getSortId(),
                        homeLists.get(position).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageArabic(
                        homeLists.get(position).getName(), homeLists.get(position).getTourguideAvailable().toString(),
                        homeLists.get(position).getImage(), homeLists.get(position).getSortId(),
                        homeLists.get(position).getId()
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }

    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<HomePageTableEnglish>> {
        private WeakReference<HomeActivity> activityReference;
        int language;

        RetriveEnglishTableData(HomeActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected List<HomePageTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageEnglishTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableEnglish> homePageTableEnglishes) {
            if (homePageTableEnglishes.size() > 0) {
                homeLists.clear();
                for (int i = 0; i < homePageTableEnglishes.size(); i++) {
                    HomeList exhibitonObject = new HomeList(homePageTableEnglishes.get(i).getName()
                            , String.valueOf(homePageTableEnglishes.get(i).getQatarmuseum_id()),
                            homePageTableEnglishes.get(i).getImage(),
                            homePageTableEnglishes.get(i).getTourguide_available(),
                            homePageTableEnglishes.get(i).getSortId());
                    homeLists.add(i, exhibitonObject);
                }

                Collections.sort(homeLists);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    public class RetriveArabicTableData extends AsyncTask<Void, Void,
            List<HomePageTableArabic>> {
        private WeakReference<HomeActivity> activityReference;
        int language;

        RetriveArabicTableData(HomeActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }


        @Override
        protected List<HomePageTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageTableDao().getAllDataFromHomePageArabicTable();

        }

        @Override
        protected void onPostExecute(List<HomePageTableArabic> homePageTableArabics) {
            if (homePageTableArabics.size() > 0) {
                homeLists.clear();
                for (int i = 0; i < homePageTableArabics.size(); i++) {
                    HomeList exhibitonObject = new HomeList(homePageTableArabics.get(i).getName()
                            , String.valueOf(homePageTableArabics.get(i).getQatarmuseum_id()),
                            homePageTableArabics.get(i).getImage(),
                            homePageTableArabics.get(i).getTourguide_available(),
                            homePageTableArabics.get(i).getSortId());
                    homeLists.add(i, exhibitonObject);
                }

                Collections.sort(homeLists);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getDataFromDataBase(int language) {
        progressBar.setVisibility(View.VISIBLE);
        if (language == 1)
            new RetriveEnglishTableData(HomeActivity.this, language).execute();
        else
            new RetriveArabicTableData(HomeActivity.this, language).execute();
    }

}
