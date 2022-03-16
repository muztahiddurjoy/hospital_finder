package com.enconiya.hospitalapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enconiya.hospitalapp.Datasets.PharmacyDataset;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;


public class PharmacyFragment extends Fragment {

    DatabaseReference reference;
    LatLng myposition;
    FusedLocationProviderClient client;
    ArrayList<PharmacyDataset> phars;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_pharmacy, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        reference = FirebaseDatabase.getInstance().getReference().child("pharmacy");
        phars = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pharmacy_map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location!=null){
                                myposition = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,15));
                                googleMap.addMarker(new MarkerOptions()
                                        .position(myposition)
                                        .icon(descriptor(getActivity(),R.drawable.ic_pharmacyhuman)));
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            PharmacyDataset dataset = ds.getValue(PharmacyDataset.class);
                                            phars.add(dataset);
                                            googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(dataset.getLat()),Double.parseDouble(dataset.getLon())))
                                            .icon(descriptor(getActivity(),R.drawable.ic_pharmacymarker))
                                            .title(dataset.getName()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    for(PharmacyDataset dataset : phars){
                                        if (marker.getTitle().equals(dataset.getName())){
                                            BottomSheetDialog dialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
                                            View bottomsheetview = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_pharmacy_layout,(LinearLayout)root.findViewById(R.id.pharmacy_bottom));
                                            TextView name = bottomsheetview.findViewById(R.id.pharacy_name);
                                            TextView opened = bottomsheetview.findViewById(R.id.opened_bottom_pharmacy);
                                            TextView distance  = bottomsheetview.findViewById(R.id.distance_bottom_pharmacy);
                                            TextView call = bottomsheetview.findViewById(R.id.phone_bottom_pharmacy);
                                            ImageButton callbtn = bottomsheetview.findViewById(R.id.call_btn_bottom_pharmacy);
                                            TextView added = bottomsheetview.findViewById(R.id.added_by);
                                            //set the value
                                            name.setText(dataset.getName());
                                            call.setText(dataset.getNumber());
                                            Calendar calendar = Calendar.getInstance();
                                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                            if (hour<Integer.parseInt(dataset.getClosetime())){
                                                opened.setText(String.valueOf(Integer.parseInt(dataset.getClosetime())-hour));
                                            }else {
                                                opened.setText(String.valueOf(hour-Integer.parseInt(dataset.getClosetime())));
                                            }
                                            float[] floats = new float[1];
                                            Location.distanceBetween(Double.parseDouble(dataset.getLat()),Double.parseDouble(dataset.getLon()),myposition.latitude,myposition.longitude,floats);
                                            distance.setText(String.format("%.2f",floats[0]/1000));
                                            added.setText(Html.fromHtml("Added By : "+"<b style=\"color:#64b5f6\">"+dataset.getAddedbyname()+"</b>"));
                                            callbtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",dataset.getNumber(),null));
                                                    startActivity(intent);
                                                }
                                            });
                                            dialog.setContentView(bottomsheetview);
                                            dialog.show();
                                        }
                                    }
                                    return false;
                                }
                            });
                            }
                        }
                    });
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setTrafficEnabled(true);
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.mapstylesilver));
            }
        });
        return root;
    }
    private BitmapDescriptor descriptor(Context context, int vecid){
        Drawable vecdraw = ContextCompat.getDrawable(context,vecid);
        vecdraw.setBounds(0,0,vecdraw.getIntrinsicWidth(),vecdraw.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vecdraw.getIntrinsicWidth(),vecdraw.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vecdraw.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}