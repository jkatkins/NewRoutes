package com.example.newroutes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newroutes.ParseObjects.FriendsManager;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.example.newroutes.ViewUserProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";
    Context context;
    ArrayList<ParseUser> users;
    ArrayList<ParseUser> friends;
    ArrayList<ParseUser> outgoingRequests;
    ArrayList<ParseUser> incomingRequests;
    private FirebaseFunctions mFunctions;

    public UsersAdapter(Context context, ArrayList<ParseUser> users,ArrayList<ParseUser> friends,ArrayList<ParseUser> outgoingRequests,ArrayList<ParseUser> incomingRequests) {
        this.context = context;
        this.users = users;
        this.friends = friends;
        this.outgoingRequests = outgoingRequests;
        this.incomingRequests = incomingRequests;
        mFunctions = FirebaseFunctions.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser newUser = users.get(position);
        holder.bind(newUser);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvNumRoutes;
        Button btnAddFriend;
        ParseUser user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNumRoutes = itemView.findViewById(R.id.tvNumRoutes);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ViewUserProfileActivity.class);
                    i.putExtra("User", Parcels.wrap(user));
                    context.startActivity(i);
                }
            });
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnAddFriend.setClickable(false);
                    btnAddFriend.setText(R.string.request_sent);
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    FriendsManager friendsManagerCurrent = (FriendsManager) currentUser.get("FriendsManager");
                    FriendsManager friendsManagerRecipient = (FriendsManager) user.get("FriendsManager");
                    friendsManagerCurrent.addOutgoing(user);
                    friendsManagerRecipient.addIncoming(currentUser);
                    friendsManagerCurrent.saveInBackground();
                    friendsManagerRecipient.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                try {
                                    addMessage("nothing");
                                } catch (ParseException innerParseException) {
                                    innerParseException.printStackTrace();
                                }
                            } else {
                                //error
                            }
                        }
                    });
                    Log.i(TAG,"sent friend request"); //TODO add error handling, disable after friend request has been sent
                }
            });
        }
        //citDXnVhRWysJMlZuCfM23:APA91bHZ2odcUDSV2ropuEJwQCc2CHcyLCfUX1SXcqbEFgg9nQDHBxPKj_ddr6GHQJ3bvFh3UpLQ6voj-x8mL2qisW-IawQH8vqH6_njRvZT_SqGi1lLHLOvNB1t9rXdBZA410-jnLKm

        private Task<String> addMessage(String text) throws ParseException {
            // Create the arguments to the callable function.
            Map<String, Object> data = new HashMap<>();
            String token = user.fetchIfNeeded().getString("Token");
            data.put("token", token);
            data.put("push", true);
            data.put("username",ParseUser.getCurrentUser().getString("username"));

            return mFunctions
                    .getHttpsCallable("sendNotification")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, String>() {
                        @Override
                        public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            // This continuation runs on either success or failure, but if the task
                            // has failed then getResult() will throw an Exception which will be
                            // propagated down.
                            String result = (String) task.getResult().getData();
                            return result;
                        }
                    });
        }

        public void bind(ParseUser newUser) {
            tvUsername.setText(newUser.getUsername());
            Glide.with(context).load(newUser.getParseFile("Picture").getUrl()).into(ivProfilePicture);
            this.user = newUser;
            if (newUser.get("Routes") == null) {
                tvNumRoutes.setText("Routes Created: 0"); //TODO Figure out how concatenation with string resources works
            } else {
                tvNumRoutes.setText("Routes Created: " + ((ArrayList<Route>) newUser.get("Routes")).size());
            }
            updateButton();

        }
        public void updateButton() {
            if (friends != null && friends.size() > 0) {
                for (ParseUser friend : friends) {
                    if (friend.getObjectId().equals(user.getObjectId())) {
                        btnAddFriend.setText(R.string.friends);
                        btnAddFriend.setClickable(false);
                    }
                }
            }
            if (outgoingRequests != null && outgoingRequests.size() > 0) {
                for (ParseUser request : outgoingRequests) {
                    if (request.getObjectId().equals(user.getObjectId())) {
                        btnAddFriend.setText(R.string.request_sent);
                        btnAddFriend.setClickable(false);
                    }
                }
            }
            if (incomingRequests != null && incomingRequests.size() > 0) {
                for (ParseUser request : incomingRequests) {
                    if (request.getObjectId().equals(user.getObjectId())) {
                        btnAddFriend.setText(R.string.respond);
                        btnAddFriend.setClickable(false);
                    }
                }
            }
        }
    }
}
