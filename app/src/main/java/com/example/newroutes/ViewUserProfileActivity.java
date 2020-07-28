package com.example.newroutes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newroutes.Adapters.RoutesAdapter;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.databinding.ActivityViewUserProfileBinding;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ViewUserProfileActivity extends AppCompatActivity implements RouteInterface{

    private RecyclerView rvRoutes;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private ArrayList<Route> routes;
    private ParseUser user;
    public RoutesAdapter adapter;
    ActivityViewUserProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        user = Parcels.unwrap(i.getParcelableExtra("User"));

        rvRoutes = binding.rvRoutes;
        ivProfilePicture = binding.ivProfilePicture;
        tvUsername = binding.tvUsername;
        routes = new ArrayList<>();
        adapter = new RoutesAdapter(this,routes,this);
        rvRoutes.setAdapter(adapter);
        rvRoutes.setLayoutManager(new GridLayoutManager(this,2));
        queryRoutes();
        tvUsername.setText(user.getUsername());
        Glide.with(this).load(user.getParseFile("Picture").getUrl()).into(ivProfilePicture);
    }

    private void queryRoutes() {
        try {
            ArrayList<Route> userRoutes = (ArrayList<Route>)user.get("Routes");
            if (userRoutes == null) {
                return;
            }
            for (Route route : userRoutes) {
                route = route.fetch();
                routes.add(route);
            }
            adapter.notifyDataSetChanged();
        } catch (ParseException e) {
            //TODO add error handling
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRouteSelected(Route route, View name, View distance) {
        Intent i = new Intent (this, RouteDetailsActivity.class);
        i.putExtra("Route",Parcels.wrap(route));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(name,"nameTransition"),Pair.create(distance,"distanceTransition"));
        startActivity(i,options.toBundle());
    }
}