package edu.neu.madcourse.vikaschandrashekar.assignment1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.vikaschandrashekar.communication.FCMMain;
import edu.neu.madcourse.vikaschandrashekar.dictionary.DictionaryMain;
import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.finalproject.activities.Description;
import edu.neu.madcourse.vikaschandrashekar.finalproject.activities.ProjectMain;
import edu.neu.madcourse.vikaschandrashekar.finalproject.activities.Registration;
import edu.neu.madcourse.vikaschandrashekar.scroggle.ScroggleMain;
import edu.neu.madcourse.vikaschandrashekar.tictactoe.TicTacToeMainActivity;
import edu.neu.madcourse.vikaschandrashekar.trickiestpart.activities.Trickiest;
import edu.neu.madcourse.vikaschandrashekar.twoplayergame.TwoPlayerMain;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);

        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
        }

        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(exitListener);

    }

    public void openTicTacToe(View view) {
        Intent intent = new Intent(this, TicTacToeMainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void showAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void openDictionary(View view) {
        Intent intent = new Intent(this, DictionaryMain.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void openScroggle(View view) {
        Intent intent = new Intent(this, ScroggleMain.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void openCommunication(View view) {
        startActivity(new Intent(this, FCMMain.class));
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void starTwoPlayerGame(View view) {
        Intent intent = new Intent(this, TwoPlayerMain.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void trickiestPart(View view) {
        Intent intent = new Intent(this, Trickiest.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void getFinalProj(View view){
        Intent intent = new Intent(this, Description.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    private View.OnClickListener exitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
