package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newroutes.R;
import com.example.newroutes.Route;
import com.example.newroutes.RoutesAdapter;
import com.example.newroutes.databinding.FragmentRoutesBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {

    public static final String TAG = "RoutesFragment";
    private ArrayList<Route> routes;
    private RecyclerView rvRoutes;
    private RoutesAdapter adapter;
    FragmentRoutesBinding binding;

    public RoutesFragment() {
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
        binding = FragmentRoutesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvRoutes = binding.rvRoutes;
        routes = new ArrayList<>();
        adapter = new RoutesAdapter(getContext(),routes);
        rvRoutes.setAdapter(adapter);
        rvRoutes.setLayoutManager(new GridLayoutManager(getContext(),2));
        queryRoutes();
    }

    protected void queryRoutes() {
        ParseQuery<Route> query = ParseQuery.getQuery(Route.class);
        query.include(Route.KEY_USER);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> newRoutes, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"issue");
                    return;
                }
                Log.i(TAG,"Adding " + newRoutes.size() + " routes");
                routes.clear();
                routes.addAll(newRoutes);
                adapter.notifyDataSetChanged();
            }
        });
    }
}