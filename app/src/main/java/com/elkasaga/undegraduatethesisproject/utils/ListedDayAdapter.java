//package com.elkasaga.undegraduatethesisproject.utils;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.elkasaga.undegraduatethesisproject.R;
//import com.elkasaga.undegraduatethesisproject.models.Day;
//import com.elkasaga.undegraduatethesisproject.models.Itinerary;
//
//import java.util.ArrayList;
//
//public class ListedDayAdapter extends RecyclerView.Adapter<ListedDayAdapter.DayViewHolder> {
//
////    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
////    private ArrayList<Day> dayList;
////
////    public ListedDayAdapter(ArrayList<Itinerary> dayList) {
////        this.dayList = dayList;
////    }
////
////    @NonNull
////    @Override
////    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
////        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snippet_day_item, viewGroup, false);
////        return new DayViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull DayViewHolder dayViewHolder, int i) {
////        Day day = dayList.get(i);
//////        dayViewHolder.dayNumber.setText((int) day.getDay());
////
////        // Create layout manager with initial prefetch item count
////        LinearLayoutManager layoutManager = new LinearLayoutManager(
////                dayViewHolder.listedItineraryContainer.getContext(),
////                LinearLayoutManager.VERTICAL,
////                false
////        );
////        layoutManager.setInitialPrefetchItemCount(day.getItinerary().size());
////
////        // Create sub item view adapter
////        ListedItineraryAdapter_ itineraryAdapter = new ListedItineraryAdapter_(day.getItinerary());
////
////        dayViewHolder.listedItineraryContainer.setLayoutManager(layoutManager);
////        dayViewHolder.listedItineraryContainer.setAdapter(itineraryAdapter);
////        dayViewHolder.listedItineraryContainer.setRecycledViewPool(viewPool);
////    }
////
////    @Override
////    public int getItemCount() {
////        return dayList.size();
////    }
////
////    class DayViewHolder extends RecyclerView.ViewHolder {
////        private TextView dayNumber;
////        private RecyclerView listedItineraryContainer;
////
////        DayViewHolder(View itemView) {
////            super(itemView);
////            dayNumber = itemView.findViewById(R.id.dayNumber);
////            listedItineraryContainer = itemView.findViewById(R.id.listedItineraryContainer);
////        }
////    }
//}