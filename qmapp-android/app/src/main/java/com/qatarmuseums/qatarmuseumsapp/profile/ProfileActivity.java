package com.qatarmuseums.qatarmuseumsapp.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.culturepass.AddCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.ReceivedCookiesInterceptor;
import com.qatarmuseums.qatarmuseumsapp.culturepasscard.CulturePassCardActivity;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private View backArrow, logOut;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    TextView usernameTxt, membershipNumberTxt, emailTxt, dobTxt, residenceTxt, nationalityTxt;
    ImageView profilePic, profileEdit;
    View myFavBtn, myCardBtn;
    private SharedPreferences qmPreferences;
    private String token, username, membershipNumber, email, dateOfBirth, residence, nationality,
            imageURL, rsvpAttendance, acceptStatus;
    private SharedPreferences.Editor editor;
    private ProgressBar logoutProgress;
    private int appLanguage;
    String language;
    private Retrofit retrofit;
    HashMap<String, String> country = new HashMap<>();
    private InputStream is;
    private LinearLayout rsvpLayout;
    private SwitchCompat acceptDeclineButton;
    Util util;
    String accepted = "0";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.common_toolbar);
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
        logoutProgress = findViewById(R.id.logout_progress);
        rsvpLayout = findViewById(R.id.rsvp_layout);
        acceptDeclineButton = findViewById(R.id.accept_decline_button);
        util = new Util();
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultOkSoIntermediateActivityWontBeShown();
                onBackPressed();
            }
        });
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
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutAction();
            }
        });
        logOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        logOut.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        token = qmPreferences.getString("TOKEN", null);
        username = qmPreferences.getString("NAME", null);
        membershipNumber = qmPreferences.getString("MEMBERSHIP_NUMBER", null);
        email = qmPreferences.getString("EMAIL", null);
        dateOfBirth = qmPreferences.getString("DOB", null);
        residence = qmPreferences.getString("RESIDENCE", null);
        nationality = qmPreferences.getString("NATIONALITY", null);
        imageURL = qmPreferences.getString("IMAGE", null);
        rsvpAttendance = qmPreferences.getString("RSVP", null);
        if (rsvpAttendance.equals("1")) {
            showDialog();
            rsvpLayout.setVisibility(View.VISIBLE);
        } else {
            rsvpLayout.setVisibility(View.GONE);
        }


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
        if (appLanguage == 1) {
            try {
                JSONArray m_jArry = new JSONArray(loadJSONFromAsset(language));
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
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
        myCardBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myCardBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        myFavBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myFavBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        acceptDeclineButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (accepted .equals("0")) {
                    accepted = "1";
                    util.showCulturalPassAlertDialog(ProfileActivity.this);
                    acceptDeclineButton.setChecked(false);
                } else {
                    accepted = "0";
                    showDeclineDialog();
                }
                return false;
            }
        });

    }

    protected void showDialog() {

        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getLayoutInflater().inflate(R.layout.vip_pop_up, null);
        dialog.setContentView(view);
        View line = (View) view.findViewById(R.id.view);
        Button acceptBtn = (Button) view.findViewById(R.id.acceptbtn);
        Button acceptLaterBtn = (Button) view.findViewById(R.id.accept_later_btn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        view.setVisibility(View.VISIBLE);
        dialogTitle.setVisibility(View.VISIBLE);
        dialogTitle.setText(getResources().getString(R.string.greetings_title));
        acceptBtn.setText(getResources().getString(R.string.accept));
        acceptLaterBtn.setText(getResources().getString(R.string.accept_later));
        dialogContent.setText(getResources().getString(R.string.greetings_content));

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                accepted = "1";
                acceptDeclineButton.setChecked(false);
                dialog.dismiss();
                util.showCulturalPassAlertDialog(ProfileActivity.this);

            }
        });
        acceptLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted = "0";
                acceptDeclineButton.setChecked(true);
                dialog.dismiss();
            }
        });

        dialog.show();
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
        dialogContent.setText(getResources().getString(R.string.decline_content));

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                dialog.dismiss();
                accepted = "0";
                acceptDeclineButton.setChecked(true);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted = "1";
                util.showCulturalPassAlertDialog(ProfileActivity.this);
                acceptDeclineButton.setChecked(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void logOutAction() {
        logoutProgress.setVisibility(View.VISIBLE);
        token = qmPreferences.getString("TOKEN", null);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(interceptor);
        builder.addInterceptor(new AddCookiesInterceptor(this));
        builder.addInterceptor(new ReceivedCookiesInterceptor(this));
        client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.apiBaseUrlSecure)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<UserData> call = apiService.logout(language, token);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    clearPreference();
                } else {
                    new Util().showToast(getResources().getString(R.string.error_logout),
                            ProfileActivity.this);
                }
                logoutProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                new Util().showToast(getResources().getString(R.string.check_network), ProfileActivity.this);
                logoutProgress.setVisibility(View.GONE);
            }
        });
    }

    public void clearPreference() {
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = qmPreferences.edit();
        editor.putString("TOKEN", null);
        editor.putString("MEMBERSHIP_NUMBER", null);
        editor.putString("EMAIL", null);
        editor.putString("DOB", null);
        editor.putString("RESIDENCE", null);
        editor.putString("NATIONALITY", null);
        editor.putString("IMAGE", null);
        editor.putString("NAME", null);
        editor.commit();
        Intent navigationIntent = new Intent(this, CulturePassActivity.class);
        navigationIntent.putExtra("IS_LOGOUT", true);
        startActivity(navigationIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
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
                CulturePassCardActivity.Companion.newIntent(this, membershipNumber, username, language);
                break;
        }
    }

    public String loadJSONFromAsset(String language) {
        String json = null;
        try {
            is = this.getAssets().open("countries_" + language + ".json");
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
}
