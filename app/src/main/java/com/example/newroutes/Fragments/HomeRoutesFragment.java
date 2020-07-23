package com.example.newroutes.Fragments;

import com.example.newroutes.ParseObjects.Route;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeRoutesFragment extends RoutesFragment{

    @Override
    protected void queryRoutes() {
        try {
            ParseUser currentUser = ParseUser.getCurrentUser();
            List<Route> userRoutes = (ArrayList<Route>)currentUser.get("Routes");
            if (userRoutes == null) {
                return;
            }
            userRoutes = ParseObject.fetchAll(userRoutes);
            routes.addAll(userRoutes);
            adapter.notifyDataSetChanged();
        } catch (ParseException e) {
            //TODO add error handling
        }
    }


}