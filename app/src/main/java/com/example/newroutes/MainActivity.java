package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.newroutes.Fragments.HomeFragment;
import com.example.newroutes.Fragments.ProfileFragment;
import com.example.newroutes.Fragments.RoutesFragment;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
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
                        return false;
                    case R.id.item_friends: //TODO move logout to its own thing, not have it here
                        //fragment =
                        replaceFragment = false;
                        ParseUser.logOutInBackground();
                        Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(logoutIntent);
                    case R.id.item_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.item_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.item_routes:
                        fragment = new RoutesFragment();
                        break;
                    default:
                        fragment = new ProfileFragment();
                }
                if (replaceFragment) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(flContainer.getId(),fragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.item_home);

    }


}