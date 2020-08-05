package com.elkasaga.undegraduatethesisproject.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.activities.SplashActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsDiscussionActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsItineraryActivity;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class NotificationBroadcast extends BroadcastReceiver {

    private String notiftype = "";
    private int reqcode = 0;

    private GroupTour gt;
    private Itinerary itinerary;


    FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    @Override
    public void onReceive(final Context context, Intent intent) {

        //important
        //get request code from intent extra
        reqcode = intent.getIntExtra("reqcode", 200);

        //set firebase settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build();
        if (mDb.getFirestoreSettings() == null){
            mDb.setFirestoreSettings(settings);
        }


        //init new intent, when push notification clicked, will be directed to as defined in this intent
        Intent itin = new Intent();

        //get sharedPreferences
        final SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(reqcode), Context.MODE_PRIVATE);
        notiftype = sharedPreferences.getString("notiftype", "");


        Log.d("Broad", "notiftype = "+notiftype);
        Log.d("Broad", "reqcode = "+reqcode);

        if ( notiftype.equals("notif_gt_start") || notiftype.equals("notif_gt_end")){
            itin = new Intent(context, SplashActivity.class);
            gt = new GroupTour( sharedPreferences.getString("tourtitle", ""), sharedPreferences.getString("tourid", ""),
                                sharedPreferences.getString("tourleader", ""), sharedPreferences.getString("startdate", ""),
                                sharedPreferences.getString("enddate", ""), sharedPreferences.getString("starttime", ""),
                                sharedPreferences.getString("endtime", ""), sharedPreferences.getLong("tourstatus", 0)
                                );
            itin.putExtra("tourid", gt.getTourid());
        } else if (notiftype.equals("ogt_iti_start") || notiftype.equals("ogt_iti_end")) {
            itin = new Intent(context, TourDetailsItineraryActivity.class);
            itinerary = new Itinerary( sharedPreferences.getString("itineraryid", ""), sharedPreferences.getString("date", ""),
                sharedPreferences.getString("starttime", ""), sharedPreferences.getString("endtime", ""), sharedPreferences.getString("description", ""),
                sharedPreferences.getLong("itinerarystatus", 0), sharedPreferences.getLong("day", 0),
                sharedPreferences.getLong("itinerarycategory", 0), sharedPreferences.getLong("indexstarttime", 0) );
        } else if (notiftype.equals("emergency")){
            itin = new Intent(context, TourDetailsDiscussionActivity.class);
            gt = new GroupTour( sharedPreferences.getString("tourtitle", ""), sharedPreferences.getString("tourid", ""),
                    sharedPreferences.getString("tourleader", ""), sharedPreferences.getString("startdate", ""),
                    sharedPreferences.getString("enddate", ""), sharedPreferences.getString("starttime", ""),
                    sharedPreferences.getString("endtime", ""), sharedPreferences.getLong("tourstatus", 0)
            );
        }


        PendingIntent toItinerary = PendingIntent.getActivity(context, sharedPreferences.getInt("reqcode", 0), itin, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sleepNotif")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(sharedPreferences.getString("title", ""))
                .setContentText(sharedPreferences.getString("message", ""))
                .setContentIntent(toItinerary)
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (notiftype.equals("notif_gt_start") || notiftype.equals("notif_gt_end")){
            DocumentReference userTourRef = mDb.collection("UserTour").document(FirebaseAuth.getInstance().getUid())
                    .collection("GroupTour").document(gt.getTourid());
            if ( notiftype.equals("notif_gt_start") ){
                gt.setTourstatus(1);
            } else if ( notiftype.equals("notif_gt_end") ){
                gt.setTourstatus(0);
            }
            userTourRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        DocumentReference gtRef = mDb.collection("GroupTour").document(gt.getTourid());
                        gtRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    ((UserClient) context.getApplicationContext()).setGroupTour(gt);
                                    notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                                }
                            }
                        });
                    } else {
                        notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                    }
                }
            });
        } else if (notiftype.equals("ogt_iti_start") || notiftype.equals("ogt_iti_end")) {
            DocumentReference itiRef = mDb.collection("GroupTour").document(sharedPreferences.getString("tourid", ""))
                    .collection("Itineraries").document(sharedPreferences.getString("itineraryid", ""));

            if ( notiftype.equals("ogt_iti_start") ) {
                itinerary.setItinerarystatus(1);
            } else if ( notiftype.equals("ogt_iti_end") ) {
                itinerary.setItinerarystatus(0);
            }

            itiRef.set(itinerary).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                        Log.d("NB", "Changing itinerary status successfull: change iti status");
                    } else {
                        notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                        Log.d("NB", "Changing itinerary status failed: change nothing");
                    }
                }
            });
        } else if ( notiftype.equals("emergency") ){
            final DocumentReference chatRef = mDb.collection("GroupTour").document(sharedPreferences.getString("tourid", ""))
                    .collection("Discussion").document(sharedPreferences.getString("chatid", ""));
            chatRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult() != null){
                        ChatMessage chatMessage = task.getResult().toObject(ChatMessage.class);
                        ArrayList<String> uid = chatMessage.getReadByList();
                        uid.add( FirebaseAuth.getInstance().getUid() );
                        chatMessage.setReadByList(uid);
                        chatRef.set(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                                } else {
                                    notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                                }
                            }
                        });
                    }
                }
            });
        }

    }

}