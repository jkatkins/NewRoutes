package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newroutes.ParseObjects.Route;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


import com.bumptech.glide.Glide;
import com.example.newroutes.databinding.ActivityCreateRouteBinding;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class CustomRouteActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";


    public static final String TAG = "CreateRouteActivity";
    private static final String ID_ICON = "placeholder";
    private MapView mapView;
    private MapboxMap map;
    private Button btnStart;
    private Button btnSave;
    private EditText etRouteName;
    private TextView tvDistanceText;
    private ImageView ivMap;
    private Button btnSaveFinal;
    private ImageView hoveringMarker;
    private SymbolManager symbolManager;
    private Symbol symbol1 = null;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private EditText etDistance;
    private MapboxDirections client;
    private MapboxGeocoding geoClient;
    private int numPoints;
    private Double distanceInMiles;
    private LatLng center;
    private Point centerPoint;
    private Double radius;
    private int targetNumPoints = 4;
    private double angle;
    private double length;
    private GeoJson routeGeoJson;
    private double width;
    private FrameLayout flSaveRoute;
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Symbol> symbols = new ArrayList<>();
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    ActivityCreateRouteBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        binding = ActivityCreateRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnStart = binding.btnStart;
        btnSave = binding.btnSave;
        flSaveRoute = binding.flSaveRoute;
        etDistance = binding.etDistance;
        etRouteName = binding.etRouteName;
        ivMap = binding.ivMap;
        tvDistanceText = binding.tvDistanceText;
        btnSaveFinal = binding.btnSaveFinal;
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        enableLocationComponent(style);
                        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
// This is done by using an image view with the default marker found in the SDK. You can
// swap out for your own marker image, just make sure it matches up with the dropped marker.
                        hoveringMarker = new ImageView(CustomRouteActivity.this);
                        hoveringMarker.setImageResource(R.drawable.mapbox_marker_icon_default);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                        hoveringMarker.setLayoutParams(params);
                        mapView.addView(hoveringMarker);

                        symbolManager = new SymbolManager(mapView, map, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);

                        initLayers(style);

                        style.addImage(ID_ICON, BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.mapbox_marker_icon_default)));

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
    }

    private void getRoute(Point origin,Point Destination) {
        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(origin)
                .destination(Destination)
                .overview(DirectionsCriteria.OVERVIEW_SIMPLIFIED)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.mapbox_access_token));

        for (Point waypoint : points) {
            builder.addWaypoint(waypoint);
            builder.continueStraight(true);
        }
        client = builder.build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    Log.e(TAG,"No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG,"No routes found");
                    return;
                }
// Get the directions route
                currentRoute = response.body().routes().get(0);
// Make a toast which displays the route's distance
                Toast.makeText(CustomRouteActivity.this, (Double.toString(currentRoute.distance()*0.000621371)), Toast.LENGTH_SHORT).show();
                //0.000621371 is conversion from meters to miles
                if (map != null) {
                    map.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
// Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                LineString drawnRoute = LineString.fromPolyline(currentRoute.geometry(), PRECISION_6);
                                routeGeoJson = drawnRoute;
                                source.setGeoJson(drawnRoute);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG,"Error");
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(CustomRouteActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private Symbol dropPin(LatLng targetLatLng) {
        Log.i(TAG,"symbol placing");
        Symbol symbol = symbolManager.create(new
                SymbolOptions()
                .withLatLng(targetLatLng)
                .withIconImage(ID_ICON)
                .withIconSize(1.1f));
        return symbol;
    }
    private Symbol dropHiddenPin(LatLng targetLatLng) {
        Log.i(TAG,"symbol placing");
        Symbol symbol = symbolManager.create(new
                SymbolOptions()
                .withLatLng(targetLatLng));
        return symbol;
    }



    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = map.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            //Handling maybe? or not needed?
        }
    }

    //back button
    @Override
    public boolean onSupportNavigateUp() {
        if (flSaveRoute.getVisibility() == View.GONE) {
            onBackPressed();
        } else {
            flSaveRoute.setVisibility(View.GONE);
        }
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