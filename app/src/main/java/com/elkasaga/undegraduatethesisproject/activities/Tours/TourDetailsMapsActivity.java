package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Maps.MapsFragment;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class TourDetailsMapsActivity extends AppCompatActivity {

    private static final String TAG = "TourDetailsMapsActivity";
    private Context mContext = TourDetailsMapsActivity.this;
    private static final int ACTIVITY_NUM = 0;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    SharedPreferences otPreference;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetailsmaps);

        otPreference = getSharedPreferences("ONGOING_GT", MODE_PRIVATE);
        Log.d(TAG, "otPreference gt = "+otPreference.getString("tourid", ""));


    }

}
