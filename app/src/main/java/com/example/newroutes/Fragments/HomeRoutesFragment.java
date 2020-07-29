package com.example.newroutes.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newroutes.ParseObjects.Route;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeRoutesFragment extends RoutesFragment{

    private int query;

    @Override
    protected void queryRoutes() {
        query = 2;
        routes.clear();
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<Route> userRoutes = (ArrayList<Route>)currentUser.get("Routes");
        if (userRoutes == null) {
            query--;
        }
        ArrayList<Route> favorites = (ArrayList<Route>)currentUser.get("Favorites");
        if (favorites == null) {
            query--;
            if (query == 0) {
                finishQuery();
            }
        }
        ParseObject.fetchAllInBackground(userRoutes, new FindCallback<Route>() {
            @Override
            public void done(List<Route> objects, ParseException e) {
                query--;
                if (e == null) {
                    routes.addAll(objects);
                } else {
                    Log.e(TAG,"failed to fetch routes");
                }
                if (query == 0) {
                    finishQuery();
                }
            }
        });
        ParseObject.fetchAllInBackground(favorites, new FindCallback<Route>() {
            @Override
            public void done(List<Route> fetchedFavorites, ParseException e) {
                query--;
                if (e == null) {
                    routes.addAll(fetchedFavorites);
                } else {
                    Log.e(TAG,"failed to fetch favorites");
                }
                if (query == 0) {
                    finishQuery();
                }
            }
        });
    }

    private void finishQuery() {
        swipeContainer.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (!routes.isEmpty()) {
            ivEmpty.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.INVISIBLE);
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }


}