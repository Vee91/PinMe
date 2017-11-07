package edu.neu.madcourse.vikaschandrashekar.twoplayergame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 4/5/2017.
 */

public class TwoPlayerAck extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.two_ack);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText("Word Game Acknowledgement");
        }
    }
}
