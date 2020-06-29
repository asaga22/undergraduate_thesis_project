package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;

import java.util.ArrayList;

public class ListedToursAdapter extends RecyclerView.Adapter <ListedToursAdapter.MyViewHolder>{

    Context context;
    ArrayList<GroupTour> grouptour;

    public ListedToursAdapter(Context c, ArrayList<GroupTour> gt){
        context = c;
        grouptour = gt;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_listed_tours_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.tourtitle.setText(grouptour.get(i).getTourtitle());
        myViewHolder.daterange.setText(grouptour.get(i).getStartdate());
        myViewHolder.tourid.setText(grouptour.get(i).getTourid());

        final String tourid = grouptour.get(i).getTourid();

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTourDetails = new Intent(context, TourDetailsActivity.class);
                toTourDetails.putExtra("tourid", tourid);
                context.startActivity(toTourDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return grouptour.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tourtitle, daterange, tourid;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tourtitle = itemView.findViewById(R.id.tourTitleBI);
            daterange = itemView.findViewById(R.id.dateRangeBI);
            tourid = itemView.findViewById(R.id.tourIdBI);

        }
    }

}
