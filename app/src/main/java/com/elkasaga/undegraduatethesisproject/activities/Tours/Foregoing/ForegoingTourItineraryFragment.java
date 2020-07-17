package com.elkasaga.undegraduatethesisproject.activities.Tours.Foregoing;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsItineraryActivity;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.utils.DateConvert;
import com.elkasaga.undegraduatethesisproject.utils.ListedItineraryAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ForegoingTourItineraryFragment extends Fragment {

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    ArrayList<String> dates;

    private RecyclerView listedItineraryContainer;
    private ArrayList<Itinerary> listItinerary;
    ArrayList<ArrayList<Itinerary>> allItinerary;
    private ListedItineraryAdapter itineraryAdapter;
    private ListenerRegistration mItineraryEventListener;
    private Set<String> mItineraryId = new HashSet<>();

    SharedPreferences tourPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foregoingtour_itinerary, container, false);
        tourPreference = getContext().getSharedPreferences("GT_BASICINFO", getContext().MODE_PRIVATE);
        dates = DateConvert.getDates(tourPreference.getString("startdate", ""), tourPreference.getString("enddate", ""));
        listedItineraryContainer = view.findViewById(R.id.listedParticipantContainerForegoing);
        listItinerary = new ArrayList<Itinerary>();
        allItinerary = new ArrayList<ArrayList<Itinerary>>();
        getItineraryData();
        initItineraryRecyclerView();
        return view;
    }

    private void getItineraryData(){
        CollectionReference itineraryPerDayQuery = mDb.collection("GroupTour")
                .document(tourPreference.getString("tourid", ""))
                .collection("Itineraries");

        mItineraryEventListener = itineraryPerDayQuery
                .orderBy("day", Query.Direction.ASCENDING)
                .orderBy("indexstarttime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            return;
                        }
                        if (queryDocumentSnapshots != null){

                            for (int i = 0; i<queryDocumentSnapshots.size(); i++){
                                Itinerary itinerary = queryDocumentSnapshots.getDocuments().get(i).toObject(Itinerary.class);
                                if (!mItineraryId.contains(itinerary.getItineraryid())){
                                    mItineraryId.add(itinerary.getItineraryid());
                                    listItinerary.add(itinerary);
                                }
                            }
                            itineraryAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    private void initItineraryRecyclerView() {

        itineraryAdapter = new ListedItineraryAdapter(getContext(), listItinerary);
        listedItineraryContainer.setAdapter(itineraryAdapter);
        listedItineraryContainer.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
