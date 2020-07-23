package com.example.newroutes.Adapters;

import android.content.Context;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    public static final String TAG = "FriendRequestAdapter";
    private ArrayList<ParseUser> friendRequests;
    private Context context;

    public FriendRequestsAdapter(ArrayList<ParseUser> friendRequests, Context context) {
        this.friendRequests = friendRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request,parent,false);
        return new FriendRequestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = friendRequests.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvNumRoutes;
        Button btnAccept;
        Button btnDecline;
        ParseUser user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNumRoutes = itemView.findViewById(R.id.tvNumRoutes);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                    try {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        FriendsManager currentFriendsManager = ((FriendsManager)currentUser.get("FriendsManager")).fetch();
                        currentFriendsManager.addFriend(user);
                        currentFriendsManager.removeIncoming(user);
                        currentFriendsManager.saveInBackground();
                        FriendsManager senderFriendsManager = ((FriendsManager)user.get("FriendsManager")).fetch();
                        senderFriendsManager.addFriend(currentUser);
                        senderFriendsManager.removeOutgoing(currentUser);
                        senderFriendsManager.saveInBackground();
                        //ParseUser.getCurrentUser().put("FriendsManager",currentFriendsManager);
                        //ParseUser.getCurrentUser().saveInBackground();//TODO Error handling
                        //user.put("FriendsManager",senderFriendsManager);
                        //user.saveInBackground();
                        friendRequests.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                    try {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        FriendsManager senderFriendsManager = ((FriendsManager)user.get("FriendsManager")).fetchIfNeeded();
                        senderFriendsManager.removeOutgoing(currentUser);
                        FriendsManager currentFriendsManager = ((FriendsManager)currentUser.get("FriendsManager")).fetchIfNeeded();
                        currentFriendsManager.removeIncoming(user);
                        senderFriendsManager.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i(TAG,"save success");
                                } else {
                                    Log.e(TAG,e.toString());
                                }
                            }
                        });
                        currentFriendsManager.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i(TAG,"save success");
                                } else {
                                    Log.e(TAG,e.toString());
                                }
                            }
                        });
                        friendRequests.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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
        }
    }
}
