package com.qatarmuseums.qatarmuseumsapp.education;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qatarmuseums.qatarmuseumsapp.education.EducationCalendarActivity.AGEGROUPPREFS;
import static com.qatarmuseums.qatarmuseumsapp.education.EducationCalendarActivity.FILTERPREFS;
import static com.qatarmuseums.qatarmuseumsapp.education.EducationCalendarActivity.INSTITUTEPREFS;
import static com.qatarmuseums.qatarmuseumsapp.education.EducationCalendarActivity.PROGRAMMEPREFS;

public class EducationFilterActivity extends AppCompatActivity {

    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_close)
    ImageView toolbarClose;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.institution_text)
    Spinner institutionText;
    @BindView(R.id.age_group_text)
    Spinner ageGroupText;
    @BindView(R.id.programme_type_text)
    Spinner programmeTypeText;
    @BindView(R.id.clear_button)
    Button clearButton;
    @BindView(R.id.filter_button)
    Button filterButton;
    @BindView(R.id.institute_layout)
    FrameLayout instituteLayout;
    @BindView(R.id.age_group_layout)
    FrameLayout ageGroupLayout;
    @BindView(R.id.programme_type_layout)
    FrameLayout programmeLayout;
    @BindView(R.id.institute_image)
    ImageView instituteImage;
    @BindView(R.id.age_group_image)
    ImageView ageGroupImage;
    @BindView(R.id.programme_type_image)
    ImageView programmeTypeImage;
    int isFillInstitute = 0;
    int isFillAge = 0;
    int isFillProgramme = 0;
    ArrayList<String> institutions;
    ArrayList<String> age;
    ArrayList<String> programmes;
    String institutionItem, ageGroupItem, programmeTypeItem;
    Intent navigationIntent;
    SharedPreferences.Editor editor;
    SharedPreferences sharedfilterpreferences;
    int institutionPosition = 0, ageGroupPosition = 0, programmeTypePosition = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_filter);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.filter));

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedfilterpreferences = getSharedPreferences(FILTERPREFS, Context.MODE_PRIVATE);

        sharedfilterpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        institutionPosition = sharedfilterpreferences.getInt(INSTITUTEPREFS, 0);
        ageGroupPosition = sharedfilterpreferences.getInt(AGEGROUPPREFS, 0);
        programmeTypePosition = sharedfilterpreferences.getInt(PROGRAMMEPREFS, 0);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        toolbarClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        toolbarClose.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Spinner Drop down elements
        institutions = new ArrayList<String>();
        institutions.add(getResources().getString(R.string.any));
        institutions.add(getResources().getString(R.string.years));
        institutions.add(getResources().getString(R.string.public_art));
        institutions.add(getResources().getString(R.string.cultural));
        institutions.add(getResources().getString(R.string.mia_education));
        institutions.add(getResources().getString(R.string.mathaf));
        institutions.add(getResources().getString(R.string.national));
        institutions.add(getResources().getString(R.string.qosm));
        institutions.add(getResources().getString(R.string.family));

        final HashMap<String, String> instituteMap = new HashMap<String, String>();
        instituteMap.put(getResources().getString(R.string.any), "All");
        instituteMap.put(getResources().getString(R.string.years), "Years");
        instituteMap.put(getResources().getString(R.string.public_art), "Public");
        instituteMap.put(getResources().getString(R.string.cultural), "Cultural");
        instituteMap.put(getResources().getString(R.string.mia_education), "MIA");
        instituteMap.put(getResources().getString(R.string.mathaf), "Mathaf");
        instituteMap.put(getResources().getString(R.string.national), "National");
        instituteMap.put(getResources().getString(R.string.qosm), "321");
        instituteMap.put(getResources().getString(R.string.family), "Family");

        age = new ArrayList<String>();
        age.add(getResources().getString(R.string.any));
        age.add(getResources().getString(R.string.all_ages));
        age.add(getResources().getString(R.string.teachers));
        age.add(getResources().getString(R.string.nursery));
        age.add(getResources().getString(R.string.early));
        age.add(getResources().getString(R.string.primary));
        age.add(getResources().getString(R.string.preparatory));
        age.add(getResources().getString(R.string.secondary));
        age.add(getResources().getString(R.string.college));
        age.add(getResources().getString(R.string.youth));
        age.add(getResources().getString(R.string.adults));
        age.add(getResources().getString(R.string.seniors));
        age.add(getResources().getString(R.string.families));
        age.add(getResources().getString(R.string.special_needs));

        final HashMap<String, String> ageMap = new HashMap<String, String>();
        ageMap.put(getResources().getString(R.string.any), "All");
        ageMap.put(getResources().getString(R.string.all_ages), "Allages");
        ageMap.put(getResources().getString(R.string.teachers), "Nursery");
        ageMap.put(getResources().getString(R.string.nursery), "Preschool");
        ageMap.put(getResources().getString(R.string.early), "EarlyPrimary");
        ageMap.put(getResources().getString(R.string.primary), "Primary");
        ageMap.put(getResources().getString(R.string.preparatory), "Preparatory");
        ageMap.put(getResources().getString(R.string.secondary), "Secondary");
        ageMap.put(getResources().getString(R.string.college), "College");
        ageMap.put(getResources().getString(R.string.youth), "Youth");
        ageMap.put(getResources().getString(R.string.adults), "Adults");
        ageMap.put(getResources().getString(R.string.seniors), "Seniors");
        ageMap.put(getResources().getString(R.string.families), "Families");
        ageMap.put(getResources().getString(R.string.special_needs), "Special");

        programmes = new ArrayList<String>();
        programmes.add(getResources().getString(R.string.any));
        programmes.add(getResources().getString(R.string.art));
        programmes.add(getResources().getString(R.string.field));
        programmes.add(getResources().getString(R.string.gallery));
        programmes.add(getResources().getString(R.string.lecture));
        programmes.add(getResources().getString(R.string.photography));
        programmes.add(getResources().getString(R.string.reading));
        programmes.add(getResources().getString(R.string.research));
        programmes.add(getResources().getString(R.string.workshop));

        final HashMap<String, String> programmeMap = new HashMap<String, String>();
        programmeMap.put(getResources().getString(R.string.any), "All");
        programmeMap.put(getResources().getString(R.string.art), "Art");
        programmeMap.put(getResources().getString(R.string.field), "Field");
        programmeMap.put(getResources().getString(R.string.gallery), "Gallery");
        programmeMap.put(getResources().getString(R.string.lecture), "Lecture");
        programmeMap.put(getResources().getString(R.string.photography), "Photography");
        programmeMap.put(getResources().getString(R.string.reading), "Reading");
        programmeMap.put(getResources().getString(R.string.research), "Research");
        programmeMap.put(getResources().getString(R.string.workshop), "Workshop");


        setUpInstituteSpinner();
        setUpAgeSpinner();
        setUpProgrammeSpinner();


        institutionText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isFillInstitute == 0) {
                    instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    isFillInstitute = 1;

                } else {
                    instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_grey_background_layout));
                    ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    isFillInstitute = 1;
                    isFillAge = 1;
                    isFillProgramme = 1;
                }
                return false;
            }
        });
        institutionText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                String item = adapterView.getItemAtPosition(position).toString();
                institutionItem = instituteMap.get(item);
                institutionPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ageGroupText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isFillAge == 0) {
                    ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    isFillAge = 1;
                } else {
                    instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_grey_background_layout));
                    programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    isFillInstitute = 1;
                    isFillAge = 1;
                    isFillProgramme = 1;
                }
                return false;
            }
        });
        ageGroupText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                String item = adapterView.getItemAtPosition(position).toString();
                ageGroupItem = ageMap.get(item);
                ageGroupPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        programmeTypeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isFillProgramme == 0) {
                    programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    isFillProgramme = 1;
                } else {
                    instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                    programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_grey_background_layout));
                    isFillInstitute = 1;
                    isFillAge = 1;
                    isFillProgramme = 1;
                }
                return false;
            }
        });
        programmeTypeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                String item = adapterView.getItemAtPosition(position).toString();
                programmeTypeItem = programmeMap.get(item);
                programmeTypePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFillInstitute = 0;
                isFillAge = 0;
                isFillProgramme = 0;
                institutionPosition = 0;
                ageGroupPosition = 0;
                programmeTypePosition = 0;
                setUpInstituteSpinner();
                setUpAgeSpinner();
                setUpProgrammeSpinner();
                setPreferences();
                instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationIntent = new Intent(EducationFilterActivity.this, EducationCalendarActivity.class);
                navigationIntent.putExtra("INSTITUTION", institutionItem);
                navigationIntent.putExtra("AGE_GROUP", ageGroupItem);
                navigationIntent.putExtra("PROGRAMME_TYPE", programmeTypeItem);
                setPreferences();
                startActivity(navigationIntent);
                finish();
            }
        });

    }

    private void setPreferences() {
        editor = sharedfilterpreferences.edit();
        editor.putInt(INSTITUTEPREFS, institutionPosition);
        editor.putInt(AGEGROUPPREFS, ageGroupPosition);
        editor.putInt(PROGRAMMEPREFS, programmeTypePosition);
        editor.commit();
    }

    private void setUpInstituteSpinner() {

        final ArrayAdapter<String> institutionDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, institutions);

        // Drop down layout style - list view with radio button
        institutionDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        institutionText.setAdapter(institutionDataAdapter);
        institutionText.setSelection(institutionPosition);

    }

    private void setUpAgeSpinner() {
        ArrayAdapter<String> ageDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, age);

        // Drop down layout style - list view with radio button
        ageDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        ageGroupText.setAdapter(ageDataAdapter);
        ageGroupText.setSelection(ageGroupPosition);
    }

    private void setUpProgrammeSpinner() {
        ArrayAdapter<String> programmeDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, programmes);

        // Drop down layout style - list view with radio button
        programmeDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        programmeTypeText.setAdapter(programmeDataAdapter);
        programmeTypeText.setSelection(programmeTypePosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.education_filter_page), null);
    }
}
