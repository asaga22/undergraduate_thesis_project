package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.models.Rating;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedRatingAdapter extends RecyclerView.Adapter <ListedRatingAdapter.MyViewHolder>{

    Context context;
    ArrayList<Rating> ratingList;

    public ListedRatingAdapter(Context c, ArrayList<Rating> rating){
        context = c;
        ratingList = rating;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_listed_ratings_foregoing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(ratingList.get(i).getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                Log.d("", "U = "+ user.getFullname());
                Picasso.with(context).load(user.getAvatar()).centerCrop().fit().into(myViewHolder.listedPaxAvaForegoing);
                myViewHolder.participantNameForegoing.setText(user.getFullname());
                myViewHolder.ratingPerPax.setRating((ratingList.get(i).getTourrate() + ratingList.get(i).getTourleaderrate())/2);
                myViewHolder.rateForTour.setText(ratingList.get(i).getTourrate() + " Out of 5 for the tour");
                myViewHolder.rateForTourLeader.setText(ratingList.get(i).getTourleaderrate() + " Out of 5 for the tour leader");
                myViewHolder.aboutTourPerPax.setText(ratingList.get(i).getAbouttour());
                myViewHolder.aboutTourLeaderPerPax.setText(ratingList.get(i).getAbouttourleader());
            }
        });


//        final String tourid = grouptour.get(i).getTourid();
//
//        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toTourDetails = new Intent(context, TourDetailsActivity.class);
//                toTourDetails.putExtra("tourid", tourid);
//                context.startActivity(toTourDetails);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView listedPaxAvaForegoing;
        TextView participantNameForegoing, rateForTour, rateForTourLeader, aboutTourPerPax, aboutTourLeaderPerPax;
        RatingBar ratingPerPax;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            listedPaxAvaForegoing = (ImageView) itemView.findViewById(R.id.listedPaxAvaForegoing);
            participantNameForegoing = (TextView) itemView.findViewById(R.id.participantNameForegoing);
            rateForTour = (TextView) itemView.findViewById(R.id.rateForTour);
            rateForTourLeader = (TextView) itemView.findViewById(R.id.rateForTourLeader);
            aboutTourPerPax = (TextView) itemView.findViewById(R.id.aboutTourPerPax);
            aboutTourLeaderPerPax = (TextView) itemView.findViewById(R.id.aboutTourLeaderPerPax);
            ratingPerPax = (RatingBar) itemView.findViewById(R.id.ratingPerPax);
        }
    }

}