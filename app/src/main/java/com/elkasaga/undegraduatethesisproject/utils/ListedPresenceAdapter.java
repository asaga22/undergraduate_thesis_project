package com.elkasaga.undegraduatethesisproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.Presence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedPresenceAdapter extends RecyclerView.Adapter <ListedPresenceAdapter.MyViewHolder>{

    Context context;
    ArrayList<Participant> participantList;


    public ListedPresenceAdapter(Context c, ArrayList<Participant> participantList){
        context = c;
        this.participantList = participantList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_presence, viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.namePres.setText(participantList.get(i).getFullname());
        Picasso.with(context).load(participantList.get(i).getAvatar()).centerCrop().fit().into(myViewHolder.avaPres);

        myViewHolder.btnPresTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckAsPresent(participantList.get(i));
            }
        });

        myViewHolder.btnAbTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckAsAbsent(participantList.get(i));
            }
        });

    }

    private void doCheckAsPresent(final Participant pax){
        SharedPreferences itiPreferences = context.getSharedPreferences("ITI_INFO", context.MODE_PRIVATE);
        DocumentReference paxPresRef = FirebaseFirestore.getInstance().collection("GroupTour")
                .document(((UserClient)context.getApplicationContext()).getGroupTour().getTourid())
                .collection("Itineraries").document(itiPreferences.getString("itineraryid", ""))
                .collection("Presence").document(pax.getUid());
        final Participant participant = pax;
        participant.setPresent(1);
        Presence presence = new Presence(0, null, participant);
        paxPresRef.set(presence).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference paxRef = FirebaseFirestore.getInstance().collection("GroupTour")
                        .document(((UserClient)context.getApplicationContext()).getGroupTour().getTourid())
                        .collection("Participants").document(pax.getUid());
                paxRef.set(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Participant will be/is present at this activity", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void doCheckAsAbsent(final Participant pax){
        SharedPreferences itiPreferences = context.getSharedPreferences("ITI_INFO", context.MODE_PRIVATE);
        DocumentReference paxPresRef = FirebaseFirestore.getInstance().collection("GroupTour")
                .document(((UserClient)context.getApplicationContext()).getGroupTour().getTourid())
                .collection("Itineraries").document(itiPreferences.getString("itineraryid", ""))
                .collection("Presence").document(pax.getUid());
        final Participant participant = pax;
        participant.setPresent(0);
        Presence presence = new Presence(0, null, participant);
        paxPresRef.set(presence).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference paxRef = FirebaseFirestore.getInstance().collection("GroupTour")
                        .document(((UserClient)context.getApplicationContext()).getGroupTour().getTourid())
                        .collection("Participants").document(pax.getUid());
                paxRef.set(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Participant will be/is absent at this activity", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView avaPres;
        TextView namePres;
        Button btnPresTl, btnAbTl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            avaPres = itemView.findViewById(R.id.avaPres);
            namePres = itemView.findViewById(R.id.namePres);
            btnPresTl = itemView.findViewById(R.id.btnPresTl);
            btnAbTl = itemView.findViewById(R.id.btnAbTl);

        }
    }

}