package com.enconiya.hospitalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class AuthActivity extends AppCompatActivity {

    ViewPager pager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        pager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new SigninFragment(),"লগইন করুন");
        adapter.addFragment(new SignUpFragment(),"নতুন একাউন্ট খুলুন");
        pager.setAdapter(adapter);
    }
}