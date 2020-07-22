package com.example.newroutes.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newroutes.Adapters.UsersAdapter;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;


public class HomeFriendsFragment extends UsersFragment {

    @Override
    public void queryUsers() {
        Log.i(TAG,"query requests");
        FriendsManager friendsManager = null;
        try {
            friendsManager = ((FriendsManager) ParseUser.getCurrentUser().get("FriendsManager")).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<ParseUser> friends = (ArrayList<ParseUser>) friendsManager.get("Friends");
        if (friends == null || friends.size() == 0) {
            return;
        }
        for (ParseUser userRequest : friends) {
            try {
                userRequest = userRequest.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            users.add(userRequest);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUsers = binding.rvUsers;
        users = new ArrayList<>();
        adapter = new UsersAdapter(getContext(),users,false);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        queryUsers();
    }
}