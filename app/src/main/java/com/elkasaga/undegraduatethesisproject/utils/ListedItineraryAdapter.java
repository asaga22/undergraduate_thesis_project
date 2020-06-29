package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ListedItineraryAdapter extends RecyclerView.Adapter <ListedItineraryAdapter.MyViewHolder>{

    Context context;
    ArrayList<Itinerary> itineraryList;

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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if(itineraryList.get(i).getItinerarystatus() == 0){
            myViewHolder.wrapper.setBackgroundResource(R.drawable.softgrey_rounded_corner);
        } else{
            myViewHolder.wrapper.setBackgroundResource(R.drawable.white_rounded_corner);
        }

        myViewHolder.dayNo.setText( "Day "+String.valueOf(itineraryList.get(i).getDay()));
        myViewHolder.startTimeInTimeline.setText(itineraryList.get(i).getStarttime());
        myViewHolder.endTimeInTimeline.setText(itineraryList.get(i).getEndtime());
        myViewHolder.activityDescription.setText(itineraryList.get(i).getDescription());

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
        return itineraryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView startTimeInTimeline, endTimeInTimeline, activityDescription, dayNo;
        RelativeLayout wrapper;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            wrapper = itemView.findViewById(R.id.itineraryWrapper);
            dayNo = itemView.findViewById(R.id.dayNo);
            startTimeInTimeline = itemView.findViewById(R.id.startTimeInTimeline);
            endTimeInTimeline = itemView.findViewById(R.id.endTimeInTimeline);
            activityDescription = itemView.findViewById(R.id.activityDescription);

        }
    }

}
