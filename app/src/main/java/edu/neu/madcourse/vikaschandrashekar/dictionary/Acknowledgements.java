package edu.neu.madcourse.vikaschandrashekar.dictionary;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvikas on 2/3/2017.
 */

public class Acknowledgements extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.dictionary_acknowledgement);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.dictionary_ack);
        }
    }
}