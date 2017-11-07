package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;


/**
 * Created by cvikas on 2/16/2017.
 */

public class CountDownTimerPausable {
    long millisInFuture = 0;
    long countDownInterval = 0;
    long millisRemaining = 0;

    CountDownTimer countDownTimer = null;

    boolean isPaused = true;
    TextView timer;

    public CountDownTimerPausable(long millisInFuture, long countDownInterval) {
        super();
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.millisRemaining = this.millisInFuture;
    }

    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(millisRemaining, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                CountDownTimerPausable.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                CountDownTimerPausable.this.onFinish();

            }
        };
    }

    public void onTick(long millisUntilFinished) {
        timer.setText("Time Remaining: " + millisUntilFinished / 1000);
        if (millisUntilFinished <= 11000)
            timer.setTextColor(Color.RED);
        else
            timer.setTextColor(Color.BLACK);
    }

    public void onFinish() {
        timer.setText("done!");
    }

    public final void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        this.millisRemaining = 0;
    }

    /**
     * Start or Resume the countdown.
     *
     * @return CountDownTimerPausable current instance
     */
    public synchronized final CountDownTimerPausable start() {
        if (isPaused) {
            createCountDownTimer();
            countDownTimer.start();
            isPaused = false;
        }
        return this;
    }

    /**
     * Pauses the CountDownTimerPausable, so it could be resumed(start)
     * later from the same point where it was paused.
     */
    public long pause() throws IllegalStateException {
        if (isPaused == false) {
            countDownTimer.cancel();
        } else {
            throw new IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.");
        }
        isPaused = true;
        return millisRemaining;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setTimer(TextView t) {
        this.timer = t;
    }
}