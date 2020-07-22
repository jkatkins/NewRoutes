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

import com.example.newroutes.Adapters.FriendRequestsAdapter;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentHomeFriendsBinding;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFriendsFragment extends Fragment {


    public static final String TAG = "HomeFriendsFragment";
    private RecyclerView rvFriendRequests;
    private FriendRequestsAdapter adapter;
    private ArrayList<ParseUser> friendRequests;
    FragmentHomeFriendsBinding binding;


    public HomeFriendsFragment() {
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
        binding = FragmentHomeFriendsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriendRequests = binding.rvFriendRequests;
        friendRequests = new ArrayList<>();
        adapter = new FriendRequestsAdapter(friendRequests,getContext());
        rvFriendRequests.setAdapter(adapter);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            queryRequests();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void queryRequests() throws ParseException {
        Log.i(TAG,"query requests");
        FriendsManager friendsManager = ((FriendsManager)ParseUser.getCurrentUser().get("FriendsManager")).fetch();
        ArrayList<ParseUser> incomingRequests = (ArrayList<ParseUser>) friendsManager.get("IncomingRequests");
        if (incomingRequests == null || incomingRequests.size() == 0) {
            return;
        }
        for (ParseUser userRequest : incomingRequests) {
            userRequest = userRequest.fetch();
            friendRequests.add(userRequest);
        }
        adapter.notifyDataSetChanged();
    }
}