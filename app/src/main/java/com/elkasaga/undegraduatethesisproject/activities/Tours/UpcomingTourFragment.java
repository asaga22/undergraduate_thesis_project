package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.utils.DateConvert;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedToursAdapter;
import com.elkasaga.undegraduatethesisproject.utils.NotificationBroadcast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcomingtours, container, false);
        mDb = FirebaseFirestore.getInstance();
        listedToursContainer = view.findViewById(R.id.listedUpcoming);
        listedTour = new ArrayList<GroupTour>();
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
                        long startmillis = DateConvert.dateToMillis(startdate) - 10000;
                        long endmillis = DateConvert.dateToMillis(enddate);

                        if (startmillis >= currentMillis && endmillis >= currentMillis){
                            Intent intent = new Intent(getContext(), NotificationBroadcast.class);
//                            Bundle mBundle = new Bundle();
//                            mBundle.putString("notiftype", "notif_gt_start");
//                            mBundle.putInt("reqcode", i);
//                            mBundle.putParcelable("gt_object",  gt);
//                            intent.putExtras(mBundle);
//                            Bundle extras = intent.getExtras();
                            NotificationBroadcast.setNotiftype("notif_gt_start");
                            NotificationBroadcast.setReqcode(i);
                            NotificationBroadcast.setGt(gt);

                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(i), getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("title", "Group Tour Reminder");
                            editor.putString("message", gt.getTourtitle() +" is started");
                            editor.putInt("reqcode", i);
                            editor.apply();

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP,
                                    startmillis,
                                    pendingIntent);
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