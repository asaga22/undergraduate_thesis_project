package com.elkasaga.undegraduatethesisproject.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.elkasaga.undegraduatethesisproject.utils.NotificationBroadcast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 30 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 7 * 2000; /* 2 sec */

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();

        if (mDb.getFirestoreSettings() == null){
            mDb.setFirestoreSettings(settings);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        startCheckChat();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            User user = ((UserClient)(getApplicationContext())).getUser();
                            Participant thisPax = ((UserClient)(getApplicationContext())).getParticipant();
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            UserLocation userLocation = new UserLocation(geoPoint, null, user, thisPax);
                            saveUserLocation(userLocation);
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveUserLocation(final UserLocation userLocation){

        try{
            //update pax loc in Ongoing GT
            DocumentReference paxLocs = mDb.collection("GroupTour")
                    .document( ((UserClient)getApplicationContext()).getGroupTour().getTourid() )
                    .collection("ParticipantLocation").document(FirebaseAuth.getInstance().getUid());
            paxLocs.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: \ninserted user location into database." +
                                "\n latitude: " + userLocation.getGeoPoint().getLatitude() +
                                "\n longitude: " + userLocation.getGeoPoint().getLongitude());
                    }
                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: "  + e.getMessage() );
            stopSelf();
        }

    }

    private void checkChat(){
        if (FirebaseAuth.getInstance().getUid() != null){
            if ( ((UserClient)getApplicationContext()).getGroupTour() != null ){
                Query emergencyRef = mDb.collection("GroupTour").document( ((UserClient)getApplicationContext()).getGroupTour().getTourid() )
                        .collection("Discussion").whereEqualTo("systemgenerated", true);
                emergencyRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots.size() != 0){
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                                ChatMessage chatMessage = queryDocumentSnapshots.getDocuments().get(i).toObject(ChatMessage.class);
                                if (!chatMessage.getUser().getUid().equals(FirebaseAuth.getInstance().getUid())){
                                    if (chatMessage.getReadByList() == null){
                                        Log.d(TAG, "DAPET NIH CHECK CHATNYA  = "+ chatMessage.getMessageid());
//                                    buildNotifForEmergency(chatMessage);
                                    } else {
                                        if ( !chatMessage.getReadByList().contains(FirebaseAuth.getInstance().getUid()) ){
                                            Log.d(TAG, "DAPET NIH CHECK CHATNYA  = "+ chatMessage.getMessageid());
                                            buildNotifForEmergency(chatMessage);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public void buildNotifForEmergency(ChatMessage chatMessage){
        Intent intent = new Intent( LocationService.this, NotificationBroadcast.class);
        intent.putExtra("reqcode", -10);

        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(-10), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title", "Emergency Notification");
        editor.putString("message", chatMessage.getMessage());
        editor.putInt("reqcode", (int) -10);
        editor.putString("chatid", chatMessage.getMessageid());
        editor.putString("notiftype", "emergency");
        editor.putString("tourtitle", ((UserClient)getApplicationContext()).getGroupTour().getTourtitle());
        editor.putString("tourid", ((UserClient)getApplicationContext()).getGroupTour().getTourid());
        editor.putString("startdate", ((UserClient)getApplicationContext()).getGroupTour().getStartdate());
        editor.putString("enddate", ((UserClient)getApplicationContext()).getGroupTour().getEnddate());
        editor.putString("starttime", ((UserClient)getApplicationContext()).getGroupTour().getStarttime());
        editor.putString("endtime", ((UserClient)getApplicationContext()).getGroupTour().getEndtime());
        editor.putLong("tourstatus", ((UserClient)getApplicationContext()).getGroupTour().getTourstatus());
        editor.putString("tourleader", ((UserClient)getApplicationContext()).getGroupTour().getTourleader());
        editor.apply();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(LocationService.this, (int) -10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                1000,
                pendingIntent);
    }

    Runnable checkChatRunnable = new Runnable(){

        @Override
        public void run() {
            try{
                checkChat();
            } finally {
                handler.postDelayed(checkChatRunnable, (10 * 1000));
            }
        }
    };

    private  void startCheckChat(){
        checkChatRunnable.run();
    }

}

