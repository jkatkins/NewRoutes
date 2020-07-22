package com.example.newroutes.Fragments;

import com.example.newroutes.ParseObjects.Route;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;


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