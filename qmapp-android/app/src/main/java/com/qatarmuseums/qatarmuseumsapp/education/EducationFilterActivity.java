package com.qatarmuseums.qatarmuseumsapp.education;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

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
    TextView institutionText;
    @BindView(R.id.age_group_text)
    TextView ageGroupText;
    @BindView(R.id.programme_type_text)
    TextView programmeTypeText;
    @BindView(R.id.clear_button)
    Button clearButton;
    @BindView(R.id.filter_button)
    Button filterButton;
    @BindView(R.id.up_arrow)
    ImageView upArrow;
    @BindView(R.id.down_arrow)
    ImageView downArrow;
    @BindView(R.id.close_view)
    ImageView closeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_filter);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.filter_activity_tittle));

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



    }
}
