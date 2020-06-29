package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.utils.ListedMessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TourDetailsDiscussionActivity extends AppCompatActivity {

    private final String TAG = "TourDetailsDiscussionActivity";
    Context mContext = TourDetailsDiscussionActivity.this;
    private static final int ACTIVITY_NUM = 1;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    ImageView sendArrow, backArrow;
    EditText inputMessage;

    SharedPreferences userPreferences, gtPreferences;

    private RecyclerView listedMessageConatiner;
    private ListedMessageAdapter listedMessageAdapter;
    private ArrayList<ChatMessage> messageList = new ArrayList<>();
    private ListenerRegistration mChatMessageEventListener;
    private Set<String> mMessageIds = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetailsdiscussion);
        initWidgets();
        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        gtPreferences = getSharedPreferences("GT_BASICINFO", MODE_PRIVATE);

        initBackArrow();
        initMessagesRecyclerView();
        getChatMessages();
        initSendMessageButton();
    }

    private void initWidgets(){
        listedMessageConatiner = (RecyclerView) findViewById(R.id.listedMessagesContainer);
        sendArrow = (ImageView) findViewById(R.id.sendArrow);
        inputMessage = (EditText) findViewById(R.id.input_message);
        backArrow = (ImageView) findViewById(R.id.backArrowInDiscussion);
    }
    private void getChatMessages(){

        CollectionReference messagesRef = mDb
                .collection("GroupTour")
                .document(gtPreferences.getString("tourid", ""))
                .collection("Discussion");

        mChatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ChatMessage message = doc.toObject(ChatMessage.class);
                                if(!mMessageIds.contains(message.getMessageid())){
                                    mMessageIds.add(message.getMessageid());
                                    messageList.add(message);
                                    listedMessageConatiner.smoothScrollToPosition(messageList.size() - 1);
                                }

                            }
                            listedMessageAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initMessagesRecyclerView(){
        listedMessageAdapter = new ListedMessageAdapter(this, messageList, new ArrayList<User>());
        listedMessageConatiner.setAdapter(listedMessageAdapter);
        listedMessageConatiner.setLayoutManager(new LinearLayoutManager(this));

        listedMessageConatiner.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    listedMessageConatiner.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(messageList.size() > 0){
                                listedMessageConatiner.smoothScrollToPosition(
                                        listedMessageConatiner.getAdapter().getItemCount() - 1);
                            }

                        }
                    }, 100);
                }
            }
        });
    }

    private void initSendMessageButton(){
        sendArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewMessage();
            }
        });
    }

    private void insertNewMessage(){

        final String message  = inputMessage.getText().toString();
        if (!message.equals("")){

            DocumentReference userRef = mDb.collection("Users").document(userPreferences.getString("uid", ""));
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        User user = task.getResult().toObject(User.class);

                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        mDb.setFirestoreSettings(settings);

                        DocumentReference chatMessageRef = mDb.collection("GroupTour")
                                .document(gtPreferences.getString("tourid", ""))
                                .collection("Discussion")
                                .document();

                        ChatMessage chatMessage = new ChatMessage(chatMessageRef.getId(), message, null, user);
                        chatMessageRef.set(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    clearMessage();
                                }
                            }
                        });
                    }
                }
            });

        }

    }

    private void clearMessage(){
        inputMessage.setText("");
    }

    private void initBackArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
}
