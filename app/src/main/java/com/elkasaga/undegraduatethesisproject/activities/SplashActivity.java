package com.elkasaga.undegraduatethesisproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    Context mContext = SplashActivity.this;
    Handler handler = new Handler();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (mAuth.getCurrentUser() != null){

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent tohome = new Intent(mContext, HomeActivity.class);
                    startActivity(tohome);
                    finish();
                }
            });

        } else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent toGetStarted = new Intent(mContext, GetStartedActivity.class);
                    startActivity(toGetStarted);
                    finish();
                }
            }, 2000);
        }

    }
}
