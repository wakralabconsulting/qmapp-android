package com.qatarmuseums.qatarmuseumsapp.park;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.List;

public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<ParkList> parkLists;
    private String latitude, longitude;
    Util util = new Util();
    Bundle mapViewBundle = null;
    MapView mapDetails;
    private GoogleMap gmap, gvalue;
    LinearLayout mapView;
    int iconView = 0;

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        gvalue=googleMap;
//        googleMap.getUiSettings().setMapToolbarEnabled(false);
//    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView mainTitle, title, shortDescription, longDescription, timingInfo, timimgTitle;
        public ImageView imageView;
        public LinearLayout locationLayout, mainTitleLayout, timimgHyphen;
        ImageView mapImageView, direction;


        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            mainTitleLayout = (LinearLayout) view.findViewById(R.id.main_title_layout);
            mainTitle = (TextView) view.findViewById(R.id.main_title);
            title = (TextView) view.findViewById(R.id.title);
            shortDescription = (TextView) view.findViewById(R.id.short_description);
            longDescription = (TextView) view.findViewById(R.id.long_description);
            timingInfo = (TextView) view.findViewById(R.id.timing_info);
            timimgTitle = (TextView) view.findViewById(R.id.timing_title);
            locationLayout = (LinearLayout) view.findViewById(R.id.location_layout);
            timimgHyphen = (LinearLayout) view.findViewById(R.id.timing_hiphen);
            mapDetails = (MapView) view.findViewById(R.id.map_info);
            mapImageView = (ImageView) view.findViewById(R.id.map_view);
            direction = (ImageView) view.findViewById(R.id.direction);
            if (mapDetails != null) {
                // Initialise the MapView
                mapDetails.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapDetails.getMapAsync(this);
            }

        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            gvalue = googleMap;
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            gvalue.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            gvalue.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));
            if(latitude!=null) {
                gvalue.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                gvalue.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
            }
        }
    }


    public ParkListAdapter(Context context, List<ParkList> parkLists) {
        this.parkLists = parkLists;
        this.mContext = context;

    }


    @NonNull
    @Override
    public ParkListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_list_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ParkListAdapter.MyViewHolder holder, int position) {
        final ParkList parkList = parkLists.get(position);
        holder.title.setText(parkList.getMainTitle());
        if (position == 0) {
            if (parkList.getLatitude() != null) {
                latitude = convertDegreetoDecimalMeasure(latitude);
                longitude = convertDegreetoDecimalMeasure(longitude);
            } else {
//                latitude = "25.286106";
//                longitude = "51.534817";
            }
            holder.title.setVisibility(View.GONE);
            holder.mainTitleLayout.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.locationLayout.setVisibility(View.VISIBLE);
            holder.mainTitle.setText(parkList.getMainTitle());
            holder.mapImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iconView == 0) {
                        if (latitude == null) {
                            util.showLocationAlertDialog(mContext);
                        } else {
                            iconView = 1;
                            holder.mapImageView.setImageResource(R.drawable.ic_map);
                            gmap = gvalue;
                            gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));

                            gmap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                        }
                    } else {
                        if (latitude == null) {
                            util.showLocationAlertDialog(mContext);
                        } else {
                            iconView = 0;
                            gmap = gvalue;
                            holder.mapImageView.setImageResource(R.drawable.ic_satellite);
                            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));

                            gmap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                        }
                    }
                }
            });

            holder.direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (latitude == null) {
                        util.showLocationAlertDialog(mContext);
                    } else {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&basemap=satellite"));
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

        }

        if (parkList.getShortDescription() != null) {
            holder.longDescription.setVisibility(View.VISIBLE);
            holder.longDescription.setText(parkList.getShortDescription());
        }
        if (parkList.getTimingInfo() != null) {
            holder.timingInfo.setVisibility(View.VISIBLE);
            holder.timimgHyphen.setVisibility(View.VISIBLE);
            holder.timimgTitle.setVisibility(View.VISIBLE);
            holder.timingInfo.setText(parkList.getTimingInfo());
        } else {
            holder.timingInfo.setVisibility(View.GONE);
            holder.timimgHyphen.setVisibility(View.GONE);
            holder.timimgTitle.setVisibility(View.GONE);
        }

        GlideApp.with(mContext)
                .load(parkList.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return parkLists.size();
    }

    private String convertDegreetoDecimalMeasure(String degreeValue) {
        String value = degreeValue.trim();
        String[] latParts = value.split("°");
        float degree = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("'");
        float min = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("\"");
        float sec = Float.parseFloat(latParts[0]);
        String result;
        result = String.valueOf(degree + (min / 60) + (sec / 3600));
        return result;
    }

}
