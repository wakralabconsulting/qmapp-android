package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CollectionDetailsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_close)
    ImageView toolbarClose;
    @BindView(R.id.collection_title)
    TextView collectionTitle;
    @BindView(R.id.long_description)
    TextView longDescription;
    @BindView(R.id.details_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.collection_share)
    ImageView shareIcon;
    Intent intent;
    @BindView(R.id.collection_favourite)
    ImageView favIcon;
    private Animation zoomOutAnimation;
    private CollectionDetailsAdapter mAdapter;
    private List<CollectionDetailsList> collectionDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        intent = getIntent();
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        collectionTitle.setText( intent.getStringExtra("MAIN_TITLE"));
        mAdapter = new CollectionDetailsAdapter(this,collectionDetailsList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        prepareCollectionDetailData();
        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        favIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        favIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });



    }


    public void prepareCollectionDetailData(){
        CollectionDetailsList cdl = new CollectionDetailsList("LUSTERWARE APOTHECARY JAR",
                "Mamluk, Syria, 14th century","image1","image2"," This jar was probably produced for the celebrated Damascene hospital of Nur al-Din Mahmud ibnZangi.",
                "The inscription around the neck says it was made for the hospital ‘of Nuri’ and the ‘fleur de lis’ design was the motif of Nur al-Din.",
                "The other inscriptions read ‘water lily’, suggesting what was contained within the jar, undoubtedly a pharmaceutical compound or preparation of the plant."
        );
        collectionDetailsList.add(cdl);
        cdl = new CollectionDetailsList("LUSTERWARE APOTHECARY JAR",
                "Mamluk, Syria, 14th century","image1","image2"," This jar was probably produced for the celebrated Damascene hospital of Nur al-Din Mahmud ibnZangi.",
                "The inscription around the neck says it was made for the hospital ‘of Nuri’ and the ‘fleur de lis’ design was the motif of Nur al-Din.",
                "The other inscriptions read ‘water lily’, suggesting what was contained within the jar, undoubtedly a pharmaceutical compound or preparation of the plant."
        );
        collectionDetailsList.add(cdl);
        cdl = new CollectionDetailsList("LUSTERWARE APOTHECARY JAR",
                "Mamluk, Syria, 14th century","image1","image2"," This jar was probably produced for the celebrated Damascene hospital of Nur al-Din Mahmud ibnZangi.",
                "The inscription around the neck says it was made for the hospital ‘of Nuri’ and the ‘fleur de lis’ design was the motif of Nur al-Din.",
                "The other inscriptions read ‘water lily’, suggesting what was contained within the jar, undoubtedly a pharmaceutical compound or preparation of the plant."
        );
        collectionDetailsList.add(cdl);
        mAdapter.notifyDataSetChanged();
    }
}
