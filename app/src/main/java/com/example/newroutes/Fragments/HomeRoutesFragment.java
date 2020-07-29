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


    @Override
    protected void queryRoutes() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<Route> userRoutes = (ArrayList<Route>)currentUser.get("Routes");
        if (userRoutes == null) {
            finishQuery();
            return;
        }
        ParseObject.fetchAllInBackground(userRoutes, new FindCallback<Route>() {
            @Override
            public void done(List<Route> objects, ParseException e) {
                if (e == null) {
                    routes.clear();
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