package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 2/18/2017.
 */

public class FinalScore extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.final_score);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
        int score = getIntent().getIntExtra("FINAL_SCORE", 0);
        TextView t = (TextView) findViewById(R.id.final_score_page);
        t.setText("Score : "+score);
    }

    public void finalQuit(View view){
        finish();
    }
}
