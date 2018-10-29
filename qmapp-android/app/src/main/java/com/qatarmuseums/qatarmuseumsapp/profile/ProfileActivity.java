package com.qatarmuseums.qatarmuseumsapp.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.LoginData;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View backArrow, logOut;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    TextView usernameTxt, membershipNumberTxt, emailTxt, dobTxt, residenceTxt, nationalityTxt;
    ImageView profilePic, profileEdit;
    View myFavBtn, myCardBtn;
    private SharedPreferences qmPreferences;
    private String token, username, membershipNumber, email, dateOfBirth, residence, nationality,
            imageURL, qatar, museum;
    private SharedPreferences.Editor editor;
    private ProgressBar logoutProgress;

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
        token = qmPreferences.getString("TOKEN", null);
        qatar = qmPreferences.getString("QATAR", null);
        museum = qmPreferences.getString("MUSEUM", null);
        username = qmPreferences.getString("NAME", null);
        membershipNumber = qmPreferences.getString("MEMBERSHIP_NUMBER", null);
        email = qmPreferences.getString("EMAIL", null);
        dateOfBirth = qmPreferences.getString("DOB", null);
        residence = qmPreferences.getString("RESIDENCE", null);
        nationality = qmPreferences.getString("NATIONALITY", null);
        imageURL = qmPreferences.getString("IMAGE", null);
        if (imageURL != null && !imageURL.equals("") && !imageURL.equals("0"))
            GlideApp.with(this)
                    .load(imageURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
        usernameTxt.setText(username);
        membershipNumberTxt.setText(membershipNumber);
        emailTxt.setText(email);
        dobTxt.setText(dateOfBirth);
        residenceTxt.setText(residence);
        nationalityTxt.setText(nationality);

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

    }

    public void logOutAction() {
        logoutProgress.setVisibility(View.VISIBLE);
        APIInterface apiService = APIClient.getClientSecure().create(APIInterface.class);
        Call<UserData> call = apiService.logout(token, new LoginData(qatar, museum));
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    clearPreference();
                } else {
                    new Util().showToast(getResources().getString(R.string.error_logout), ProfileActivity.this);
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
}
