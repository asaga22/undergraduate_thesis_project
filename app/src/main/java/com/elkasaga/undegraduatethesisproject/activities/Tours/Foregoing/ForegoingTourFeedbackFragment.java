package com.elkasaga.undegraduatethesisproject.activities.Tours.Foregoing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Rating;
import com.elkasaga.undegraduatethesisproject.utils.ListedRatingAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ForegoingTourFeedbackFragment extends Fragment {

    TextView feedbackTitle, nofeedback;
    RatingBar ratingAll;
    RecyclerView listedRatingForegoing;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private SharedPreferences tourPreferences;
    private ArrayList<Rating> ratingList;
    private ListenerRegistration ratingListener;
    private Set<String> uids = new HashSet<>();
    private ListedRatingAdapter ratingAdapter;

    private float overallRatingVal = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foregoingtour_feedback, container, false);
        feedbackTitle = (TextView) view.findViewById(R.id.feedbackTitle);
        ratingAll = (RatingBar) view.findViewById(R.id.ratingAll);
        listedRatingForegoing = (RecyclerView) view.findViewById(R.id.listedRatingForegoing);
        nofeedback = (TextView) view.findViewById(R.id.nofeedbackfound);
        ratingList = new ArrayList<>();
        tourPreferences = getContext().getSharedPreferences("GT_BASICINFO", getContext().MODE_PRIVATE);

        initRatingRecyclerView();
        getRatingsData();
        return view;
    }

    private void getRatingsData(){
        CollectionReference ratingsRef = mDb.collection("GroupTour")
                .document(tourPreferences.getString("tourid", ""))
                .collection("Ratings");
        ratingListener = ratingsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() != 0){
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                        Rating rating  = snapshot.toObject(Rating.class);
                        if (!uids.contains(rating.getUid())){
                            uids.add(rating.getUid());
                            ratingList.add(rating);
                            overallRatingVal += (rating.getTourrate() + rating.getTourrate()) / 2;
                        }
                    }
                    ratingAdapter.notifyDataSetChanged();
                    ratingAll.setRating(overallRatingVal / ratingList.size());
                    feedbackTitle.setText("Over All Rating\n" + new DecimalFormat("#.#").format((overallRatingVal / ratingList.size())) + " Out of 5");
                } else {
                    nofeedback.setVisibility(View.VISIBLE);
                    feedbackTitle.setVisibility(View.GONE);
                    ratingAll.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initRatingRecyclerView(){
        ratingAdapter = new ListedRatingAdapter(getContext(), ratingList);
        listedRatingForegoing.setAdapter(ratingAdapter);
        listedRatingForegoing.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
