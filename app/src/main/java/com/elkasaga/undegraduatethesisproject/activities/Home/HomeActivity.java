package com.elkasaga.undegraduatethesisproject.activities.Home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Maps.MapsFragment;
import com.elkasaga.undegraduatethesisproject.activities.NewTour.NewTourActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsDiscussionActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsItineraryActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsParticipantActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.ToursActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.elkasaga.undegraduatethesisproject.services.LocationService;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static com.elkasaga.undegraduatethesisproject.Constants.ERROR_DIALOG_REQUEST;
import static com.elkasaga.undegraduatethesisproject.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.elkasaga.undegraduatethesisproject.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    SharedPreferences userPreferences;
    SharedPreferences isOngoingProference;

    TextView createTourLink, seeListedTourLink, greetingN, greeting,
             tourTitleHome, startDateInHome, endDateInHome, mPleaseWait;
    RelativeLayout homeMaps, homeDiscussion, homeParticipant, homeItinerary;
    RelativeLayout homeNoTour;
    RelativeLayout homeLayout;
    ProgressBar mProgressBar;
    private ListenerRegistration mOngoingTourEventListener;


    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation = null;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private Set<String> mParticipantId = new HashSet<>();
    private GroupTour gt;


    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location mLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);
        initWidgets();
        setupBottomNavigationView();
        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        isOngoingProference = getSharedPreferences("IS_ONGOING", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SharedPreferences ongoingPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
        if (isOngoingProference.getBoolean("isongoing", false)){

            Log.d(TAG, "onCreate Lifecycle");
            GroupTour gt = new GroupTour(
                    ongoingPreferences.getString("tourtitle", ""),
                    ongoingPreferences.getString("tourid", ""),
                    ongoingPreferences.getString("tourleader", ""),
                    ongoingPreferences.getString("startdate", ""),
                    ongoingPreferences.getString("endate", ""),
                    ongoingPreferences.getString("starttime", ""),
                    ongoingPreferences.getString("endtime", ""),
                    ongoingPreferences.getLong("tourstatus", 0)
                    );
            Log.d(TAG, "ONGOING TOUR SKELETON IS SET: "+gt.getTourtitle());
            ((UserClient)getApplicationContext()).setGroupTour(gt);

            homeNoTour.setVisibility(View.GONE);
            homeLayout.setVisibility(View.VISIBLE);
            tourTitleHome.setText(gt.getTourtitle());
            startDateInHome.setText(gt.getStartdate());
            endDateInHome.setText(gt.getEndate());
        } else{
            Log.d(TAG, "ONGOING TOUR GA ADA");
            homeLayout.setVisibility(View.GONE);
            homeNoTour.setVisibility(View.VISIBLE);
            greetingN.setText(getGreeting());
            SharedPreferences.Editor editorIsOngoing = isOngoingProference.edit();
            editorIsOngoing.putBoolean("isongoing", false);
            editorIsOngoing.apply();
        }

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
        greeting.setText(getGreeting());

        initOngoingMapsButton();
        initOngoingItineraryButton();
        initOngoingParticipantButton();
        initOngoingDiscussionButton();

        initCreateNewTourLink();
        initSeeListedTourLink();


    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                HomeActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("ccom.elkasaga.undegraduatethesisproject.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private String getGreeting(){
        String[] name = new String[0];
        if ( userPreferences.getString("fullname", "").contains(" ")){
            name = userPreferences.getString("fullname", "").trim().split("\\s+");
        } else{
            name[0] = userPreferences.getString("fullname", "");
        }
        String firstname = name[0];

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = "";

        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting = "Good Morning, "+firstname;
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting = "Good Afternoon, "+firstname;
        }else if(timeOfDay >= 16 && timeOfDay <= 23){
            greeting = "Good Evening, "+firstname;
        }
        Log.d(TAG, "GREETING IN GREETING = "+greeting);
        return greeting;
    }

    private void initWidgets(){
        homeLayout = (RelativeLayout) findViewById(R.id.homeLayout);
        homeNoTour = (RelativeLayout) findViewById(R.id.homeNoTourLayout);
        greeting = (TextView) findViewById(R.id.greeting);
        homeMaps = (RelativeLayout) findViewById(R.id.homeMap);
        homeDiscussion = (RelativeLayout) findViewById(R.id.homeDiscussion);
        homeItinerary = (RelativeLayout) findViewById(R.id.homeItinerary);
        homeParticipant = (RelativeLayout) findViewById(R.id.homeParticipant);
        tourTitleHome = (TextView) findViewById(R.id.tourTitleHome);
        endDateInHome = (TextView) findViewById(R.id.endDateInHome);
        startDateInHome = (TextView) findViewById(R.id.startDateInHome);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarHome);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWaitHome);
        createTourLink = (TextView) findViewById(R.id.createNewTourLink);
        seeListedTourLink = (TextView) findViewById(R.id.seeListedTourLink);
        createTourLink.setText(Html.fromHtml(getString(R.string.create_new_tour)));
        seeListedTourLink.setText(Html.fromHtml(getString(R.string.see_listed_tour)));
        greetingN = (TextView) findViewById(R.id.greetingNothing);
    }

    private void initOngoingMapsButton(){
        final GroupTour gt = ((UserClient)getApplicationContext()).getGroupTour();
        homeMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference paxLocs = mDb.collection("GroupTour")
                        .document(gt.getTourid())
                        .collection("ParticipantLocation");

                paxLocs.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                UserLocation location = doc.toObject(UserLocation.class);
                                if (!mParticipantId.contains(location.getUser().getUid())){
                                    mParticipantId.add(location.getUser().getUid());
                                    mUserLocations.add(location);
                                }
                            }

                            Log.d(TAG, "Pax Size = "+queryDocumentSnapshots.size());

                            MapsFragment fragment = MapsFragment.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("pax_locations", mUserLocations);
                            bundle.putString("OT_ID", gt.getTourid());
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.mapFrameLayout, fragment, "Maps Fragment");
                            transaction.addToBackStack("Maps Fragment");
                            transaction.commitAllowingStateLoss();;
                        }
                    }
                });
            }
        });
    }

    private void initOngoingItineraryButton(){
        homeItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toItinerary = new Intent(mContext, TourDetailsItineraryActivity.class);
                startActivity(toItinerary);
            }
        });
    }

    private void initOngoingParticipantButton(){
        homeParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toParticipant = new Intent(mContext, TourDetailsParticipantActivity.class);
                startActivity(toParticipant);
            }
        });
    }

    private void initOngoingDiscussionButton(){
        homeDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDiscussion = new Intent(mContext, TourDetailsDiscussionActivity.class);
                startActivity(toDiscussion);
            }
        });
    }

    private void initCreateNewTourLink(){
        createTourLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNewTour = new Intent(mContext, NewTourActivity.class);
                startActivity(toNewTour);
            }
        });
    }


    private void initSeeListedTourLink(){
        seeListedTourLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toListedTour = new Intent(mContext, ToursActivity.class);
                startActivity(toListedTour);
            }
        });
    }

    private void saveUserLocation(){
        if (mUserLocation != null){

            //ADD USER LOC TO UserLocation in DB
            DocumentReference locationRef = mDb.collection("UserLocation").document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "USER LOC ADDED TO DB");

                        //save participant location to db
                        if ( ((UserClient)getApplicationContext()).getGroupTour() != null ){
                            GroupTour gt = ((UserClient)getApplicationContext()).getGroupTour();
                            DocumentReference participantLocRef = mDb.collection("GroupTour")
                                    .document(gt.getTourid())
                                    .collection("ParticipantLocation")
                                    .document(FirebaseAuth.getInstance().getUid());

                            participantLocRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, "GT PAX LOC! "+ mUserLocation.getGeoPoint());
                                        Log.d(TAG, "saveUserLocation: \ninserted user location into database."
                                                +"\n latitude: "+ mUserLocation.getGeoPoint().getLatitude()
                                                +"\n longitude: "+mUserLocation.getGeoPoint().getLongitude());
                                    }
                                }
                            });
                        }

                    }
                }
            });

        }
    }

    private void getUserDetails(){
        if (mUserLocation == null){
            mUserLocation = new UserLocation();

            DocumentReference userRef = mDb.collection("Users").document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully get the  user details.");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        ((UserClient)getApplicationContext()).setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        } else{
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation(){
        Log.d(TAG, "getLastKnownLocation: called.");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    if (location != null){
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG,"onComplete: latitude: "+geoPoint.getLatitude());
                        Log.d(TAG,"onComplete: longitude: "+geoPoint.getLongitude());

                        mUserLocation.setGeoPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        mUserLocation.setTimestamp(null);
                        saveUserLocation();
                        startLocationService();
                    }
                }
            }
        });
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
//                    getChatrooms();
                    getUserDetails();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Lifecycle");
        if ( ((UserClient)getApplicationContext()).getGroupTour() != null ){
            getLastKnownLocation();
            if (checkMapServices()){
                if (mLocationPermissionGranted){
                    getUserDetails();
                }else{
                    getLocationPermission();
                }
            }
        }
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
