package com.example.newroutes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Route")
public class Route extends ParseObject {

    public static final String KEY_NAME = "Name";
    public static final String KEY_DISTANCE = "Distance";
    public static final String KEY_LINESTRING = "Linestring";
    public static final String KEY_IMAGEURL = "ImageUrl";
    public static final String KEY_ORIGIN = "Origin";
    public static final String KEY_USER = "User";


    public String getName () {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME,name);
    }

    public Double getDistance() {
        return getDouble(KEY_DISTANCE);
    }
    public void setDistance(Double distance) {
        put(KEY_DISTANCE,distance);
    }

    public ArrayList<Coordinate> getLinestring() {
        return (ArrayList<Coordinate>)get(KEY_LINESTRING);
    }
    public void setLinestring(ArrayList<Coordinate> coordinates) {
        put(KEY_LINESTRING,coordinates);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGEURL);
    }
    public void setImageUrl(String imageUrl) {
        put(KEY_IMAGEURL,imageUrl);
    }

    public Coordinate getOrigin() {
        return (Coordinate)get(KEY_ORIGIN);
    }
    public void setOrigin(Coordinate origin){
        put(KEY_ORIGIN,origin);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER,user);
    }


}
