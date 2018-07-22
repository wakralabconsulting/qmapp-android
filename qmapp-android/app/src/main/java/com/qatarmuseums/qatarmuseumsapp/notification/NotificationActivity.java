package com.qatarmuseums.qatarmuseumsapp.notification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.qatarmuseums.qatarmuseumsapp.commonactivity.CommonListAdapter;
import com.qatarmuseums.qatarmuseumsapp.commonactivity.CommonModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<NotificationModel> models = new ArrayList<>();
    private NotificationListAdapter mAdapter;
    private ImageView backArrow;
    private Animation zoomOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction);
        toolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        recyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);

        mAdapter = new NotificationListAdapter(this, models);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        prepareNotificationData();
    }

    private void prepareNotificationData() {
        NotificationModel model = new NotificationModel("1", "Check the new event at MIA!");
        models.add(model);
        model = new NotificationModel("2", "German Design Exhibition is now live! Check it out now.");
        models.add(model);
        model = new NotificationModel("3", "Check our new National Museum Teachers workshop here.");
        models.add(model);
        model = new NotificationModel("4", "Check the new event at Zubara Fort.");
        models.add(model);
        model = new NotificationModel("5", "Culture Pass Notification.");
        models.add(model);
        model = new NotificationModel("6", "Checkout new collections at MIA.");
        models.add(model);
        model = new NotificationModel("7", "Checkout new collections at MIA.");
        models.add(model);
        model = new NotificationModel("8", "Check the new event at Zubara Fort.");
        models.add(model);
        model = new NotificationModel("9", "Culture Pass Notification.");
        models.add(model);

        // TO show recently added item first
        Collections.reverse(models);

        mAdapter.notifyDataSetChanged();
    }
}
