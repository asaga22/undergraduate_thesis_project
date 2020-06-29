package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.ListedToursAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ToursActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = ToursActivity.this;
    private static final int ACTIVITY_NUM = 1;
    ImageView more;
    RecyclerView listedToursContainer;
    ArrayList<GroupTour> listedTour;
    ListedToursAdapter groupTourAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    String uid;
    long category;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours);
        setupBottomNavigationView();
        mDb = FirebaseFirestore.getInstance();
        getUserDetailsFromPreference();
        initListedToursRecyclerView();

    }

    private void initListedToursRecyclerView(){

        listedTour = new ArrayList<GroupTour>();
        listedToursContainer = (RecyclerView) findViewById(R.id.listedUpcoming);
        listedToursContainer.setLayoutManager(new LinearLayoutManager(this));

        Query userTourRef = mDb
                .collection("UserTour")
                .document(uid).collection("GroupTour").whereEqualTo("tourstatus", 2);

        userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                        GroupTour gt = queryDocumentSnapshots.getDocuments().get(i).toObject(GroupTour.class);
                        listedTour.add(gt);
                        Log.d(TAG, "TOURTITLE "+i+" "+gt.getTourtitle()+" | STATUS = "+gt.getTourstatus());
                    }
                    groupTourAdapter = new ListedToursAdapter(ToursActivity.this, listedTour);
                    listedToursContainer.setAdapter(groupTourAdapter);
                }
            }
        });
    }

    private void getUserDetailsFromPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        category = sharedPreferences.getLong("category", 0);
    }

    /*
     * Bottom Navigation Setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setBottomNavigationView: setting up BottomNavView");

        Integer color = ResourcesCompat.getColor(getResources(), R.color.primaryBlue, null);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        BottomNavigationViewHelper.setsetNavMenuItemThemeColors(bottomNavigationView, color);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
