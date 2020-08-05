package com.example.newroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.newroutes.Fragments.CustomTutorialFragment;
import com.example.newroutes.Fragments.RandomTutorialFragment;
import com.example.newroutes.ParseObjects.Route;
import com.example.newroutes.databinding.ActivityCustomRouteBinding;
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
import com.mapbox.geojson.Feature;
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

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomRouteActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";


    public static final String TAG = "CustomRouteActivity";
    private static final String ID_ICON = "placeholder";
    private Context context;
    private MapView mapView;
    private MapboxMap map;
    private Button btnStart;
    private Button btnSave;
    private EditText etDescription;
    private Button btnSaveRoute;
    private EditText etRouteName;
    private FrameLayout flInstructions;
    private TextView tvDistanceText;
    private ImageView ivMap;
    private Button btnSaveFinal;
    private Double totalDistance = 0.0;
    private Symbol firstPoint;
    private ImageView hoveringMarker;
    private SymbolManager symbolManager;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private GeoJson routeGeoJson;
    private GeoJson buildingGeoJson;
    private FrameLayout flSaveRoute;
    private Symbol lastLocation;
    private LatLng lastGenerated;
    private boolean isActive = false;
    private ArrayList<Symbol> symbols = new ArrayList<>();
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    ActivityCustomRouteBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        binding = ActivityCustomRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;
        btnStart = binding.btnStart;
        btnSave = binding.btnSave;
        etDescription = binding.etDescription;
        btnSaveRoute = binding.btnSaveRoute;
        flSaveRoute = binding.flSaveRoute;
        etRouteName = binding.etRouteName;
        ivMap = binding.ivMap;
        tvDistanceText = binding.tvDistanceText;
        btnSaveFinal = binding.btnSaveFinal;
        mapView = binding.mapView;
        flInstructions = binding.flInstructions;
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

                        btnStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isActive) {
                                    resetRoute(style);
                                    return;
                                }
                                isActive = true;
                                LatLng targetLatLng = map.getCameraPosition().target;
                                lastLocation = dropPin(targetLatLng);
                                firstPoint = lastLocation;
                                btnStart.setText(R.string.reset);
                                btnSave.setVisibility(View.VISIBLE);
                                btnSave.setClickable(true);
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            periodicUpdates();
                                        } catch (InterruptedException e) {
                                            Log.i(TAG,"threading error");
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                            }
                        });
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnSaveRoute.setVisibility(View.VISIBLE);
                                btnSaveRoute.setClickable(true);
                                LatLng targetLatLng = map.getCameraPosition().target;
                                lastLocation = dropPin(targetLatLng);
                                totalDistance += currentRoute.distance() * 0.000621371;
                                Toast.makeText(context, "Current distance: " + totalDistance, Toast.LENGTH_SHORT).show();
                                if (buildingGeoJson == null) {
                                    buildingGeoJson = routeGeoJson;
                                } else {
                                    buildingGeoJson = combineGeoJsons(buildingGeoJson,routeGeoJson);
                                }
                            }
                        });

                        btnSaveRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveRoute();
                            }
                        });

                        Fragment fragment = new CustomTutorialFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        fm.beginTransaction().replace(flInstructions.getId(),fragment).commit();
                    }
                });
    }

    private void resetRoute(Style style) {
        finish(); //TODO improve this to actually clear the map and restart it
    }

    private void saveRoute() {
        flSaveRoute.setVisibility(View.VISIBLE);
        LineString overlay = shortenGeoJson();
        MapboxStaticMap staticImage = MapboxStaticMap.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .styleId(StaticMapCriteria.LIGHT_STYLE)
                .cameraPoint(firstPoint.getGeometry()) // Image's centerpoint on map
                .cameraZoom(11)
                .width(320) // Image width
                .height(320) // Image height
                .retina(true) // Retina 2x image will be returned
                .geoJson(overlay)
                .build();
        Toast.makeText(this, "Loading map", Toast.LENGTH_SHORT).show();
        final String imageUrl = staticImage.url().toString();
        DecimalFormat df = new DecimalFormat("#.##");
        String shortDistance = df.format(totalDistance);
        tvDistanceText.setText(shortDistance +" Miles");
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_uploading)
                .error(R.drawable.ic_upload_failed)
                .into(ivMap);

        JsonObject jsonObject = JsonParser.parseString(buildingGeoJson.toJson()).getAsJsonObject();

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
                route.setDistance(totalDistance);
                route.setName(etRouteName.getText().toString());
                route.setDescription(etDescription.getText().toString());
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
                                        Toasty.success(CustomRouteActivity.this, R.string.route_saved, Toast.LENGTH_LONG).show();
                                        flSaveRoute.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(CustomRouteActivity.this, R.string.route_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toasty.error(CustomRouteActivity.this, R.string.route_failed, Toast.LENGTH_SHORT).show();
                            Log.e(TAG,e.toString());
                        }
                    }
                });
            }
        });
    }

    private LineString shortenGeoJson() {
        JsonObject jsonObject = JsonParser.parseString(buildingGeoJson.toJson()).getAsJsonObject();
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

    public void periodicUpdates() throws InterruptedException {
        while (true) {
            Thread.sleep(350);
            Handler mainHandler = new Handler(context.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    if (lastGenerated != null && lastGenerated.equals(map.getCameraPosition().target)) {
                        return;
                    }
                    Log.i(TAG,"calling new route");
                    Symbol point2 = dropHiddenPin(map.getCameraPosition().target);
                    getRoute(lastLocation.getGeometry(), point2.getGeometry());
                    lastGenerated = point2.getLatLng();
                } // This is your code
            };
            mainHandler.post(myRunnable);
        }
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

    /*
    Combines the 2 input Linestring GeoJsons by concatenating the coordinates of the 2nd to the end of the first.
     */
    private LineString combineGeoJsons(GeoJson geoJson1, GeoJson geoJson2) {
        JsonObject jsonObject1 = JsonParser.parseString(geoJson1.toJson()).getAsJsonObject();
        JsonArray coordinatesJsonArray1 = jsonObject1.getAsJsonArray("coordinates");
        JsonObject jsonObject2 = JsonParser.parseString(geoJson2.toJson()).getAsJsonObject();
        JsonArray coordinatesJsonArray2 = jsonObject2.getAsJsonArray("coordinates");
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0;i < coordinatesJsonArray1.size();i++) {
            JsonArray currentSpot = coordinatesJsonArray1.get(i).getAsJsonArray();
            Point newPoint = Point.fromLngLat(currentSpot.get(0).getAsDouble(),currentSpot.get(1).getAsDouble());
            points.add(newPoint);
        }
        for (int i = 0;i < coordinatesJsonArray2.size();i++) {
            JsonArray currentSpot = coordinatesJsonArray2.get(i).getAsJsonArray();
            Point newPoint = Point.fromLngLat(currentSpot.get(0).getAsDouble(),currentSpot.get(1).getAsDouble());
            points.add(newPoint);
        }
        LineString lineString = LineString.fromLngLats(points);
        return lineString;
    }

    private void getRoute(Point origin,Point Destination) {
        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(origin)
                .destination(Destination)
                .overview(DirectionsCriteria.OVERVIEW_SIMPLIFIED)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.mapbox_access_token));


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
                //Toast.makeText(CustomRouteActivity.this, (Double.toString(currentRoute.distance()*0.000621371)), Toast.LENGTH_SHORT).show();
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
                                if (buildingGeoJson == null) {
                                    source.setGeoJson(drawnRoute);
                                } else {
                                    source.setGeoJson(combineGeoJsons(buildingGeoJson,routeGeoJson));
                                }
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