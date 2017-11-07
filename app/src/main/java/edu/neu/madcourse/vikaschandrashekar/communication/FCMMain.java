package edu.neu.madcourse.vikaschandrashekar.communication;

/**
 * Created by cvika on 3/3/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.communication.fcm.FCMActivity;
import edu.neu.madcourse.vikaschandrashekar.communication.database.RealtimeDatabaseActivity;

public class FCMMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_main);
    }

    public void openFCMActivity(View view) {
        startActivity(new Intent(FCMMain.this, FCMActivity.class));
    }

    public void openDBActivity(View view) {
        startActivity(new Intent(FCMMain.this, RealtimeDatabaseActivity.class));
    }
}
