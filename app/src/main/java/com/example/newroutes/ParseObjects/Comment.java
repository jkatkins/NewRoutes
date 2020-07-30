package com.example.newroutes.ParseObjects;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_CONTENT = "Content";
    public static final String KEY_CREATOR = "Creator";
    public static final String KEY_ROUTE = "Route";

    public String getContent() {
        return getString(KEY_CONTENT);
    }
    public void setContent(String content) {
        put(KEY_CONTENT,content);
    }

    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }
    public void setCreator(ParseUser user) {
        put(KEY_CREATOR,user);
    }

    public Route getRoute() {
        return (Route)get(KEY_ROUTE);
    }
    public void setRoute(Route route) {
        put(KEY_ROUTE,route);
    }

}
