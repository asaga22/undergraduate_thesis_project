package com.elkasaga.undegraduatethesisproject.activities.Tours.Foregoing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantForegoingAdapter;
import com.elkasaga.undegraduatethesisproject.utils.ListedRatingAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ForegoingTourParticipantFragment extends Fragment {

    private RecyclerView listedParticipantContainer;
    private ArrayList<Participant> listedParticipant;
    private ListedParticipantForegoingAdapter participantAdapter;
    private ListenerRegistration mParticipantEventListener;
    private Set<String> mParticipantId = new HashSet<>();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private SharedPreferences sharedPreferences;
    private TextView total;
    private int allRepPaxTotal = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foregoingtour_participants, container, false);
        sharedPreferences = getContext().getSharedPreferences("GT_BASICINFO", getContext().MODE_PRIVATE);
        listedParticipantContainer = view.findViewById(R.id.listedParticipantContainerForegoing);
        listedParticipant = new ArrayList<Participant>();
        total = view.findViewById(R.id.totalParticipant);
        initRecyclerView();
        getParticipantData();
        return view;
    }


    private void getParticipantData(){
        Log.d("TAG", "get pax data again!");
        CollectionReference userTourRef = mDb
                .collection("GroupTour")
                .document(sharedPreferences.getString("tourid", "")).collection("Participants");
        mParticipantEventListener = userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Participant pax = doc.toObject(Participant.class);
                        if (!mParticipantId.contains(pax.getUid())){
                            mParticipantId.add(pax.getUid());
                            listedParticipant.add(pax);
                        }
                    }
                    Log.d("", "ALLREPPAXTOTAL = "+allRepPaxTotal);
                    participantAdapter.notifyDataSetChanged();
                    total.setText(listedParticipant.size() + " Participants");
                }
            }
        });
    }

    private void initRecyclerView(){
        participantAdapter = new ListedParticipantForegoingAdapter(getContext(), listedParticipant);
        listedParticipantContainer.setAdapter(participantAdapter);
        listedParticipantContainer.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
