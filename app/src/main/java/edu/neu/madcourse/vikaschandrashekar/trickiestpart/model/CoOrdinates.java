package edu.neu.madcourse.vikaschandrashekar.trickiestpart.model;

/**
 * Created by cvikas on 4/9/2017.
 */

public class CoOrdinates {
    private double lat;
    private double lon;

    public CoOrdinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public CoOrdinates() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
