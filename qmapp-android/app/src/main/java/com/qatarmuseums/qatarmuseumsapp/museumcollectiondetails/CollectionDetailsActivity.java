package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.AsyncTask;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
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

    @BindView(R.id.details_layout)
    ScrollView detailLyout;

    private Animation zoomOutAnimation;
    private CollectionDetailsAdapter mAdapter;
    private ArrayList<CollectionDetailsList> collectionDetailsList = new ArrayList<>();
    private String categoryId;
    Util util;
    int appLanguage;
    SharedPreferences qmPreferences;
    int collectionDetailRowCount;
    QMDatabase qmDatabase;
    MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish;
    MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        intent = getIntent();
        qmDatabase = QMDatabase.getInstance(CollectionDetailsActivity.this);
        util = new Util();
        categoryId = intent.getStringExtra("MAIN_TITLE");
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        collectionTitle.setText(intent.getStringExtra("MAIN_TITLE"));
        longDescription.setText(intent.getStringExtra("LONG_DESC"));
        mAdapter = new CollectionDetailsAdapter(this, collectionDetailsList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setFocusable(false);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (util.isNetworkAvailable(CollectionDetailsActivity.this))
            getMuseumCollectionDetailFromAPI();
        else
            getMuseumCollectionDetailFromDatabase();
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
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getTempClient().create(APIInterface.class);
        Call<ArrayList<CollectionDetailsList>> call = apiService.getMuseumCollectionDetails(language, categoryId);
        call.enqueue(new Callback<ArrayList<CollectionDetailsList>>() {
            @Override
            public void onResponse(Call<ArrayList<CollectionDetailsList>> call, Response<ArrayList<CollectionDetailsList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        detailLyout.setVisibility(View.VISIBLE);
                        collectionDetailsList.addAll(response.body());
                        removeHtmlTags(collectionDetailsList);
                        mAdapter.notifyDataSetChanged();
                        new CollectionDetailRowCount(CollectionDetailsActivity.this, appLanguage).execute();


                    } else {
                        detailLyout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    detailLyout.setVisibility(View.GONE);
                    noResultFoundLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CollectionDetailsList>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                detailLyout.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumCollectionDetailFromDatabase() {
        if (appLanguage == 1) {
            new RetriveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity.this, appLanguage).execute();
        } else {
            new RetriveMuseumCollectionDetailDataArabic(CollectionDetailsActivity.this, appLanguage).execute();
        }
    }

    public void removeHtmlTags(ArrayList<CollectionDetailsList> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));

        }
    }

    public class CollectionDetailRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;


        CollectionDetailRowCount(CollectionDetailsActivity context, int apiLanguage) {
            activityReference = new WeakReference<>(context);
            if (appLanguage == 1) {
                language = "en";
            } else {
                language = "ar";
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            collectionDetailRowCount = integer;
            if (collectionDetailRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumCollectionDetailDBRowExist(CollectionDetailsActivity.this, language).execute();

            } else {
                //create databse
                new InsertMuseumCollectionDetailListDataToDataBase(CollectionDetailsActivity.this, museumCollectionDetailTableEnglish,
                        museumCollectionDetailTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().getNumberOfRowsArabic();
            }
        }

        public class InsertMuseumCollectionDetailListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
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
                if (collectionDetailsList != null) {
                    if (language.equals("en")) {
                        for (int i = 0; i < collectionDetailsList.size(); i++) {
                            museumCollectionDetailTableEnglish = new MuseumCollectionDetailTableEnglish(collectionDetailsList.get(i).getCategoryId(), collectionDetailsList.get(i).getMainTitle(),
                                    collectionDetailsList.get(i).getAbout(), collectionDetailsList.get(i).getImage1(),
                                    collectionDetailsList.get(i).getImage2(), collectionDetailsList.get(i).getFirstDescription(),
                                    collectionDetailsList.get(i).getSecondDescription(), collectionDetailsList.get(i).getThirdDescription()
                            );
                            activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertEnglishTable(museumCollectionDetailTableEnglish);
                        }
                    } else {
                        for (int i = 0; i < collectionDetailsList.size(); i++) {
                            museumCollectionDetailTableArabic = new MuseumCollectionDetailTableArabic(collectionDetailsList.get(i).getCategoryId(), collectionDetailsList.get(i).getMainTitle(),
                                    collectionDetailsList.get(i).getAbout(), collectionDetailsList.get(i).getImage1(),
                                    collectionDetailsList.get(i).getImage2(), collectionDetailsList.get(i).getFirstDescription(),
                                    collectionDetailsList.get(i).getSecondDescription(), collectionDetailsList.get(i).getThirdDescription()
                            );
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


        public class CheckMuseumCollectionDetailDBRowExist extends AsyncTask<Void, Void, Void> {
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
                if (collectionDetailsList.size() > 0) {
                    if (language.equals("en")) {
                        for (int i = 0; i < collectionDetailsList.size(); i++) {
                            int n = activityReference.get().qmDatabase.getMuseumCollectionDetailDao().checkTitleExistEnglish(
                                    collectionDetailsList.get(i).getMainTitle());
                            if (n > 0) {
                                //updateEnglishTable same id
                                new UpdateMuseumCollectionDetailTable(CollectionDetailsActivity.this, appLanguage, i).execute();

                            } else {
                                //create row with corresponding name
                                museumCollectionDetailTableEnglish = new MuseumCollectionDetailTableEnglish(collectionDetailsList.get(i).getCategoryId(), collectionDetailsList.get(i).getMainTitle(),
                                        collectionDetailsList.get(i).getAbout(), collectionDetailsList.get(i).getImage1(),
                                        collectionDetailsList.get(i).getImage2(), collectionDetailsList.get(i).getFirstDescription(),
                                        collectionDetailsList.get(i).getSecondDescription(), collectionDetailsList.get(i).getThirdDescription()
                                );
                                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertEnglishTable(museumCollectionDetailTableEnglish);

                            }
                        }
                    } else {
                        for (int i = 0; i < collectionDetailsList.size(); i++) {
                            int n = activityReference.get().qmDatabase.getMuseumCollectionDetailDao().checkTitleExistArabic(
                                    collectionDetailsList.get(i).getMainTitle());
                            if (n > 0) {
                                //updateEnglishTable same id
                                new UpdateMuseumCollectionDetailTable(CollectionDetailsActivity.this, appLanguage, i).execute();

                            } else {
                                //create row with corresponding name
                                museumCollectionDetailTableArabic = new MuseumCollectionDetailTableArabic(collectionDetailsList.get(i).getCategoryId(), collectionDetailsList.get(i).getMainTitle(),
                                        collectionDetailsList.get(i).getAbout(), collectionDetailsList.get(i).getImage1(),
                                        collectionDetailsList.get(i).getImage2(), collectionDetailsList.get(i).getFirstDescription(),
                                        collectionDetailsList.get(i).getSecondDescription(), collectionDetailsList.get(i).getThirdDescription()
                                );
                                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertArabicTable(museumCollectionDetailTableArabic);

                            }
                        }
                    }
                }
                return null;
            }
        }

    }


    public class UpdateMuseumCollectionDetailTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        int language;
        int position;

        UpdateMuseumCollectionDetailTable(CollectionDetailsActivity context, int apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language == 1) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().updateMuseumDetailTableEnglish(
                        collectionDetailsList.get(position).getCategoryId(),
                        collectionDetailsList.get(position).getAbout(), collectionDetailsList.get(position).getImage1(),
                        collectionDetailsList.get(position).getImage2(), collectionDetailsList.get(position).getFirstDescription(),
                        collectionDetailsList.get(position).getSecondDescription(), collectionDetailsList.get(position).getThirdDescription(),
                        collectionDetailsList.get(position).getMainTitle());

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getMuseumCollectionDetailDao().updateMuseumDetailTableArabic(
                        collectionDetailsList.get(position).getCategoryId(),
                        collectionDetailsList.get(position).getAbout(), collectionDetailsList.get(position).getImage1(),
                        collectionDetailsList.get(position).getImage2(), collectionDetailsList.get(position).getFirstDescription(),
                        collectionDetailsList.get(position).getSecondDescription(), collectionDetailsList.get(position).getThirdDescription(),
                        collectionDetailsList.get(position).getMainTitle());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }


    public class RetriveMuseumCollectionDetailDataEnglish extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableEnglish>> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        int language;

        RetriveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionDetailTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().
                    getAllDataFromMuseumDetailEnglishTable(categoryId);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionDetailTableEnglish> museumCollectionListTableEnglishes) {
            collectionDetailsList.clear();
            if (museumCollectionListTableEnglishes.size() > 0) {
                for (int i = 0; i < museumCollectionListTableEnglishes.size(); i++) {
                    CollectionDetailsList collectionDetailsList1 = new CollectionDetailsList(museumCollectionListTableEnglishes.get(i).getDetail_title(), museumCollectionListTableEnglishes.get(i).getDetail_about(),
                            museumCollectionListTableEnglishes.get(i).getDetail_image1(),
                            museumCollectionListTableEnglishes.get(i).getDetail_image2(),
                            museumCollectionListTableEnglishes.get(i).getDetail_description1(),
                            museumCollectionListTableEnglishes.get(i).getDetail_description2(),
                            museumCollectionListTableEnglishes.get(i).getDetail_description3(),
                            museumCollectionListTableEnglishes.get(i).getCategory_id());
                    collectionDetailsList.add(i, collectionDetailsList1);

                }
                detailLyout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                detailLyout.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    public class RetriveMuseumCollectionDetailDataArabic extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableArabic>> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        int language;

        RetriveMuseumCollectionDetailDataArabic(CollectionDetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionDetailTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().
                    getAllDataFromMuseumDetailArabicTable(categoryId);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionDetailTableArabic> museumCollectionDetailTableArabics) {
            collectionDetailsList.clear();
            if (museumCollectionDetailTableArabics.size() > 0) {
                for (int i = 0; i < museumCollectionDetailTableArabics.size(); i++) {
                    CollectionDetailsList collectionDetailsList1 = new CollectionDetailsList(museumCollectionDetailTableArabics.get(i).getDetail_title(), museumCollectionDetailTableArabics.get(i).getDetail_about(),
                            museumCollectionDetailTableArabics.get(i).getDetail_image1(),
                            museumCollectionDetailTableArabics.get(i).getDetail_image2(),
                            museumCollectionDetailTableArabics.get(i).getDetail_description1(),
                            museumCollectionDetailTableArabics.get(i).getDetail_description2(),
                            museumCollectionDetailTableArabics.get(i).getDetail_description3(),
                            museumCollectionDetailTableArabics.get(i).getCategory_id());
                    collectionDetailsList.add(i, collectionDetailsList1);

                }
                detailLyout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                detailLyout.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }


}
