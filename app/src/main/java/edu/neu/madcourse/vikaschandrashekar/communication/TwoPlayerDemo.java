package edu.neu.madcourse.vikaschandrashekar.communication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 3/6/2017.
 */

public class TwoPlayerDemo extends Activity {

    private TextView play1message;
    private TextView play2message;
    private TextView play1status;
    private TextView play2status;
    private TextView play1score;
    private TextView play2score;
    private TextView play1turn;
    private TextView play2turn;
    private DatabaseReference mDatabase;
    private RadioGroup toggle;
    private RadioButton player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_demo);

        play1message = (TextView) findViewById(R.id.play1message);
        play2message = (TextView) findViewById(R.id.play2message);
        play1status = (TextView) findViewById(R.id.play1status);
        play2status = (TextView) findViewById(R.id.play2status);
        play1score = (TextView) findViewById(R.id.play1score);
        play2score = (TextView) findViewById(R.id.play2score);
        play1turn = (TextView) findViewById(R.id.play1turn);
        play2turn = (TextView) findViewById(R.id.play2turn);
        toggle = (RadioGroup) findViewById(R.id.toggle_player);
        toggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //2131558542
                makeMove(checkedId);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void makeMove(int checkId){
        if(checkId == 2131558542){
            play1message.setText("Its your turn");
            play1status.setText("Player2 found a word in cell x");
            play1turn.setText("Your Turn?: Yes");
            play2message.setText("Player1 is making move");
            play2status.setText(null);
            play2turn.setText("Your Turn?: No");
        }
        else
        {
            play2message.setText("Its your turn");
            play2status.setText("Player1 found a word in cell x");
            play2turn.setText("Your Turn?: Yes");
            play1message.setText("Player2 is making move");
            play1status.setText(null);
            play1turn.setText("Your Turn?: No");
        }
    }
}
