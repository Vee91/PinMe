package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvikas on 2/14/2017.
 */

public class ScroggleMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.scroggle_main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
    }

    public void openNewGame(View view) {
        Intent intent = new Intent(this, MainGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void continueGame(View view) {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra(MainGameActivity.KEY_RESTORE, true);
        startActivity(intent);
    }

    public void openInstructions(View view) {
        Intent intent = new Intent(this, Instructions.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void getScroggleAck(View view){
        Intent intent = new Intent(this, ScroggleAcknowledgement.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void quitGame(View view) {
        finish();
    }

}
