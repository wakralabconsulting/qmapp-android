package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.vision.text.Text;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

public class ObjectPreviewDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView closeBtn, image1, image2, image3, image4, imageToZoom;
    TextView maiTtitle, shortDescription, image1Description, historyTitle, historyDescription,
            image2Description;
    String title, description, history, summary, mainImage;
    ArrayList<String> imageList;

    private Intent intent;
    private Util utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        closeBtn = findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageToZoom = findViewById(R.id.image_to_zoom);
        maiTtitle = findViewById(R.id.title);
        shortDescription = findViewById(R.id.short_description);
        historyTitle = findViewById(R.id.history_title);
        historyDescription = findViewById(R.id.history_description);
        image1 = findViewById(R.id.image_1);
        image2 = findViewById(R.id.image_2);
        image3 = findViewById(R.id.image_3);
        image4 = findViewById(R.id.image_4);
        image1Description = findViewById(R.id.image_desc1);
        image2Description = findViewById(R.id.image_desc2);
        utils = new Util();
        intent = getIntent();
        title = intent.getStringExtra("Title");
        mainImage = intent.getStringExtra("Image");
        description = intent.getStringExtra("Description");
        history = intent.getStringExtra("History");
        summary = intent.getStringExtra("Summary");
        imageList = intent.getStringArrayListExtra("Images");
        imageToZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForZoomingImage();
            }
        });

        maiTtitle.setText(utils.html2string(title));
        shortDescription.setText(description);
        GlideApp.with(this)
                .load(mainImage)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into(imageToZoom);
        if (history != null && !history.equals("")) {
            historyTitle.setVisibility(View.VISIBLE);
            historyDescription.setVisibility(View.VISIBLE);
            historyDescription.setText(history);
            if (summary != null) {
                image2Description.setText(utils.html2string(summary));
                image2Description.setVisibility(View.VISIBLE);
            }

        }
        if (imageList != null && imageList.size() > 0) {
            switch (imageList.size()) {
                case 1:
                    setImage1();
                    break;
                case 2:
                    setImage1();
                    setImage2();
                    break;
                case 3:
                    setImage1();
                    setImage2();
                    setImage3();
                    break;
                case 4:
                    setImage1();
                    setImage2();
                    setImage3();
                    setImage4();
                    break;
            }
        }

    }

    public void setImage1() {
        GlideApp.with(this)
                .load(imageList.get(0))
                .into(image1);
        image1.setVisibility(View.VISIBLE);
    }

    public void setImage2() {
        GlideApp.with(this)
                .load(imageList.get(1))
                .into(image2);
        image2.setVisibility(View.VISIBLE);
    }

    public void setImage3() {
        GlideApp.with(this)
                .load(imageList.get(2))
                .into(image3);
        image3.setVisibility(View.VISIBLE);
    }

    public void setImage4() {
        GlideApp.with(this)
                .load(imageList.get(3))
                .into(image4);
        image4.setVisibility(View.VISIBLE);
    }

    public void openDialogForZoomingImage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this/*,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen*/);
        View mView = getLayoutInflater().inflate(R.layout.zooming_layout, null);
        PhotoView photoView = mView.findViewById(R.id.imageView);
        photoView.setImageURI(Uri.parse(mainImage));
        GlideApp.with(this)
                .load(mainImage)
                .placeholder(R.drawable.placeholder_portrait)
                .into(photoView);

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setCancelable(true);
    }

}
