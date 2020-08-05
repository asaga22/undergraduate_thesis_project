package com.elkasaga.undegraduatethesisproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Home.PresenceActivity;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.Presence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListedItineraryAdapter extends RecyclerView.Adapter <ListedItineraryAdapter.MyViewHolder>{

    Context context;
    ArrayList<Itinerary> itineraryList;
    SharedPreferences tourPreferences, userPreferences;
    DocumentReference paxRef;

    public ListedItineraryAdapter(Context c, ArrayList<Itinerary> itinerary){
        context = c;
        itineraryList = itinerary;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_itinerary_item_timeline, viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        tourPreferences = context.getSharedPreferences("GT_BASICINFO", context.MODE_PRIVATE);
        userPreferences = context.getSharedPreferences("USER_DETAILS", context.MODE_PRIVATE);

        if(itineraryList.get(i).getItinerarystatus() == 0){
            myViewHolder.wrapper.setCardElevation(0);
        } else if (itineraryList.get(i).getItinerarystatus() == 1){
            myViewHolder.wrapper.setBackgroundResource(R.drawable.white_rounded_corner);
            myViewHolder.circleStart.setImageResource(R.drawable.circle_toscagreen);
            myViewHolder.circleEnd.setImageResource(R.drawable.circle_toscagreen);
            myViewHolder.line.setBackgroundResource(R.color.toscagreen);
        } else if ( itineraryList.get(i).getItinerarystatus() == 2 ){
            myViewHolder.wrapper.setBackgroundResource(R.drawable.white_rounded_corner);
        }

        myViewHolder.dayNo.setText( "Day "+String.valueOf(itineraryList.get(i).getDay()));
        myViewHolder.startTimeInTimeline.setText(itineraryList.get(i).getStarttime());
        myViewHolder.endTimeInTimeline.setText(itineraryList.get(i).getEndtime());
        myViewHolder.activityDescription.setText(itineraryList.get(i).getDescription());

        if (tourPreferences.getLong("tourstatus", 0) == 1){
            if (userPreferences.getLong("category", 0) == 1){
                myViewHolder.relPresAb.setVisibility(View.VISIBLE);
                myViewHolder.btnPresence.setVisibility(View.GONE);
                if (itineraryList.get(i).getItinerarystatus() == 2 || itineraryList.get(i).getItinerarystatus() == 0){
                    myViewHolder.btnPresentPar.setEnabled(false);
                    myViewHolder.btnAbsentPar.setEnabled(false);
                    myViewHolder.btnPresentPar.setBackgroundResource(R.drawable.grey_outline_rounded_corner);
                    myViewHolder.btnAbsentPar.setBackgroundResource(R.drawable.grey_outline_rounded_corner);
                    myViewHolder.btnPresentPar.setTextColor(context.getResources().getColor(R.color.defaultGrey));
                    myViewHolder.btnAbsentPar.setTextColor(context.getResources().getColor(R.color.defaultGrey));
                }
            } else if (userPreferences.getLong("category", 0) == 0){
                myViewHolder.relPresAb.setVisibility(View.GONE);
                myViewHolder.btnPresence.setVisibility(View.VISIBLE);
                if (itineraryList.get(i).getItinerarystatus() == 2 || itineraryList.get(i).getItinerarystatus() == 0){
                    myViewHolder.btnPresence.setEnabled(false);
                    myViewHolder.btnPresence.setBackgroundResource(R.drawable.grey_outline_rounded_corner);
                    myViewHolder.btnPresence.setTextColor(context.getResources().getColor(R.color.defaultGrey));
                }
            }
        } else {
            myViewHolder.relPresAb.setVisibility(View.GONE);
            myViewHolder.btnPresence.setVisibility(View.GONE);
        }

         paxRef = FirebaseFirestore.getInstance().collection("GroupTour")
                .document(tourPreferences.getString("tourid", "")).collection("Participants")
                .document(FirebaseAuth.getInstance().getUid());

        //btn present click
        myViewHolder.btnPresentPar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPresent(tourPreferences.getString("tourid", ""), itineraryList.get(i).getItineraryid());
            }
        });

        //btn absent click
        myViewHolder.btnAbsentPar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAbsent(tourPreferences.getString("tourid", ""), itineraryList.get(i).getItineraryid());
            }
        });

        //btn presence click
        myViewHolder.btnPresence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences itiPreferences = context.getSharedPreferences("ITI_INFO", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = itiPreferences.edit();
                String itineraryid, date, starttime, endtime, description;
                long itinerarystatus, day, itinerarycategory, indexstarttime;
                editor.putString("itineraryid", itineraryList.get(i).getItineraryid());
                editor.putString("date", itineraryList.get(i).getDate());
                editor.putString("starttime", itineraryList.get(i).getStarttime());
                editor.putString("endtime", itineraryList.get(i).getEndtime());
                editor.putString("description", itineraryList.get(i).getDescription());
                editor.putLong("itinerarystatus", itineraryList.get(i).getItinerarystatus());
                editor.putLong("day", itineraryList.get(i).getDay());
                editor.putLong("itinerarycategory", itineraryList.get(i).getItinerarycategory());
                editor.putLong("indexstarttime", itineraryList.get(i).getIndexstarttime());
                editor.apply();
                Intent toPresence = new Intent(context, PresenceActivity.class);
                context.startActivity(toPresence);
            }
        });

    }

    public void doPresent(String gtid, String itinid) {
        final DocumentReference userPresRef = FirebaseFirestore.getInstance().collection("GroupTour")
                .document(gtid).collection("Itineraries").document(itinid).collection("Presence")
                .document(FirebaseAuth.getInstance().getUid());
        paxRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final Participant pax = task.getResult().toObject(Participant.class);
                    pax.setPresent(1);
                    Presence presence = new Presence(1, null, pax);
                    userPresRef.set(presence).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                paxRef.set(pax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(context, "You will be/are present at this activity", Toast.LENGTH_SHORT).show();
                                        } else{
                                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void doAbsent(String gtid, String itinid){
        final DocumentReference userPresRef = FirebaseFirestore.getInstance().collection("GroupTour")
                .document(gtid).collection("Itineraries").document(itinid).collection("Presence")
                .document(FirebaseAuth.getInstance().getUid());
        paxRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final Participant pax = task.getResult().toObject(Participant.class);
                    pax.setPresent(0);
                    Presence presence = new Presence(1, null, pax);
                    userPresRef.set(presence).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                paxRef.set(pax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "You will be /are absent at this activity", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return itineraryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView startTimeInTimeline, endTimeInTimeline, activityDescription, dayNo;
        CardView wrapper;
        RelativeLayout relPresAb;
        Button btnPresentPar, btnAbsentPar, btnPresence;
        ImageView circleStart, circleEnd;
        View line;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            wrapper = itemView.findViewById(R.id.itineraryWrapper);
            dayNo = itemView.findViewById(R.id.dayNo);
            startTimeInTimeline = itemView.findViewById(R.id.startTimeInTimeline);
            endTimeInTimeline = itemView.findViewById(R.id.endTimeInTimeline);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            relPresAb = itemView.findViewById(R.id.relPresAb);
            btnAbsentPar = itemView.findViewById(R.id.btnAbsentPar);
            btnPresentPar = itemView.findViewById(R.id.btnPresentPar);
            btnPresence = itemView.findViewById(R.id.btnPresence);
            circleEnd = itemView.findViewById(R.id.circleEnd);
            circleStart = itemView.findViewById(R.id.circleStart);
            line = itemView.findViewById(R.id.line);

        }
    }

}