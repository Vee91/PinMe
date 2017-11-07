package edu.neu.madcourse.vikaschandrashekar.twoplayergame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 4/5/2017.
 */

public class TwoPlayerMain extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.two_player_main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
    }

    public void openNewTwoPlayerGame(View view) {
        startActivity(new Intent(this, MatchMaking.class));
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }


    public void getTwoAck(View view){
        Intent intent = new Intent(this, TwoPlayerAck.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void quitTwoGame(View view) {
        finish();
    }
}
