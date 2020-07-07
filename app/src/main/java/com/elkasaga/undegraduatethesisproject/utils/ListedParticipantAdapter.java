package com.elkasaga.undegraduatethesisproject.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsParticipantActivity;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.RepresentedPassanger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedParticipantAdapter extends RecyclerView.Adapter <ListedParticipantAdapter.MyViewHolder>{

    Context context;
    ArrayList<Participant> participant;
    ProgressBar mProgressBar;
    TextView mPleaseWait, descRepPaxDialog;
    Button addRepPax;
    EditText inputRepPax;
    ImageView closeDialog;
    SharedPreferences tourPreferences;

    public ListedParticipantAdapter(Context c, ArrayList<Participant> p){
        context = c;
        participant = p;
    }

    @NonNull
    @Override
    public ListedParticipantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListedParticipantAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_listed_participants, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListedParticipantAdapter.MyViewHolder myViewHolder, final int i) {

        myViewHolder.participantname.setText(participant.get(i).getFullname());
        Picasso.with(context).load(participant.get(i).getAvatar()).centerCrop().fit().into(myViewHolder.avatar);

        if (!participant.get(i).isRepresent()){
            myViewHolder.participantisrepresent.setVisibility(View.GONE);
        } else if(participant.get(i).isRepresent()){
            myViewHolder.participantisrepresent.setText("Representing other participant(s)");

        }

        final String userid = participant.get(i).getUid();

        myViewHolder.moreAddRepPax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, myViewHolder.moreAddRepPax);
                //inflating menu from xml resource
                popup.inflate(R.menu.participant_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addRepPaxMenu :
                                buildDialogAdd(i);
                                break;
                            case R.id.delParMenu:
                                buildDialogDeletePax(i);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


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

    private void buildDialogDeletePax(final int index){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        tourPreferences = context.getSharedPreferences("GT_BASICINFO", context.MODE_PRIVATE);

        builder.setTitle("Delete Participant");
        builder.setMessage("Are you sure to delete " +participant.get(index).getFullname()+" from the Group Tour?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                builder.setView(R.layout.progressbar).create();
                DocumentReference paxToDelRef = FirebaseFirestore.getInstance()
                        .collection("GroupTour")
                        .document(tourPreferences.getString("tourid", ""))
                        .collection("Participants")
                        .document(participant.get(index).getUid());
                paxToDelRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Participant has successfully removed from Thr Group Tour!", Toast.LENGTH_SHORT).show();
                            notifyItemMoved(index, index);
                            dialog.dismiss();
                        } else{
                            Toast.makeText(context, "Participant has successfully removed from Thr Group Tour!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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

    private void buildDialogAdd(final int index){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reprsented_pax);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        mProgressBar = dialog.findViewById(R.id.progressBarAddRepPax);
        mPleaseWait = dialog.findViewById(R.id.pleaseWaitAddRepPax);
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
        descRepPaxDialog = dialog.findViewById(R.id.descRepPaxDialog);
        addRepPax = dialog.findViewById(R.id.addRepresented);
        inputRepPax = dialog.findViewById(R.id.inputRepresentedName);
        closeDialog = dialog.findViewById(R.id.closeDialogRepPax);

        descRepPaxDialog.setText("Note: Type the full name of participant \nrepresented by "+participant.get(index).getFullname());
        tourPreferences = context.getSharedPreferences("GT_BASICINFO", context.MODE_PRIVATE);

        addRepPax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                if (!inputRepPax.getText().toString().equals("")){
                    DocumentReference paxRef = FirebaseFirestore.getInstance()
                            .collection("GroupTour")
                            .document(tourPreferences.getString("tourid", ""))
                            .collection("Participants")
                            .document(participant.get(index).getUid())
                            .collection("Represented")
                            .document();

                    DocumentReference isRepRef = FirebaseFirestore.getInstance()
                            .collection("GroupTour")
                            .document(tourPreferences.getString("tourid", ""))
                            .collection("Participants")
                            .document(participant.get(index).getUid());
                    isRepRef.update("represent", true);

                    RepresentedPassanger repax = new RepresentedPassanger(inputRepPax.getText().toString(), participant.get(index).getUid());
                    paxRef.set(repax).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgressBar.setVisibility(View.GONE);
                                mPleaseWait.setVisibility(View.GONE);
                                Toast.makeText(context, "Represented participant has succesfully added!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else{
                                mProgressBar.setVisibility(View.GONE);
                                mPleaseWait.setVisibility(View.GONE);
                                Toast.makeText(context, "Failed to add represented participant!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mPleaseWait.setVisibility(View.GONE);
                    Toast.makeText(context, "Field must be filled out!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
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

            participantname = itemView.findViewById(R.id.participantName);
            participantisrepresent = itemView.findViewById(R.id.participantIsRepresent);
            avatar = itemView.findViewById(R.id.lisedPaxAva);
            moreAddRepPax = itemView.findViewById(R.id.moreRepPax);
        }


    }


}
