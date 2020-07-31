package com.example.newroutes;

import android.app.Application;

import com.example.newroutes.ParseObjects.Comment;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.ParseObjects.Route;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Route.class);
        ParseObject.registerSubclass(FriendsManager.class);
        ParseObject.registerSubclass(Comment.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("newroutes") // should correspond to APP_ID env variable
                .clientKey("183945701345")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://new-routes.herokuapp.com/parse")
                .build());

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }


}