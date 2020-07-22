package com.example.newroutes.Fragments;

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

import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.Adapters.UsersAdapter;
import com.example.newroutes.databinding.FragmentUsersBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    public static final String TAG = "UsersFragment";
    public RecyclerView rvUsers;
    public UsersAdapter adapter;
    public ArrayList<ParseUser> users;
    FragmentUsersBinding binding;


    public UsersFragment() {
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
        binding = FragmentUsersBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUsers = binding.rvUsers;
        users = new ArrayList<>();
        adapter = new UsersAdapter(getContext(),users,true);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        queryUsers();
    }

    public void queryUsers() {
        Log.i(TAG,"Query users");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(Route.KEY_USER);
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> newUsers, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"issue");
                    return;
                }
                Log.i(TAG,"Adding " + newUsers.size() + " users");
                users.clear();
                users.addAll(newUsers);
                adapter.notifyDataSetChanged();
            }
        });
    }
}