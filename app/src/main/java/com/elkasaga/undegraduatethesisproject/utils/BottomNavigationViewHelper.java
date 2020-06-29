package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.activities.Account.AccountActivity;
import com.elkasaga.undegraduatethesisproject.activities.Home.HomeActivity;
import com.elkasaga.undegraduatethesisproject.activities.NewTour.NewTourActivity;
import com.elkasaga.undegraduatethesisproject.activities.Notification.NotificationActivity;
import com.elkasaga.undegraduatethesisproject.activities.Tours.ToursActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHelper";

    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_agenda:
                        Intent intent2 = new Intent(context, ToursActivity.class);
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_add:

                        Intent intent3 = new Intent(context, NewTourActivity.class);
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(context, NotificationActivity.class);
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_person:
                        Intent intent5 = new Intent(context, AccountActivity.class);
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }

    public static void setsetNavMenuItemThemeColors(BottomNavigationView bottomNavigationView, int color) {
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#AEAEAE");
        int navDefaultIconColor = Color.parseColor("#AEAEAE");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        bottomNavigationView.setItemTextColor(navMenuTextList);
        bottomNavigationView.setItemIconTintList(navMenuIconList);
    }

}