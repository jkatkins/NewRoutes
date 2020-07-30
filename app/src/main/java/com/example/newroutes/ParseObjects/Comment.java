package com.example.newroutes.ParseObjects;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_CONTENT = "Content";
    public static final String KEY_CREATOR = "Creator";
    public static final String KEY_ROUTE = "Route";

    private String getContent() {
        return getString(KEY_CONTENT);
    }
    private void setContent(String content) {
        put(KEY_CONTENT,content);
    }

    private ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }
    private void setCreator(ParseUser user) {
        put(KEY_CREATOR,user);
    }

    private Route getRoute() {
        return (Route)get(KEY_ROUTE);
    }
    private void setRoute(Route route) {
        put(KEY_ROUTE,route);
    }

}
