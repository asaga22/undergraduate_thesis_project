package com.elkasaga.undegraduatethesisproject.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.elkasaga.undegraduatethesisproject.UserClient;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.models.GroupTour;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SigninActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDb;

    Button btnSignin;
    TextView signupLink, pleaseWait;
    EditText mEmail, mPassword;
    ProgressBar mProgressBar;

    private Context mContext = SigninActivity.this;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initializeWidgets();
        setupFirebaseAuth();
        initSigningIn();
    }

    private void initializeWidgets(){
        mDb = FirebaseFirestore.getInstance();
        btnSignin = (Button) findViewById(R.id.button_signin);
        signupLink = (TextView) findViewById(R.id.signUpLink);
        mEmail = (EditText) findViewById(R.id.signinEmail);
        mPassword = (EditText) findViewById(R.id.signinPassword);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        pleaseWait = (TextView) findViewById(R.id.pleaseWaitLogin);
        mProgressBar.setVisibility(View.GONE);
        pleaseWait.setVisibility(View.GONE);
    }

    private void initSigningIn(){

        Button btnLogin = (Button) findViewById(R.id.button_signin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (email.equals("") || password.equals("")){
                    Toast.makeText(mContext, "you must fill out all the field", Toast.LENGTH_SHORT).show();
                } else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    pleaseWait.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(mContext, getString(R.string.auth_success),
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        pleaseWait.setVisibility(View.GONE);

                                        DocumentReference userRef = mDb.collection("Users").document(FirebaseAuth.getInstance().getUid());
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    Log.d(TAG, "onComplete: successfully get the  user details.");
                                                    User user = task.getResult().toObject(User.class);
                                                    //simpan username (key) ke local
                                                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("uid", user.getUid());
                                                    editor.putString("fullname", user.getFullname());
                                                    editor.putString("username", user.getUsername());
                                                    editor.putString("email", user.getEmail());
                                                    editor.putLong("ongoingtour", user.getOngoingtour());
                                                    editor.putLong("foregoingtour", user.getForegoingtour());
                                                    editor.putLong("upcomingtour", user.getUpcomingtour());
                                                    editor.putLong("category", user.getCategory());
                                                    editor.putString("avatar", user.getAvatar());
                                                    editor.apply();

                                                    //navigate to home if authentication success
                                                    Intent toHome = new Intent(mContext, HomeActivity.class);
                                                    startActivity(toHome);
                                                    finish();

                                                }
                                            }
                                        });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, getString(R.string.auth_failed),
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        pleaseWait.setVisibility(View.GONE);
                                    }

                                    // ...
                                }
                            });
                }

            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(mContext, SignupActivity.class);
                startActivity(toRegister);
            }
        });

    }

    /*
     *setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signin: "+ user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged: signout");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
