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

public class PageFragment extends Fragment {

    private TextView mainTitle;
    private ImageView mainImage;

    public static PageFragment newInstance(int page, boolean isLast) {
        Bundle args = new Bundle();
        args.putInt("page", page);
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
        mainImage = (ImageView) view.findViewById(R.id.main_image);
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ObjectPreviewDetailsActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int page = getArguments().getInt("page", 0);
//        if (getArguments().containsKey("isLast"))
//            mainTitle.setText("You're done!");
//        else
//            mainTitle.setText(Integer.toString(page));
    }
}