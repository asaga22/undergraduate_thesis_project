package com.elkasaga.undegraduatethesisproject.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    Context mContext = SplashActivity.this;
    Handler handler = new Handler();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (mAuth.getCurrentUser() != null){

            //init user skeleton
            DocumentReference userRef = mDb.collection("Users").document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        User user = task.getResult().toObject(User.class);
                        ((UserClient)getApplicationContext()).setUser(user);
                        //check whether user has tour going on
                        Query ongoingRef = mDb.collection("UserTour")
                                .document(FirebaseAuth.getInstance().getUid()).collection("GroupTour")
                                .whereEqualTo("tourstatus", 1);

                        ongoingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d(TAG, "onCreate Lifecycle");
                                if (task.getResult().size() != 0){

                                    //display layout for ongoing tour
                                    GroupTour gt = task.getResult().getDocuments().get(0).toObject(GroupTour.class);

                                    //set group tour to the userclient skeleton
                                    ((UserClient)getApplicationContext()).setGroupTour(gt);
                                    Log.d(TAG, "ONGOING TOUR SKELETON IS SET: "+((UserClient)getApplicationContext()).getGroupTour().getTourtitle());



                                    //simpan touid basic info (key) ke local
                                    SharedPreferences sharedPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("tourtitle", gt.getTourtitle());
                                    editor.putString("tourid", gt.getTourid());
                                    editor.putString("startdate", gt.getStartdate());
                                    editor.putString("enddate", gt.getEnddate());
                                    editor.putString("starttime", gt.getStarttime());
                                    editor.putString("endtime", gt.getEndtime());
                                    editor.putLong("tourstatus", gt.getTourstatus());
                                    editor.putString("tourleader", gt.getTourleader());
                                    editor.apply();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent tohome = new Intent(mContext, HomeActivity.class);
                                            startActivity(tohome);
                                            finish();
                                        }
                                    });
                                } else{
                                    //display layout for no ongoing tour
                                    Log.d(TAG, "ONGOING TOUR GA ADA");
                                    SharedPreferences isOngoingProference = getSharedPreferences("IS_ONGOING", MODE_PRIVATE);
                                    SharedPreferences.Editor editorIsOngoing = isOngoingProference.edit();
                                    editorIsOngoing.putBoolean("isongoing", false);
                                    editorIsOngoing.apply();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent tohome = new Intent(mContext, HomeActivity.class);
                                            startActivity(tohome);
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                    }
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
