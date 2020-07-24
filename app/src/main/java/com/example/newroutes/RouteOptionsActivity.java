package com.example.newroutes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.newroutes.databinding.ActivityRouteOptionsBinding;

public class RouteOptionsActivity extends AppCompatActivity {


    private CardView cvRandom;
    private CardView cvCustom;
    ActivityRouteOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cvRandom = binding.cvRandom;
        cvCustom = binding.cvCustom;

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cvRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RouteOptionsActivity.this,CreateRouteActivity.class);
                startActivity(i);
            }
        });
        cvCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RouteOptionsActivity.this,CustomRouteActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}