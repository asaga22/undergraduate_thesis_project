package com.elkasaga.undegraduatethesisproject.activities.NewTour;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Tours.TourDetailsActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.JoinGroupTourRequest;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.StringManipulation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewTourActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = NewTourActivity.this;
    private static final int ACTIVITY_NUM = 2;
    final Calendar myCalendar = Calendar.getInstance();

    private FirebaseFirestore mDb;


    EditText mInputTourTitle, mInputStartDate, mInputStartTime, mInputEndDate, mInputEndTime, mInputTourId;
    Button btnContinue, btnSendRequest;
    String tourTitle, tlUid, startDate, endDate, startTime, endTime;
    long tlCategory;
    TextView mPleaseWait, mPleaseWaitJoinTour;
    ProgressBar mProgressBar, mProgressBarJoinTour;
    String userId;

    SharedPreferences userPreferences, tourPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        tourPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
        getUserDetailsFromPreference();

        if (tlCategory == 0 ){
            setContentView(R.layout.activity_newtour);
            initWidgetsCategory0();
            timeSetter();
            initButtonCreateTour();
            setupBottomNavigationView();
        } else{
            setContentView(R.layout.activity_newtour_jointour);
            setupBottomNavigationView();
            initWidgetsCategory1();
            initButtonSendRequest();
        }

        mDb = FirebaseFirestore.getInstance();

    }

    private void initWidgetsCategory0(){
        mInputTourTitle = (EditText) findViewById(R.id.inputTourTitle);
        mInputStartDate = (EditText) findViewById(R.id.inputStartDate);
        mInputStartTime = (EditText) findViewById(R.id.inputStartTime);
        mInputEndDate = (EditText) findViewById(R.id.inputEndDate);
        mInputEndTime = (EditText) findViewById(R.id.inputEndTime);
        btnContinue = (Button) findViewById(R.id.continueToStepTwo);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarCreateNewTour);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWaitCreateNewTour);
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
    }

    private void initWidgetsCategory1(){
        btnSendRequest = (Button) findViewById(R.id.btnSendRequest);
        mInputTourId = (EditText) findViewById(R.id.inputTourId);
        mProgressBarJoinTour = (ProgressBar) findViewById(R.id.progressBarJoinTour);
        mPleaseWaitJoinTour = (TextView) findViewById(R.id.pleaseWaitJoinTour);
        mPleaseWaitJoinTour.setVisibility(View.GONE);
        mProgressBarJoinTour.setVisibility(View.GONE);
    }

    private void initButtonSendRequest(){
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBarJoinTour.setVisibility(View.VISIBLE);
                mPleaseWaitJoinTour.setVisibility(View.VISIBLE);
                final String tourid = mInputTourId.getText().toString();
                if (!tourid.equals("")){

                    //search if the gt id is valid
                    Query gtQuery = mDb
                            .collection("GroupTour").whereEqualTo("tourid", tourid);
                    gtQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots.size()!=0){
                                final GroupTour gt = queryDocumentSnapshots.getDocuments().get(0).toObject(GroupTour.class);

                               //check if user has already a pax or send request to the group
                                Query query = mDb.collection("UserTour").document(userPreferences.getString("uid", ""))
                                        .collection("GroupTour").whereEqualTo("tourid", tourid);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().size() == 0){
                                            Query query1 = mDb.collection("JoinGroupTourRequest").whereEqualTo("tourid", tourid)
                                                    .whereEqualTo("uid", userPreferences.getString("uid", ""));
                                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.getResult().size() == 0){
                                                        String jrid = "JRQ"+StringManipulation.getSixRandomNumber();
                                                        //save req to db
                                                        DocumentReference sendReqRef = mDb.collection("JoinGroupTourRequest")
                                                                .document(jrid);

                                                        JoinGroupTourRequest joinGroupTourRequest = new JoinGroupTourRequest(jrid, gt.getTourid(), userPreferences.getString("uid", ""), gt.getTourleader(), null);

                                                        sendReqRef.set(joinGroupTourRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    mInputTourId.setText("");
                                                                    Toast.makeText(mContext, "Join Request has successfully sent!", Toast.LENGTH_SHORT).show();
                                                                    mProgressBarJoinTour.setVisibility(View.GONE);
                                                                    mPleaseWaitJoinTour.setVisibility(View.GONE);
                                                                } else{
                                                                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                                    mProgressBarJoinTour.setVisibility(View.GONE);
                                                                    mPleaseWaitJoinTour.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    } else{
                                                        Toast.makeText(mContext, "You have already sent join request to the group tour!", Toast.LENGTH_SHORT).show();
                                                        mProgressBarJoinTour.setVisibility(View.GONE);
                                                        mPleaseWaitJoinTour.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(mContext, "You already a participant in the group tour!", Toast.LENGTH_SHORT).show();
                                            mProgressBarJoinTour.setVisibility(View.GONE);
                                            mPleaseWaitJoinTour.setVisibility(View.GONE);
                                        }
                                    }
                                });


                            } else{
                                Toast.makeText(mContext, "Given Tour Id is not valid!", Toast.LENGTH_SHORT).show();
                                mProgressBarJoinTour.setVisibility(View.GONE);
                                mPleaseWaitJoinTour.setVisibility(View.GONE);
                            }
                        }
                    });
                } else{
                    Toast.makeText(mContext, "Tour Id cannot be empty!", Toast.LENGTH_SHORT).show();
                    mProgressBarJoinTour.setVisibility(View.GONE);
                    mPleaseWaitJoinTour.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initButtonCreateTour(){
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                tourTitle = mInputTourTitle.getText().toString();
                startDate = mInputStartDate.getText().toString();
                endDate = mInputEndDate.getText().toString();
                startTime = mInputStartTime.getText().toString();
                endTime = mInputEndTime.getText().toString();
                final String tourid = "GT"+ StringManipulation.getSixRandomNumber();

                Query findGtIdQuery = mDb.collection("GroupTour")
                        .whereEqualTo("tourid", "tourid");
                findGtIdQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots.size() == 0){
                            if (tlCategory == 0){
                                if (!isInputNull(tourTitle, startDate, endDate, startTime, endTime)){
                                    final GroupTour gt = new GroupTour(tourTitle,  tourid, tlUid, startDate, endDate, startTime, endTime, 2);
                                    Log.d(TAG, "GT IS SET = "+gt);

                                    DocumentReference gtRef = mDb.collection("GroupTour").document(tourid);
                                    Log.d(TAG, "GTREF = "+gtRef);
                                    gtRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.d(TAG, "NEW GT IS ADDED TO DB!!");

                                                //usertour
                                                DocumentReference tlRef = mDb.collection("UserTour").document(tlUid)
                                                        .collection("GroupTour").document(tourid);
                                                tlRef.set(gt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            incrementUpcomingTourCounter();
                                                            mPleaseWait.setVisibility(View.GONE);
                                                            mProgressBar.setVisibility(View.GONE);
                                                            Log.d(TAG, "NEW GT ADDED TO USER TOUR (TOUR LEADER)!!");
                                                            Toast.makeText(mContext, "New tour has successfully added!", Toast.LENGTH_SHORT).show();
                                                            Intent toTourDetails = new Intent(mContext, TourDetailsActivity.class);
                                                            toTourDetails.putExtra("tourid", tourid);
                                                            SharedPreferences.Editor editor = tourPreferences.edit();
                                                            editor.putString("tourid", gt.getTourid());
                                                            editor.apply();
                                                            startActivity(toTourDetails);
                                                            finish();
                                                        } else{
                                                            mPleaseWait.setVisibility(View.GONE);
                                                            mProgressBar.setVisibility(View.GONE);
                                                            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else{
                                                mPleaseWait.setVisibility(View.GONE);
                                                mProgressBar.setVisibility(View.GONE);
                                                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else{
                                    mPleaseWait.setVisibility(View.GONE);
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(mContext, "All fields must be filled out!", Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                mPleaseWait.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(mContext, "You are not authorized to perform the task!", Toast.LENGTH_SHORT).show();
                            }

                        } else{
                            initButtonCreateTour();
                        }
                    }
                });
            }
        });
    }

    public Task<Void> incrementUpcomingTourCounter() {
        final DocumentReference shardRef = mDb.collection("Users").document(tlUid);

        return mDb.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                User user = transaction.get(shardRef).toObject(User.class);
                user.setUpcomingtour(user.getUpcomingtour() + 1);
                transaction.set(shardRef, user);
                SharedPreferences.Editor editor = userPreferences.edit();
                editor.putLong("upcomingtour", user.getUpcomingtour());
                editor.apply();
                return null;
            }
        });
    }


    private boolean isInputNull(String tourTitle, String startDate, String endDate, String startTime, String endTime){
        Log.d(TAG, "checkInput: checking inputs for null values");

        if (tourTitle.isEmpty()|| startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()){
            Toast.makeText(mContext, "All field must be filled out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void getUserDetailsFromPreference(){
        tlUid = userPreferences.getString("uid", "");
        tlCategory = userPreferences.getLong("category", 0);
    }

    private void timeSetter(){
        final DatePickerDialog.OnDateSetListener start_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDate(mInputStartDate);
            }

        };

        final DatePickerDialog.OnDateSetListener end_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDate(mInputEndDate);
            }

        };

        final TimePickerDialog.OnTimeSetListener start_time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabelTime(mInputStartTime);
            }
        };

        final TimePickerDialog.OnTimeSetListener end_time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabelTime(mInputEndTime);
            }
        };

        mInputStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, start_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mInputEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, end_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mInputStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, start_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        mInputEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, end_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });
    }

    private void updateLabelDate(EditText editText) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editText.setText(sdf.format(myCalendar.getTime()));
    }


    private void updateLabelTime(EditText editText){
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    /*
     * Bottom Navigation Setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setBottomNavigationView: setting up BottomNavView");

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
