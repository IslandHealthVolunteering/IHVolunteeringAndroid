package com.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.volunteer.model.User;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private FrameLayout frame;
    private DrawerLayout drawerLayout;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame = findViewById(R.id.frame);
        configureNavigationDrawer();
        configureToolbar();
        Intent intent = getIntent();

        // Set up the current user if they have signed in.
        if (intent.getExtras() != null && intent.getExtras().containsKey("currUser")) {
            currUser = (User) intent.getSerializableExtra("currUser");
        }
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new DashboardFragment()).commit();
        navigationView.setCheckedItem(R.id.dashboard);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.dashboard) {
                    fragment = new DashboardFragment();
                } else if (itemId == R.id.learn) {
                    fragment = new LearnFragment();
                } else if (itemId == R.id.profile) {
                    fragment = new ProfileFragment();
                } else if (itemId == R.id.hospital_emergency) {
                    fragment = new HospitalEmergencyFragment();
                } else if (itemId == R.id.help) {
                    fragment = new HelpFragment();
                }
                if (fragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, fragment);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
