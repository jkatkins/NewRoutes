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

import com.example.newroutes.Adapters.FriendRequestsAdapter;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentHomeFriendRequestsBinding;
import com.example.newroutes.databinding.FragmentHomeFriendsBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFriendRequestsFragment extends Fragment {


    public static final String TAG = "HomeFriendRequestsFrag";
    private RecyclerView rvFriendRequests;
    private FriendRequestsAdapter adapter;
    public ArrayList<ParseUser> friendRequests;
    private ProgressBar progressBar;
    private BadgeDrawable badgeDrawable;
    private SwipeRefreshLayout swipeContainer;
    private TabLayout tlTabs;
    FragmentHomeFriendRequestsBinding binding;


    public HomeFriendRequestsFragment() {
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
        binding = FragmentHomeFriendRequestsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriendRequests = binding.rvFriendRequests;
        progressBar = binding.progressBar;
        swipeContainer = binding.swipeContainer;
        badgeDrawable = null;
        tlTabs = ((View)view.getParent()).getRootView().findViewById(R.id.tlTabs);
        friendRequests = new ArrayList<>();
        adapter = new FriendRequestsAdapter(friendRequests,getContext());
        rvFriendRequests.setAdapter(adapter);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            progressBar.setVisibility(View.VISIBLE);
            queryRequests();
        } catch (ParseException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    queryRequests();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateBadge() {
        if (friendRequests.size() > 0) {
            badgeDrawable.setNumber(friendRequests.size());
        } else {
            badgeDrawable.setNumber(0);
            badgeDrawable.setVisible(false);
        }
    }

    private void queryRequests() throws ParseException {
        Log.i(TAG,"query requests");
        ((FriendsManager)ParseUser.getCurrentUser().get("FriendsManager")).fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                FriendsManager friendsManager = (FriendsManager)object;
                ArrayList<ParseUser> incomingRequests = (ArrayList<ParseUser>) friendsManager.get("IncomingRequests");
                if (incomingRequests == null || incomingRequests.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    return;
                }
                ParseObject.fetchAllInBackground(incomingRequests, new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        progressBar.setVisibility(View.GONE);
                        swipeContainer.setRefreshing(false);
                        if (e == null) {
                            friendRequests.clear();
                            friendRequests.addAll(objects);
                            adapter.notifyDataSetChanged();
                            Log.i(TAG,"query success!");
                        } else {
                            Log.e(TAG,e.toString());
                        }
                        badgeDrawable = tlTabs.getTabAt(1).getOrCreateBadge();
                        badgeDrawable.setNumber(friendRequests.size());
                        badgeDrawable.setVisible(true);
                    }
                });
            }
        });
    }
}