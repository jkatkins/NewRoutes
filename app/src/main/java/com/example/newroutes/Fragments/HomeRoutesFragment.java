package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeRoutesFragment extends RoutesFragment{

    @Override
    protected void queryRoutes() {
        try {
            ParseUser currentUser = ParseUser.getCurrentUser();
            ArrayList<Route> userRoutes = (ArrayList<Route>)currentUser.get("Routes");
            if (userRoutes == null) {
                return;
            }
            for (Route route : userRoutes) {
                route = route.fetch();
                routes.add(route);
            }
            adapter.notifyDataSetChanged();
        } catch (ParseException e) {
            //TODO add error handling
        }
    }
}