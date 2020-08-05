package com.elkasaga.undegraduatethesisproject.activities.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsParticipantActivity;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.Presence;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.utils.DateConvert;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantPresenceAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedPresenceAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PresenceActivity extends AppCompatActivity {

    private final String TAG = "PresenceActivity";
    Context mContext = PresenceActivity.this;

    ImageView backArrow;
    TextView presItiDesc, presItiTime, daydate;
    RecyclerView presenceContainer;
    SharedPreferences itiPreferences, paxOrPresPref;

    private ListedParticipantPresenceAdapter presenceAdapter;
    private ArrayList<Participant> participantList;
    private ListenerRegistration presenceListener;
    private Set<String> mParticipantId = new HashSet<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_presence);
        initWidgets();
        initBackArrow();
//        paxOrPresPref = getSharedPreferences("PARTICIPANT_OR_PRESENCE", MODE_PRIVATE);
//        SharedPreferences.Editor editor = paxOrPresPref.edit();
//        editor.putString("from", "presence");
//        editor.apply();
        itiPreferences = getSharedPreferences("ITI_INFO", MODE_PRIVATE);
        presItiTime.setText(itiPreferences.getString("starttime", "") + " - "+ itiPreferences.getString("endtime", ""));
        presItiDesc.setText(itiPreferences.getString("description", ""));
        Date date = DateConvert.convertStringToDate(itiPreferences.getString("date", ""));
        daydate.setText("Day "+itiPreferences.getLong("day", 0)+", "
                        + date.getDay() + " " + DateConvert.threeLettersMonth(date.getMonth()) + " " + date.getYear());
        participantList = new ArrayList<>();
        initparticipantRecyclerView();
        getParticipantData();

    }

    private void initWidgets(){
        backArrow = findViewById(R.id.backArrowInPresence);
        presItiDesc = findViewById(R.id.presItiDesc);
        presItiTime = findViewById(R.id.presItiTime);
        daydate = findViewById(R.id.daydate);
        presenceContainer = findViewById(R.id.presenceContainer);
    }

    private void initBackArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getParticipantData(){
        Log.d("TAG", "get pax data again!");
        CollectionReference userTourRef = FirebaseFirestore.getInstance()
                .collection("GroupTour")
                .document(((UserClient)getApplicationContext()).getGroupTour().getTourid()).collection("Participants");
        presenceListener = userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Participant pax = doc.toObject(Participant.class);
                    if (!mParticipantId.contains(pax.getUid())){
                        mParticipantId.add(pax.getUid());
                        participantList.add(pax);
                    }
                }
                presenceAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initparticipantRecyclerView(){
        presenceAdapter = new ListedParticipantPresenceAdapter(mContext, participantList);
        presenceContainer.setAdapter(presenceAdapter);
        presenceContainer.setLayoutManager(new LinearLayoutManager(this));
    }
}
