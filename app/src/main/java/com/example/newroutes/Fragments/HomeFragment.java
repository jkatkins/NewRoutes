package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {


    private Button btnFriends;
    private Button btnRoutes;
    private FrameLayout flHomeContainer;
    FragmentHomeBinding binding;

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFriends = binding.btnFriends;
        btnRoutes = binding.btnRoutes;
        flHomeContainer = binding.flHomeContainer;
        btnRoutes.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.i("homeroutes","Routes was clicked");
               FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
               fragmentManager.beginTransaction().replace(flHomeContainer.getId(),new HomeRoutesFragment()).commit();
           }
       });
    }
}