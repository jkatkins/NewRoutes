package com.example.newroutes.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.example.newroutes.RouteInterface;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Route> routes;
    private ArrayList<Route> favorites;
    private RouteInterface routeInterface;


    public RoutesAdapter(Context context, ArrayList<Route> routes, ArrayList<Route> favorites, RouteInterface routeInterface) {
        this.context = context;
        this.routes = routes;
        this.favorites = favorites;
        this.routeInterface = routeInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route,parent,false);
        return new RoutesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routes.get(position);
        holder.bind(route);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMap;
        TextView tvRouteName;
        TextView tvDistance;
        ImageButton ibFavorite;
        Route route;
        boolean favorited;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMap = itemView.findViewById(R.id.ivMap);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    routeInterface.onRouteSelected(route,tvRouteName,tvDistance);
                }
            });
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    //TODO handle adding route to favorites list here
                    if (favorited) {
                        ibFavorite.setImageResource(R.drawable.ic_favorite_empty);
                        for (Route favoriteRoute : favorites) {
                            if (favoriteRoute.getObjectId().equals(route.getObjectId())) {
                                favorites.remove(favoriteRoute);
                                break;
                            }
                        }
                        currentUser.remove("Favorites");
                        currentUser.put("Favorites",favorites);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //good
                                }
                            }
                        });
                    } else {
                        ibFavorite.setImageResource(R.drawable.ic_favorite_filled);
                        favorites.add(route);
                        currentUser.remove("Favorites");
                        currentUser.put("Favorites",favorites);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //good
                                }
                            }
                        });
                    }
                    favorited = !favorited;
                }
            });
        }

        public void bind(Route route) {
            ibFavorite.setClickable(false);
            favorited = false;
            DecimalFormat df = new DecimalFormat("#.##");
            String distance = df.format(route.getDistance());
            tvDistance.setText(distance + " Miles");
            tvRouteName.setText(route.getName());
            String imageUrl = route.getImageUrl();
            Glide.with(context).load(imageUrl).into(ivMap);
            this.route = route;
            for (Route favoriteRoute : favorites) {
                if (favoriteRoute.getObjectId().equals(route.getObjectId())) {
                    ibFavorite.setImageResource(R.drawable.ic_favorite_filled);
                    favorited = true;
                    break;
                }
            }
            ibFavorite.setClickable(true);
        }
    }
}
