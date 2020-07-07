package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.RepresentedPassanger;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.models.UserLocation;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.ListedParticipantAdapter;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TourDetailsParticipantActivity extends AppCompatActivity{

    private final String TAG = "TourDetailsParticipantActivity";
    Context mContext = TourDetailsParticipantActivity.this;
    private static final int ACTIVITY_NUM = 1;

    String tourid;
    Dialog dialog;
    ImageView backArrow, addButton, closeDialog;
    EditText username_text;
    Button addParticipant;
    ProgressBar mProgressBar;
    TextView mPleaseWait;
    RecyclerView listedParticipantContainer;
    ArrayList<Participant> listedParticipant;
    ListedParticipantAdapter participantAdapter;
    private ListenerRegistration mParticipantEventListener;
    private Set<String> mParticipantId = new HashSet<>();

    SharedPreferences sharedPreferences;
    SharedPreferences isOngoingPreferece;
    SharedPreferences userpreferences;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetailsparticipant);
        setupBottomNavigationView();
        isOngoingPreferece = getSharedPreferences("IS_ONGOING", MODE_PRIVATE);
        userpreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        initWidgets();
        initBackArrow();
        sharedPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);
        listedParticipant = new ArrayList<Participant>();
        initparticipantRecyclerView();
        getParticipantData();
        initButtonAddParticipant();
    }

    private void initBackArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    private void initWidgets(){
        listedParticipantContainer = (RecyclerView) findViewById(R.id.listedParticipantContainer);
        backArrow = (ImageView) findViewById(R.id.backArrowInListedParticipant);
        addButton = (ImageView) findViewById(R.id.add_icon);
        if (isOngoingPreferece.getBoolean("isongoing", false)){
            addButton.setVisibility(View.GONE);
        } else {
            if (userpreferences.getLong("category", 0) == 1){
                addButton.setVisibility(View.GONE);
            }
        }
    }

    private void initButtonAddParticipant(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_addparticipant);
                username_text = (EditText) dialog.findViewById(R.id.addParticipantForm);
                addParticipant = (Button) dialog.findViewById(R.id.addParticipant);
                mProgressBar = (ProgressBar) dialog.findViewById(R.id.progressBarAddPax);
                mPleaseWait = (TextView) dialog.findViewById(R.id.pleaseWaitAddPax);
                mProgressBar.setVisibility(View.GONE);
                mPleaseWait.setVisibility(View.GONE);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                addParticipant.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(View v) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mPleaseWait.setVisibility(View.VISIBLE);

                        Log.d(TAG, "validating given username: "+username_text.getText().toString());
                        //query user equals given username
                        Query participantUidQuery = mDb
                                .collection("Users").whereEqualTo("username", username_text.getText().toString());

                        participantUidQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().size() != 0){
                                    final User user = task.getResult().getDocuments().get(0).toObject(User.class);
                                    user.setUpcomingtour(user.getUpcomingtour()+1);
                                    incrementUpcomingTourCounter(user.getUid());

                                    Log.d(TAG, "username "+username_text.getText().toString()+" valid! Go to next procecess..");
                                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                            .setTimestampsInSnapshotsEnabled(true)
                                            .build();
                                    mDb.setFirestoreSettings(settings);

                                    //add pax data to participant data at the the selected GT
                                    DocumentReference tourPax = mDb
                                            .collection("GroupTour")
                                            .document(sharedPreferences.getString("tourid", "")).collection("Participants").document(user.getUid());
                                    Participant pax = new Participant(user.getUid(), user.getUsername(), user.getFullname(), user.getAvatar(), user.getCategory(), false, false);
                                    tourPax.set(pax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, username_text.getText().toString()+" sucessfully added to GT DB as pax!");
                                                //add user tour data
                                                DocumentReference userTour = mDb
                                                        .collection("UserTour")
                                                        .document(user.getUid())
                                                        .collection("GroupTour")
                                                        .document(sharedPreferences.getString("tourid", ""));
                                                userTour.set(sharedPreferences.getAll()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Log.d(TAG, "GT "+tourid+" has succesfully added to "+username_text.getText().toString()+" tour list!");
                                                            //add user loc to participant loc at GT data
                                                            DocumentReference userLocQuery = mDb.collection("UserLocation")
                                                                    .document(user.getUid());
                                                            userLocQuery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()){
                                                                        UserLocation uLoc = task.getResult().toObject(UserLocation.class);
                                                                        DocumentReference paxLoc = mDb.collection("GroupTour")
                                                                                .document(sharedPreferences.getString("tourid", ""))
                                                                                .collection("ParticipantLocation")
                                                                                .document(user.getUid());
                                                                        paxLoc.set(uLoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
//                                                                                    incrementUpcomingTourCounter(userPax.getUid());
                                                                                    Toast.makeText(mContext, "New Participant has successfully added!", Toast.LENGTH_SHORT).show();
                                                                                    mProgressBar.setVisibility(View.GONE);
                                                                                    mPleaseWait.setVisibility(View.GONE);
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        } else{
                                                            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                            mProgressBar.setVisibility(View.GONE);
                                                            mPleaseWait.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                                //update upcoming tour total of added user
                                            } else{
                                                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                } else {
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);
                                    Toast.makeText(mContext, "Given username is not valid!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                closeDialog = (ImageView) dialog.findViewById(R.id.closeDialog);
                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
    }

    public Task<Void> incrementUpcomingTourCounter(String userId) {
        final DocumentReference shardRef = mDb.collection("Users").document(userId);

        return mDb.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                User user = transaction.get(shardRef).toObject(User.class);
                user.setUpcomingtour(user.getUpcomingtour() + 1);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("upcomingtour", user.getUpcomingtour());
                editor.apply();
                transaction.set(shardRef, user);
                return null;
            }
        });
    }

    public void getParticipantData(){
        Log.d("TAG", "get pax data again!");
        CollectionReference userTourRef = mDb
                .collection("GroupTour")
                .document(sharedPreferences.getString("tourid", "")).collection("Participants");
        mParticipantEventListener = userTourRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Participant pax = doc.toObject(Participant.class);
                    if (!mParticipantId.contains(pax.getUid())){
                        mParticipantId.add(pax.getUid());
                        listedParticipant.add(pax);
                    }
                }
                participantAdapter.notifyDataSetChanged();
            }
        });
    }

     public void initparticipantRecyclerView(){
        participantAdapter = new ListedParticipantAdapter(mContext, listedParticipant);
        listedParticipantContainer.setAdapter(participantAdapter);
        listedParticipantContainer.setLayoutManager(new LinearLayoutManager(this));
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
