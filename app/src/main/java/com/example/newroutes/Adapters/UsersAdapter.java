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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";
    Context context;
    ArrayList<ParseUser> users;
    ArrayList<ParseUser> friends;
    ArrayList<ParseUser> outgoingRequests;
    ArrayList<ParseUser> incomingRequests;

    public UsersAdapter(Context context, ArrayList<ParseUser> users,ArrayList<ParseUser> friends,ArrayList<ParseUser> outgoingRequests,ArrayList<ParseUser> incomingRequests) {
        this.context = context;
        this.users = users;
        this.friends = friends;
        this.outgoingRequests = outgoingRequests;
        this.incomingRequests = incomingRequests;
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
                    friendsManagerRecipient.saveInBackground();
                    Log.i(TAG,"sent friend request"); //TODO add error handling, disable after friend request has been sent
                }
            });
        }

        private void sendFCMPush() {

            String Legacy_SERVER_KEY = "";
            String msg = "this is test message,.,,.,.";
            String title = "my title";
            String token = "";

            JSONObject obj = null;
            JSONObject objData = null;
            JSONObject dataobjData = null;

            try {
                obj = new JSONObject();
                objData = new JSONObject();

                objData.put("body", msg);
                objData.put("title", title);
                objData.put("sound", "default");
                objData.put("icon", "icon_name"); //   icon_name image must be there in drawable
                objData.put("tag", token);
                objData.put("priority", "high");

                dataobjData = new JSONObject();
                dataobjData.put("text", msg);
                dataobjData.put("title", title);

                obj.put("to", token);
                //obj.put("priority", "high");

                obj.put("notification", objData);
                obj.put("data", dataobjData);
                Log.e("!_@rj@_@@_PASS:>", obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
