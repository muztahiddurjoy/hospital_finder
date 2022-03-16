package com.enconiya.hospitalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends Activity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            startActivity(new Intent(this, AuthActivity.class));
        }
    }
}
