package edu.neu.madcourse.vikaschandrashekar.finalproject.model;

import java.util.List;

/**
 * Created by cvikas on 4/9/2017.
 */

public class User {
    private String userName;
    private Pin lastPin;
    private List<Pin> allPins;
    private String firebaseToken;
    private List<String> followers; // tokens
    private List<String> following;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Pin getLastPin() {
        return lastPin;
    }

    public void setLastPin(Pin lastPin) {
        this.lastPin = lastPin;
    }

    public List<Pin> getAllPins() {
        return allPins;
    }

    public void setAllPins(List<Pin> allPins) {
        this.allPins = allPins;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }
}
