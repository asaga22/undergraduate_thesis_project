package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Notification.NotificationActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.models.JoinGroupTourRequest;
import com.elkasaga.undegraduatethesisproject.models.JoinRequestRespond;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedJoinGroupTourRequestAdapter extends RecyclerView.Adapter <ListedJoinGroupTourRequestAdapter.MyViewHolder>{

    Context context;
    ArrayList<JoinGroupTourRequest> requestList;
    ArrayList<JoinRequestRespond> respondList;
    SharedPreferences userPreferences;
    RelativeLayout notifRespond, notifRequest;

    public ListedJoinGroupTourRequestAdapter(Context c, ArrayList<JoinGroupTourRequest> requestList, ArrayList<JoinRequestRespond> respondList){
        context = c;
        this.requestList = requestList;
        this.respondList = respondList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        userPreferences = context.getSharedPreferences("USER_DETAILS", context.MODE_PRIVATE);
        View view  = LayoutInflater.from(context).inflate(R.layout.snippet_listed_notification, viewGroup, false);
        notifRequest = view.findViewById(R.id.notifRequest);
        notifRespond = view.findViewById(R.id.notifRespond);
        if (userPreferences.getLong("category", 0) == 1){
            Log.d("ADAPT", "coba aku dijalanin engga ?");
            notifRequest.setVisibility(View.GONE);
        } else if (userPreferences.getLong("category", 0) == 0){
            notifRespond.setVisibility(View.GONE);
        }
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        userPreferences = context.getSharedPreferences("USER_DETAILS", context.MODE_PRIVATE);

        if (userPreferences.getLong("category", 0) == 0){
            if (requestList != null){
                //joinRequestReference
                final DocumentReference jrqRef = FirebaseFirestore.getInstance().collection("JoinGroupTourRequest")
                        .document(requestList.get(i).getRequestid());

                //requestRespondReference
                final DocumentReference respondRef = FirebaseFirestore.getInstance().collection("JoinRequestRespond")
                        .document(requestList.get(i).getUid()).collection("Respond").document(requestList.get(i).getRequestid());

                //set value to widgets
                DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                        .document(requestList.get(i).getUid());
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final User user = task.getResult().toObject(User.class);
                        Picasso.with(context).load(user.getAvatar()).centerCrop().fit().into(myViewHolder.notificationImage);

                        final DocumentReference gtRef = FirebaseFirestore.getInstance().collection("GroupTour")
                                .document(requestList.get(i).getTourid());
                        gtRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final GroupTour gt = task.getResult().toObject(GroupTour.class);

                                myViewHolder.reqUsername.setText(user.getFullname() +" request to join " + gt.getTourtitle() +" ("+ gt.getTourid() + ")");

                                final Participant pax = new Participant(user.getUid(), user.getUsername(), user.getFullname(), user.getAvatar(), 1, false, false);
                                //accept
                                myViewHolder.btnAcceptReq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        JoinRequestRespond requestRespond = new JoinRequestRespond(requestList.get(i).getUid(), requestList.get(i).getTourid(), requestList.get(i).getRequestid(), true, null);
                                        respondRef.set(requestRespond).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                jrqRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //add to GroupTour data
                                                        gtRef.collection("Participants").document(user.getUid()).set(pax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                DocumentReference userTourRef = FirebaseFirestore.getInstance().collection("UserTour")
                                                                        .document(user.getUid()).collection("GroupTour").document(gt.getTourid());
                                                                userTourRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        DocumentReference userLocRef = FirebaseFirestore.getInstance().collection("UserLocation").document(user.getUid());
                                                                        userLocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.getResult() != null){
                                                                                    UserLocation userLocation = task.getResult().toObject(UserLocation.class);
                                                                                    userLocation.setUser(null);
                                                                                    userLocation.setParticipant(pax);

                                                                                    DocumentReference paxLoc = gtRef.collection("ParticipantLocation").document(user.getUid());
                                                                                    paxLoc.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            Intent toItSelf = new Intent(context, NotificationActivity.class);
                                                                                            context.startActivity(toItSelf);
                                                                                            Toast.makeText(context, "New Participant has successfully added to the Group Tour", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });

                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                });


                //decline
                myViewHolder.btnDeclineReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JoinRequestRespond requestRespond = new JoinRequestRespond(requestList.get(i).getUid(), requestList.get(i).getTourid(), requestList.get(i).getRequestid(), false, null);
                        respondRef.set(requestRespond).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                jrqRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent toItSelf = new Intent(context, NotificationActivity.class);
                                        context.startActivity(toItSelf);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        } else if (userPreferences.getLong("category", 0) == 1){
            if (respondList != null){
                if (respondList.get(i).isAccepted()){
                    myViewHolder.notDesc.setText("Your join request to Group Tour: "+ respondList.get(i).getTourid()+" is accepted");
                } else {
                    myViewHolder.notDesc.setText("Your join request to Group Tour: "+ respondList.get(i).getTourid()+" is decline");
                }

                myViewHolder.relTime.setText(DateConvert.getDisplayableTime(DateConvert.dateToMillis(respondList.get(i).getTimestamp())));
            }
        }

    }

    @Override
    public int getItemCount() {
        userPreferences = context.getSharedPreferences("USER_DETAILS", context.MODE_PRIVATE);
        int size = 0;
        if (userPreferences.getLong("category", 0) == 1){
            size = respondList.size();
        } else if (userPreferences.getLong("category", 0) == 0){
            size = requestList.size();
        }
        return size;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        SharedPreferences userPreferences;
        ImageView notificationImage;
        TextView reqUsername, notDesc, relTime;
        Button btnAcceptReq, btnDeclineReq;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationImage = (ImageView) itemView.findViewById(R.id.notificationImage);
            reqUsername = (TextView) itemView.findViewById(R.id.reqUsername);
            btnAcceptReq = (Button) itemView.findViewById(R.id.btnAcceptReq);
            btnDeclineReq = (Button) itemView.findViewById(R.id.btnDeclineReq);

            notDesc = (TextView) itemView.findViewById(R.id.notDesc);
            relTime = (TextView) itemView.findViewById(R.id.relTime);

        }
    }

}