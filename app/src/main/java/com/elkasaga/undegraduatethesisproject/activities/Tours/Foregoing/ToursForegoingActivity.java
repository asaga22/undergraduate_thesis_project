package com.elkasaga.undegraduatethesisproject.activities.Tours.Foregoing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.ForegoingTourFragment;
import com.elkasaga.undegraduatethesisproject.activities.Tours.UpcomingTourFragment;
import com.elkasaga.undegraduatethesisproject.models.Rating;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;

public class ToursForegoingActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = ToursForegoingActivity.this;
    private static final int ACTIVITY_NUM = 1;

    private SharedPreferences userPreferences, tourPreferences;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    String uid;
    long category;
    ImageView backArrow;
    RelativeLayout ratings, noRatedel, ratedRel;
    EditText aboutTheTour, aboutTheTourLeader;
    RatingBar rateTheTour, rateTheTourLeader, ratedOverallBar, ratedTour, ratedTourLeader;
    Button btnSubmitRating;
    TextView ratedOverallTitle, ratedTourTitle, ratedTourLeaderTitle, mPleaseWait,
            aboutRatedTourLeader, aboutRatedTour;
    ProgressBar mProgressBar;
    float tourRate, tlRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        tourPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
        setContentView(R.layout.activity_tours_foregoing);
        initUnivWidgets();

        if (userPreferences.getLong("category", 0) == 0){
            ratings.setVisibility(View.GONE);
            setupViewPager();
        } else if (userPreferences.getLong("category", 0) == 1){
            mPleaseWait.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            ratings.setVisibility(View.VISIBLE);
            noRatedel = (RelativeLayout) findViewById(R.id.noRatedRel);
            ratedRel = (RelativeLayout) findViewById(R.id.ratedRel);
            final Query rateRef = mDb.collection("UserRating")
                    .document(userPreferences.getString("uid", ""))
                    .collection("GroupTourRating")
                    .whereEqualTo("tourid", tourPreferences.getString("tourid", ""));
            rateRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    mPleaseWait.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    if (queryDocumentSnapshots.size() != 0){
                        Rating rateResult = queryDocumentSnapshots.getDocuments().get(0).toObject(Rating.class);
                        //udah rate
                        noRatedel.setVisibility(View.GONE);
                        ratedRel.setVisibility(View.VISIBLE);
                        initWidgetRated();
                        ratedOverallBar.setRating((rateResult.getTourrate() + rateResult.getTourleaderrate()) / 2);
                        ratedTour.setRating(rateResult.getTourrate());
                        ratedTourLeader.setRating(rateResult.getTourleaderrate());
                        ratedOverallTitle.setText("You rated "+(rateResult.getTourrate() + rateResult.getTourleaderrate()) / 2+" out of 5");
                        ratedTourTitle.setText("You rated "+rateResult.getTourrate()+" out of 5");
                        ratedTourLeaderTitle.setText("You rated "+rateResult.getTourleaderrate()+" out of 5");
                        aboutRatedTour.setText(rateResult.getAbouttour());
                        aboutRatedTourLeader.setText(rateResult.getAbouttourleader());
                    } else {
                        //belum rate
                        ratedRel.setVisibility(View.GONE);
                        noRatedel.setVisibility(View.VISIBLE);
                        initWidgetNotRated();
                        initSubmitRatingButton();
                    }
                }
            });

        }
        backArrow = findViewById(R.id.backArrowInTourDetailsForegoing);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUnivWidgets(){
        ratings = (RelativeLayout) findViewById(R.id.ratings);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWaitForegoing);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarForegoing);
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void initWidgetNotRated(){
        aboutTheTour = (EditText) findViewById(R.id.aboutTheTour);
        aboutTheTourLeader = (EditText) findViewById(R.id.aboutTheTourLeader);
        rateTheTour = (RatingBar) findViewById(R.id.rateTheTour);
        rateTheTourLeader = (RatingBar) findViewById(R.id.rateTheTourLeader);
        btnSubmitRating = (Button) findViewById(R.id.btnSubmitRating);
    }

    private void initWidgetRated(){
        ratedOverallBar = (RatingBar) findViewById(R.id.ratedOverallBar);
        ratedTour = (RatingBar) findViewById(R.id.ratedTour);
        ratedTourLeader = (RatingBar) findViewById(R.id.ratedTourLeader);
        ratedOverallTitle = (TextView) findViewById(R.id.ratedOverallTitle);
        ratedTourTitle = (TextView) findViewById(R.id.ratedTourTitle);
        ratedTourLeaderTitle = (TextView) findViewById(R.id.ratedTourLeaderTitle);
        aboutRatedTour = (TextView) findViewById(R.id.aboutRatedTour);
        aboutRatedTourLeader = (TextView) findViewById(R.id.aboutRatedTourLeader);
    }

    private void initSubmitRatingButton(){
        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPleaseWait.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                if (rateTheTour.getRating() != 0 && rateTheTourLeader.getRating() != 0 && !aboutTheTour.getText().equals("") && !aboutTheTourLeader.getText().equals("")){
                    Log.d(TAG, "RATE = "+rateTheTour.getRating());

                    final Rating rating = new Rating(rateTheTour.getRating(),  rateTheTourLeader.getRating(),
                            aboutTheTour.getText().toString(), aboutTheTourLeader.getText().toString(),
                            tourPreferences.getString("tourid", ""),
                            userPreferences.getString("uid", ""));
                    //set rating data to UserRating collection
                    DocumentReference userRating = mDb.collection("UserRating")
                            .document(userPreferences.getString("uid", ""))
                            .collection("GroupTourRating")
                            .document(tourPreferences.getString("tourid", ""));
                    userRating.set(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                //set rating data to GroupTour collection
                                DocumentReference gtRef = mDb.collection("GroupTour")
                                        .document(tourPreferences.getString("tourid", ""))
                                        .collection("Ratings")
                                        .document(userPreferences.getString("uid", ""));

                                gtRef.set(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Your ratings fot the tour has successfully submitted!", Toast.LENGTH_SHORT).show();
                                            Intent toItSelf = new Intent(mContext, ToursForegoingActivity.class);
                                            mPleaseWait.setVisibility(View.GONE);
                                            mProgressBar.setVisibility(View.GONE);
                                            startActivity(toItSelf);
                                            finish();
                                        } else{
                                            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            mPleaseWait.setVisibility(View.GONE);
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                mPleaseWait.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                } else{
                    Toast.makeText(mContext, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    mPleaseWait.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
     * Responsible for adding 3 tabs: camera, home, and messages
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ForegoingTourParticipantFragment());
        adapter.addFragment(new ForegoingTourItineraryFragment());
        adapter.addFragment(new ForegoingTourFeedbackFragment());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Participant");
        tabLayout.getTabAt(1).setText("Itinerary");
        tabLayout.getTabAt(2).setText("Feedback");

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
