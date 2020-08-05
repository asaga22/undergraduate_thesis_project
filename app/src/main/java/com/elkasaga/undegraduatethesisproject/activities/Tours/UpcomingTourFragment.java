package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.utils.DateConvert;
import com.elkasaga.undegraduatethesisproject.utils.ListedToursAdapter;
import com.elkasaga.undegraduatethesisproject.utils.NotificationBroadcast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UpcomingTourFragment extends Fragment {

    private static final String TAG = "";
    private static final int ACTIVITY_NUM = 1;

    RecyclerView listedToursContainer;
    ArrayList<GroupTour> listedTour;
    private Set<String> mGtId = new HashSet<>();
    ListedToursAdapter groupTourAdapter;
    ListenerRegistration ListenerUpcomingTour;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    String uid;
    long category;
    TextView noupcoming;
    ScrollView scrollupcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcomingtours, container, false);
        mDb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build();

        if (mDb.getFirestoreSettings() == null){
            mDb.setFirestoreSettings(settings);
        }
        listedToursContainer = view.findViewById(R.id.listedUpcoming);
        listedTour = new ArrayList<GroupTour>();
        noupcoming = view.findViewById(R.id.noupcoming);
        scrollupcoming = view.findViewById(R.id.scrollUpcoming);
        getUserDetailsFromPreference();
        initRecycleView();
        initListedToursRecyclerView();
        return view;
    }

    private void initListedToursRecyclerView(){

        Query userTourRef = mDb
                .collection("UserTour")
                .document(uid).collection("GroupTour").whereEqualTo("tourstatus", 2);

        userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++){

                        final GroupTour gt = queryDocumentSnapshots.getDocuments().get(i).toObject(GroupTour.class);

                        Date currentDate = new Date();
                        Date startdate = DateConvert.convertStringToDate(gt.getStartdate()+" "+gt.getStarttime());
                        Date enddate = DateConvert.convertStringToDate(gt.getEnddate()+" "+gt.getEndtime());
                        long currentMillis  = DateConvert.dateToMillis(currentDate);
                        long startmillis = DateConvert.dateToMillis(startdate);
                        long endmillis = DateConvert.dateToMillis(enddate);

                        if (startmillis >= currentMillis && endmillis >= currentMillis){

                            //start
                            Intent intent = new Intent(getContext(), NotificationBroadcast.class);
                            intent.putExtra("reqcode", i);

                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(i), getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("title", "Group Tour Reminder");
                            editor.putString("message", gt.getTourtitle() +" is started");
                            editor.putInt("reqcode", i);
                            editor.putString("notiftype", "notif_gt_start");
                            editor.putString("tourtitle", gt.getTourtitle());
                            editor.putString("tourid", gt.getTourid());
                            editor.putString("startdate", gt.getStartdate());
                            editor.putString("enddate", gt.getEnddate());
                            editor.putString("starttime", gt.getStarttime());
                            editor.putString("endtime", gt.getEndtime());
                            editor.putLong("tourstatus", gt.getTourstatus());
                            editor.putString("tourleader", gt.getTourleader());
                            editor.apply();

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP,
                                    startmillis,
                                    pendingIntent);

                            Intent intentEnd = new Intent(getContext(), NotificationBroadcast.class);
                            intentEnd.putExtra("reqcode", (i+100));

                            SharedPreferences sharedPreferencesEnd = getContext().getSharedPreferences(String.valueOf((i+100)), getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editorEnd = sharedPreferencesEnd.edit();
                            editorEnd.putString("title", "Group Tour Reminder");
                            editorEnd.putString("message", gt.getTourtitle() +" is over");
                            editorEnd.putString("notiftype", "notif_gt_end");
                            editorEnd.putString("tourtitle", gt.getTourtitle());
                            editorEnd.putString("tourid", gt.getTourid());
                            editorEnd.putString("startdate", gt.getStartdate());
                            editorEnd.putString("enddate", gt.getEnddate());
                            editorEnd.putString("starttime", gt.getStarttime());
                            editorEnd.putString("endtime", gt.getEndtime());
                            editorEnd.putLong("tourstatus", gt.getTourstatus());
                            editorEnd.putString("tourleader", gt.getTourleader());
                            editorEnd.putInt("reqcode", (i+100));
                            editorEnd.apply();

                            PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(getContext(), (i+100), intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManagerEnd = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            alarmManagerEnd.set(AlarmManager.RTC_WAKEUP,
                                    endmillis,
                                    pendingIntentEnd);
                        }

                        if (startdate.after(currentDate) && enddate.after(currentDate)){
                            if (!mGtId.contains(gt.getTourid())){
                                mGtId.add(gt.getTourid());
                                listedTour.add(gt);
                                Log.d(TAG, "TOURTITLE "+i+" "+gt.getTourtitle()+" | STATUS = "+gt.getTourstatus());
                            }
                        }
                    }

                    groupTourAdapter.notifyDataSetChanged();
                } else {
                    noupcoming.setVisibility(View.VISIBLE);
                    scrollupcoming.setVisibility(View.GONE);
                }
            }
        });
    }

    private  void initRecycleView(){
        groupTourAdapter = new ListedToursAdapter(getContext(), listedTour);
        listedToursContainer.setAdapter(groupTourAdapter);
        listedToursContainer.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getUserDetailsFromPreference(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_DETAILS", getContext().MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        category = sharedPreferences.getLong("category", 0);
    }

}