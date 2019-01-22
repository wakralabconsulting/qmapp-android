package com.qatarmuseums.qatarmuseumsapp.createaccount;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Spinner titleSpinner, residenceSpinner, nationalitySpinner;
    private TextInputLayout titleSpinnerLayout;
    private List<String> titles, countries;
    private ArrayAdapter<String> titleAdapter, countryAdapter;
    private View createAccountButton, closeButton;
    private Animation zoomOutAnimation, iconZoomOutAnimation;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        closeButton = findViewById(R.id.close_btn);
        usernameEditText = findViewById(R.id.username_edit_text);
        titleSpinnerLayout = findViewById(R.id.title_input_layout);
        titleSpinner = findViewById(R.id.title_spinner);
        residenceSpinner = findViewById(R.id.residence_spinner);
        nationalitySpinner = findViewById(R.id.nationality_spinner);
        createAccountButton = findViewById(R.id.create_account_btn);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        // Spinner Drop down elements
        titles = new ArrayList<String>();
        titles.add("MR");
        titles.add("MS");
        titles.add("MRS");
        titles.add("MISS");

        countries = new ArrayList<String>();
        countries.add("QATAR");
        countries.add("INDIA");

        // Creating adapter for spinner
        titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);

        // Drop down layout style - list view with radio button
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        titleSpinner.setAdapter(titleAdapter);
        residenceSpinner.setAdapter(countryAdapter);
        nationalitySpinner.setAdapter(countryAdapter);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Util().showComingSoonDialog(CreateAccountActivity.this, R.string.coming_soon_content);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        createAccountButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        createAccountButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        closeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        closeButton.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }
}
