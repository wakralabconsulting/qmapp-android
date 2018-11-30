package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CulturePassActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View backArrow;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    private View becomeMember, loginButton, dialogLoginButton;
    Util util;
    private Dialog loginDialog;
    private LayoutInflater layoutInflater;
    private ImageView closeBtn;
    private EditText mUsernameView, mPasswordView;
    private ProgressBar mProgressView;
    private FrameLayout mLoginFormView;
    private TextInputLayout mPasswordViewLayout, mUsernameViewLayout;
    private TextView forgotPassword;
    private int REQUEST_CODE = 123;
    private String language;
    private String qatar, museum;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    private ProfileDetails profileDetails;
    private SharedPreferences.Editor editor;
    private Intent navigationIntent;
    private Boolean isLogout;
    private APIInterface apiService;
    private String token;
    private LoginData loginData;
    private Retrofit retrofit;
    private String RSVP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_pass);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        becomeMember = findViewById(R.id.become_a_member_btn);
        loginButton = findViewById(R.id.login_btn);
        setSupportActionBar(toolbar);
        util = new Util();
        isLogout = getIntent().getBooleanExtra("IS_LOGOUT", false);
        if (isLogout) {
            util.showNormalDialog(this, R.string.logout_success_message);
        }
        profileDetails = new ProfileDetails();
        backArrow = findViewById(R.id.toolbar_back);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        token = qmPreferences.getString("TOKEN", null);
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        becomeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Temporary
//                 navigationIntent = new Intent(CulturePassActivity.this, CreateAccountActivity.class);
                navigationIntent = new Intent(CulturePassActivity.this, WebviewActivity.class);
                navigationIntent.putExtra("url", "http://www.qm.org.qa/" + language +
                        "/user/register#user-register-form");
                startActivity(navigationIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
        becomeMember.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        becomeMember.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        loginButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

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
                        CulturePassActivity.this);
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
                    util.showToast(t.getMessage(), CulturePassActivity.this);
                else
                    util.showToast(getResources().getString(R.string.check_network),
                            CulturePassActivity.this);
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
                        }
                    }
                    navigateToProfile(RSVP);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                new Util().showToast(getResources().getString(R.string.check_network), CulturePassActivity.this);
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
        dialogLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                hideSoftKeyboard(view);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog.dismiss();

            }
        });
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
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptForgotPassword();

            }
        });
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
                        CulturePassActivity.this);
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
                        util.showNormalDialog(CulturePassActivity.this, R.string.password_reset_successful);
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
                        CulturePassActivity.this);
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

    public void navigateToProfile(String RSVP) {
        loginDialog.dismiss();
        navigationIntent = new Intent(CulturePassActivity.this, ProfileActivity.class);
        navigationIntent.putExtra("RSVP", RSVP);
        startActivityForResult(navigationIntent, REQUEST_CODE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                util.showNormalDialog(this, R.string.logout_success_message);
            }
        }
    }
}
