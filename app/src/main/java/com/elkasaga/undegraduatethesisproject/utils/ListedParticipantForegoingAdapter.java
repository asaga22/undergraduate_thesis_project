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
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.RepresentedPassanger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListedParticipantForegoingAdapter extends RecyclerView.Adapter <ListedParticipantForegoingAdapter.MyViewHolder>{

    Context context;
    ArrayList<Participant> participant;
    ProgressBar mProgressBar;
    TextView mPleaseWait, descRepPaxDialog;
    Button addRepPax;
    EditText inputRepPax;
    ImageView closeDialog;
    SharedPreferences tourPreferences;

    public ListedParticipantForegoingAdapter(Context c, ArrayList<Participant> p){
        context = c;
        participant = p;
    }

    @NonNull
    @Override
    public ListedParticipantForegoingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListedParticipantForegoingAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_listed_participants_foregoing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListedParticipantForegoingAdapter.MyViewHolder myViewHolder, final int i) {
        tourPreferences = context.getSharedPreferences("GT_BASICINFO", context.MODE_PRIVATE);
        myViewHolder.participantname.setText(participant.get(i).getFullname());
        Picasso.with(context).load(participant.get(i).getAvatar()).centerCrop().fit().into(myViewHolder.avatar);

        if (!participant.get(i).isRepresent()){
            myViewHolder.participantisrepresent.setVisibility(View.GONE);
        } else if(participant.get(i).isRepresent()){

            CollectionReference repRef = FirebaseFirestore.getInstance()
                    .collection("GroupTour")
                    .document(tourPreferences.getString("tourid", ""))
                    .collection("Participants")
                    .document(participant.get(i).getUid())
                    .collection("Represented");

            repRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    myViewHolder.participantisrepresent.setText("Representing "+ queryDocumentSnapshots.size() + " other participant(s)");
                }
            });
        }

        final String userid = participant.get(i).getUid();
        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!participant.get(i).isRepresent()){
                    return false;
                } else {
                    buildDialogShowRepresentedList(i);
                    return true;
                }
            }
        });

    }

    private void buildDialogShowRepresentedList(final int index){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_showreprsented_pax);
        final TextView repPaxListText = (TextView) dialog.findViewById(R.id.repPaxList);
        final TextView descRepPaxList = (TextView) dialog.findViewById(R.id.descRepPaxList);
        ImageView closeDialogRepPaxList = (ImageView) dialog.findViewById(R.id.closeDialogRepPaxList);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        tourPreferences = context.getSharedPreferences("GT_BASICINFO", context.MODE_PRIVATE);
        final LinearLayout lView = new LinearLayout(context);
        CollectionReference repPaxListRef = FirebaseFirestore.getInstance()
                .collection("GroupTour")
                .document(tourPreferences.getString("tourid", ""))
                .collection("Participants")
                .document(participant.get(index).getUid())
                .collection("Represented");
        repPaxListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int j = 0;
                descRepPaxList.setText(participant.get(index).getFullname() + " represent "+queryDocumentSnapshots.size()+" other participants");
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    j++;
                    RepresentedPassanger repPaxList = doc.toObject(RepresentedPassanger.class);
                    repPaxListText.append(String.valueOf(j) + ". "+ repPaxList.getFullname());
                    repPaxListText.append("\n");
//                    lView.addView(repPaxListText);
                }
            }
        });

        closeDialogRepPaxList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    @Override
    public int getItemCount() {
        return participant.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView participantname, participantisrepresent;
        ImageView avatar, moreAddRepPax;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            participantname = itemView.findViewById(R.id.participantNameForegoing);
            participantisrepresent = itemView.findViewById(R.id.participantIsRepresentForegoing);
            avatar = itemView.findViewById(R.id.lisedPaxAvaForegoing);
        }


    }


}
