package edu.neu.madcourse.vikaschandrashekar.twoplayergame;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.dictionary.Expander;
import edu.neu.madcourse.vikaschandrashekar.multiplayer.PlayerModel;

/**
 * Created by cvika on 3/19/2017.
 */

public class TwoPlayerMainGameP1 extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";

    TwoPlayerGameFragmentP1 mGameFragment;
    private boolean submitted = false;
    private DatabaseReference mDatabase;

    boolean isPaused = true;
    MediaPlayer mMediaPlayer;
    boolean mute = false;
    int score = 0;
    ValueEventListener player1Listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.two_player_p1);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.scroggle_title);
        }
        mGameFragment = (TwoPlayerGameFragmentP1) getFragmentManager()
                .findFragmentById(R.id.scroggle_fragment_game_2p);


        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            if (!submitted) {
                String gameData = getPreferences(MODE_PRIVATE)
                        .getString(PREF_RESTORE, null);
                if (gameData != null) {
                    mGameFragment.putState(gameData);
                }
            }
        }
      //  showHideListener(R.id.pause_2p);
        initiateGameData();
    }


    public void hideButtons(ValueEventListener valueEventListener){
        Button endTurn = (Button) findViewById(R.id.end_turn1);
        Button mute = (Button) findViewById(R.id.mute_2p_1);
        Button quit = (Button) findViewById(R.id.quit_2p_1);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        endTurn.setVisibility(View.INVISIBLE);
        mute.setVisibility(View.INVISIBLE);
        quit.setVisibility(View.INVISIBLE);
        ft.hide(mGameFragment);
        ft.commit();
    }

    public void showButtons(ValueEventListener valueEventListener){
        Button endTurn = (Button) findViewById(R.id.end_turn1);
        Button mute = (Button) findViewById(R.id.mute_2p_1);
        Button quit = (Button) findViewById(R.id.quit_2p_1);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        endTurn.setVisibility(View.VISIBLE);
        mute.setVisibility(View.VISIBLE);
        quit.setVisibility(View.VISIBLE);
        ft.show(mGameFragment);
        ft.commit();
    }



    private void updateTextViews(String playMessage, String statusMessage) {
        TextView pm = (TextView) findViewById(R.id.playMessage1);
        TextView sm = (TextView) findViewById(R.id.statusMessage1);
        if(!playMessage.equals("null"))
            pm.setText(playMessage);
        else
            pm.setText("");
        if(!statusMessage.equals("null"))
            sm.setText(statusMessage);
        else
            sm.setText("");
    }

    private void initiateGameData() {

        mDatabase.child("players").child("player1")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setTurn("true");
                        u.setPlayMessage("It's your turn");
                        u.setStatusMessage("null");
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
    }

    public void endTurn(View view){
        String s = mGameFragment.getNextTile();
        String output = "";
        mDatabase.child("players").child("player2")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setTurn("true");
                        u.setPlayMessage("It's your turn");
                        u.setStatusMessage("Player1 found a word. Find the purple cell");
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
        if(s!=null) {
            mDatabase.child("players").child("player1")
                    .runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            PlayerModel u = mutableData.getValue(PlayerModel.class);
                            if (u == null) {
                                return Transaction.success(mutableData);
                            }
                            u.setTurn("false");
                            u.setPlayMessage("Player 2 is making the move");
                            u.setStatusMessage("null");
                            mutableData.setValue(u);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                        }
                    });

        }

        if(s != null && s.length()!=0){
            try {
                output = Expander.expand(this, s.charAt(0));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(s == null){
            submitWords(view);
        }
        if (output.length() > 0) {
            List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
            if (outputList.contains(s)) {
                mGameFragment.updatePrevResults(true);
            } else
                mGameFragment.updatePrevResults(false);
        }
    }

    public void submitWords(View view) {
        submitted = true;
        Button endturn = (Button) findViewById(R.id.end_turn1);
        Button mute = (Button) findViewById(R.id.mute_2p_1);
        endturn.setVisibility(View.INVISIBLE);
        mute.setVisibility(View.INVISIBLE);

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

        TextView words = (TextView) findViewById(R.id.statusMessage1);
        TextView show = (TextView) findViewById(R.id.playMessage1);
        show.setText("Words Found");
        show.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        show.setTextColor(Color.WHITE);
        words.setText(showWords);
        words.setTextSize(getResources().getDimension(R.dimen.timer_size_submit));
        words.setTextColor(Color.WHITE);

        calculateScore(correctWords);

        final String finalShowWords = showWords;
        mDatabase.child("players").child("player1")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setPlayMessage("Words Found");
                        u.setStatusMessage(finalShowWords);
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
    }

    private void calculateScore(List<String> correctWords) {
        TextView s = (TextView) findViewById(R.id.score_2p);
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
        mDatabase.child("players").child("player1")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setScore(String.valueOf(score));
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
        ValueEventListener scoreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                PlayerModel p = dataSnapshot.getValue(PlayerModel.class);
                if(p!=null && !p.getScore().equals("null")) {
                    reportWinner(p.getScore(), score, this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mDatabase.child("players").child("player2").addValueEventListener(scoreListener);
    }

    private void reportWinner(String p2Score, int thisScore, ValueEventListener scoreListener){
        System.out.println("p2Score = " + p2Score);
        System.out.println("P1 score" + thisScore);
        TextView win = (TextView) findViewById(R.id.winner_1);
        if(thisScore > Integer.valueOf(p2Score)){
            win.setText("You win");
        }
        else {
            win.setText("You lose. Better luck next time");
        }
        mDatabase.child("players").child("player2").removeEventListener(scoreListener);
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
        Button b = (Button) findViewById(R.id.mute_2p_1);
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

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        String gameData = mGameFragment.getState();
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

        player1Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayerModel p = dataSnapshot.getValue(PlayerModel.class);
                updateTextViews(p.getPlayMessage(),p.getStatusMessage());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("players").child("player1").addValueEventListener(player1Listener);
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        if(player1Listener != null){
            mDatabase.child("players").child("player1").removeEventListener(player1Listener);
        }
        mDatabase.child("players").child("player2")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setName("null");
                        u.setIsPlaying("false");
                        u.setPlayMessage("null");
                        u.setScore("null");
                        u.setStatusMessage("null");
                        u.setTurn("true");
                        u.setNineLetterWords("null");
                        u.setPlayerToken("null");
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });

        mDatabase.child("players").child("player1")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setName("null");
                        u.setIsPlaying("false");
                        u.setPlayMessage("null");
                        u.setScore("null");
                        u.setStatusMessage("null");
                        u.setTurn("true");
                        u.setNineLetterWords("null");
                        u.setPlayerToken("null");
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
    }
}
