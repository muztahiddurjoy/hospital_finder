package com.enconiya.hospitalapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enconiya.hospitalapp.Datasets.PharmacyAddDataset;
import com.enconiya.hospitalapp.Datasets.PharmacyDataset;
import com.enconiya.hospitalapp.Datasets.UserDataset;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;


public class ContributeHospital extends Fragment {
    private FusedLocationProviderClient client;
    private TextInputEditText name, phone;
    private LatLng latlon;
    private Button openbtn, closebtn, add_photo, submit;
    private TextView open,close,image_confirm;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private ImageView pharImage;
    private Uri imageuri;
    private int openhour, closeour;
    private StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_contribute_hospital, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        auth = FirebaseAuth.getInstance();
        image_confirm = root.findViewById(R.id.image_selected_confirm);
        reference = FirebaseDatabase.getInstance().getReference().child("pharmacyreqs");
        storageReference = FirebaseStorage.getInstance().getReference().child("phars");
        name = root.findViewById(R.id.pharmacy_name_add);
        phone = root.findViewById(R.id.pharmacy_phone_add);
        open = root.findViewById(R.id.selected_opening_time);
        close = root.findViewById(R.id.selected_closing_time);
        openbtn = root.findViewById(R.id.select_opening_time);
        closebtn = root.findViewById(R.id.select_closing_time);
        add_photo = root.findViewById(R.id.add_phar_photo_btn);
        pharImage = root.findViewById(R.id.image_selected_phar);
        submit = root.findViewById(R.id.submit_add_phar);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_contribute_pharmacy);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,16));
                                googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(descriptor(getActivity(),R.drawable.ic_baseline_push_pin_24)));
                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        googleMap.clear();
                                        latlon = latLng;
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .icon(descriptor(getActivity(),R.drawable.ic_baseline_push_pin_24)).draggable(true));
                                    }
                                });
                            }
                        }
                    });
                }else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        openhour = i;
                        open.setText("খোল হয়ঃ "+i+":"+i1+"মিনিটে");
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),onTimeSetListener,openhour,0, true);
                timePickerDialog.setTitle("সময় নির্ধারণ করুন");
                timePickerDialog.show();
            }
        });
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        closeour = i;
                        close.setText("বন্ধ হয়ে যায়ঃ "+i+":"+i1+" মিনিটে");
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(getActivity(),listener,openhour,0,true);
                dialog.setTitle("সময় নির্ধারণ করুন");
                dialog.show();
            }
        });
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UserDataset dataset = snapshot.getValue(UserDataset.class);
                                String nameUser = dataset.getName();
                                String pharName = name.getText().toString();
                                String pharPhone = phone.getText().toString();
                                if (pharName.isEmpty()){
                                    Toast.makeText(getActivity(), "দয়া করে ফার্মেসির নামটি লিখুন", Toast.LENGTH_SHORT).show();
                                }
                                else if (pharPhone.isEmpty()){
                                    Toast.makeText(getActivity(), "দয়া করে ফার্মেসির ফোন নাম্বারটি লিখুন", Toast.LENGTH_SHORT).show();
                                }
                                else if (openhour<0){
                                    Toast.makeText(getActivity(), "দয়া করে ফার্মেসি খোলার সময় লিখুন", Toast.LENGTH_SHORT).show();
                                }
                                else if(closeour<0){
                                    Toast.makeText(getActivity(), "দয়া করে ফার্মেসি বন্ধের সময় লিখুন", Toast.LENGTH_SHORT).show();
                                }
                                else if(imageuri == null){
                                    Toast.makeText(getActivity(), "দয়া করে ফার্মেসির একটি ছবি যুক্ত করুন", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    ProgressDialog builder = new ProgressDialog(getActivity());
                                    storageReference.putFile(imageuri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            builder.setTitle("ছবি আপলোড করা হচ্ছে...");
                                            builder.setMessage("আপলোড করা হয়েছেঃ " + (snapshot.getTotalByteCount() * snapshot.getBytesTransferred()) / 100 + "%");
                                            builder.show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isComplete()) {
                                                builder.dismiss();
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                                            Task<Uri> downurl = snapshot.getStorage().getDownloadUrl();
                                            while (!downurl.isComplete()) ;
                                            downurl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String url = uri.toString();
                                                    reference.push().setValue(new PharmacyAddDataset(pharName,
                                                            String.valueOf(latlon.latitude),
                                                            String.valueOf(latlon.longitude),
                                                            String.valueOf(openhour),
                                                            String.valueOf(closeour),
                                                            nameUser,
                                                            pharPhone,
                                                            url))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getActivity(), "আপনার অনুরোধটি গ্রহণ করা হয়েছে!", Toast.LENGTH_SHORT).show();
                                                                    name.setText("");
                                                                    phone.setText("");
                                                                    open.setText("");
                                                                    close.setText("");
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                            downurl.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else {
                                Toast.makeText(getActivity(), "Snapshot doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1 && data.getData()!=null){
         imageuri = data.getData();
//             pharImage.setImageURI(data.getData());
            Glide.with(getActivity()).load(data.getData()).into(pharImage);
            image_confirm.setText("ছবি যুক্ত করা হুয়েছে!");
        }
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