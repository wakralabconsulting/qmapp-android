package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import java.util.ArrayList;

public class PageFragment extends Fragment {

    private TextView mainTitle, acessionText, productionText, productionDateText,
            periodStyleText, techniqueMaterialText, dimensionText;
    private ImageView mainImageView;
    String title, description, history, summary, mainImage;
    ArrayList<String> imageList;

    public PageFragment() {
    }

    public PageFragment newInstance(int position, boolean isLast, String mainTitle, String accessionNumber,
                                    String image, String productionText, String productionDateText,
                                    String periodStyleText, String techniqueMaterial, String dimension,
                                    String curatorialDescription, String objectHistory, String objectENGSummary,
                                    ArrayList<String> images) {

        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        args.putString("MAINTITLE", mainTitle);
        args.putString("ACCESIONNUMBER", accessionNumber);
        args.putString("IMAGE", image);
        args.putString("PRODUCTION", productionText);
        args.putString("PRODUCTIONDATE", productionDateText);
        args.putString("PERIODSTYLETEXT", periodStyleText);
        args.putString("TECHNIQUEMATERIAL", techniqueMaterial);
        args.putString("DIMENSION", dimension);
        args.putString("DESCRIPTION", curatorialDescription);
        args.putString("HISTORY", objectHistory);
        args.putString("SUMMARY", objectENGSummary);
        args.putStringArrayList("IMAGES", images);
        if (isLast)
            args.putBoolean("isLast", true);
        final PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_page, container, false);
        mainTitle = (TextView) view.findViewById(R.id.main_title);
        acessionText = (TextView) view.findViewById(R.id.acession_number);
        productionText = (TextView) view.findViewById(R.id.production_text);
        productionDateText = (TextView) view.findViewById(R.id.production_date_text);
        periodStyleText = (TextView) view.findViewById(R.id.period_style);
        techniqueMaterialText = (TextView) view.findViewById(R.id.technique_material);
        dimensionText = (TextView) view.findViewById(R.id.dimension_text);

        mainImageView = (ImageView) view.findViewById(R.id.main_image);
        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ObjectPreviewDetailsActivity.class);
                intent.putExtra("Title", title);
                intent.putExtra("Image", mainImage);
                intent.putExtra("Description", description);
                intent.putExtra("History", history);
                intent.putExtra("Summary", summary);
                intent.putStringArrayListExtra("Images", imageList);
                startActivity(intent);

            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int position = getArguments().getInt("POSITION", 0);
        title = getArguments().getString("MAINTITLE");
        mainImage = getArguments().getString("IMAGE");
        description = getArguments().getString("DESCRIPTION");
        history = getArguments().getString("HISTORY");
        summary = getArguments().getString("SUMMARY");
        imageList = getArguments().getStringArrayList("IMAGES");

        mainTitle.setText(title);
        acessionText.setText(getArguments().getString("ACCESIONNUMBER"));
        productionText.setText(getArguments().getString("PRODUCTION"));
        productionDateText.setText(getArguments().getString("PRODUCTIONDATE"));
        periodStyleText.setText(getArguments().getString("PERIODSTYLETEXT"));
        techniqueMaterialText.setText(getArguments().getString("TECHNIQUEMATERIAL"));
        dimensionText.setText(getArguments().getString("DIMENSION"));
        GlideApp.with(this)
                .load(mainImage)
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .into(mainImageView);
//        if (getArguments().containsKey("isLast"))
//            mainTitle.setText("You're done!");
//        else
//            mainTitle.setText(Integer.toString(page));
    }
}