package com.example.newroutes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.R;
import com.example.newroutes.RouteInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Route> routes;
    private RouteInterface routeInterface;


    public RoutesAdapter(Context context, ArrayList<Route> routes,RouteInterface routeInterface) {
        this.context = context;
        this.routes = routes;
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
        Route route;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMap = itemView.findViewById(R.id.ivMap);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO implement an interface
                    routeInterface.onRouteSelected(route);
                }
            });
        }

        public void bind(Route route) {
            DecimalFormat df = new DecimalFormat("#.##");
            String distance = df.format(route.getDistance());
            tvDistance.setText(distance + " Miles");
            tvRouteName.setText(route.getName());
            String imageUrl = route.getImageUrl();
            Glide.with(context).load(imageUrl).into(ivMap);
            this.route = route;
        }
    }
}
