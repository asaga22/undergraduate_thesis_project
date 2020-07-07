package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
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
import com.google.firebase.firestore.QuerySnapshot;

public class TourDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TourDetailsActivity";
    private static final int ACTIVITY_NUM = 1;
    Context mContext = TourDetailsActivity.this;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    ImageView backArrow;
    TextView tourTitle, tourId, startDate, endDate, mPleaseWait, statusTour;
    RelativeLayout itinerary, discussion, participant;
    ProgressBar mProgressBar;

    private String tourid;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetails);
        setupBottomNavigationView();
        initWidgets();
        Bundle bundle = getIntent().getExtras();
        tourid = bundle.getString("tourid");
        Log.d(TAG, "TOURID NYA ADALAH === " + tourid);
        initTourDetails();
        initBackArrow();
        initButtonToParticipant();
        initButtonToDiscussion();
        initButtonToItinerary();
    }

    private void initButtonToParticipant(){
        participant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toParticipant = new Intent(mContext, TourDetailsParticipantActivity.class);
                startActivity(toParticipant);
            }
        });
    }

    private void initButtonToItinerary(){
        itinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toItinerary = new Intent(mContext, TourDetailsItineraryActivity.class);
                startActivity(toItinerary);
            }
        });
    }

    private void initButtonToDiscussion(){
        discussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDiscussion = new Intent(mContext, TourDetailsDiscussionActivity.class);
                startActivity(toDiscussion);
            }
        });
    }

    private void initTourDetails(){
        DocumentReference detailsRef = mDb
                .collection("GroupTour").document(tourid);
        detailsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    GroupTour gt = task.getResult().toObject(GroupTour.class);
                    mProgressBar.setVisibility(View.GONE);
                    mPleaseWait.setVisibility(View.GONE);
                    Log.d(TAG, "DATA TOUR = "+ gt.getTourtitle());

                    tourTitle.setText(gt.getTourtitle());
                    tourId.setText(tourid);
                    startDate.setText(gt.getStartdate());
                    endDate.setText(gt.getEndate());

                    if ( gt.getTourstatus() == 2){
                        statusTour.setText("Upcoming Tour");
                    } else if ( gt.getTourstatus() == 1){
                        statusTour.setText("Ongoing Tour");
                    } else if ( gt.getTourstatus() == 0){
                        statusTour.setText("Foregoing Tour");
                    }

                    //simpan touid basic info (key) ke local
                    SharedPreferences sharedPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("tourtitle", gt.getTourtitle());
                    editor.putString("tourid", gt.getTourid());
                    editor.putString("startdate", gt.getStartdate());
                    editor.putString("enddate", gt.getEndate());
                    editor.putString("starttime", gt.getStarttime());
                    editor.putString("endtime", gt.getEndtime());
                    editor.putLong("tourstatus", gt.getTourstatus());
                    editor.putString("tourleader", gt.getTourleader());
                    editor.apply();
                }
            }
        });
    }

    private void initWidgets(){
        backArrow = (ImageView) findViewById(R.id.backArrowInTourDetails);
        tourTitle = (TextView) findViewById(R.id.tourTitleInTourDetails);
        tourId = (TextView) findViewById(R.id.tourIdInTourDetails);
        startDate = (TextView) findViewById(R.id.startDateInTourDetails);
        endDate = (TextView) findViewById(R.id.endDateInTourDetails);
        itinerary = (RelativeLayout) findViewById(R.id.itineraryInTourDetails);
        discussion = (RelativeLayout) findViewById(R.id.discussionInTourDetails);
        participant = (RelativeLayout) findViewById(R.id.participantsInTourDetails);
        statusTour = (TextView) findViewById(R.id.statusTourInTourDetails);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarTourDetails);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWaitTourDetails);
    }

    private void initBackArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTourActivity = new Intent(mContext, ToursActivity.class);
                startActivity(toTourActivity);
            }
        });
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
