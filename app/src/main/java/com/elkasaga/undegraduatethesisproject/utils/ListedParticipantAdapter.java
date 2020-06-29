package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedParticipantAdapter extends RecyclerView.Adapter <ListedParticipantAdapter.MyViewHolder>{

    Context context;
    ArrayList<Participant> participant;

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
    public void onBindViewHolder(@NonNull ListedParticipantAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.participantname.setText(participant.get(i).getFullname());
        Picasso.with(context).load(participant.get(i).getAvatar()).centerCrop().fit().into(myViewHolder.avatar);

        if (participant.get(i).getIs_represent() == 0){
            myViewHolder.participantisrepresent.setVisibility(View.GONE);
        } else if(participant.get(i).getIs_represent() == 1){
            myViewHolder.participantisrepresent.setText("Representing other participant(s)");
        }

        final String userid = participant.get(i).getUid();

//        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toTourDetails = new Intent(context, TourDetailsActivity.class);
//                toTourDetails.putExtra("userid", userid);
//                context.startActivity(toTourDetails);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return participant.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView participantname, participantisrepresent;
        ImageView avatar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            participantname = itemView.findViewById(R.id.participantName);
            participantisrepresent = itemView.findViewById(R.id.participantIsRepresent);
            avatar = itemView.findViewById(R.id.lisedPaxAva);
        }
    }

}
