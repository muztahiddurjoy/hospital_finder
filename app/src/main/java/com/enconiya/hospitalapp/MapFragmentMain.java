package com.enconiya.hospitalapp;

import android.Manifest;
import android.app.Activity;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enconiya.hospitalapp.Datasets.HospitalDataset;
import com.enconiya.hospitalapp.Datasets.Result;
import com.enconiya.hospitalapp.Datasets.Route;
import com.enconiya.hospitalapp.Interfaces.APIInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapFragmentMain extends Fragment{

    private DatabaseReference reference;
    private ArrayList<HospitalDataset> hospitaldatas;
    private FusedLocationProviderClient client;
    private LatLng myposition;
    private Polyline polyline;
    private GoogleMap map;
    APIInterface apiInterface;
    List<LatLng> polylineslist;
    private PolylineOptions polylineOptions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map_main, container, false);
        reference = FirebaseDatabase.getInstance().getReference().child("hospitals");
        hospitaldatas = new ArrayList<>();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
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
                                .icon(descriptor(getActivity(),R.drawable.ic_humanlocation))
                                .title("myMarker"));
                            }
                        }
                    });
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            HospitalDataset dataset = ds.getValue(HospitalDataset.class);
                            if(!hospitaldatas.contains(dataset)){
                                hospitaldatas.add(dataset);
                            }
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(dataset.getLat()),Double.valueOf(dataset.getLon())))
                                    .title(dataset.getName()))
                                    .setIcon(descriptor(getActivity(),R.drawable.ic_markerhospitak));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(@NonNull LatLng latLng) {
//                        MarkerOptions options = new MarkerOptions();
//                        options.position(latLng);
//                        options.title(latLng.latitude+" : "+latLng.longitude);
//                        googleMap.clear();
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//                        googleMap.addMarker(options);
//                    }
//                });
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setTrafficEnabled(true);
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.mapstylesilver));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        for(HospitalDataset data : hospitaldatas){
                            if(data.getName().equals(marker.getTitle())){
                                BottomSheetDialog dialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
                                View bottomsheetview = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_layout,(LinearLayout)root.findViewById(R.id.bottom_layout ));
                                TextView name = bottomsheetview.findViewById(R.id.title_bottom);
                                TextView beds =  bottomsheetview.findViewById(R.id.beds_bottom);
                                TextView doctor  = bottomsheetview.findViewById(R.id.doctors_bottom);
                                TextView distance = bottomsheetview.findViewById(R.id.distance_bottom);
                                TextView grade = bottomsheetview.findViewById(R.id.grade_hospital);
                                TextView phonebottom = bottomsheetview.findViewById(R.id.phone_bottom);
                                ImageButton callbtn = bottomsheetview.findViewById(R.id.call_btn_bottom);
                                //define the values
                                beds.setText(data.getBeds());
                                doctor.setText(data.getActivedoctors());
                                name.setText(data.getName());
                                grade.setText(data.getGrade());
                                float[] values = new float[1];
                                Location.distanceBetween(myposition.latitude,myposition.longitude,Double.parseDouble(data.getLat()),Double.parseDouble(data.getLon()),values);
                                distance.setText(String.format("%.2f",values[0]/1000));
                                phonebottom.setText(data.getPhone());
                                callbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",data.getPhone(),null));
                                        startActivity(intent);
                                    }
                                });
//                                start.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        String url = getUrl(myposition,new LatLng(Double.valueOf(data.getLat()), Double.valueOf(data.getLon())),"driving");
//                                        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
//                                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                                                .baseUrl("https://maps.googleapis.com/")
//                                                .build();
//                                        apiInterface = retrofit.create(APIInterface.class);
//                                        getDirection(myposition.latitude+","+myposition.longitude,data.getLat()+","+data.getLon(),myposition,new LatLng(Double.valueOf(data.getLat()),Double.valueOf(data.getLon())));
//                                    }
//                                });

//                                distance.setText(Integer.parseInt(String.valueOf(distance(myposition.latitude,myposition.longitude,Double.valueOf(data.getLat()),Double.valueOf(data.getLon())))));
                                dialog.setContentView(bottomsheetview);
                                dialog.show();
                            }
                        }
//                        Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

            }


        });

        return root;
    }

    private String getUrl(LatLng myposition, LatLng latLng, String driving) {
        String str_origin ="origin="+myposition.latitude+","+myposition.longitude;
        String str_dest = "destination="+latLng.latitude+","+latLng.longitude;
        String mode = "mode="+driving;
        String parameters = str_origin+"&"+str_dest+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&key="+getString(R.string.apikey);
        return  url;
    }

    private BitmapDescriptor descriptor(Context context, int vecid){
        Drawable vecdraw = ContextCompat.getDrawable(context,vecid);
        vecdraw.setBounds(0,0,vecdraw.getIntrinsicWidth(),vecdraw.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vecdraw.getIntrinsicWidth(),vecdraw.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vecdraw.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Task<Location> task = client.getLastLocation();
               task.addOnSuccessListener(new OnSuccessListener<Location>() {
                   @Override
                   public void onSuccess(Location location) {
                       myposition = new LatLng(location.getLatitude(),location.getLongitude());
                   }
               });
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

//    @Override
//    public void onTaskDone(Object... values) {
//        if (polyline!=null){
//            polyline.remove();
//        }
//        polyline = map.addPolyline((PolylineOptions) values[0]);
//    }
    private void getDirection(String origin, String destination, LatLng myposition, LatLng latLng){
        apiInterface.getDirection("driving","less_driving",origin,destination,getString(R.string.apikey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Result value) {
                        polylineslist = new ArrayList<>();
                        List<Route> routeList = value.getRoutes();
                        for(Route route : routeList){
                            String polyline = route.getOverview_polyline().getPoints();
                            polylineslist.addAll(decodePoly(polyline));
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getActivity(),R.color.redPrimary));
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineslist);
                        map.addPolyline(polylineOptions);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(myposition);
                        builder.include(latLng);
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }
    // deocdePoly implementation
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}