package com.elkasaga.undegraduatethesisproject.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.Presence;
import com.elkasaga.undegraduatethesisproject.models.RepresentedPassanger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedParticipantPresenceAdapter extends RecyclerView.Adapter <ListedParticipantPresenceAdapter.MyViewHolder>{

    Context context;
    ArrayList<Participant> participant;
    SharedPreferences tourPreferences;

    public ListedParticipantPresenceAdapter(Context c, ArrayList<Participant> p){
        context = c;
        participant = p;
    }

    @NonNull
    @Override
    public ListedParticipantPresenceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListedParticipantPresenceAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_presence, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListedParticipantPresenceAdapter.MyViewHolder myViewHolder, final int i) {
        SharedPreferences itiPreferences = context.getSharedPreferences("ITI_INFO", context.MODE_PRIVATE);

        myViewHolder.namePres.setText(participant.get(i).getFullname());
        Picasso.with(context).load(participant.get(i).getAvatar()).centerCrop().fit().into(myViewHolder.avaPres);


        if (participant.get(i).getPresent() == 2){
            myViewHolder.textCheckedBy.setVisibility(View.GONE);
            myViewHolder.textPresent.setVisibility(View.GONE);
        } else {
            myViewHolder.btnAbTl.setVisibility(View.GONE);
            myViewHolder.btnPresTl.setVisibility(View.GONE);

            DocumentReference paxPresPref = FirebaseFirestore.getInstance().collection("GroupTour")
                    .document(((UserClient)context.getApplicationContext()).getGroupTour().getTourid())
                    .collection("Itineraries").document(itiPreferences.getString("itineraryid", ""))
                    .collection("Presence").document(participant.get(i).getUid());
            paxPresPref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Presence presence = task.getResult().toObject(Presence.class);
                        if (presence.getCheckedby() == 0){
                            myViewHolder.textCheckedBy.setText("checked by Participant");
                        } else {
                            myViewHolder.textCheckedBy.setText("checked by Tour Leader");
                        }

                        if (participant.get(i).getPresent() == 1){
                            myViewHolder.textPresent.setText("Present");
                        } else if (participant.get(i).getPresent() == 0){
                            myViewHolder.textPresent.setText("Absent");
                        }
                    }
                }
            });

        }


        myViewHolder.btnPresTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckAsPresent(participant.get(i));
            }
        });

        myViewHolder.btnAbTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckAsAbsent(participant.get(i));
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
        return participant.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView avaPres;
        TextView namePres, textPresent, textCheckedBy;
        Button btnPresTl, btnAbTl;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            avaPres = itemView.findViewById(R.id.avaPres);
            namePres = itemView.findViewById(R.id.namePres);
            btnPresTl = itemView.findViewById(R.id.btnPresTl);
            textPresent = itemView.findViewById(R.id.textPresAb);
            textCheckedBy = itemView.findViewById(R.id.textConfirmedBy);
            btnAbTl = itemView.findViewById(R.id.btnAbTl);
        }


    }


}
