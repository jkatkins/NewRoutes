package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newroutes.R;
import com.example.newroutes.databinding.ActivityMainBinding;
import com.example.newroutes.databinding.FragmentProfileBinding;
import com.parse.ParseException;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Button btnMap;
    Button btnRoutes;
    Button btnRecent;
    TextView tvUsername;
    ImageView ivProfilePic;
    FrameLayout flProfileFragments;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnMap = binding.btnFavorites;
        btnRoutes = binding.btnFriends;
        btnRecent = binding.btnRecomended;
        tvUsername = binding.tvUsername;
        ivProfilePic = binding.ivProfilePic;
        flProfileFragments = binding.flProfileFragments;

        //Fill with data
        ParseUser user = null; //Lets us access custom parse fields
        user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        Glide.with(this).load(user.getParseFile("Picture").getUrl()).into(ivProfilePic);
    }
}