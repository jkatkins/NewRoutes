package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.newroutes.ParseObjects.FriendsManager;
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
    public ArrayList<ParseUser> friends;
    public ArrayList<ParseUser> outgoingRequests;
    public ArrayList<ParseUser> incomingRequests;
    public ProgressBar progressBar;
    public SwipeRefreshLayout swipeContainer;
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
        progressBar = binding.progressBar;
        swipeContainer = binding.swipeContainer;
        users = new ArrayList<>();
        friends = new ArrayList<>();
        outgoingRequests = new ArrayList<>();
        try {
            FriendsManager friendsManager = ((FriendsManager) ParseUser.getCurrentUser().get("FriendsManager")).fetchIfNeeded();
            friends = friendsManager.getFriends();
            outgoingRequests = friendsManager.getOutgoing();
            incomingRequests = friendsManager.getIncoming();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter = new UsersAdapter(getContext(),users,friends,outgoingRequests,incomingRequests);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.VISIBLE);
        queryUsers();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryUsers();
            }
        });
    }

    public void queryUsers() {
        Log.i(TAG,"Query users");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(Route.KEY_USER);
        query.whereNotEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> newUsers, ParseException e) {
                progressBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
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