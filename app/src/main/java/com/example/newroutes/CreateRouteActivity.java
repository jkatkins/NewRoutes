package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Toast;


import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


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
import com.mapbox.geojson.Feature;
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
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class CreateRouteActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";

    public static final String TAG = "CreateRouteActivity";
    private static final String ID_ICON = "placeholder";
    private MapView mapView;
    private MapboxMap map;
    private Button btnStart;
    private ImageView hoveringMarker;
    private SymbolManager symbolManager;
    private Symbol symbol1 = null;
    private Symbol symbol2 = null;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private EditText etDistance;
    private MapboxDirections client;
    private MapboxGeocoding geoClient;
    private int numPoints;
    private LatLng center;
    private Double radius;
    private int targetNumPoints = 4;
    private double angle;
    private ArrayList<Point> points = new ArrayList<>();
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
        etDistance = binding.etDistance;
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
                        hoveringMarker = new ImageView(CreateRouteActivity.this);
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

                        btnStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final LatLng mapTargetLatLng = map.getCameraPosition().target;
                                //dropPin(mapTargetLatLng);
                                //createRoute(mapTargetLatLng,map,style);
                                //routeFromRandStart(mapTargetLatLng,map,style);
                                if (symbol1 == null) { //User's first click on button
                                    symbol1 = dropPin(mapTargetLatLng);
                                    btnStart.setText(R.string.generate_route);
                                    hoveringMarker.setVisibility(View.INVISIBLE);
                                } else if (numPoints == targetNumPoints) { //reset route
                                    numPoints = 0;
                                    btnStart.setText(R.string.choose_origin);
                                    GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
                                    source.setGeoJson(symbol1.getGeometry());
                                    symbolManager.delete(symbol1);
                                    symbolManager.delete(symbol2);
                                    symbol1 = null;
                                    symbol2 = null;
                                    points.clear();
                                    hoveringMarker.setVisibility(View.VISIBLE);
                                } else if (numPoints > 0) { //Start has been validated, distance is invalid
                                    generateMore(map,style);
                                } else if (etDistance.getText().toString().isEmpty() || Double.parseDouble(etDistance.getText().toString())<=0){ //Generate click
                                    Toast.makeText(CreateRouteActivity.this, "Enter a valid distance", Toast.LENGTH_SHORT).show();
                                } else { //generate with valid distance
                                    checkPoint(symbol1.getGeometry(),style);
                                }
                            }
                        });
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
    }

    private void clearRoute() {

    }

    private void getRoute(final MapboxMap mapboxMap, Point origin, Point destination) {
        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(origin)
                .destination(origin)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.mapbox_access_token));

        for (Point waypoint : points) {
            builder.addWaypoint(waypoint);
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
                Toast.makeText(CreateRouteActivity.this, (Double.toString(currentRoute.distance()*0.000621371)), Toast.LENGTH_SHORT).show();
                //0.000621371 is conversion from meters to miles
                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

// Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);


// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                LineString drawnRoute = LineString.fromPolyline(currentRoute.geometry(), PRECISION_6);
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
                Toast.makeText(CreateRouteActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMore(MapboxMap map,Style loadedMapStyle) {
        if (loadedMapStyle.getSourceAs(ROUTE_SOURCE_ID) == null) {
            loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
        }
        if (numPoints == targetNumPoints) {
            getRoute(map,symbol1.getGeometry(),symbol2.getGeometry());
            btnStart.setText(R.string.reset);
            return;
        }
        symbol2 = randFromCenter();
        checkPoint(symbol2.getGeometry(),loadedMapStyle);
    }

    private void checkPoint(final Point point, final Style style) {
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(point)
                .geocodingTypes(GeocodingCriteria.TYPE_POSTCODE)
                .build();
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {
                    // Log the first results Point.
                    if (numPoints >0) {
                        points.add(point);
                    } else {
                        radius = Double.parseDouble(etDistance.getText().toString())/(2 * Math.PI);
                        radius = radius/69;
                        generateCenter(symbol1.getGeometry());
                    }
                    Point firstResultPoint = results.get(0).center();
                    Log.d(TAG, "onResponse: " + firstResultPoint.toString());
                    numPoints++;
                    generateMore(map,style);
                } else {
                    // No result for your request were found.
                    if (numPoints == 0) { //failed to choose a valid start
                        symbolManager.delete(symbol1);
                        symbol1 = null;
                        btnStart.setText(R.string.choose_origin);
                        Toast.makeText(CreateRouteActivity.this, R.string.invalid_start, Toast.LENGTH_SHORT).show();
                        hoveringMarker.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onResponse: No result found");
                    } else { //failed to choose a valid random point
                        symbolManager.delete(symbol2);
                        symbol2 = null;
                        generateMore(map,style);
                    }
                }
            }
            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
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

    private void generateCenter(Point origin) {
        double degrees = Math.random() * 360;
        angle = (degrees + 180) % 360;
        LatLng newPoint = new LatLng(origin.latitude() + radius * Math.sin(degrees),
                origin.longitude() + radius * Math.cos(degrees));
        center = newPoint;
        Symbol centerPin = dropPin(newPoint);
        centerPin.setIconSize(2f);
    }

    private Symbol randFromCenter() {
        double degrees = (angle + (360/(targetNumPoints-1))) % 360;
        angle = degrees;
        LatLng newPoint = new LatLng(center.getLatitude() + radius * Math.sin(Math.toRadians(degrees)),
                center.getLongitude() + radius* Math.cos(Math.toRadians(degrees)));
        return dropPin(newPoint);

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
            Toast.makeText(this, "R.string.user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //back button
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