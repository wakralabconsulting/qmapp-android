package com.qatarmuseums.qatarmuseumsapp.education;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_filter);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.filter));


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
        institutions.add("Any Institution");
        institutions.add("Years of Cultural Education");
        institutions.add("Public Art Education");
        institutions.add("MIA Education");
        institutions.add("Mathaf Education");

        age = new ArrayList<String>();
        age.add("Any Age Group");
        age.add("A");
        age.add("B");
        age.add("C");
        age.add("D");

        programmes = new ArrayList<String>();
        programmes.add("Any Programme");
        programmes.add("Programme 1");
        programmes.add("Programme 2");
        programmes.add("Programme 3");
        programmes.add("Programme 4");

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
                setUpInstituteSpinner();
                setUpAgeSpinner();
                setUpProgrammeSpinner();
                instituteLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                ageGroupLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
                programmeLayout.setBackground(getResources().getDrawable(R.drawable.rectangular_plain_background_layout));
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setUpInstituteSpinner() {

        final ArrayAdapter<String> institutionDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, institutions);

        // Drop down layout style - list view with radio button
        institutionDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        institutionText.setAdapter(institutionDataAdapter);

    }

    private void setUpAgeSpinner() {
        ArrayAdapter<String> ageDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, age);

        // Drop down layout style - list view with radio button
        ageDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        ageGroupText.setAdapter(ageDataAdapter);
    }

    private void setUpProgrammeSpinner() {
        ArrayAdapter<String> programmeDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, programmes);

        // Drop down layout style - list view with radio button
        programmeDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        programmeTypeText.setAdapter(programmeDataAdapter);
    }
}
