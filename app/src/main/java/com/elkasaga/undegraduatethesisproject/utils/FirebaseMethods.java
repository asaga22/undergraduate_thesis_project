package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elkasaga.undegraduatethesisproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseMethods {

    private static final String TAG = "firebaseMethods";
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    public boolean isRegisterNewEmailSucced;
    public boolean isUsernameExists;
    private boolean isUsernameExistsInGroupTour;


    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mContext = context;

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /*
     *Register a new email to firebase
     */


}
