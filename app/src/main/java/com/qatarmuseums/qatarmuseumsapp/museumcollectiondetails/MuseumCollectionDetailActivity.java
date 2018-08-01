package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MuseumCollectionDetailActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_close)
    ImageView toolbarClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_collection_detail);
        ButterKnife.bind(this);
        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
