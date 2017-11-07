package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.dictionary.Expander;


/**
 * Created by cvika on 2/14/2017.
 */

public class MainGameActivity extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";

    ScroggleGameFragment mGameFragment;
    private boolean submitted = false;

    long millisInFuture = 0;
    long countDownInterval = 0;
    long millisRemaining = 0;
    CountDownTimer countDownTimer = null;
    boolean isPaused = true;
    long timeRemaining;
    TextView timer;
    MediaPlayer mMediaPlayer;
    boolean mute = false;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.scroggle_activity_game);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
        mGameFragment = (ScroggleGameFragment) getFragmentManager()
                .findFragmentById(R.id.scroggle_fragment_game);
        timer = (TextView) findViewById(R.id.timer);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            if (!submitted) {
                String gameData = getPreferences(MODE_PRIVATE)
                        .getString(PREF_RESTORE, null);
                if (gameData != null) {
                    timeRemaining = mGameFragment.putState(gameData);
                }
                this.millisInFuture = timeRemaining;
                this.countDownInterval = 1000;
                this.millisRemaining = this.millisInFuture;
                start();
            }
        } else {
            this.millisInFuture = 90000;
            this.countDownInterval = 1000;
            this.millisRemaining = this.millisInFuture;
            start();
        }
        showHideListener(R.id.pause);
    }

    public void getNextTile(View view) {
        if (!submitted) {
            String s = mGameFragment.getNextTile();
            String output = "";
            if (s != null) {
                try {
                    output = Expander.expand(this, s.charAt(0));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output.length() > 0) {
                List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
                if (outputList.contains(s)) {
                    mGameFragment.updatePrevResults(true);
                } else
                    mGameFragment.updatePrevResults(false);
            }
        }
    }

    public void getPrevTile(View view) {
        if (!submitted) {
            String s = mGameFragment.getPrevTile();
            String output = "";
            if (s != null) {
                try {
                    output = Expander.expand(this, s.charAt(0));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output.length() > 0) {
                List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
                if (outputList.contains(s)) {
                    mGameFragment.updateNextResults(true);
                } else
                    mGameFragment.updateNextResults(false);
            }
        }
    }

    public void submitWords(View view) {
        submitted = true;
        Button next = (Button) findViewById(R.id.next);
        Button prev = (Button) findViewById(R.id.prev);
        Button submit = (Button) findViewById(R.id.submitWords);
        Button pause = (Button) findViewById(R.id.pause);
        Button mute = (Button) findViewById(R.id.mute);
        Button quit = (Button) findViewById(R.id.quit);
        next.setVisibility(View.GONE);
        prev.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        mute.setVisibility(View.GONE);
        quit.setVisibility(View.GONE);

        countDownTimer.cancel();
        timer.setText("Select one letter from each green square");
        timer.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        timer.setTextColor(Color.BLACK);

        String s = mGameFragment.getLastWord();
        String output = "";
        if (s != null) {
            try {
                output = Expander.expand(this, s.charAt(0));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (output.length() > 0) {
            List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
            if (outputList.contains(s)) {
                mGameFragment.updateCurResults(true);
            } else
                mGameFragment.updateCurResults(false);
        }

        List<String> correctWords = mGameFragment.submitWords();
        String showWords = "";
        for (int i = 0; i < correctWords.size(); i++) {
            showWords += correctWords.get(i) + ", ";
        }

        if (showWords.length() > 0) {
            MediaPlayer mMediaPlayer1 = MediaPlayer.create(this, R.raw.beep);
            mMediaPlayer1.setVolume(0.5f, 0.5f);
            mMediaPlayer1.setLooping(false);
            mMediaPlayer1.start();
        }

        TextView words = (TextView) findViewById(R.id.correctWords);
        TextView show = (TextView) findViewById(R.id.found);
        show.setText("Words Found");
        show.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        show.setTextColor(Color.BLACK);
        words.setText(showWords);
        words.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        words.setTextColor(Color.BLACK);

        calculateScore(correctWords);
        if (correctWords.size() >= 3)
            mGameFragment.initiatePhaseTwo(score);
        else {
            timer.setText("You need to find 3 or more words to progress to phase 2");
            timer.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
            timer.setTextColor(Color.BLACK);
        }
    }

    private void calculateScore(List<String> correctWords) {
        TextView s = (TextView) findViewById(R.id.score);
        if (correctWords.size() > 0) {
            for (int i = 0; i < correctWords.size(); i++) {
                String text = correctWords.get(i);
                if (text.length() == 9)
                    score += 30;
                for (int j = 0; j < text.length(); j++) {
                    score += getAlphabetValue(text.charAt(j));
                }
            }
            s.setText("Score : " + score);
        } else {
            s.setText("Score : 0");
        }
    }

    private int getAlphabetValue(char x) {
        if (x == 'e' || x == 'a' || x == 'i' || x == 'o' || x == 'n' ||
                x == 'r' || x == 't' || x == 'l' || x == 's' || x == 'u')
            return 1;
        else if (x == 'd' || x == 'g')
            return 2;
        else if (x == 'b' || x == 'c' || x == 'm' || x == 'p')
            return 3;
        else if (x == 'f' || x == 'h' || x == 'v' || x == 'w' || x == 'y')
            return 4;
        else if (x == 'k')
            return 5;
        else if (x == 'j' || x == 'x')
            return 8;
        else if (x == 'q' || x == 'z')
            return 10;
        return 0;
    }

    public void muteMusic(View view) {
        Button b = (Button) findViewById(R.id.mute);
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

    public void quit(View view) {
        finish();
    }

    public void showHideListener(int buttonId) {
        final Button pause = (Button) findViewById(buttonId);
        final Button next = (Button) findViewById(R.id.next);
        final Button prev = (Button) findViewById(R.id.prev);
        final Button submit = (Button) findViewById(R.id.submitWords);
        final Button mute = (Button) findViewById(R.id.mute);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                if (mGameFragment.isHidden()) {
                    next.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    mute.setVisibility(View.VISIBLE);
                    ft.show(mGameFragment);
                    start();
                    pause.setText("Pause");
                } else {
                    next.setVisibility(View.INVISIBLE);
                    prev.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.INVISIBLE);
                    mute.setVisibility(View.INVISIBLE);
                    ft.hide(mGameFragment);
                    timeRemaining = pause();
                    pause.setText("Resume");
                }
                ft.commit();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeRemaining = pause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        String gameData = mGameFragment.getState();
        gameData = gameData + String.valueOf(timeRemaining) + ",";
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.vexento_lonely_star);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.start();
        if (!submitted)
            start();
    }

    ///// Timer code starts
    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(millisRemaining, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                MainGameActivity.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                MainGameActivity.this.onFinish();

            }
        };
    }

    public void onTick(long millisUntilFinished) {
        timer.setText("Phase 2 Starts in: " + millisUntilFinished / 1000);
        timer.setTextSize(getResources().getDimension(R.dimen.timer_size));
        if (millisUntilFinished <= 11000)
            timer.setTextColor(Color.RED);
        else
            timer.setTextColor(Color.BLACK);
    }

    public void onFinish() {
        timer.setText("Select one letter from each green cell");
        timer.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        timer.setTextColor(Color.BLACK);
        submitWords(findViewById(android.R.id.content));
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

    ////// Timer code ends


}
