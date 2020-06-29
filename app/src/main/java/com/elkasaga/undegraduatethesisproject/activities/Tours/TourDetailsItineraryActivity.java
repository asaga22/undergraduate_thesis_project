package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Itinerary;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.ListedItineraryAdapter;
import com.elkasaga.undegraduatethesisproject.utils.StringManipulation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TourDetailsItineraryActivity extends AppCompatActivity {

    private static final String TAG = "TourDetailsItineraryActivity";
    private static final int ACTIVITY_NUM = 1;
    Context mContext = TourDetailsItineraryActivity.this;
    final Calendar myCalendar = Calendar.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    SharedPreferences tourPreference;
    SharedPreferences isOngoingPreferece;

    String tourid, tourtitle, startdate, enddate, starttime, endtime, tourleader, status;
    int day;
    ArrayList<String> dates;

    Dialog dialog;
    ImageView addActivity, closeDialog, backarrow;
    EditText inputDate, inputStartTime, inputEndTime, inputDescription;
    Button btnAddActivity;
    RecyclerView listedItineraryContainer;
    ArrayList<Itinerary> listItinerary;
    ArrayList<ArrayList<Itinerary>> allItinerary;
    ListedItineraryAdapter itineraryAdapter;
    private ListenerRegistration mItineraryEventListener;
    private Set<String> mItineraryId = new HashSet<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetailsitinerary);
        setupBottomNavigationView();
        tourPreference = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
        tourid = tourPreference.getString("tourid", "");
        isOngoingPreferece = getSharedPreferences("IS_ONGOING", MODE_PRIVATE);
        initWidgets();
        dates = getDates(tourPreference.getString("startdate", ""), tourPreference.getString("enddate", ""));
        initBackArrow();
        listedItineraryContainer = (RecyclerView) findViewById(R.id.listItineraryRv);
        listItinerary = new ArrayList<Itinerary>();
        allItinerary = new ArrayList<ArrayList<Itinerary>>();
        initItineraryRecyclerView();
        getItineraryData();
        initButtonAddActivity();

    }

    private void getItineraryData(){
        CollectionReference itineraryPerDayQuery = mDb.collection("GroupTour")
                .document(tourPreference.getString("tourid", ""))
                .collection("Itineraries");

        mItineraryEventListener = itineraryPerDayQuery
                .orderBy("day", Query.Direction.ASCENDING)
                .orderBy("indexstarttime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots != null){

                    for (int i = 0; i<queryDocumentSnapshots.size(); i++){
                        Itinerary itinerary = queryDocumentSnapshots.getDocuments().get(i).toObject(Itinerary.class);
                        if (!mItineraryId.contains(itinerary.getItineraryid())){
                            mItineraryId.add(itinerary.getItineraryid());
                            listItinerary.add(itinerary);
                            Log.d(TAG, "ITIN STAT= "+ itinerary.getItinerarystatus());
                        }
                    }
                    itineraryAdapter.notifyDataSetChanged();

                }

            }
        });

    }

    private void initItineraryRecyclerView() {

        itineraryAdapter = new ListedItineraryAdapter(mContext, listItinerary);
        listedItineraryContainer.setAdapter(itineraryAdapter);
        listedItineraryContainer.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initWidgets(){
        addActivity = (ImageView) findViewById(R.id.add_itinerary_icon);
        backarrow = (ImageView) findViewById(R.id.backArrowInListedItinerary);
        if (isOngoingPreferece.getAll().size() != 0){
            if (isOngoingPreferece.getBoolean("isongoing", false) == true){
                addActivity.setVisibility(View.GONE);
            }
        }
    }

    private void initBackArrow(){
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initButtonAddActivity(){
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_additinerary);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                inputDate = (EditText) dialog.findViewById(R.id.inputDateActivity);
                inputStartTime = (EditText) dialog.findViewById(R.id.inputStartTimeActivity);
                inputEndTime = (EditText) dialog.findViewById(R.id.inputEndTimeActivity);
                inputDescription = (EditText) dialog.findViewById(R.id.activityDescription);
                btnAddActivity = (Button) dialog.findViewById(R.id.addActivityItinerary);
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (Calendar.YEAR == 2020 || Calendar.MONTH == Calendar.JUNE || Calendar.DAY_OF_MONTH == 20){

                        }
                        updateLabelDate(inputDate);
                    }

                };
                inputDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(mContext, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                final TimePickerDialog.OnTimeSetListener start_time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        updateLabelTime(inputStartTime);
                    }
                };

                final TimePickerDialog.OnTimeSetListener end_time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        updateLabelTime(inputEndTime);
                    }
                };

                inputStartTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(mContext, start_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                    }
                });

                inputEndTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(mContext, end_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                    }
                });

                btnAddActivity.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "HAI AKU BUTTON ADD ACTIVITY! AKU DI KLIK!");
                        String date = inputDate.getText().toString();
                        String starttime = inputStartTime.getText().toString();
                        String endtime = inputEndTime.getText().toString();
                        String description = inputDescription.getText().toString();

                        String id_activity = "ACT"+ StringManipulation.getSixRandomNumber();

                        if (date.isEmpty() || starttime.isEmpty() || endtime.isEmpty() || description.isEmpty()){
                            Toast.makeText(mContext, "All feilds must be filled out!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (dates.contains(date)){
                                Log.d(TAG, "DAY " + determineDay(date));

                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mDb.setFirestoreSettings(settings);

                                DocumentReference itineraryRef = mDb
                                        .collection("GroupTour")
                                        .document(tourid)
                                        .collection("Itineraries").document(id_activity);

                                String index[] = starttime.split(":");
                                long indexstarttime = Long.parseLong(index[0]);

                                Itinerary itinerary = new Itinerary(id_activity, date, starttime, endtime, description, 2, determineDay(date), 1, indexstarttime);

                                itineraryRef.set(itinerary).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "New Activity has successfully added to the itinerary", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else{
                                            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else{
                                Toast.makeText(mContext, "Selected date is out of schedule!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

                closeDialog = (ImageView) dialog.findViewById(R.id.closeDialogAddItinerary);
                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void updateLabelDate(EditText editText) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelTime(EditText editText){
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private static ArrayList<String> getDates(String dateString1, String dateString2)
    {
        ArrayList<String> dates = new ArrayList<>();
        String myFormat = "dd/MM/yy";
        SimpleDateFormat df1 = new SimpleDateFormat(myFormat, Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(df1.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private long determineDay(String selectedDate){
        for (int i = 0; i < dates.size(); i++){
            if (dates.get(i).equals(selectedDate)){
                day = i+1;
            }
        }
        return day;
    }

    private void setupBottomNavigationView(){
        Integer color = ResourcesCompat.getColor(getResources(), R.color.primaryBlue, null);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        BottomNavigationViewHelper.setsetNavMenuItemThemeColors(bottomNavigationView, color);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
