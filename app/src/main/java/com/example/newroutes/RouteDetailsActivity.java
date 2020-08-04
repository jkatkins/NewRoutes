package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.newroutes.Adapters.CommentAdapter;
import com.example.newroutes.ParseObjects.Comment;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.databinding.ActivityRouteDetailsBinding;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static final String TAG = "RouteDetailsActivity";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private MapboxMap map;
    private CardView cvInfoContainer;
    private MapView mapView;
    private TextView tvDistance;
    private TextView tvRouteName;
    private CardView cvInfoContainerOpen;
    private TextView tvDistanceOpen;
    private TextView tvRouteNameOpen;
    private TextView tvDescription;
    private EditText etAddComment;
    private Button btnSubmit;
    private RecyclerView rvComments;
    private Route route;
    private CommentAdapter adapter;
    private List<Comment> comments;
    ActivityRouteDetailsBinding binding;
    private boolean detailsOpened = false;

    private Scene s1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        route = Parcels.unwrap(i.getParcelableExtra("Route"));

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        binding = ActivityRouteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cvInfoContainer = binding.cvInfoContainer;
        mapView = binding.mapView;
        tvDistance = binding.tvDistance;
        tvRouteName = binding.tvRouteName;
        cvInfoContainerOpen = binding.cvInfoContainerOpen;
        tvDistanceOpen = binding.tvDistanceOpen;
        tvRouteNameOpen = binding.tvRouteNameOpen;
        tvDescription = binding.tvDescription;
        btnSubmit = binding.btnSubmit;
        etAddComment = binding.etAddComment;
        rvComments = binding.rvComments;

        DecimalFormat df = new DecimalFormat("#.##");
        String distance = df.format(route.getDistance());
        tvDistance.setText(distance + " miles");
        tvRouteName.setText(route.getName());
        tvDistanceOpen.setText(distance + " miles");
        tvRouteNameOpen.setText(route.getName());
        tvDescription.setText(route.getDescription());

        comments = new ArrayList<>();
        adapter = new CommentAdapter(this,comments);//fix this
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        queryComments();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        cvInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvInfoContainer.setVisibility(View.GONE);
                cvInfoContainerOpen.setVisibility(View.VISIBLE);
            }
        });
        cvInfoContainerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvInfoContainerOpen.setVisibility(View.GONE);
                cvInfoContainer.setVisibility(View.VISIBLE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = etAddComment.getText().toString();
                final Comment comment = new Comment();
                comment.setContent(commentContent);
                comment.setCreator(ParseUser.getCurrentUser());
                comment.setRoute(route);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i(TAG,"comment saved!");
                            route.addComment(comment);
                            route.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        comments.add(comment);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG,"error saving: " + e);
                        }
                    }
                });
            }
        });

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
//
//        ParseQuery<Comment> parseQuery = ParseQuery.getQuery(Comment.class);
//
//        SubscriptionHandling<Comment> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
//
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Comment>() {
//            @Override
//            public void onEvent(ParseQuery<Comment> query, Comment object) {
//                queryComments();
//            }
//        });
    }

    private void queryComments() {
        ArrayList<Comment> fetchedComments = (ArrayList<Comment>)route.get("Comments");
        if (fetchedComments == null) {
            return;
        }
        ParseObject.fetchAllInBackground(fetchedComments, new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    Log.i("Comment","added " + objects.size());
                    comments.clear();
                    comments.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    //error
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        initLayers(style);
                    }
                });

    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
        GeoJsonSource source = loadedMapStyle.getSourceAs(ROUTE_SOURCE_ID);
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<ArrayList<Double>> routeArray = route.getLinestring();
        for (ArrayList<Double> coordPair : routeArray) {
            Point newPoint = Point.fromLngLat(coordPair.get(0),coordPair.get(1));
            points.add(newPoint);
        }
        LineString routeLineString = LineString.fromLngLats(points);
        source.setGeoJson(routeLineString);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(points.get(0).latitude(), points.get(0).longitude()))
                .zoom(12)
                .build();
        map.setCameraPosition(position);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //lifecycle methods for the map
    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}