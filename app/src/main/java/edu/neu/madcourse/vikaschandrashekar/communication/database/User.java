package edu.neu.madcourse.vikaschandrashekar.communication.database;

/**
 * Created by cvika on 3/3/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by aniru on 2/18/2017.
 */
@IgnoreExtraProperties
public class User {

    public String username;
    public String score;


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String score){
        this.username = username;
        this.score = score;
    }

}
