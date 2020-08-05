package com.elkasaga.undegraduatethesisproject.activities.Account;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class EditAccountActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = EditAccountActivity.this;
    private static final int ACTIVITY_NUM = 4;

    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    ProgressBar mProgressBar;
    Button editAccount;
    TextView mPleaseWait, editAccountName, editAvatar, addPic;
    EditText inputEditFullName, inputEditUsername, inputEditEmail;
    ImageView profile_image;

    private Uri photo_location;
    private String email, fullname, username, ava;
    SharedPreferences userPrefereces;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_account_edit);
        initWidgets();
        setupBottomNavigationView();
        userPrefereces = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        username = userPrefereces.getString("username", "");
        fullname = userPrefereces.getString("fullname", "");
        email = userPrefereces.getString("email", "");
        ava = userPrefereces.getString("avatar", "");
        Picasso.with(this).load(ava).centerCrop().fit().into(profile_image);
        editAccountName.setText(fullname);
        inputEditEmail.setText(email);
        inputEditFullName.setText(fullname);
        inputEditUsername.setText(username);
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        initAddPic();
        initEditAccount();
    }

    private void initWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarEditAccount);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWaitEditAccount);
        inputEditEmail = (EditText) findViewById(R.id.inputEditEmail);
        inputEditFullName = (EditText) findViewById(R.id.inputEditFullName);
        inputEditUsername = (EditText) findViewById(R.id.inputEditUsername);
        addPic = (TextView) findViewById(R.id.addPic);
        editAccountName = (TextView) findViewById(R.id.editAccountName);
        editAccount = (Button) findViewById(R.id.editAccount);
        profile_image = (ImageView) findViewById(R.id.profile_image_edit);
    }

    private void initEditAccount(){
        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                final String newEmail = inputEditEmail.getText().toString();
                final String newFullname = inputEditFullName.getText().toString();
                final String newUsername = inputEditUsername.getText().toString();

                final DocumentReference userRefDb = mDb.collection("Users").document(userPrefereces.getString("uid", ""));

                if (!newEmail.equals("") && !newFullname.equals("") && !newUsername.equals("")){

                    if (photo_location != null){
                        final StorageReference userStorage = mStorage.getReference()
                                .child("UserAvatars")
                                .child(userPrefereces.getString("uid", ""))
                                .child(System.currentTimeMillis() + "." + getFileExtension((photo_location)));

                        userStorage.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String newAva = uri.toString();
                                        User user = new User(userPrefereces.getString("uid", ""),
                                                newEmail, newFullname, newUsername,
                                                userPrefereces.getLong("ongoinftour", 0),
                                                userPrefereces.getLong("upcomingtour", 0),
                                                userPrefereces.getLong("foregoingtour", 0),
                                                userPrefereces.getLong("category", 0), newAva);
                                        userRefDb.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(mContext, "User Info has successfully updated!", Toast.LENGTH_SHORT).show();
                                                } else{
                                                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                }
                                                resetWidgetValue(newFullname, newUsername, newEmail, newAva);
                                                resetUserPreferences(newFullname, newUsername, newEmail, newAva);
                                                photo_location = null;
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {

                        User user = new User(userPrefereces.getString("uid", ""),
                            newEmail, newFullname, newUsername,
                            userPrefereces.getLong("ongoinftour", 0),
                            userPrefereces.getLong("upcomingtour", 0),
                            userPrefereces.getLong("foregoingtour", 0),
                            userPrefereces.getLong("category", 0),
                            ava);
                        userRefDb.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(mContext, "User Info has successfully updated!", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                                resetWidgetValue(newFullname, newUsername, newEmail, ava);
                                resetUserPreferences(newFullname, newUsername, newEmail, ava);
                                mProgressBar.setVisibility(View.GONE);
                                mPleaseWait.setVisibility(View.GONE);
                            }
                        });
                    }

                } else{
                    Toast.makeText(mContext, "User Info fields cannot bew empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resetWidgetValue(String newFullname, String newUsername, String newEmail, String newAva){
        editAccountName.setText(newFullname);
        inputEditFullName.setText(newFullname);
        inputEditUsername.setText(newUsername);
        inputEditEmail.setText(newEmail);
        Picasso.with(this).load(newAva).centerCrop().fit().into(profile_image);
    }

    private void resetUserPreferences(String newFullname, String newEmail, String newUsername, String newAva){
        SharedPreferences.Editor editor = userPrefereces.edit();
        editor.putString("fullname", newFullname);
        editor.putString("username", newUsername);
        editor.putString("email", newEmail);
        editor.putString("avatar", newAva);
        editor.apply();
    }

    private void initAddPic(){
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            photo_location = data.getData();
            Picasso.with(this).load(photo_location).centerCrop().fit().into(profile_image);
        }

    }

    public void findPhoto(){
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, 1);
    }

    /*
     * Bottom Navigation Setup
     */
    private void setupBottomNavigationView(){
        Integer color = ResourcesCompat.getColor(getResources(), R.color.primaryBlue, null);

        Log.d(TAG, "setBottomNavigationView: setting up BottomNavView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        BottomNavigationViewHelper.setsetNavMenuItemThemeColors(bottomNavigationView, color);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
