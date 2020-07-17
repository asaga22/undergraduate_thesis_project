package com.elkasaga.undegraduatethesisproject.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.SplashActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsItineraryActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationBroadcast extends BroadcastReceiver {

    private static String notiftype = "";
    public static GroupTour gt;
    public static int reqcode = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {

        Intent itin = new Intent();
        Log.d("Broad", "notiftype = "+notiftype);
        Log.d("Broad", "reqcode = "+reqcode);

        if (notiftype.equals("notif_gt_start")){
            itin = new Intent(context, SplashActivity.class);
            itin.putExtra("tourid", gt.getTourid());
        } else if (notiftype.equals("ogt_iti_start")){
            itin = new Intent(context, TourDetailsItineraryActivity.class);
        }

        final SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(reqcode), Context.MODE_PRIVATE);

        PendingIntent toItinerary = PendingIntent.getActivity(context, sharedPreferences.getInt("reqcode", 0), itin, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sleepNotif")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(sharedPreferences.getString("title", ""))
                .setContentText(sharedPreferences.getString("message", ""))
                .setContentIntent(toItinerary)
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (notiftype.equals("notif_gt_start")){
            DocumentReference userTourRef = FirebaseFirestore.getInstance().collection("UserTour").document(FirebaseAuth.getInstance().getUid())
                    .collection("GroupTour").document(gt.getTourid());
            gt.setTourstatus(1);
            userTourRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                    } else {
                        notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
                    }
                }
            });
        } else{
            notificationManager.notify(sharedPreferences.getInt("reqcode", 0), builder.build());
        }

    }

   public static void setGt(GroupTour groupTour){
        gt = new GroupTour(groupTour.getTourtitle(), groupTour.getTourid(), groupTour.getTourleader(), groupTour.getStartdate(),
                groupTour.getEnddate(), groupTour.getStarttime(), groupTour.getEndtime(), groupTour.getTourstatus());
   }

    public static void setNotiftype(String notif){
        notiftype = notif;
    }

    public static void setReqcode(int reqCode){
        reqcode = reqCode;
    }


}