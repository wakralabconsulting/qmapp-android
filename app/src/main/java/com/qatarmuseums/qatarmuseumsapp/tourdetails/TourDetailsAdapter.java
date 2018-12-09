package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.List;

public class TourDetailsAdapter extends RecyclerView.Adapter<TourDetailsAdapter.MyViewHolder> {
    private Context mContext;
    private List<TourDetailsModel> tourDetailsModelList = null;
    Util util = new Util();
    String latitude, longitude;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_details_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TourDetailsModel model = tourDetailsModelList.get(position);
        holder.mainTitle.setText(model.getTourTitle());
        holder.shortDescription.setText(model.getTourBody());
        holder.initializeMapView();
        if (model.getTourImage().size() > 0) {
            GlideApp.with(mContext)
                    .load(model.getTourImage().get(0))
                    .into(holder.tourImage);
        }
        if (model.getTourRegistered().equalsIgnoreCase("No"))
            holder.interestToggle.setChecked(true);
        else
            holder.interestToggle.setChecked(false);
        holder.dateDetails.setText(model.getTourDate());
        latitude = model.getTourLatitude();
        longitude = model.getTourLongtitude();
        if (latitude != null) {
            if (latitude.contains("°")) {
                latitude = util.convertDegreeToDecimalMeasure(latitude);
                longitude = util.convertDegreeToDecimalMeasure(longitude);
            }
        }
        holder.mapImageView.setOnClickListener(v -> {
            if (holder.mapImageView.getDrawable().getConstantState() ==
                    mContext.getResources().getDrawable(R.drawable.ic_satellite).getConstantState()) {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(mContext);
                } else {
                    holder.mapImageView.setImageResource(R.drawable.ic_map);
                    holder.mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    holder.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));

                    holder.mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    holder.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            } else {
                if (latitude == null || latitude.equals("")) {
                    util.showLocationAlertDialog(mContext);
                } else {
                    holder.mapImageView.setImageResource(R.drawable.ic_satellite);
                    holder.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    holder.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));

                    holder.mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    holder.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 10));
                }
            }
        });

        holder.direction.setOnClickListener(view -> {
            if (latitude == null || latitude.equals("")) {
                util.showLocationAlertDialog(mContext);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&basemap=satellite"));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
        if (model.getTourContactPhone().equals("") && model.getTourContactEmail().equals(""))
            holder.contactLayout.setVisibility(View.GONE);
        else
            holder.contactDetails.setText(model.getTourContactPhone() + "\n" + model.getTourContactEmail());
        holder.interestToggle.setOnTouchListener((v, event) -> {
            util.showComingSoonDialog((Activity) mContext, R.string.coming_soon_content);
            // Commented for registration API
//            if (model.getTourRegistered().equalsIgnoreCase("No")) {
//                model.setTourRegistered("yes");
//                holder.interestToggle.setChecked(false);
//                util.showCulturalPassAlertDialog((Activity) mContext);
//            } else {
//                showDeclineDialog(holder.interestToggle, position);
//            }
            return false;
        });
    }

    private void showDeclineDialog(SwitchCompat interestToggle, int position) {

        final Dialog dialog = new Dialog(mContext, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(mContext).inflate(R.layout.vip_pop_up, null);
        dialog.setContentView(view);

        View line = (View) view.findViewById(R.id.view);
        Button yes = (Button) view.findViewById(R.id.acceptbtn);
        Button no = (Button) view.findViewById(R.id.accept_later_btn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        line.setVisibility(View.GONE);
        dialogTitle.setVisibility(View.GONE);
        yes.setText(mContext.getResources().getString(R.string.yes));
        no.setText(mContext.getResources().getString(R.string.no));
        dialogContent.setText(mContext.getResources().getString(R.string.decline_content_tour));

        yes.setOnClickListener(view1 -> {
            dialog.dismiss();
            interestToggle.setChecked(true);
            tourDetailsModelList.get(position).setTourRegistered("no");
        });
        no.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        if (holder.mMap != null) {
            holder.mMap.clear();
            holder.mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return tourDetailsModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView mainTitle, subTitle, shortDescription, longDescription, dateDetails,
                locationDetails, contactDetails;
        public ImageView tourImage, mapImageView, direction;
        LinearLayout locationLayout, contactLayout, mapViewLayout;
        SwitchCompat interestToggle;
        MapView mapView;
        GoogleMap mMap;
        String latitude, longitude;
        private float curveRadius = 30F;


        public MyViewHolder(View view) {
            super(view);
            tourImage = (ImageView) view.findViewById(R.id.tour_img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                tourImage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(),
                                (int) (view.getHeight() + curveRadius), curveRadius);
                    }
                });

                tourImage.setClipToOutline(true);
            }
            mainTitle = (TextView) view.findViewById(R.id.main_title);
            shortDescription = (TextView) view.findViewById(R.id.short_description);
            longDescription = (TextView) view.findViewById(R.id.long_description);
            dateDetails = (TextView) view.findViewById(R.id.date_info);
            locationDetails = (TextView) view.findViewById(R.id.location_info);
            mapImageView = view.findViewById(R.id.map_view);
            direction = view.findViewById(R.id.direction);
            mapViewLayout = (LinearLayout) view.findViewById(R.id.map_layout);
            mapView = (MapView) view.findViewById(R.id.map_info);
            contactDetails = (TextView) view.findViewById(R.id.contact_info);
            contactLayout = (LinearLayout) view.findViewById(R.id.contact_layout);
            interestToggle = view.findViewById(R.id.interest_toggle_button);
            locationLayout = view.findViewById(R.id.location_layout);


        }

        public void initializeMapView() {
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext.getApplicationContext());
            mMap = googleMap;
            mMap.getUiSettings().setAllGesturesEnabled(false);
            GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
            latitude = tourDetailsModelList.get(getAdapterPosition()).getTourLatitude();
            longitude = tourDetailsModelList.get(getAdapterPosition()).getTourLongtitude();
            if (latitude != null) {
                if (latitude.contains("°")) {
                    latitude = util.convertDegreeToDecimalMeasure(latitude);
                    longitude = util.convertDegreeToDecimalMeasure(longitude);
                }
            }
            if (latitude != null && !latitude.equals("")) {
                LatLng ny = new LatLng(Double.valueOf(latitude),
                        Double.valueOf(longitude));
                mMap.setMinZoomPreference(12);
                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(true);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ny);
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
            }
        }

    }

    public TourDetailsAdapter(Context context, List<TourDetailsModel> tourDetailsModelList) {
        this.tourDetailsModelList = tourDetailsModelList;
        this.mContext = context;
    }
}
