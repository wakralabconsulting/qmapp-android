package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.collection_title_divider)
    View collectionTitleDivider;
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
    NestedScrollView detailLayout;

    private Animation zoomOutAnimation;
    private CollectionDetailsAdapter mAdapter;
    private NMoQParkListDetailsAdapter mAdapterPark;
    private ArrayList<CollectionDetailsList> collectionDetailsList = new ArrayList<>();
    private ArrayList<NMoQParkListDetails> nMoQParkListDetails = new ArrayList<>();
    private String categoryName, nid;
    Util util;
    String appLanguage;
    int collectionDetailRowCount;
    QMDatabase qmDatabase;
    MuseumCollectionDetailTableEnglish museumCollectionDetailTableEnglish;
    MuseumCollectionDetailTableArabic museumCollectionDetailTableArabic;
    private NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish;
    private NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic;

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
        categoryName = intent.getStringExtra("MAIN_TITLE");
        nid = intent.getStringExtra("NID");
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.sidemenu_parks_text))) {
            mAdapterPark = new NMoQParkListDetailsAdapter(this, nMoQParkListDetails);
            collectionTitle.setVisibility(View.GONE);
            collectionTitleDivider.setVisibility(View.GONE);
            longDescription.setVisibility(View.GONE);
            recyclerView.setAdapter(mAdapterPark);
        } else {
            mAdapter = new CollectionDetailsAdapter(this, collectionDetailsList);
            collectionTitle.setText(categoryName);
            longDescription.setText(intent.getStringExtra("LONG_DESC"));
            recyclerView.setAdapter(mAdapter);
        }
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setFocusable(false);
        appLanguage = LocaleManager.getLanguage(this);
        if (util.isNetworkAvailable(CollectionDetailsActivity.this)) {
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.sidemenu_parks_text)))
                getNMoQParkDetailsFromAPI(nid);
            else
                getMuseumCollectionDetailFromAPI();
        } else {
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.sidemenu_parks_text)))
                getNMoQParkListDetailsFromDataBase(nid);
            else
                getMuseumCollectionDetailFromDatabase();
        }
        retryButton.setOnClickListener(v -> {
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.sidemenu_parks_text)))
                getNMoQParkDetailsFromAPI(nid);
            else
                getMuseumCollectionDetailFromAPI();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnClickListener(view -> finish());
        favIcon.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    favIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        shareIcon.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shareIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });


    }

    private void getNMoQParkDetailsFromAPI(String nid) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQParkListDetails>> call = apiService.getNMoQParkListDetails(appLanguage, nid);
        call.enqueue(new Callback<ArrayList<NMoQParkListDetails>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQParkListDetails>> call,
                                   @NonNull Response<ArrayList<NMoQParkListDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        detailLayout.setVisibility(View.VISIBLE);
                        nMoQParkListDetails.addAll(response.body());
                        removeParkHtmlTags(nMoQParkListDetails);
                        Collections.sort(nMoQParkListDetails);
                        mAdapterPark.notifyDataSetChanged();

                        // Commented for Nid issue in park list details API
//                        new NMoQParkListDetailsRowCount(CollectionDetailsActivity.this, appLanguage).execute();
                    } else {
                        detailLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    detailLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQParkListDetails>> call, @NonNull Throwable t) {
                detailLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
            public void onResponse(@NonNull Call<ArrayList<CollectionDetailsList>> call,
                                   @NonNull Response<ArrayList<CollectionDetailsList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        detailLayout.setVisibility(View.VISIBLE);
                        collectionDetailsList.addAll(response.body());
                        removeHtmlTags(collectionDetailsList);
                        mAdapter.notifyDataSetChanged();
                        new CollectionDetailRowCount(CollectionDetailsActivity.this).execute();


                    } else {
                        detailLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    detailLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CollectionDetailsList>> call,
                                  @NonNull Throwable t) {
                detailLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumCollectionDetailFromDatabase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity.this).execute();
        } else {
            new RetrieveMuseumCollectionDetailDataArabic(CollectionDetailsActivity.this).execute();
        }
    }

    public void removeHtmlTags(ArrayList<CollectionDetailsList> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));
            models.get(i).setAbout(util.html2string(models.get(i).getAbout()));
        }
    }

    private void removeParkHtmlTags(ArrayList<NMoQParkListDetails> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));
            models.get(i).setDescription(util.html2string(models.get(i).getDescription()));
        }
    }

    public static class NMoQParkListDetailsRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;

        NMoQParkListDetailsRowCount(CollectionDetailsActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                new CheckNMoQParkListDetailsDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertNMoQParkListDetailsDataToDataBase(
                        activityReference.get(),
                        activityReference.get().nMoQParkListDetailsTableEnglish,
                        activityReference.get().nMoQParkListDetailsTableArabic, language).execute();
            }
        }
    }

    public static class CheckNMoQParkListDetailsDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic;
        String language;
        private NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish;

        CheckNMoQParkListDetailsDBRowExist(CollectionDetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().nMoQParkListDetails.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().checkIdExistEnglish(
                                Integer.parseInt(activityReference.get().nMoQParkListDetails.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkListDetailsTable(activityReference.get(), language).execute();
                        } else {
                            nMoQParkListDetailsTableEnglish = new NMoQParkListDetailsTableEnglish(
                                    activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                    activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                    activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                    activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                    activityReference.get().nMoQParkListDetails.get(i).getDescription());

                            activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                    insertEnglishTable(nMoQParkListDetailsTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        int n = activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().checkIdExistArabic(
                                Integer.parseInt(activityReference.get().nMoQParkListDetails.get(i).getNid()));
                        if (n > 0) {
                            new UpdateNMoQParkListDetailsTable(activityReference.get(), language).execute();
                        } else {

                            nMoQParkListDetailsTableArabic = new NMoQParkListDetailsTableArabic(
                                    activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                    activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                    activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                    activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                    activityReference.get().nMoQParkListDetails.get(i).getDescription());

                            activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                    insertArabicTable(nMoQParkListDetailsTableArabic);
                        }
                    }

                }
            }
            return null;
        }
    }

    public static class UpdateNMoQParkListDetailsTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;

        UpdateNMoQParkListDetailsTable(CollectionDetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().updateNMoQParkListDetailsEnglish(
                        activityReference.get().nMoQParkListDetails.get(0).getMainTitle(),
                        activityReference.get().nMoQParkListDetails.get(0).getImages(),
                        activityReference.get().nMoQParkListDetails.get(0).getSortId(),
                        activityReference.get().nMoQParkListDetails.get(0).getNid(),
                        activityReference.get().nMoQParkListDetails.get(0).getDescription()
                );

            } else {
                activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().updateNMoQParkListDetailsArabic(
                        activityReference.get().nMoQParkListDetails.get(0).getMainTitle(),
                        activityReference.get().nMoQParkListDetails.get(0).getImages(),
                        activityReference.get().nMoQParkListDetails.get(0).getSortId(),
                        activityReference.get().nMoQParkListDetails.get(0).getNid(),
                        activityReference.get().nMoQParkListDetails.get(0).getDescription()
                );

            }
            return null;
        }
    }

    public static class InsertNMoQParkListDetailsDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<CollectionDetailsActivity> activityReference;
        private NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish;
        private NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic;
        String language;

        InsertNMoQParkListDetailsDataToDataBase(CollectionDetailsActivity context,
                                                NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish,
                                                NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkListDetailsTableEnglish = nMoQParkListDetailsTableEnglish;
            this.nMoQParkListDetailsTableArabic = nMoQParkListDetailsTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().nMoQParkListDetails != null &&
                        activityReference.get().nMoQParkListDetails.size() > 0) {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        nMoQParkListDetailsTableEnglish = new NMoQParkListDetailsTableEnglish(
                                activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                activityReference.get().nMoQParkListDetails.get(i).getDescription());
                        activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                insertEnglishTable(nMoQParkListDetailsTableEnglish);
                    }
                }
            } else {
                if (activityReference.get().nMoQParkListDetails != null &&
                        activityReference.get().nMoQParkListDetails.size() > 0) {
                    for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                        nMoQParkListDetailsTableArabic = new NMoQParkListDetailsTableArabic(
                                activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                activityReference.get().nMoQParkListDetails.get(i).getDescription());
                        activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                insertArabicTable(nMoQParkListDetailsTableArabic);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public void getNMoQParkListDetailsFromDataBase(String nid) {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishNMoQParkListDetailsData(CollectionDetailsActivity.this, nid).execute();
        } else {
            new RetrieveArabicNMoQParkListDetailsData(CollectionDetailsActivity.this, nid).execute();
        }
    }

    public static class RetrieveEnglishNMoQParkListDetailsData extends AsyncTask<Void, Void,
            List<NMoQParkListDetailsTableEnglish>> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private int nid;

        RetrieveEnglishNMoQParkListDetailsData(CollectionDetailsActivity context, String nid) {
            this.activityReference = new WeakReference<>(context);
            this.nid = Integer.parseInt(nid);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListDetailsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                    getNMoQParkListDetailsEnglishTable(nid);
        }

        @Override
        protected void onPostExecute(List<NMoQParkListDetailsTableEnglish> nMoQParkListDetailsTableEnglishes) {
            NMoQParkListDetails nMoQParkListDetails;
            if (nMoQParkListDetailsTableEnglishes.size() > 0) {
                for (int i = 0; i < nMoQParkListDetailsTableEnglishes.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListDetailsTableEnglishes.get(i).getParkImages());

                    nMoQParkListDetails = new NMoQParkListDetails(
                            nMoQParkListDetailsTableEnglishes.get(i).getParkNid(),
                            nMoQParkListDetailsTableEnglishes.get(i).getParkTitle(),
                            image,
                            nMoQParkListDetailsTableEnglishes.get(i).getParkDescription(),
                            nMoQParkListDetailsTableEnglishes.get(i).getParkSortId());
                    activityReference.get().nMoQParkListDetails.add(i, nMoQParkListDetails);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().progressBar.setVisibility(View.GONE);
                Collections.sort(activityReference.get().nMoQParkListDetails);
                activityReference.get().mAdapterPark.notifyDataSetChanged();
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class RetrieveArabicNMoQParkListDetailsData extends AsyncTask<Void, Void,
            List<NMoQParkListDetailsTableArabic>> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private int nid;

        RetrieveArabicNMoQParkListDetailsData(CollectionDetailsActivity context, String nid) {
            this.activityReference = new WeakReference<>(context);
            this.nid = Integer.parseInt(nid);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListDetailsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                    getNMoQParkListDetailsArabicTable(nid);
        }

        @Override
        protected void onPostExecute(List<NMoQParkListDetailsTableArabic> nMoQParkListDetailsTableArabics) {
            NMoQParkListDetails nMoQParkListDetails;
            if (nMoQParkListDetailsTableArabics.size() > 0) {
                for (int i = 0; i < nMoQParkListDetailsTableArabics.size(); i++) {
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListDetailsTableArabics.get(i).getParkImages());
                    nMoQParkListDetails = new NMoQParkListDetails(
                            nMoQParkListDetailsTableArabics.get(i).getParkNid(),
                            nMoQParkListDetailsTableArabics.get(i).getParkTitle(),
                            image,
                            nMoQParkListDetailsTableArabics.get(i).getParkDescription(),
                            nMoQParkListDetailsTableArabics.get(i).getParkSortId());
                    activityReference.get().nMoQParkListDetails.add(i, nMoQParkListDetails);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                Collections.sort(activityReference.get().nMoQParkListDetails);
                activityReference.get().mAdapterPark.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
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
                    if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
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
                    if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
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


    public static class RetrieveMuseumCollectionDetailDataEnglish extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableEnglish>> {
        private WeakReference<CollectionDetailsActivity> activityReference;

        RetrieveMuseumCollectionDetailDataEnglish(CollectionDetailsActivity context) {
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
                activityReference.get().detailLayout.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().detailLayout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    public static class RetrieveMuseumCollectionDetailDataArabic extends AsyncTask<Void, Void, List<MuseumCollectionDetailTableArabic>> {
        private WeakReference<CollectionDetailsActivity> activityReference;

        RetrieveMuseumCollectionDetailDataArabic(CollectionDetailsActivity context) {
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
                activityReference.get().detailLayout.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().detailLayout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }


}
