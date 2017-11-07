package edu.neu.madcourse.vikaschandrashekar.trickiestpart.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Created by cvikas on 4/9/2017.
 * Constructor has to be called before calling get location
 * pass the context do not save the context
 */

public class LocationService extends Service implements LocationListener {

    private Context context;
    private boolean isGpsEnabled;
    private boolean isNetworkEnabled;
    private boolean canGetLocation;
    private Location location;
    private double lat;
    private double lon;
    private LocationManager locationManager;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 60000;

    public LocationService(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        this.locationManager = getLocationManager();
        this.isGpsEnabled = isGpsEnabled();
        this.isNetworkEnabled = isNetworkEnabled();
        if (!isGpsEnabled && !isNetworkEnabled) {
            this.canGetLocation = false;
                /*no location provider enabled*/
        } else {
            this.canGetLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        this.lat = location.getLatitude();
                        this.lon = location.getLongitude();
                    }
                }
                    /*if gps is enabled then get location using gps*/
                if (isGpsEnabled && location == null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                this.lat = location.getLatitude();
                                this.lon = location.getLongitude();
                            }

                        }
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public boolean isGpsEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkEnabled(){
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public LocationManager getLocationManager(){
        return (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }
}
