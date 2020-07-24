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

public class CreateRouteActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";

    final FragmentManager fragmentManager = getSupportFragmentManager();

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
                                    source.setGeoJson(FeatureCollection.fromJson(""));
                                    symbolManager.delete(symbol1);
                                    symbol1 = null;
                                    points.clear();
                                    for (Symbol symbol : symbols) {
                                        symbolManager.delete(symbol);
                                    }
                                    symbols.clear();
                                    hoveringMarker.setVisibility(View.VISIBLE);
                                } else if (numPoints > 0) { //Start has been validated, distance is invalid
                                    generateMore(map,style);
                                } else if (etDistance.getText().toString().isEmpty() || Double.parseDouble(etDistance.getText().toString())<=0){ //Generate click
                                    Toast.makeText(CreateRouteActivity.this, "Enter a valid distance", Toast.LENGTH_SHORT).show();
                                } else { //generate with valid distance
                                    distanceInMiles = Double.parseDouble(etDistance.getText().toString());
                                    checkPoint(symbol1.getGeometry(),style);
                                }
                            }
                        });
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               SaveRoute();
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
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
    }


    private void SaveRoute() {
        flSaveRoute.setVisibility(View.VISIBLE);
        LineString overlay = shortenGeoJson();
        MapboxStaticMap staticImage = MapboxStaticMap.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .styleId(StaticMapCriteria.LIGHT_STYLE)
                .cameraPoint(centerPoint) // Image's centerpoint on map
                .cameraZoom(11)
                .width(320) // Image width
                .height(320) // Image height
                .retina(true) // Retina 2x image will be returned
                .geoJson(overlay)
                .build();
        Toast.makeText(this, "Loading map", Toast.LENGTH_SHORT).show();
        final Double distance = currentRoute.distance() * 0.000621371;
        final String imageUrl = staticImage.url().toString();
        tvDistanceText.setText(distance.toString() + " Miles");
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_uploading)
                .error(R.drawable.ic_upload_failed)
                .into(ivMap);

        JsonObject jsonObject = JsonParser.parseString(routeGeoJson.toJson()).getAsJsonObject();

        JsonArray coordinatesJsonArray = jsonObject.getAsJsonArray("coordinates");
        final ArrayList<ArrayList<Double>> coordinates = new ArrayList<>();

        for (JsonElement i: coordinatesJsonArray) {
            JsonArray currentCoord = i.getAsJsonArray();
            ArrayList<Double> toInsert = new ArrayList<>();
            toInsert.add(currentCoord.get(0).getAsDouble());
            toInsert.add(currentCoord.get(1).getAsDouble());
            coordinates.add(toInsert);
        }

        btnSaveFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save route here

                final Route route = new Route();
                route.setDistance(distance);
                route.setName(etRouteName.getText().toString());
                if (etRouteName.getText().toString().isEmpty()) {
                    route.setName(getString(R.string.unnamed_route));
                }
                route.setImageUrl(imageUrl);
                route.setLinestring(coordinates);
                route.setUser(ParseUser.getCurrentUser());
                route.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseUser.getCurrentUser().add("Routes",route);
                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(CreateRouteActivity.this, R.string.route_saved, Toast.LENGTH_LONG).show();
                                        flSaveRoute.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(CreateRouteActivity.this, R.string.route_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(CreateRouteActivity.this, R.string.route_failed, Toast.LENGTH_SHORT).show();
                            Log.e(TAG,e.toString());
                        }
                    }
                });
            }
        });
    }

    /* MapBox geojsons in static maps have a limit of 100 coordinates, this method continues to
       cut the geojson saved under routeGeoJson in half by removing every other coordinate,
       until its size is under 100. */
    private LineString shortenGeoJson() {
        JsonObject jsonObject = JsonParser.parseString(routeGeoJson.toJson()).getAsJsonObject();
        final JsonArray coordinatesJsonArray = jsonObject.getAsJsonArray("coordinates");
        Log.i(TAG,"size is " + coordinatesJsonArray.size());
        while (coordinatesJsonArray.size() >= 100) {
            for (int i = 0;i<coordinatesJsonArray.size();i++) {
                coordinatesJsonArray.remove(i);
            }
        }
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0;i < coordinatesJsonArray.size();i++) {
            JsonArray currentSpot = coordinatesJsonArray.get(i).getAsJsonArray();
            Point newPoint = Point.fromLngLat(currentSpot.get(0).getAsDouble(),currentSpot.get(1).getAsDouble());
            points.add(newPoint);
        }
        LineString lineString = LineString.fromLngLats(points);
        return lineString;
    }

    private void getRoute(final MapboxMap mapboxMap, Point origin) {
        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(origin)
                .destination(origin)
                .alternatives(true)
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
                for (DirectionsRoute route : response.body().routes()) {
                    double currentDistance = route.distance()*0.000621371;
                    if (Math.abs(currentDistance - distanceInMiles) < (currentRoute.distance() -distanceInMiles)) {
                        currentRoute = route;
                    }
                }
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
                Toast.makeText(CreateRouteActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMore(MapboxMap map,Style loadedMapStyle) {
        if (numPoints == targetNumPoints) {
            getRoute(map,symbol1.getGeometry());
            btnStart.setText(R.string.reset);
            return;
        }
        Symbol newSymbol = randFromCenter();
        symbols.add(newSymbol);
        checkPoint(newSymbol.getGeometry(),loadedMapStyle);
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
                        radius = distanceInMiles/(2 * Math.PI);
                        radius = radius/69;
                        generateCenter(symbol1.getGeometry());
                        generateRectangle(symbol1.getGeometry());
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
                    } else { //failed to choose a valid random point, repick center and restart process
                        symbols.clear();
                        numPoints = 1;
                        generateCenter(symbol1.getGeometry());
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
    private Symbol dropHiddenPin(LatLng targetLatLng) {
        Log.i(TAG,"symbol placing");
        Symbol symbol = symbolManager.create(new
                SymbolOptions()
                .withLatLng(targetLatLng));
        return symbol;
    }

    private void generateCenter(Point origin) {
        double degrees = Math.random() * 360;
        angle = (degrees) % 360;
        LatLng newPoint = new LatLng(origin.latitude() + radius * Math.sin(Math.toRadians(degrees)),
                origin.longitude() + radius * Math.cos(Math.toRadians(degrees)));
        center = newPoint;
        centerPoint = dropHiddenPin(center).getGeometry();
    }

    public void generateRectangle(Point origin) {
        width =(Math.random() * (distanceInMiles/2));
        length = (distanceInMiles - (width*2))/2;
        width = width/69;
        length = length/69;
        double degrees = Math.random() * 360;
        LatLng latlng1 = new LatLng(origin.latitude() + width * Math.sin(Math.toRadians(degrees)),
                origin.longitude() + width * Math.cos(Math.toRadians(degrees)));
        LatLng latlng2 = new LatLng(latlng1.getLatitude() + length * Math.sin(Math.toRadians(degrees+90)),
                latlng1.getLongitude() + length * Math.cos(Math.toRadians(degrees+90)));
        LatLng latlng3 = new LatLng(latlng2.getLatitude() - width * Math.sin(Math.toRadians(degrees+180)),
                latlng2.getLongitude() - width * Math.cos(Math.toRadians(degrees+180)));
        dropHiddenPin(latlng1);
        dropHiddenPin(latlng2);
        dropHiddenPin(latlng3);
    }

    private Symbol randFromCenter() {
        double degrees = (angle + (360/(targetNumPoints))) % 360;
        angle = degrees;
        LatLng newPoint = new LatLng(center.getLatitude() - radius * Math.sin(Math.toRadians(degrees)),
                center.getLongitude() - radius* Math.cos(Math.toRadians(degrees)));
        return dropHiddenPin(newPoint);

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