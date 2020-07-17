package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.Foregoing.ToursForegoingActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ListedToursAdapter extends RecyclerView.Adapter <ListedToursAdapter.MyViewHolder>{

    Context context;
    ArrayList<GroupTour> grouptour;
    long millis;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        if (grouptour != null){
            myViewHolder.tourtitle.setText(grouptour.get(i).getTourtitle());
            myViewHolder.daterange.setText(grouptour.get(i).getStartdate());
            myViewHolder.tourid.setText(grouptour.get(i).getTourid());
            String collapsedDate = "";

            if (!grouptour.get(i).getEnddate().equals("") && !grouptour.get(i).getStartdate().equals("")){
                Date start = DateConvert.convertStringToDate(grouptour.get(i).getStartdate());
                Date end = DateConvert.convertStringToDate(grouptour.get(i).getEnddate());
                collapsedDate = DateConvert.getCollapsedDateRange(start, end, context);
            }
            myViewHolder.daterange.setText(collapsedDate);

            final String tourid = grouptour.get(i).getTourid();

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences isOngoing = context.getSharedPreferences("IS_ONGOING", context.MODE_PRIVATE);
                    SharedPreferences.Editor editorIsOngoing = isOngoing.edit();
                    editorIsOngoing.putBoolean("isongoing", false);
                    editorIsOngoing.apply();
                    if (grouptour.get(i).getTourstatus() == 2){
                        Intent toTourDetails = new Intent(context, TourDetailsActivity.class);
                        toTourDetails.putExtra("tourid", tourid);
                        context.startActivity(toTourDetails);
                    } else if (grouptour.get(i).getTourstatus() == 0){
                        Intent toTourHistoryDetails = new Intent(context, ToursForegoingActivity.class);
                        toTourHistoryDetails.putExtra("tourid", tourid);
                        context.startActivity(toTourHistoryDetails);
                    }
                }
            });
        }


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
