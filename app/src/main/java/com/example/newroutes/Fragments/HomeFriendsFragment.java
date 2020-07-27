package com.example.newroutes.Fragments;

import android.content.Intent;
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

import com.example.newroutes.Adapters.FriendsAdapter;
import com.example.newroutes.Adapters.RoutesAdapter;
import com.example.newroutes.Adapters.UsersAdapter;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentHomeFriendRequestsBinding;
import com.example.newroutes.databinding.FragmentHomeFriendsBinding;
import com.example.newroutes.databinding.FragmentRoutesBinding;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFriendsFragment extends Fragment {


    public static final String TAG = "HomeFriendsFragment";
    public FriendsAdapter adapter;
    public ProgressBar progressBar;
    public ArrayList<ParseUser> allFriends;
    public RecyclerView rvFriends;
    public SwipeRefreshLayout swipeContainer;
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
        progressBar = binding.progressBar;
        swipeContainer = binding.swipeContainer;
        allFriends = new ArrayList<>();
        adapter = new FriendsAdapter(getContext(),allFriends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            progressBar.setVisibility(View.VISIBLE);
            queryUsers();
        } catch (ParseException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    queryUsers();
                } catch (ParseException e) {
                    e.printStackTrace();
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }

    public void queryUsers() throws ParseException {
        Log.i(TAG,"query requestss");
        ((FriendsManager) ParseUser.getCurrentUser().get("FriendsManager")).fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                FriendsManager friendsManager = (FriendsManager) object;
                ArrayList<ParseUser> friends = (ArrayList<ParseUser>) friendsManager.get("Friends");
                if (friends == null || friends.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    return;
                }
                ParseObject.fetchAllInBackground(friends, new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        swipeContainer.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        if (e == null) {
                            allFriends.clear();
                            allFriends.addAll(objects);
                            adapter.notifyDataSetChanged();
                            Log.i(TAG,"query success!");
                        } else {
                            Log.e(TAG,e.toString());
                        }
                    }
                });
            }
        });
    }

}