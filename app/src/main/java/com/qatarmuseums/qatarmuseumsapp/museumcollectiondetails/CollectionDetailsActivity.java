package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBar;
    @BindView(R.id.no_result_layout)
    TextView noResultFoundLayout;
    @BindView(R.id.retry_layout)
    LinearLayout retryLayout;
    @BindView(R.id.retry_btn)
    Button retryButton;

    @BindView(R.id.details_layout)
    NestedScrollView detailLyout;

    private Animation zoomOutAnimation;
    private CollectionDetailsAdapter mAdapter;
    private ArrayList<CollectionDetailsList> collectionDetailsList = new ArrayList<>();
    private String categoryName;
    Util util;
    String appLanguage;
    int collectionDetailRowCount;
    QMDatabase qmDatabase;
    MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish;
    MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        intent = getIntent();
        qmDatabase = QMDatabase.getInstance(CollectionDetailsActivity.this);
        util = new Util();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        categoryName = intent.getStringExtra("MAIN_TITLE");
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        collectionTitle.setText(categoryName);
        longDescription.setText(intent.getStringExtra("LONG_DESC"));
        mAdapter = new CollectionDetailsAdapter(this, collectionDetailsList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setFocusable(false);
        appLanguage = LocaleManager.getLanguage(this);
        if (util.isNetworkAvailable(CollectionDetailsActivity.this))
            getMuseumCollectionDetailFromAPI();
        else
            getMuseumCollectionDetailFromDatabase();

        retryButton.setOnClickListener(v -> {
            getMuseumCollectionDetailFromAPI();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        retryButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
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


    public void getMuseumCollectionDetailFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<CollectionDetailsList>> call = apiService.getMuseumCollectionDetails(appLanguage, categoryName);
        call.enqueue(new Callback<ArrayList<CollectionDetailsList>>() {
            @Override
            public void onResponse(Call<ArrayList<CollectionDetailsList>> call, Response<ArrayList<CollectionDetailsList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        detailLyout.setVisibility(View.VISIBLE);
                        collectionDetailsList.addAll(response.body());
                        removeHtmlTags(collectionDetailsList);
                        mAdapter.notifyDataSetChanged();
                        new CollectionDetailRowCount(CollectionDetailsActivity.this).execute();


                    } else {
                        detailLyout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    detailLyout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CollectionDetailsList>> call, Throwable t) {
                detailLyout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumCollectionDetailFromDatabase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetriveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity.this).execute();
        } else {
            new RetriveMuseumCollectionDetailDataArabic(CollectionDetailsActivity.this).execute();
        }
    }

    public void removeHtmlTags(ArrayList<CollectionDetailsList> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));

        }
    }

    public static class CollectionDetailRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CollectionDetailsActivity> activityReference;

        CollectionDetailRowCount(CollectionDetailsActivity context) {
            activityReference = new WeakReference<>(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().collectionDetailRowCount = integer;
            if (activityReference.get().collectionDetailRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumCollectionDetailDBRowExist(activityReference.get(),
                        activityReference.get().appLanguage).execute();

            } else {
                //create databse
                new InsertMuseumCollectionDetailListDataToDataBase(activityReference.get(),
                        activityReference.get().museumCollectionDetailTableEnglish,
                        activityReference.get().museumCollectionDetailTableArabic,
                        activityReference.get().appLanguage).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (activityReference.get().appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().getNumberOfRowsArabic();
            }
        }

        public static class InsertMuseumCollectionDetailListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
            private WeakReference<CollectionDetailsActivity> activityReference;
            private MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish;
            private MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic;
            String language;

            InsertMuseumCollectionDetailListDataToDataBase(CollectionDetailsActivity context,
                                                           MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish,
                                                           MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic, String appLanguage) {
                activityReference = new WeakReference<>(context);
                this.museumCollectionDetailTableEnglish = museumCollectionDetailTableEnglish;
                this.museumCollectionDetailTableArabic = museumCollectionDetailTableArabic;
                this.language = appLanguage;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (activityReference.get().collectionDetailsList != null) {
                    if (language.equals("en")) {
                        for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                            museumCollectionDetailTableEnglish = new MuseumCollectionDetailTableEnglish(
                                    activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                                    activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                                    activityReference.get().collectionDetailsList.get(i).getImage1(),
                                    activityReference.get().collectionDetailsList.get(i).getAbout());
                            activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertEnglishTable(museumCollectionDetailTableEnglish);
                        }
                    } else {
                        for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                            museumCollectionDetailTableArabic = new MuseumCollectionDetailTableArabic(
                                    activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                                    activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                                    activityReference.get().collectionDetailsList.get(i).getImage1(),
                                    activityReference.get().collectionDetailsList.get(i).getAbout());
                            activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertArabicTable(museumCollectionDetailTableArabic);
                        }
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

            }
        }


        public static class CheckMuseumCollectionDetailDBRowExist extends AsyncTask<Void, Void, Void> {
            private WeakReference<CollectionDetailsActivity> activityReference;
            private MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish;
            private MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic;
            String language;

            CheckMuseumCollectionDetailDBRowExist(CollectionDetailsActivity context, String apiLanguage) {
                activityReference = new WeakReference<>(context);
                language = apiLanguage;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (activityReference.get().collectionDetailsList.size() > 0) {
                    if (language.equals("en")) {
                        for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                            int n = activityReference.get().qmDatabase.getMuseumCollectionDetailDao().checkTitleExistEnglish(
                                    activityReference.get().collectionDetailsList.get(i).getMainTitle());
                            if (n > 0) {
                                //updateEnglishTable same id
                                new UpdateMuseumCollectionDetailTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                            } else {
                                //create row with corresponding name
                                museumCollectionDetailTableEnglish = new MuseumCollectionDetailTableEnglish(
                                        activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                                        activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                                        activityReference.get().collectionDetailsList.get(i).getImage1(),
                                        activityReference.get().collectionDetailsList.get(i).getAbout());
                                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertEnglishTable(museumCollectionDetailTableEnglish);

                            }
                        }
                    } else {
                        for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                            int n = activityReference.get().qmDatabase.getMuseumCollectionDetailDao().checkTitleExistArabic(
                                    activityReference.get().collectionDetailsList.get(i).getMainTitle());
                            if (n > 0) {
                                //updateEnglishTable same id
                                new UpdateMuseumCollectionDetailTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                            } else {
                                //create row with corresponding name
                                museumCollectionDetailTableArabic = new MuseumCollectionDetailTableArabic(
                                        activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                                        activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                                        activityReference.get().collectionDetailsList.get(i).getImage1(),
                                        activityReference.get().collectionDetailsList.get(i).getAbout());
                                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertArabicTable(museumCollectionDetailTableArabic);

                            }
                        }
                    }
                }
                return null;
            }
        }

    }


    public static class UpdateMuseumCollectionDetailTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;
        int position;

        UpdateMuseumCollectionDetailTable(CollectionDetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().updateMuseumDetailTableEnglish(
                        activityReference.get().collectionDetailsList.get(position).getCategoryName(),
                        activityReference.get().collectionDetailsList.get(position).getImage1(),
                        activityReference.get().collectionDetailsList.get(position).getAbout(),
                        activityReference.get().collectionDetailsList.get(position).getMainTitle());

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().updateMuseumDetailTableArabic(
                        activityReference.get().collectionDetailsList.get(position).getCategoryName(),
                        activityReference.get().collectionDetailsList.get(position).getImage1(),
                        activityReference.get().collectionDetailsList.get(position).getAbout(),
                        activityReference.get().collectionDetailsList.get(position).getMainTitle());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }


    public static class RetriveMuseumCollectionDetailDataEnglish extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableEnglish>> {
        private WeakReference<CollectionDetailsActivity> activityReference;

        RetriveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionDetailTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().
                    getAllDataFromMuseumDetailEnglishTable(activityReference.get().categoryName);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionDetailTableEnglish> museumCollectionListTableEnglishes) {
            activityReference.get().collectionDetailsList.clear();
            if (museumCollectionListTableEnglishes.size() > 0) {
                for (int i = 0; i < museumCollectionListTableEnglishes.size(); i++) {
                    CollectionDetailsList collectionDetailsList1 = new CollectionDetailsList(
                            museumCollectionListTableEnglishes.get(i).getDetail_title(),
                            museumCollectionListTableEnglishes.get(i).getDetail_image1(),
                            museumCollectionListTableEnglishes.get(i).getDetail_about(),
                            museumCollectionListTableEnglishes.get(i).getCategory_id());
                    activityReference.get().collectionDetailsList.add(i, collectionDetailsList1);

                }
                activityReference.get().detailLyout.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().detailLyout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    public static class RetriveMuseumCollectionDetailDataArabic extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableArabic>> {
        private WeakReference<CollectionDetailsActivity> activityReference;

        RetriveMuseumCollectionDetailDataArabic(CollectionDetailsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionDetailTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().
                    getAllDataFromMuseumDetailArabicTable(activityReference.get().categoryName);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionDetailTableArabic> museumCollectionDetailTableArabics) {
            activityReference.get().collectionDetailsList.clear();
            if (museumCollectionDetailTableArabics.size() > 0) {
                for (int i = 0; i < museumCollectionDetailTableArabics.size(); i++) {
                    CollectionDetailsList collectionDetailsList1 = new CollectionDetailsList(museumCollectionDetailTableArabics.get(i).getDetail_title(),
                            museumCollectionDetailTableArabics.get(i).getDetail_image1(),
                            museumCollectionDetailTableArabics.get(i).getDetail_about(),
                            museumCollectionDetailTableArabics.get(i).getCategory_id());
                    activityReference.get().collectionDetailsList.add(i, collectionDetailsList1);

                }
                activityReference.get().detailLyout.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().detailLyout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.collection_details_page), null);
    }
}
