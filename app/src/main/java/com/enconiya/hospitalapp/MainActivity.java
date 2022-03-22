package com.enconiya.hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment = new MapFragmentMain();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapfragment_container,fragment)
                .commit();
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.hospital_find:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mapfragment_container,new MapFragmentMain()).commit();
                        break;
                    case R.id.pharmacy_find:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mapfragment_container, new PharmacyFragment()).commit();
                        break;
                    case  R.id.contribute:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mapfragment_container, new ContributeHospital()).commit();
                        break;
                    case R.id.about_app:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mapfragment_container, new AboutFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}