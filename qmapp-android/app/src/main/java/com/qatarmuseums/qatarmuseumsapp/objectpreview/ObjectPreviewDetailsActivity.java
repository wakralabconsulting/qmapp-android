package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


import com.qatarmuseums.qatarmuseumsapp.R;

public class ObjectPreviewDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview_details);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        closeBtn = (ImageView) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
