package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newroutes.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    private TabLayout tlTabs;
    private ViewPager vpPager;
    FragmentHomeBinding binding;

    private HomeRoutesFragment homeRoutesFragment;
    private HomeFriendRequestsFragment homeFriendRequestsFragment;
    private HomeFriendsFragment homeFriendsFragment;

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
        vpPager = binding.vpPager;
        tlTabs = binding.tlTabs;

        homeRoutesFragment = new HomeRoutesFragment();
        homeFriendRequestsFragment = new HomeFriendRequestsFragment();
        homeFriendsFragment = new HomeFriendsFragment();


        tlTabs.setupWithViewPager(vpPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        vpPager.setAdapter(adapter);
        adapter.addFragment(homeRoutesFragment,"Routes");
        adapter.addFragment(homeFriendRequestsFragment,"Friend Requests");
        adapter.addFragment(homeFriendsFragment,"Friends");
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment,String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }


}