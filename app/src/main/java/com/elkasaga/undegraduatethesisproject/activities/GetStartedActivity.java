package com.elkasaga.undegraduatethesisproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.elkasaga.undegraduatethesisproject.R;

public class GetStartedActivity extends AppCompatActivity {

    Animation ltr, rtl, btt, ttb;
    TextView t1, t2, text_t, text_gether;
    Button signIn, signUp;
    ImageView logo;
    Context mContext = GetStartedActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

        //load animation
        ltr = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        rtl = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        btt = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        ttb = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);

        //load attribute
        t1 = findViewById(R.id.text_keeping);
        t2 = findViewById(R.id.text_you);
        text_t = findViewById(R.id.text_t);
        text_gether = findViewById(R.id.text_gether);
        signIn = findViewById(R.id.btn_signin);
        signUp = findViewById(R.id.btn_signup);
        logo = findViewById(R.id.logo_white);

        //do animate the attribute
        t1.startAnimation(ltr);
        t2.startAnimation(rtl);
        text_t.startAnimation(ltr);
        text_gether.startAnimation(rtl);
        signIn.startAnimation(btt);
        signUp.startAnimation(btt);
        logo.startAnimation(ttb);

        //Listener Sign Up Button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pindah activity ke SignupActivity
                Intent toSignup = new Intent(mContext, SignupActivity.class);
                startActivity(toSignup);
            }
        });

        //Listener Sign In Button
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pindah activity ke SigninActivity
                Intent toSignin = new Intent(mContext, SigninActivity.class);
                startActivity(toSignin);
            }
        });
    }
}
