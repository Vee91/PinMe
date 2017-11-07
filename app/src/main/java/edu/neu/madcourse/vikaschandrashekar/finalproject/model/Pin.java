package edu.neu.madcourse.vikaschandrashekar.finalproject.model;

import java.util.List;

/**
 * Created by cvikas on 4/7/2017.
 */

public class Pin {

    private String name; // name given by user
    private CoOrdinates avg; // co ordinates of this pin
    private List<CoOrdinates> allCoOrdinates; // all the readings
    private float detectionRadius;
    private int time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoOrdinates getAvg() {
        return avg;
    }

    public void setAvg(CoOrdinates avg) {
        this.avg = avg;
    }

    public List<CoOrdinates> getAllCoOrdinates() {
        return allCoOrdinates;
    }

    public void setAllCoOrdinates(List<CoOrdinates> allCoOrdinates) {
        this.allCoOrdinates = allCoOrdinates;
    }

    public float getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(float detectionRadius) {
        this.detectionRadius = detectionRadius;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}