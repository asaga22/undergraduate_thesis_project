package com.elkasaga.undegraduatethesisproject.activities.Tours;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.utils.BottomNavigationViewHelper;
import com.elkasaga.undegraduatethesisproject.utils.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;

public class ToursActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Context mContext = ToursActivity.this;
    private static final int ACTIVITY_NUM = 1;
    ImageView more;

    String uid;
    long category;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours);
        setupBottomNavigationView();
        setupViewPager();

    }

    /*
     * Responsible for adding 3 tabs: camera, home, and messages
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        adapter.addFragment(new UpcomingTourFragment());
        adapter.addFragment(new ForegoingTourFragment());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Upcoming Tour");
        tabLayout.getTabAt(1).setText("Tour History");
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
