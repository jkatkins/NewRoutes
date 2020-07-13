package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.newroutes.Fragments.RecentFragment;
import com.example.newroutes.Fragments.RoutesFragment;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    Button btnMap;
    Button btnRoutes;
    Button btnRecent;
    TextView tvUsername;
    ImageView ivProfilePic;
    FrameLayout flProfileFragments;
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        //Setting up View binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Defining variables from XML layout
        btnMap = binding.btnMap;
        btnRoutes = binding.btnRoutes;
        btnRecent = binding.btnRecent;
        tvUsername = binding.tvUsername;
        ivProfilePic = binding.ivProfilePic;
        flProfileFragments = binding.flProfileFragments;
        bottomNavigationView = binding.bottomNavigation;

        //Fill with data
        try {
            ParseUser user = ParseUser.getCurrentUser().fetch(); //Lets us access custom parse fields
            tvUsername.setText(user.getUsername());
            Glide.with(this).load(user.getParseFile("Picture").getUrl()).into(ivProfilePic);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //OnClickListeners

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(flProfileFragments.getId(),new MapFragment()).commit();
            }
        });

        btnRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(flProfileFragments.getId(),new RoutesFragment()).commit();
            }
        });

        btnRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(flProfileFragments.getId(),new RecentFragment()).commit();
            }
        });

        //OnClick for Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemAdd:
                        Intent i = new Intent(MainActivity.this,NewRouteActivity.class);
                        startActivity(i);
                    case R.id.itemProfile:
                    default:
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.itemProfile);

    }

}