package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;


public class HorizontalLayoutFragment extends Fragment {
    ArrayList<String> images = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        images = getArguments().getStringArrayList("imageList");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horizontal_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout frameLayout = view.findViewById(R.id.layout_frame);
        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                view.findViewById(R.id.hicvp);
        horizontalInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(getContext(), HorizontalLayoutFragment.this, images));
    }

    public void getItemPosition(int value) {
        ((DetailsActivity) getActivity()).imageValue(value);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
