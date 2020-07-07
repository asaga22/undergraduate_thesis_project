package com.elkasaga.undegraduatethesisproject.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.core.view.MenuCompat;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Account.AccountActivity;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ParticipantMenuHelper extends AccountActivity {

    private static final String TAG = "BottomNavigationViewHelper";

    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.addRepPaxMenu:
                        Dialog dialog = new Dialog(context);
                        dialog.show();
                        break;

                    case R.id.delParMenu:
                        Dialog dialog1 = new Dialog(context);
                        dialog1.show();
                        break;
                }

                return false;
            }
        });
    }

}
