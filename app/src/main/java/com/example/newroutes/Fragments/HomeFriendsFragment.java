package com.example.newroutes.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newroutes.Adapters.FriendsAdapter;
import com.example.newroutes.Adapters.RoutesAdapter;
import com.example.newroutes.Adapters.UsersAdapter;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentHomeFriendRequestsBinding;
import com.example.newroutes.databinding.FragmentHomeFriendsBinding;
import com.example.newroutes.databinding.FragmentRoutesBinding;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;


public class HomeFriendsFragment extends Fragment {


    public static final String TAG = "HomeFriendsFragment";
    public FriendsAdapter adapter;
    public ArrayList<ParseUser> allFriends;
    public RecyclerView rvFriends;
    FragmentHomeFriendsBinding binding;

    //Required empty constructor
    public HomeFriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeFriendsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFriends = binding.rvFriends;
        allFriends = new ArrayList<>();
        adapter = new FriendsAdapter(getContext(),allFriends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            queryUsers();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void queryUsers() throws ParseException {
        Log.i(TAG,"query requestss");
        FriendsManager friendsManager = ((FriendsManager) ParseUser.getCurrentUser().get("FriendsManager")).fetchIfNeeded();
        ArrayList<ParseUser> friends = (ArrayList<ParseUser>) friendsManager.get("Friends");
        if (friends == null || friends.size() == 0) {
            return;
        }
        ParseObject.fetchAll(friends);
        allFriends.addAll(friends);
        adapter.notifyDataSetChanged();
    }

}