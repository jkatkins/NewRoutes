package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentSaveRouteBinding;
import com.parse.ParseUser;

public class SaveRouteFragment extends Fragment {

    FragmentSaveRouteBinding binding;
    private EditText etRouteName;
    private TextView tvDistance;
    private ImageView ivMap;
    private Double distance;
    private String imageUrl;

    public SaveRouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        distance = bundle.getDouble("distance");
        imageUrl = bundle.getString("imageUrl");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSaveRouteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        etRouteName = binding.etRouteName;
        tvDistance = binding.tvDistance;
        ivMap = binding.ivMap;
        tvDistance.setText(distance.toString());
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.alert_progress_drawable)
                .into(ivMap);
    }
}