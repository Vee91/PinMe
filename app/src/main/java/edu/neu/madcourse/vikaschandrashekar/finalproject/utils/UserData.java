package edu.neu.madcourse.vikaschandrashekar.finalproject.utils;

import android.app.Application;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.vikaschandrashekar.finalproject.model.User;

/**
 * Created by cvikas on 4/19/2017.
 */

public class UserData extends Application {
    private User thisUser = new User();

    private final String token = "eYAXMaap0uc:APA91bFq-xBwcG6kvKs7kcHqW3jSglMLJGOK0nPDdS9Z21KcWgBHC0p9Ur63hGymCplioKtkhQsMul3JO5M_O70bzqY_Of80dfewyyaS1LKOwdw6Aki5UJJCV359Usq19FigCkN_IxTl";

    public User getThisUser() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        getUserData(database);
        return thisUser;
    }

    private void getUserData(final DatabaseReference database) {
        ValueEventListener userdataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if(temp != null)
                    setThisUser(temp, this, database);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("Project").child("Users")
                .child(token).addValueEventListener(userdataListener);
    }

    private void setThisUser(User db, ValueEventListener valueEventListener, DatabaseReference database){
        thisUser.setUserName(db.getUserName());
        thisUser.setAllPins(db.getAllPins());
        thisUser.setFirebaseToken(db.getFirebaseToken());
        thisUser.setFollowers(db.getFollowers());
        thisUser.setFollowing(db.getFollowing());
        thisUser.setLastPin(db.getLastPin());
        database.child("Project").child("Users")
                .child(token).removeEventListener(valueEventListener);
    }
}
