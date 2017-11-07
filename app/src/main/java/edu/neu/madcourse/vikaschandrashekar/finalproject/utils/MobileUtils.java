package edu.neu.madcourse.vikaschandrashekar.finalproject.utils;


import edu.neu.madcourse.vikaschandrashekar.finalproject.model.CoOrdinates;

/**
 * Created by cvikas on 4/9/2017.
 */

public class MobileUtils {

    private static int earthRadius = 6371;

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    public static double distanceInKm(CoOrdinates c1, CoOrdinates c2) {
        double dLat = degreeToRadian(c2.getLat() - c1.getLat());
        double dLon = degreeToRadian(c2.getLon() - c1.getLon());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) *
                        Math.cos(degreeToRadian(c1.getLat())) * Math.cos(degreeToRadian(c2.getLat()));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }
}
