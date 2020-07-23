package com.example.newroutes.ParseObjects;


import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("FriendsManager")
public class FriendsManager extends ParseObject {

    public static final String KEY_FRIENDS = "Friends";
    public static final String KEY_INCOMING_REQUESTS = "IncomingRequests";
    public static final String KEY_OUTGOING_REQUESTS = "OutgoingRequests";

    public ArrayList<ParseUser> getFriends() {
        return (ArrayList<ParseUser>) get(KEY_FRIENDS);
    }
    public void addFriend(ParseUser toAdd) {
        add(KEY_FRIENDS,toAdd);
    }

    public ArrayList<ParseUser> getIncoming() {
        return (ArrayList<ParseUser>) get(KEY_INCOMING_REQUESTS);
    }
    public void addIncoming(ParseUser toAdd) {
        add(KEY_INCOMING_REQUESTS,toAdd);
    }
    public void removeIncoming(ParseUser toRemove) {
        ArrayList<ParseUser> incomingRequests = getIncoming();
        for (ParseUser request : incomingRequests){
            if (request.getObjectId().equals(toRemove.getObjectId())) {
                incomingRequests.remove(request);
            }
        }
        remove(KEY_INCOMING_REQUESTS);
        put(KEY_INCOMING_REQUESTS,incomingRequests);
    }

    public ArrayList<ParseUser> getOutgoing() {
        return (ArrayList<ParseUser>) get(KEY_OUTGOING_REQUESTS);
    }
    public void addOutgoing(ParseUser toAdd) {
        add(KEY_OUTGOING_REQUESTS,toAdd);
    }
    public void removeOutgoing(ParseUser toRemove) throws ParseException {
        ArrayList<ParseUser> outgoingRequests = getOutgoing();
        for (ParseUser request : outgoingRequests) {
            if (request.getObjectId().equals(toRemove.getObjectId())) {
                outgoingRequests.remove(request);
                break;
            }
        }
        remove(KEY_OUTGOING_REQUESTS);
        put(KEY_OUTGOING_REQUESTS,outgoingRequests);
    }



}
