package com.elkasaga.undegraduatethesisproject.activities.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.models.JoinGroupTourRequest;
import com.elkasaga.undegraduatethesisproject.models.JoinRequestRespond;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.ListedJoinGroupTourRequestAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private Context mContext = NotificationActivity.this;
    private static final int ACTIVITY_NUM = 3;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private RecyclerView listedNotifContainer;
    private ArrayList<JoinGroupTourRequest> joinReqList;
    private ArrayList<JoinRequestRespond> respondList;
    private Set<String> mReqUid = new HashSet<>();
    private Set<String> mRespId = new HashSet<>();
    private ListedJoinGroupTourRequestAdapter joinGroupTourRequestAdapter;
    private ListenerRegistration reqListener;
    private SharedPreferences userPreferenes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setupBottomNavigationView();
        listedNotifContainer = findViewById(R.id.listedNotifContainer);
        userPreferenes = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        joinReqList = new ArrayList<>();
        respondList = new ArrayList<>();

        getNotifData();
        initRequestRecyclerView();
    }

    private void getNotifData(){

        if (userPreferenes.getLong("category", 0) == 0){
            Query reqRef = mDb.collection("JoinGroupTourRequest")
                    .whereEqualTo("tourleader", userPreferenes.getString("uid", ""));
            reqListener = reqRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots.size() != 0){

                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                            JoinGroupTourRequest joinGroupTourRequest = doc.toObject(JoinGroupTourRequest.class);
                            if (!mReqUid.contains(joinGroupTourRequest.getUid())){
                                mReqUid.add(joinGroupTourRequest.getUid());
                                joinReqList.add(joinGroupTourRequest);
                            }
                        }
                        joinGroupTourRequestAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else if(userPreferenes.getLong("category", 0) == 1){
            CollectionReference respRef = mDb.collection("JoinRequestRespond")
                    .document(userPreferenes.getString("uid", ""))
                    .collection("Respond");
            respRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().size() != 0){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            JoinRequestRespond requestRespond = document.toObject(JoinRequestRespond.class);
                            if (!mRespId.contains(requestRespond.getRequestid())){
                                mRespId.add(requestRespond.getRequestid());
                                respondList.add(requestRespond);
                                Log.d(TAG, "RESPOND  = "+requestRespond.getTourid());
                            }
                        }
                        joinGroupTourRequestAdapter = new ListedJoinGroupTourRequestAdapter(mContext, joinReqList, respondList);
                        listedNotifContainer.setAdapter(joinGroupTourRequestAdapter);
                        listedNotifContainer.setLayoutManager(new LinearLayoutManager(mContext));
                    }
                }
            });
        }


    }

    public void initRequestRecyclerView(){
        joinGroupTourRequestAdapter = new ListedJoinGroupTourRequestAdapter(mContext, joinReqList, respondList);
        listedNotifContainer.setAdapter(joinGroupTourRequestAdapter);
        listedNotifContainer.setLayoutManager(new LinearLayoutManager(this));
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
