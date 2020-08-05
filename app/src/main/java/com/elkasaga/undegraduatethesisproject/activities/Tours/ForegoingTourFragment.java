package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.utils.ListedToursAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ForegoingTourFragment extends Fragment {

    private static final String TAG = "";
    private static final int ACTIVITY_NUM = 1;

    RecyclerView listedToursContainer;
    ArrayList<GroupTour> listedTour;
    ListenerRegistration listedForegoingTourListener;
    ListedToursAdapter groupTourAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    String uid;
    long category;

    private TextView nohistory;
    private ScrollView scrollHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foregoingtours, container, false);
        mDb = FirebaseFirestore.getInstance();
        listedToursContainer = (RecyclerView) view.findViewById(R.id.listedForegoing);
        listedTour = new ArrayList<GroupTour>();
        nohistory = view.findViewById(R.id.nohistory);
        scrollHistory = view.findViewById(R.id.scrollHistory);
        getUserDetailsFromPreference();
        initForgoingTourRecyclerView();
        getForeGoingTourData();
        return view;
    }

    private void getForeGoingTourData(){
        Query userTourRef = mDb
                .collection("UserTour")
                .document(uid).collection("GroupTour").whereEqualTo("tourstatus", 0);

        listedForegoingTourListener = userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                        GroupTour gt = queryDocumentSnapshots.getDocuments().get(i).toObject(GroupTour.class);
                        listedTour.add(gt);
                        Log.d(TAG, "TOURTITLE "+i+" "+gt.getTourtitle()+" | STATUS = "+gt.getTourstatus());
                    }
                    groupTourAdapter.notifyDataSetChanged();
                } else {
                    nohistory.setVisibility(View.VISIBLE);
                    scrollHistory.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initForgoingTourRecyclerView(){
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
