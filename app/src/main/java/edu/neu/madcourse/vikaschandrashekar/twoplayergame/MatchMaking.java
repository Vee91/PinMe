package edu.neu.madcourse.vikaschandrashekar.twoplayergame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.multiplayer.PlayerModel;
import edu.neu.madcourse.vikaschandrashekar.scroggle.MainGameActivity;

/**
 * Created by cvika on 3/17/2017.
 */

public class MatchMaking extends Activity {

    private DatabaseReference mDatabase;
    private static final String SERVER_KEY = "key=AAAAebHHBsI:APA91bEofuzHb8FWUZEKUT0_QdKG_Vi7mlzDqgKtBB962Qv6FImKT7yYWVDGUdaZujN5oNOI-R5hO_q1uLMaFEWj5LMcpzYF5euzPncxyOT8taRqGSrrgkSUn6z-b_Q0srYmATr2VW17";
    private boolean playerFound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener player1Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                PlayerModel p = dataSnapshot.getValue(PlayerModel.class);
                if(p.getName().equals("Player1")){
                    addPlayer2(this);
                }
                else {
                    addPlayer1(this);
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mDatabase.child("players").child("player1").addValueEventListener(player1Listener);
        setContentView(R.layout.waiting);
    }

    private void addPlayer1(final ValueEventListener valueEventListener){
        final String token = FirebaseInstanceId.getInstance().getToken();
        mDatabase.child("players").child("player1")
                .runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PlayerModel u = mutableData.getValue(PlayerModel.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                mDatabase.child("players").child("player1").removeEventListener(valueEventListener);
                u.setName("Player1");
                u.setPlayerToken(token);
                u.setIsPlaying("false");
                u.setPlayMessage("null");
                u.setScore("null");
                u.setStatusMessage("null");
                u.setTurn("true");
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
        ValueEventListener player2Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayerModel p = dataSnapshot.getValue(PlayerModel.class);
                if(p.getName().equals("Player2")){
                    startP1Game(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("players").child("player2").addValueEventListener(player2Listener);
    }

    private void addPlayer2(final ValueEventListener valueEventListener){
        final String token = FirebaseInstanceId.getInstance().getToken();
        mDatabase.child("players").child("player2")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        mDatabase.child("players").child("player1").removeEventListener(valueEventListener);
                        u.setName("Player2");
                        u.setPlayerToken(token);
                        u.setIsPlaying("false");
                        u.setPlayMessage("null");
                        u.setScore("null");
                        u.setStatusMessage("null");
                        u.setTurn("false");
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
        finish();
        playerFound = true;
        Intent intent = new Intent(this, TwoPlayerMainGameP2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    private void startP1Game(ValueEventListener player2Listener){
        finish();
        mDatabase.child("players").child("player2").removeEventListener(player2Listener);
        Intent intent = new Intent(this, TwoPlayerMainGameP1.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

}


