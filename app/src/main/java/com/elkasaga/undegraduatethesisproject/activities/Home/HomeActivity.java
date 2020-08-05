package com.elkasaga.undegraduatethesisproject.activities.Home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Maps.MapsFragment;
import com.elkasaga.undegraduatethesisproject.activities.NewTour.NewTourActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsDiscussionActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsItineraryActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsParticipantActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.ToursActivity;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.elkasaga.undegraduatethesisproject.services.LocationService;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.DateConvert;
import com.elkasaga.undegraduatethesisproject.utils.NotificationBroadcast;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    RelativeLayout homeMaps, homeDiscussion, homeParticipant, homeItinerary, emergencyBtn;
    RelativeLayout homeNoTour;
    RelativeLayout homeLayout;
    ProgressBar mProgressBar;
    private ListenerRegistration mOngoingTourEventListener;
    RelativeLayout hl;
    SwipeRefreshLayout swipeRefreshLayout;


    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation = null;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private Set<String> mParticipantId = new HashSet<>();
    private ArrayList<Itinerary> itineraryList = new ArrayList<>();
    private Set<String> itineraryIdList = new HashSet<>();
    private GroupTour gt;
    ListenerRegistration itiListener;

    private ArrayList<AlarmManager> alarmManagers= new ArrayList<>();
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();

        if (mDb.getFirestoreSettings() == null){
            mDb.setFirestoreSettings(settings);
        }

        initWidgets();
        setupBottomNavigationView();
        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();

        if ( ((UserClient)getApplicationContext()).getGroupTour() != null ){

            Log.d(TAG, "HOME: GT SKELETON: "+((UserClient)getApplicationContext()).getGroupTour().getTourid());
            gt = new GroupTour(((UserClient)getApplicationContext()).getGroupTour().getTourtitle(),
                    ((UserClient)getApplicationContext()).getGroupTour().getTourid(),
                    ((UserClient)getApplicationContext()).getGroupTour().getTourleader(),
                    ((UserClient)getApplicationContext()).getGroupTour().getStartdate(),
                    ((UserClient)getApplicationContext()).getGroupTour().getEnddate(),
                    ((UserClient)getApplicationContext()).getGroupTour().getStarttime(),
                    ((UserClient)getApplicationContext()).getGroupTour().getEndtime(),
                    ((UserClient)getApplicationContext()).getGroupTour().getTourstatus());
            Log.d(TAG, "Home: am i called? i hope so");
            getLastKnownLocation();
            getParticipantLocation();

            if (checkMapServices()){
                if (mLocationPermissionGranted){
                    getUserDetails();
                }else{
                    getLocationPermission();
                }
            }

            homeNoTour.setVisibility(View.GONE);
            homeLayout.setVisibility(View.VISIBLE);
            tourTitleHome.setText(gt.getTourtitle());
            startDateInHome.setText(DateConvert.dateToDateWithTextMonth(DateConvert.convertStringToDate(gt.getStartdate())));
            endDateInHome.setText(DateConvert.dateToDateWithTextMonth(DateConvert.convertStringToDate(gt.getEnddate())));
            mProgressBar.setVisibility(View.GONE);
            mPleaseWait.setVisibility(View.GONE);
            checkItinerary();
            //check user category
            if (((UserClient)getApplicationContext()).getUser().getCategory() == 0){
                emergencyBtn.setVisibility(View.GONE);
            } else {
                initEmergencyBtn();
            }
        } else {
            homeLayout.setVisibility(View.GONE);
            homeNoTour.setVisibility(View.VISIBLE);
            greetingN.setText(getGreeting());
            mProgressBar.setVisibility(View.GONE);
            mPleaseWait.setVisibility(View.GONE);
        }

        //universal setup
        greeting.setText(getGreeting());
        initSwipeRefreshLayout();

        initOngoingMapsButton();
        initOngoingItineraryButton();
        initOngoingParticipantButton();
        initOngoingDiscussionButton();

        initCreateNewTourLink();
        initSeeListedTourLink();
//        startGroupTourStatusService();


    }

    private void initEmergencyBtn(){
        emergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildEmergencyDialog();
            }
        });
    }

    private void buildEmergencyDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Are you in Emergency situation?");
        builder.setMessage("note: a notification will be broadcast to every member of the Group Tour");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            String message = ((UserClient)getApplicationContext()).getUser().getFullname() + "is in emergency situation!";

            public void onClick(final DialogInterface dialog, int which) {
                // Do some task: insert new message in gt discussion db

                DocumentReference chatMessageRef = mDb.collection("GroupTour")
                        .document(((UserClient)getApplicationContext()).getGroupTour().getTourid())
                        .collection("Discussion")
                        .document();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(Calendar.getInstance().getTime().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ArrayList<String> readBy = new ArrayList<>();
                readBy.add(FirebaseAuth.getInstance().getUid());

                ChatMessage chatMessage = new ChatMessage(chatMessageRef.getId(), message, true, date, ((UserClient)getApplicationContext()).getUser(), readBy);
                chatMessageRef.set(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "Your emergency situation has been notify to all members", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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

//    private void startGroupTourStatusService(){
//        if(!isGroupTourStatusService()){
//            Intent serviceIntent = new Intent(this, UpdateGroupTourStatusService.class);
//            this.startService(serviceIntent);
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//
//                HomeActivity.this.startForegroundService(serviceIntent);
//            }else{
//                startService(serviceIntent);
//            }
//        }
//    }

    private boolean isGroupTourStatusService() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("ccom.elkasaga.undegraduatethesisproject.services.UpdateGroupTourStatusService".equals(service.service.getClassName())) {
                Log.d(TAG, "isGroupTourStatusService: gt service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isGroupTourStatusService: gt service is not running.");
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
        emergencyBtn = (RelativeLayout) findViewById(R.id.emergencyBtn);
        swipeRefreshLayout = findViewById(R.id.swipeToRefreshHome);
    }

    private void initSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent toItsSefl = new Intent(mContext, HomeActivity.class);
                startActivity(toItsSefl);
            }
        });
    }

    private void initOngoingMapsButton(){
        homeMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment fragment = MapsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("pax_locations", mUserLocations);
                bundle.putParcelable("current_user_location", mUserLocation);
                bundle.putString("OT_ID", gt.getTourid());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mapFrameLayout, fragment, "Maps Fragment");
                transaction.addToBackStack("Maps Fragment");
                transaction.commitAllowingStateLoss();
                hl = (RelativeLayout) findViewById(R.id.hl);
                hl.setVisibility(View.GONE);
            }
        });
    }


    private void getParticipantLocation(){

        CollectionReference paxLocs = mDb.collection("GroupTour")
                .document(((UserClient)getApplicationContext()).getGroupTour().getTourid())
                .collection("ParticipantLocation");

        paxLocs.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    Log.d(TAG, "UserLocation aladah test: "+queryDocumentSnapshots.getDocuments().get(0).get("participant"));
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                        UserLocation userLocation = queryDocumentSnapshots.getDocuments().get(i).toObject(UserLocation.class);
                        mUserLocations.add(userLocation);
                    }
                }
            }
        });
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    private void checkItinerary(){
        final Date currentDate = new Date();
        CollectionReference itiRef = mDb.collection("GroupTour").document(gt.getTourid())
                .collection("Itineraries");
        itiListener = itiRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                        Itinerary itinerary = queryDocumentSnapshots.getDocuments().get(i).toObject(Itinerary.class);
                        if (!itineraryIdList.contains(itinerary.getItineraryid())){
                            itineraryIdList.add(itinerary.getItineraryid());
                            itineraryList.add(itinerary);
                            long currentMillis = DateConvert.dateToMillis(currentDate);
                            long itiMillis = DateConvert.dateToMillis(DateConvert.convertStringToDate(itinerary.getDate() +" "+itinerary.getStarttime()));
                            long endMillis = DateConvert.dateToMillis(DateConvert.convertStringToDate(itinerary.getDate() +" "+itinerary.getEndtime()));
                            long timeToNotify = (itiMillis - currentMillis);

                            Log.d(TAG, "CURR MILLIS = "+ currentMillis);
                            Log.d(TAG, "ITI MILLIS = "+ itiMillis);
                            Log.d(TAG, "TIME TO NOTIFY = "+ timeToNotify);

                            if (itiMillis >= currentMillis){
                                Intent intent = new Intent(HomeActivity.this, NotificationBroadcast.class);
                                intent.putExtra("reqcode", (int) timeToNotify);

                                SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(timeToNotify), MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("title", "Itinerary Reminder");
                                editor.putString("message", "it's time for activity: "+itinerary.getDescription());
                                editor.putInt("reqcode", (int) timeToNotify);
                                editor.putString("tourid", ((UserClient)getApplicationContext()).getGroupTour().getTourid());
                                editor.putString("notiftype", "ogt_iti_start");
                                editor.putString("itineraryid", itinerary.getItineraryid());
                                editor.putString("date", itinerary.getDate());
                                editor.putString("starttime", itinerary.getStarttime());
                                editor.putString("endtime", itinerary.getEndtime());
                                editor.putString("description", itinerary.getDescription());
                                editor.putLong("itinerarystatus", itinerary.getItinerarystatus());
                                editor.putLong("day", itinerary.getDay());
                                editor.putLong("itinerarycategory", itinerary.getItinerarycategory());
                                editor.putLong("indexstarttime", itinerary.getIndexstarttime());
                                editor.apply();

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, (int) timeToNotify, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                alarmManager.set(AlarmManager.RTC_WAKEUP,
                                        itiMillis,
                                        pendingIntent);

                                Intent intentEnd = new Intent(HomeActivity.this, NotificationBroadcast.class);
                                intentEnd.putExtra("reqcode", (int) (timeToNotify+100));

                                SharedPreferences sharedPreferencesEnd = getSharedPreferences(String.valueOf(timeToNotify+100), MODE_PRIVATE);
                                SharedPreferences.Editor editoreEnd = sharedPreferencesEnd.edit();
                                editoreEnd.putString("title", "Itinerary Reminder");
                                editoreEnd.putString("message", "activity "+itinerary.getDescription()+" is over");
                                editoreEnd.putInt("reqcode", (int) (timeToNotify+100));
                                editoreEnd.putString("tourid", ((UserClient)getApplicationContext()).getGroupTour().getTourid());
                                editoreEnd.putString("notiftype", "ogt_iti_end");
                                editoreEnd.putString("itineraryid", itinerary.getItineraryid());
                                editoreEnd.putString("date", itinerary.getDate());
                                editoreEnd.putString("starttime", itinerary.getStarttime());
                                editoreEnd.putString("endtime", itinerary.getEndtime());
                                editoreEnd.putString("description", itinerary.getDescription());
                                editoreEnd.putLong("itinerarystatus", itinerary.getItinerarystatus());
                                editoreEnd.putLong("day", itinerary.getDay());
                                editoreEnd.putLong("itinerarycategory", itinerary.getItinerarycategory());
                                editoreEnd.putLong("indexstarttime", itinerary.getIndexstarttime());
                                editoreEnd.apply();

                                PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(HomeActivity.this, (int) (timeToNotify+100), intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(ALARM_SERVICE);
                                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP,
                                        endMillis-100,
                                        pendingIntentEnd);
                            }

                        }
                    }

                }
            }
        });

    }



    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Participant";
            String description = "Channel for sleep notif";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("sleepNotif", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
                        final User user = task.getResult().toObject(User.class);

                        DocumentReference userRef = mDb.collection("GroupTour").document(gt.getTourid())
                                .collection("Participants").document(FirebaseAuth.getInstance().getUid());
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: successfully get the  this pax details.");
                                    Participant thisPax = task.getResult().toObject(Participant.class);
                                    if (thisPax == null){
                                        Participant fromUser = new Participant(user.getUid(), user.getUsername(), user.getFullname(), user.getAvatar(), user.getCategory(), 2, false);
                                        mUserLocation.setParticipant(fromUser);
                                        ((UserClient)getApplicationContext()).setParticipant(fromUser);
                                    } else {
                                        mUserLocation.setParticipant(thisPax);
                                        ((UserClient)getApplicationContext()).setParticipant(thisPax);
                                    }
                                    mUserLocation.setUser(user);
                                    ((UserClient)getApplicationContext()).setUser(user);
                                    getLastKnownLocation();
                                }
                            }
                        });
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
