package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.newroutes.databinding.ActivityNewRouteBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewRouteActivity extends AppCompatActivity {

    ActivityNewRouteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view binding
        binding = ActivityNewRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Define variables from XML

    }
}