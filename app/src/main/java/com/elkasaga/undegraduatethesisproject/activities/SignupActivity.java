package com.elkasaga.undegraduatethesisproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.utils.FirebaseMethods;
import com.elkasaga.undegraduatethesisproject.utils.StringManipulation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private Context mContext = SignupActivity.this;
    private String email, fullname, password, username, append = "";

    private String defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/myundergraduatethesisproject.appspot.com/o/UserAvatars%2Fno_pic.png?alt=media&token=428e2c03-4be6-43fd-a8f6-5c964419d443";

    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDb;

    TextView signInLink, pleaseWaitRegister;
    EditText mFullname, mEmail, mPassword;
    Button button_signup;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseMethods = new FirebaseMethods(mContext);
        mDb = FirebaseFirestore.getInstance();
        initWidgets();
        initButtonSignUp();
    }

    private void initWidgets(){
        mContext = SignupActivity.this;
        mAuth = FirebaseAuth.getInstance();
        mFullname = (EditText) findViewById(R.id.signupFullName);
        mEmail = (EditText) findViewById(R.id.signupEmail);
        mPassword = (EditText) findViewById(R.id.signupPassword);
        pleaseWaitRegister = (TextView) findViewById(R.id.pleaseWaitSignup);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarSignup);
        button_signup = (Button) findViewById(R.id.button_signup);
        mProgressBar.setVisibility(View.GONE);
        pleaseWaitRegister.setVisibility(View.GONE);
        signInLink = (TextView) findViewById(R.id.signInLink);
    }

    private void initButtonSignUp(){
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_signup.setEnabled(false);
                button_signup.setText("Siging Up...");

                email = mEmail.getText().toString();
                fullname = mFullname.getText().toString();
                password = mPassword.getText().toString();

                if (!isInputNull(email, fullname, password)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    pleaseWaitRegister.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if (task.isSuccessful()){
                                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        String username = StringManipulation.condenseUsername(fullname);

                                        //insert some default data
                                        User user = new User(FirebaseAuth.getInstance().getUid(), email, fullname, username, 0, 0, 0,1,defaultAvatar);

                                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                                .setTimestampsInSnapshotsEnabled(true)
                                                .build();
                                        mDb.setFirestoreSettings(settings);

                                        DocumentReference newUserRef = mDb
                                                .collection("Users")
                                                .document(FirebaseAuth.getInstance().getUid());

                                        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext, "New user has successfully registered! Start Sign In!", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    Intent toSignIn = new Intent(mContext, SigninActivity.class);
                                                    startActivity(toSignIn);
                                                }else{
                                                    View parentLayout = findViewById(android.R.id.content);
                                                    Toast.makeText(mContext, "Failed to register new user!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });

                } else{
                    Toast.makeText(mContext, "All fields must be filled out!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isInputNull(String email, String fullname, String password){
        Log.d(TAG, "checkInput: checking inputs for null values");

        if (email.isEmpty()|| fullname.isEmpty() || password.isEmpty()){
            Toast.makeText(mContext, "All field must be filled out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
