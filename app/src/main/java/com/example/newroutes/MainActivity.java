package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newroutes.Fragments.MapFragment;
import com.example.newroutes.Fragments.ProfileFragment;
import com.example.newroutes.Fragments.RecentFragment;
import com.example.newroutes.Fragments.RoutesFragment;
import com.example.newroutes.Fragments.SavedRoutesFragment;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private FrameLayout flContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting up View binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = binding.drawerLayout;
        nvDrawer = binding.nvView;
        flContent = binding.flContent;

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        //Starts off by opening up the profile fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(flContent.getId(), new ProfileFragment()).commit();
        setTitle("Profile");
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Boolean replaceFragment = true;
        switch(menuItem.getItemId()) {
            case R.id.nav_create:
                replaceFragment = false;
                Intent createIntent = new Intent(MainActivity.this,CreateRouteActivity.class);
                startActivity(createIntent);
                break;
            case R.id.nav_logout:
                replaceFragment = false;
                ParseUser.logOutInBackground();
                Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logoutIntent);
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_saved_routes:
                fragment = new SavedRoutesFragment();
                break;
            case R.id.nav_recent:
            default:
                fragment = new RecentFragment();
        }
        // Insert the fragment by replacing any existing fragment
        if (replaceFragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(flContent.getId(),fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
        }

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}