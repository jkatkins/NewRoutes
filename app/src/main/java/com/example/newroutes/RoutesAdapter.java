package com.example.newroutes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Route> routes;


    public RoutesAdapter(Context context, ArrayList<Route> routes) {
        this.context = context;
        this.routes = routes;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMap = itemView.findViewById(R.id.ivMap);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }

        public void bind(Route route) {
            DecimalFormat df = new DecimalFormat("#.##");
            String distance = df.format(route.getDistance());
            tvDistance.setText(distance + " Miles");
            tvRouteName.setText(route.getName());
            String imageUrl = route.getImageUrl();
            Glide.with(context).load(imageUrl).into(ivMap);
        }
    }
}
