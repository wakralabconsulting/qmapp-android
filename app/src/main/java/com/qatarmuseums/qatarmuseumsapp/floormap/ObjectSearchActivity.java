package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_close)
    ImageView toolbarClose;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.number_pad_text)
    TextView numberPadDisplay;
    @BindView(R.id.qr_scanner)
    ImageView qrScanner;
    @BindView(R.id.qr_scanner_link)
    TextView qrScannerLink;
    @BindView(R.id.clear_button)
    ImageView displayClearButton;
    @BindView(R.id.done_button)
    ImageView doneButton;
    LinearLayout[] displayButtons;
    String display = "";
    SharedPreferences qmPreferences;
    int language;
    ArrayList<ArtifactDetails> artifactList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object__search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.object_search));
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        displayButtons = new LinearLayout[10];
        for (int i = 0; i < displayButtons.length; i++) {
            {
                String textViewID = "display_number_" + (i);

                int resID = getResources().getIdentifier(textViewID, "id", getPackageName());
                displayButtons[i] = ((LinearLayout) findViewById(resID));
                displayButtons[i].setOnClickListener(this);
            }
        }
        displayClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display = "";
                numberPadDisplay.setText("");
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberPadDisplay.getText().toString() == "") {
                    new Util().showAlertDialog(ObjectSearchActivity.this);
                } else {

                    getDetailsFromApi(display,language);
                    display = "";
                    numberPadDisplay.setText("");
                }
            }
        });
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

        qrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent=new Intent(ObjectSearchActivity.this,BarCodeCaptureActivity.class);
                startActivity(imageIntent);
            }
        });

        qrScannerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent=new Intent(ObjectSearchActivity.this,BarCodeCaptureActivity.class);
                startActivity(imageIntent);
            }
        });

    }

    private void getDetailsFromApi(String id, int applanguage) {
        final String language;
        if (applanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ArtifactDetails>> call = apiService.getObjectSearchDetails(language, id);
        call.enqueue(new Callback<ArrayList<ArtifactDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<ArtifactDetails>> call, Response<ArrayList<ArtifactDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        artifactList = response.body();
                        Intent intent = new Intent(ObjectSearchActivity.this, ObjectPreviewDetailsActivity.class);
                        intent.putExtra("Title", artifactList.get(0).getTitle());
                        intent.putExtra("Image", artifactList.get(0).getImage());
                        intent.putExtra("Description", artifactList.get(0).getCuratorialDescription());
                        intent.putExtra("History", artifactList.get(0).getObjectHistory());
                        intent.putExtra("Summary", artifactList.get(0).getObjectENGSummary());
                        intent.putExtra("Audio", artifactList.get(0).getAudioFile());
                        intent.putStringArrayListExtra("Images", artifactList.get(0).getImages());
                        startActivity(intent);
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ArtifactDetails>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int index = 0;
        for (int i = 0; i < displayButtons.length; i++) {
            if (displayButtons[i].getId() == view.getId()) {
                index = i;
                break;
            }
        }

        switch (index) {
            case 0:
                display = display + "0";
                numberPadDisplay.setText(display);
                break;
            case 1:
                display = display + "1";
                numberPadDisplay.setText(display);
                break;
            case 2:
                display = display + "2";
                numberPadDisplay.setText(display);
                break;
            case 3:
                display = display + "3";
                numberPadDisplay.setText(display);
                break;
            case 4:
                display = display + "4";
                numberPadDisplay.setText(display);
                break;
            case 5:
                display = display + "5";
                numberPadDisplay.setText(display);
                break;
            case 6:
                display = display + "6";
                numberPadDisplay.setText(display);
                break;
            case 7:
                display = display + "7";
                numberPadDisplay.setText(display);
                break;
            case 8:
                display = display + "8";
                numberPadDisplay.setText(display);
                break;
            case 9:
                display = display + "9";
                numberPadDisplay.setText(display);
                break;
        }
    }
}
