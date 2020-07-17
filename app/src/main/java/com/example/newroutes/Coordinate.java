package com.example.newroutes;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Coordinate")
public class Coordinate extends ParseObject {

    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";

    public Double getLatitude() {
        return getDouble(KEY_LATITUDE);
    }
    public void setLatitude(Double latitude) {
        put(KEY_LATITUDE,latitude);
    }
    public Double getLongitude() {
        return getDouble(KEY_LONGITUDE);
    }
    public void setLongitude(Double longitude) {
        put(KEY_LONGITUDE,longitude);
    }

}
