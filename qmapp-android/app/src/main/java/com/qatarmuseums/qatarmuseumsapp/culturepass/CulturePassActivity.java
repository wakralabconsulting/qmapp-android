package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.UserRegistrationModel;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebViewActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.qatarmuseums.qatarmuseumsapp.apicall.APIClient.apiBaseUrl;

public class CulturePassActivity extends AppCompatActivity {

    private View backArrow;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    private View becomeMember, loginButton, dialogLoginButton;
    Util util;
    private Dialog loginDialog;
    private EditText mUsernameView, mPasswordView;
    private ProgressBar mProgressView;
    private FrameLayout mLoginFormView;
    private TextInputLayout mPasswordViewLayout, mUsernameViewLayout;
    private int REQUEST_CODE = 123;
    private String language;
    private String qatar;
    private SharedPreferences qmPreferences;
    private ProfileDetails profileDetails;
    private SharedPreferences.Editor editor;
    private Intent navigationIntent;
    private APIInterface apiService;
    private String token;
    private LoginData loginData;
    private Retrofit retrofit;
    private String RSVP = null;
    private QMDatabase qmDatabase;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_pass);
        Toolbar toolbar = findViewById(R.id.common_toolbar);
        becomeMember = findViewById(R.id.become_a_member_btn);
        loginButton = findViewById(R.id.login_btn);
        setSupportActionBar(toolbar);
        qmDatabase = QMDatabase.getInstance(CulturePassActivity.this);
        util = new Util();
        Boolean isLogout = getIntent().getBooleanExtra("IS_LOGOUT", false);
        if (isLogout) {
            util.showNormalDialog(this, R.string.logout_success_message);
        }
        profileDetails = new ProfileDetails();
        backArrow = findViewById(R.id.toolbar_back);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = qmPreferences.getString("TOKEN", null);

        language = LocaleManager.getLanguage(this);
        backArrow.setOnClickListener(v -> onBackPressed());
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(iconZoomOutAnimation);
                    break;
            }
            return false;
        });
        becomeMember.setOnClickListener(v -> {
            navigationIntent = new Intent(CulturePassActivity.this, WebViewActivity.class);
            navigationIntent.putExtra("url", apiBaseUrl + language +
                    "/user/register#user-register-form");
            startActivity(navigationIntent);
        });
        loginButton.setOnClickListener(v -> showLoginDialog());
        becomeMember.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    becomeMember.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        loginButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    loginButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
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
                .baseUrl(apiBaseUrl)
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
                .baseUrl(apiBaseUrl)
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

                        // Commenting VIP user check for temporary
//                        checkRSVP(profileDetails.getUser().getuId(), profileDetails.getToken());

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
                        editor.commit();

                        // Remove this navigation if VIP user check is enabled
                        navigateToProfile(RSVP);
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
                .baseUrl(apiBaseUrl)
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
                            getUserRegistrationDetails(uid);
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

    private ArrayList<UserRegistrationModel> registeredEventLists = new ArrayList<>();
    UserRegistrationDetailsTable userRegistrationDetailsTable;

    public void getUserRegistrationDetails(String userID) {
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<UserRegistrationModel>> call = apiService.getUserRegistrationDetails("en", userID);
        call.enqueue(new Callback<ArrayList<UserRegistrationModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserRegistrationModel>> call, Response<ArrayList<UserRegistrationModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        registeredEventLists.addAll(response.body());
                        new InsertRegistrationDatabaseTask(CulturePassActivity.this,
                                userRegistrationDetailsTable, registeredEventLists).execute();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserRegistrationModel>> call, Throwable t) {
            }
        });
    }

    public static class InsertRegistrationDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CulturePassActivity> activityReference;
        private UserRegistrationDetailsTable userRegistrationDetailsTable;
        private ArrayList<UserRegistrationModel> registeredEventLists;

        InsertRegistrationDatabaseTask(CulturePassActivity context,
                                       UserRegistrationDetailsTable userRegistrationDetailsTable,
                                       ArrayList<UserRegistrationModel> registeredEventLists) {
            activityReference = new WeakReference<>(context);
            this.userRegistrationDetailsTable = userRegistrationDetailsTable;
            this.registeredEventLists = registeredEventLists;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (registeredEventLists != null) {
                for (int i = 0; i < registeredEventLists.size(); i++) {
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

    protected void showLoginDialog() {
        loginDialog = new Dialog(this, R.style.DialogNoAnimation);
        loginDialog.setCancelable(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.login_popup, null);
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
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                util.showNormalDialog(this, R.string.logout_success_message);
            }
        }
    }
}
