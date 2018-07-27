package com.qatarmuseums.qatarmuseumsapp.museum;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MuseumActivity extends BaseActivity implements View.OnClickListener {
    private List<MuseumHScrollModel> museumHScrollModelList = new ArrayList<>();
    private MuseumHorizontalScrollViewAdapter museumHorizontalScrollViewAdapter;
    @BindView(R.id.horizontal_scrol_previous_icon)
    ImageView scrollBarPreviousIcon;
    LinearLayoutManager recyclerviewLayoutManager;
    @BindView(R.id.horizontal_scrol_next_icon)
    ImageView scrollBarNextIcon;
    @BindView(R.id.settings_page_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        ButterKnife.bind(this);
        setToolbarForMuseumActivity();
        setupClickListeners();
        museumHorizontalScrollViewAdapter = new MuseumHorizontalScrollViewAdapter(this, museumHScrollModelList);
        recyclerviewLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(recyclerviewLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(museumHorizontalScrollViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = 0;
                lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView
                        .getLayoutManager()).findLastVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == museumHScrollModelList.size() - 1) {
                    showRightArrow();
                } else if (lastCompletelyVisibleItemPosition == 1) {

                    showLeftArrow();

                } else {
//                    showBothArrows();
                }

            }
        });
//        SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
//        snapHelperStart.attachToRecyclerView(recyclerView);
        prepareRecyclerViewData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.horizontal_scrol_previous_icon:
                // scroll view previous action
                recyclerView.getLayoutManager().scrollToPosition(recyclerviewLayoutManager.findFirstVisibleItemPosition() - 1);
                break;

//            case R.id.horizontal_scrol_about_icon:
//                // scroll viewm about action
//                break;
//
//            case R.id.horizontal_scrol_tour_guide_icon:
//                // scroll viewm tour guide action
//                break;
//
//            case R.id.horizontal_scrol_exhibition_icon:
//                // scroll viewm exhibition action
//                break;
//
//            case R.id.horizontal_collection_icon:
//                // scroll view collection action
//                break;
//
//            case R.id.horizontal_scrol_parks_icon:
//                // scroll viewm parks action
//                break;
//
//            case R.id.horizontal_scrol_dining_icon:
//                // scroll viewm dining action
//                break;

            case R.id.horizontal_scrol_next_icon:
                // scroll viewm next action
                recyclerView.getLayoutManager().scrollToPosition(recyclerviewLayoutManager.findLastVisibleItemPosition() + 1);
                break;

            default:
                break;


        }
    }

    public void setupClickListeners() {
        scrollBarPreviousIcon.setOnClickListener(this);
        scrollBarNextIcon.setOnClickListener(this);


    }

    public void showBothArrows() {
        scrollBarNextIcon.setVisibility(View.VISIBLE);
        scrollBarPreviousIcon.setVisibility(View.VISIBLE);
    }

    public void showLeftArrow() {
        scrollBarNextIcon.setVisibility(View.GONE);
        scrollBarPreviousIcon.setVisibility(View.VISIBLE);

    }


    public void showRightArrow() {
        scrollBarNextIcon.setVisibility(View.VISIBLE);
        scrollBarPreviousIcon.setVisibility(View.GONE);
    }

    public void prepareRecyclerViewData() {
        MuseumHScrollModel model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_about_text), R.drawable.about_icon);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_tour_guide_text), R.drawable.audio_circle);
        museumHScrollModelList.add(model);
        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_exhibition_text), R.drawable.exhibition_black);
        museumHScrollModelList.add(model);

        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.museum_collection_text), R.drawable.collections);
        museumHScrollModelList.add(model);


        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_parks_text), R.drawable.park_black);
        museumHScrollModelList.add(model);


        model = new MuseumHScrollModel(this,
                getResources().getString(R.string.sidemenu_dining_text), R.drawable.dining);
        museumHScrollModelList.add(model);
        museumHorizontalScrollViewAdapter.notifyDataSetChanged();
    }

}
