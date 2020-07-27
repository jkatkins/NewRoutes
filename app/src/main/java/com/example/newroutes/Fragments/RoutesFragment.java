package com.example.newroutes.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.RouteDetailsActivity;
import com.example.newroutes.RouteInterface;
import com.example.newroutes.Adapters.RoutesAdapter;
import com.example.newroutes.databinding.FragmentRoutesBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment implements RouteInterface {

    public static final String TAG = "RoutesFragment";
    public ArrayList<Route> routes;
    public RecyclerView rvRoutes;
    public RoutesAdapter adapter;
    public ImageView ivEmpty;
    public TextView tvEmpty;
    public ProgressBar progressBar;
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
        tvEmpty = binding.tvEmpty;
        ivEmpty = binding.ivEmpty;
        progressBar = binding.progressBar;
        routes = new ArrayList<>();
        adapter = new RoutesAdapter(getContext(),routes,this);
        rvRoutes.setAdapter(adapter);
        rvRoutes.setLayoutManager(new GridLayoutManager(getContext(),2));
        queryRoutes();
    }


    protected void queryRoutes() {
        ParseQuery<Route> query = ParseQuery.getQuery(Route.class);
        query.include(Route.KEY_USER);
        query.setLimit(20);
        progressBar.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> newRoutes, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG,"issue");
                    return;
                }
                Log.i(TAG,"Adding " + newRoutes.size() + " routes");
                routes.clear();
                routes.addAll(newRoutes);
                adapter.notifyDataSetChanged();
                if (!routes.isEmpty()) {
                    ivEmpty.setVisibility(View.INVISIBLE);
                    tvEmpty.setVisibility(View.INVISIBLE);
                } else {
                    ivEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRouteSelected(Route route) {
        Intent i = new Intent (getContext(), RouteDetailsActivity.class);
        i.putExtra("Route",Parcels.wrap(route));
        startActivity(i);
    }
}