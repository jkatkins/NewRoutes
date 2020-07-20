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
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.newroutes.Fragments.ProfileFragment;
import com.example.newroutes.Fragments.RecentFragment;
import com.example.newroutes.Fragments.SavedRoutesFragment;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private Toolbar toolbar;
    private FrameLayout flContainer;
    private BottomNavigationView bottomNavigationView;

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

        flContainer = binding.flContainer;
        bottomNavigationView = binding.bottomNavigation;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                boolean replaceFragment = true;
                switch(item.getItemId()) {
                    case R.id.item_new:
                        replaceFragment = false;
                        Intent createIntent = new Intent(MainActivity.this,CreateRouteActivity.class);
                        startActivity(createIntent);
                        break;
                    case R.id.item_recent:
                    case R.id.item_friends:
                    case R.id.item_home:
                    case R.id.item_routes:
                    default:
                        fragment = new RecentFragment();
                        if (replaceFragment) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(flContainer.getId(),fragment).commit();
                        }
                }



                return false;
            }
        });


    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        boolean replaceFragment = true;
        switch(menuItem.getItemId()) {
            case R.id.nav_create:
                replaceFragment = false;
                Intent createIntent = new Intent(MainActivity.this,CreateRouteActivity.class);
                startActivity(createIntent);
                break;
            case R.id.nav_logout:
                replaceFragment = false;
                ParseUser.logOut();
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
            fragmentManager.beginTransaction().replace(flContainer.getId(),fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
        }

    }


}