package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFavoritesFragment extends RoutesFragment {


    @Override
    protected void queryRoutes() {
        routes.clear();
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<Route> favoriteRoutes = (ArrayList<Route>)currentUser.get("Favorites");
        if (favoriteRoutes == null) {
            finishQuery();
            return;
        }
        ParseObject.fetchAllInBackground(favoriteRoutes, new FindCallback<Route>() {
            @Override
            public void done(List<Route> objects, ParseException e) {
                if (e == null) {
                    routes.addAll(objects);
                    finishQuery();
                } else {
                    Log.e(TAG,"failed to fetch routes");
                }
            }
        });
    }

    private void finishQuery() {
        swipeContainer.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}