package com.qatarmuseums.qatarmuseumsapp.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.ReceivedCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepasscard.CulturePassCardActivity;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.DeCryptor;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_ENCRYPTION_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_KEY_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_VECTOR_SUFFIX;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private View backArrow, logOut;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    TextView usernameTxt, membershipNumberTxt, emailTxt, dobTxt, residenceTxt, nationalityTxt;
    ImageView profilePic, profileEdit;
    View myFavBtn, myCardBtn;
    private SharedPreferences qmPreferences;
    private String token;
    private String username;
    private String membershipNumber;
    private SharedPreferences.Editor editor;
    private LinearLayout progressBar;
    String language;
    private Retrofit retrofit;
    HashMap<String, String> country = new HashMap<>();
    private SwitchCompat acceptDeclineButton;
    Util util;
    String accepted;
    private String uid;
    private List<Und> unds = new ArrayList<>();
    private QMDatabase qmDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        backArrow = findViewById(R.id.toolbar_back);
        logOut = findViewById(R.id.log_out);
        usernameTxt = findViewById(R.id.username_txt);
        membershipNumberTxt = findViewById(R.id.membership_number_txt);
        emailTxt = findViewById(R.id.email_txt);
        dobTxt = findViewById(R.id.date_of_birth_txt);
        residenceTxt = findViewById(R.id.residence_txt);
        nationalityTxt = findViewById(R.id.nationality_txt);
        profilePic = findViewById(R.id.profile_pic);
        profileEdit = findViewById(R.id.profile_edit);
        myFavBtn = findViewById(R.id.view_my_fav_btn);
        myCardBtn = findViewById(R.id.view_my_card_btn);
        progressBar = findViewById(R.id.logout_progress);
        acceptDeclineButton = findViewById(R.id.accept_decline_button);
        util = new Util();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnClickListener(v -> {
            Timber.i("Back button clicked");
            setResultOkSoIntermediateActivityWontBeShown();
            onBackPressed();
        });
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(iconZoomOutAnimation);
                    break;
            }
            return false;
        });
        logOut.setOnClickListener(v -> {
            Timber.i("Logout button clicked");
            logOutAction();
        });
        logOut.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    logOut.startAnimation(iconZoomOutAnimation);
                    break;
            }
            return false;
        });
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = LocaleManager.getLanguage(this);
        try {
            token = new DeCryptor().decryptData(QM_ALIAS, this);
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            e.printStackTrace();
        }
        username = qmPreferences.getString("NAME", null);
        membershipNumber = qmPreferences.getString("MEMBERSHIP_NUMBER", null);
        String email = qmPreferences.getString("EMAIL", null);
        String dateOfBirth = qmPreferences.getString("DOB", null);
        String residence = qmPreferences.getString("RESIDENCE", null);
        String nationality = qmPreferences.getString("NATIONALITY", null);
        String imageURL = qmPreferences.getString("IMAGE", null);
        uid = qmPreferences.getString("UID", null);
        qmDatabase = QMDatabase.getInstance(ProfileActivity.this);
        if (imageURL != null && !imageURL.equals("") && !imageURL.equals("0")) {
            profilePic.setBackground(getDrawable(R.drawable.circular_bg));
            GlideApp.with(this)
                    .load(imageURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
        }
        usernameTxt.setText(username);
        membershipNumberTxt.setText(membershipNumber);
        emailTxt.setText(email);
        dobTxt.setText(dateOfBirth);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            try {
                JSONArray m_jArray = new JSONArray(loadJSONFromAsset(language));
                for (int i = 0; i < m_jArray.length(); i++) {
                    JSONObject jo_inside = m_jArray.getJSONObject(i);
                    String key = jo_inside.getString("alpha-2");
                    String value = jo_inside.getString("name");
                    country.put(key, value);
                }
                residenceTxt.setText(country.get(residence));
                nationalityTxt.setText(country.get(nationality));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset(language));
                residenceTxt.setText(obj.getString(residence));
                nationalityTxt.setText(obj.getString(nationality));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        myCardBtn.setOnClickListener(this);
        myCardBtn.setOnTouchListener((v, event) -> {
            Timber.i("My Card button clicked");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    myCardBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        myFavBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    myFavBtn.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        progressBar.setOnClickListener(v -> {
        });
    }

    public void logOutAction() {
        Timber.i("logOutAction()");
        progressBar.setVisibility(View.VISIBLE);
        try {
            token = new DeCryptor().decryptData(QM_ALIAS, this);
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            e.printStackTrace();
        }
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

        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<UserData> call = apiService.logout(language, token);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    Timber.i("logOutAction() - isSuccessful");
                    clearPreference();
                } else {
                    Timber.w("Response not successful %s", getResources().getString(R.string.error_logout));
                    util.showToast(getResources().getString(R.string.error_logout),
                            ProfileActivity.this);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Timber.e("getParkDetailsFromAPI() - onFailure: %s", t.getMessage());
                if (t.getMessage().contains("timeout"))
                    util.showToast(t.getMessage(), ProfileActivity.this);
                else
                    util.showToast(getResources().getString(R.string.check_network), ProfileActivity.this);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void clearPreference() {
        Timber.e("clearPreference()");
        new DeleteBannerTableEnglish(ProfileActivity.this).execute();
        new DeleteRegistratioTable(ProfileActivity.this).execute();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = qmPreferences.edit();
        editor.putString("UID", null);
        editor.putString("MEMBERSHIP_NUMBER", null);
        editor.putString("EMAIL", null);
        editor.putString("DOB", null);
        editor.putString("RESIDENCE", null);
        editor.putString("NATIONALITY", null);
        editor.putString("IMAGE", null);
        editor.putString("NAME", null);
        editor.putString("RSVP", null);
        editor.putString("ACCEPTED", "0");
        editor.putString(QM_ALIAS + QM_ALIAS_KEY_SUFFIX, null);
        editor.putString(QM_ALIAS + QM_ALIAS_ENCRYPTION_SUFFIX, null);
        editor.putString(QM_ALIAS + QM_ALIAS_VECTOR_SUFFIX, null);
        editor.commit();
        Intent navigationIntent = new Intent(this, CulturePassActivity.class);
        navigationIntent.putExtra("IS_LOGOUT", true);
        startActivity(navigationIntent);
        finish();

    }

    public static class DeleteRegistratioTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<ProfileActivity> activityReference;

        DeleteRegistratioTable(ProfileActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("nukeRegistrationTable()");
            activityReference.get().qmDatabase.getUserRegistrationTaleDao().nukeRegistrationTable();
            return null;
        }
    }

    public static class DeleteBannerTableEnglish extends AsyncTask<Void, Void, Void> {
        private WeakReference<ProfileActivity> activityReference;

        DeleteBannerTableEnglish(ProfileActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("nukeBannerTable()");
            activityReference.get().qmDatabase.getHomePageBannerTableDao().nukeBannerTable();
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        Timber.i("onBackPressed()");
        setResultOkSoIntermediateActivityWontBeShown();
        super.onBackPressed();
    }

    private void setResultOkSoIntermediateActivityWontBeShown() {
        Intent intent = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_my_card_btn:
                Timber.i("View my card button clicked");
                CulturePassCardActivity.Companion.newIntent(this, membershipNumber, username, language);
                break;
        }
    }

    public String loadJSONFromAsset(String language) {
        Timber.i("loadJSONFromAsset()");
        String json = null;
        try {
            Timber.i("onBackPressed()");
            InputStream is = this.getAssets().open("countries_" + language + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.profile_page), null);
    }
}
