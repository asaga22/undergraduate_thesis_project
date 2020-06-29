package com.elkasaga.undegraduatethesisproject.activities.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.GetStartedActivity;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = AccountActivity.this;
    private static final int ACTIVITY_NUM = 4;

    private FirebaseAuth mAuth;

    private String uid, fullname, ava;
    private long category, on, up, fore;
    TextView accountName, accountCategory, totalOngoing, totalUpcoming, totalForegoing;
    Button btnSignout, btnEditAccount;
    ProgressBar mProgressBar;
    TextView mPleaseWait;
    ImageView profile_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setupBottomNavigationView();
        initWidgets();
        mAuth = FirebaseAuth.getInstance();
        getUserDetailsFromPreference();
        initAccountInfo();
        initSignout();
        initBtnEditAccount();
    }

    private void initAccountInfo(){
        if (category == 0){
            accountCategory.setText("Tour Leader");
        } else if (category == 1){
            accountCategory.setText("Tour Participant");
        }
        accountName.setText(fullname);
        totalForegoing.setText(String.valueOf(fore));
        totalOngoing.setText(String.valueOf(on));
        totalUpcoming.setText(String.valueOf(up));
        Picasso.with(this).load(ava).centerCrop().fit().into(profile_image);
    }

    private void initSignout(){
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                mAuth.signOut();
                Intent intent = new Intent(mContext, GetStartedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initBtnEditAccount(){
       btnEditAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              Intent toEdit = new Intent(mContext, EditAccountActivity.class);
              startActivity(toEdit);
           }
       });
    }

    private void initWidgets(){
        accountName = (TextView) findViewById(R.id.accountName);
        accountCategory = (TextView) findViewById(R.id.accountCategory);
        totalForegoing = (TextView) findViewById(R.id.totalHistory);
        totalOngoing = (TextView) findViewById(R.id.totalOngoing);
        totalUpcoming = (TextView) findViewById(R.id.totalUpcoming);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        btnSignout = findViewById(R.id.button_signout);
        btnEditAccount = findViewById(R.id.btnEditAccount);
        mProgressBar = findViewById(R.id.progressBarSignout);
        mPleaseWait = findViewById(R.id.pleaseWaitSignout);
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
    }

    private void getUserDetailsFromPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        fullname = sharedPreferences.getString("fullname", "");
        category = sharedPreferences.getLong("category", 0);
        on = sharedPreferences.getLong("ongoingtour", 0);
        up = sharedPreferences.getLong("upcomingtour", 0);
        fore = sharedPreferences.getLong("foregoingtour", 0);
        ava = sharedPreferences.getString("avatar", "");
        Log.d(TAG, "avatar = "+ava);
    }

    /*
     * Bottom Navigation Setup
     */
    private void setupBottomNavigationView(){
        Integer color = ResourcesCompat.getColor(getResources(), R.color.primaryBlue, null);

        Log.d(TAG, "setBottomNavigationView: setting up BottomNavView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        BottomNavigationViewHelper.setsetNavMenuItemThemeColors(bottomNavigationView, color);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
