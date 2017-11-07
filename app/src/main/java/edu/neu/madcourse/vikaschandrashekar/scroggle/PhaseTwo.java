package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.dictionary.Expander;

/**
 * Created by cvika on 2/17/2017.
 */

public class PhaseTwo extends Activity {

    PhaseTwoFragment mGameFragment;
    TextView timer;
    long millisInFuture = 0;
    long countDownInterval = 0;
    long millisRemaining = 0;
    CountDownTimer countDownTimer = null;
    boolean isPaused = true;
    boolean mute = false;
    long timeRemaining;

    private List<String> foundWords = new ArrayList<String>();
    private List<String> wrongWords = new ArrayList<String>();
    int score = 0;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.phase_two);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
        mGameFragment = (PhaseTwoFragment) getFragmentManager()
                .findFragmentById(R.id.phase_two_fragment);
        timer = (TextView) findViewById(R.id.phase_two_timer);
        this.millisInFuture = 90000;
        this.countDownInterval = 1000;
        this.millisRemaining = this.millisInFuture;
        start();
        showHideListener(R.id.phase_two_pause);
        score = getIntent().getIntExtra("SCORE", 0);
        TextView s = (TextView) findViewById(R.id.final_score);
        s.setText("Score : "+score);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeRemaining = pause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.vexento_lonely_star);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.start();
        start();
    }

    public void finalMuteMusic(View view) {
        Button b = (Button) findViewById(R.id.final_mute);
        if (mute) {
            b.setText("Mute");
            mute = false;
            mMediaPlayer.setVolume(0.5f, 0.5f);
        } else {
            mute = true;
            mMediaPlayer.setVolume(0.0f, 0.0f);
            b.setText("Unmute");
        }
    }

    public void endGame(View view){
        finish();
        Intent intent = new Intent(getApplicationContext(), FinalScore.class);
        intent.putExtra("FINAL_SCORE", score);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public void addWords(View view) {
        String word = mGameFragment.getWord();
        String showWords = "";
        mGameFragment.setWord("");
        String output = "";
        if (word != null && !"".equals(word)) {
            try {
                output = Expander.expand(this, word.charAt(0));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (output.length() > 0) {
            TextView words = (TextView) findViewById(R.id.foundList);
            TextView finalScore = (TextView) findViewById(R.id.final_score);
            List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
            if (outputList.contains(word) && !(foundWords.contains(word))){
                foundWords.add(word);
                MediaPlayer mMediaPlayer1 = MediaPlayer.create(this, R.raw.beep);
                mMediaPlayer1.setVolume(0.5f, 0.5f);
                mMediaPlayer1.setLooping(false);
                mMediaPlayer1.start();
                for(int c =0; c<word.length(); c++){
                    score += getAlphabetValue(word.charAt(c));
                }
                for (int i = 0; i < foundWords.size(); i++) {
                    showWords += foundWords.get(i) + ", ";
                }
                words.setText(showWords);
                words.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
                words.setTextColor(Color.BLACK);
                finalScore.setText("Score : "+score);
            }
            else {
                wrongWords.add(word);
                score -= 30;
                finalScore.setText("Score : "+score);
            }
        }

    }

    private int getAlphabetValue(char x) {
        if (x == 'e' || x == 'a' || x == 'i' || x == 'o' || x == 'n' ||
                x == 'r' || x == 't' || x == 'l' || x == 's' || x == 'u')
            return 5;
        else if (x == 'd' || x == 'g')
            return 10;
        else if (x == 'b' || x == 'c' || x == 'm' || x == 'p')
            return 15;
        else if (x == 'f' || x == 'h' || x == 'v' || x == 'w' || x == 'y')
            return 20;
        else if (x == 'k')
            return 25;
        else if (x == 'j' || x == 'x')
            return 45;
        else if (x == 'q' || x == 'z')
            return 50;
        return 0;
    }

    public void showHideListener(int buttonId) {
        final Button pause = (Button) findViewById(buttonId);
        final Button add = (Button) findViewById(R.id.addWords);
        final Button mute = (Button) findViewById(R.id.final_mute);
        final Button submit = (Button) findViewById(R.id.final_submit);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                if (mGameFragment.isHidden()) {
                    add.setVisibility(View.VISIBLE);
                    mute.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    ft.show(mGameFragment);
                    start();
                    pause.setText("Pause");
                } else {
                    add.setVisibility(View.INVISIBLE);
                    mute.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.INVISIBLE);
                    ft.hide(mGameFragment);
                    timeRemaining = pause();
                    pause.setText("Resume");
                }
                ft.commit();
            }
        });
    }


    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(millisRemaining, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                PhaseTwo.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                PhaseTwo.this.onFinish();

            }
        };
    }

    public void onTick(long millisUntilFinished) {
        timer.setText("Game ends in: " + millisUntilFinished / 1000);
        timer.setTextSize(getResources().getDimension(R.dimen.timer_size));
        if (millisUntilFinished <= 11000)
            timer.setTextColor(Color.RED);
        else
            timer.setTextColor(Color.BLACK);
    }

    public void onFinish() {
        timer.setText("Time Up");
        timer.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        timer.setTextColor(Color.BLACK);
        endGame(findViewById(android.R.id.content));
        //submitWords(findViewById(android.R.id.content));
    }

    public synchronized final void start() {
        if (isPaused) {
            createCountDownTimer();
            countDownTimer.start();
            isPaused = false;
        }
    }

    public long pause() throws IllegalStateException {
        if (isPaused == false) {
            countDownTimer.cancel();
        } else {
            throw new IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.");
        }
        isPaused = true;
        return millisRemaining;
    }
}
