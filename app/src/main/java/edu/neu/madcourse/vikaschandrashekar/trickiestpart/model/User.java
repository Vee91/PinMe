package edu.neu.madcourse.vikaschandrashekar.trickiestpart.model;

import java.util.List;

/**
 * Created by cvika on 4/9/2017.
 */

public class User {
    private String userName;
    private Pin lastPin;
    private List<Pin> allPins;
    private String firebaseToken;
    //private List<String> followers;
    //private List<String> following;

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
}
