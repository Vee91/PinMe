package edu.neu.madcourse.vikaschandrashekar.twoplayergame;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import edu.neu.madcourse.vikaschandrashekar.R;
import edu.neu.madcourse.vikaschandrashekar.dictionary.Expander;
import edu.neu.madcourse.vikaschandrashekar.multiplayer.PlayerModel;
import edu.neu.madcourse.vikaschandrashekar.scroggle.Scrambler;
import edu.neu.madcourse.vikaschandrashekar.scroggle.ScroggleTile;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by cvika on 3/19/2017.
 */

public class TwoPlayerGameFragmentP2 extends Fragment  implements SensorEventListener {

    private DatabaseReference mDatabase;
    static private int mLargeIds[] = {R.id.scroggle_large1, R.id.scroggle_large2, R.id.scroggle_large3,
            R.id.scroggle_large4, R.id.scroggle_large5, R.id.scroggle_large6, R.id.scroggle_large7, R.id.scroggle_large8,
            R.id.scroggle_large9,};
    static private int mSmallIds[] = {R.id.scroggle_small1, R.id.scroggle_small2, R.id.scroggle_small3,
            R.id.scroggle_small4, R.id.scroggle_small5, R.id.scroggle_small6, R.id.scroggle_small7, R.id.scroggle_small8,
            R.id.scroggle_small9,};
    static private int mSmallTextIds[] = {R.id.scroggle_textsmall1, R.id.scroggle_textsmall2, R.id.scroggle_textsmall3,
            R.id.scroggle_textsmall4, R.id.scroggle_textsmall5, R.id.scroggle_textsmall6, R.id.scroggle_textsmall7, R.id.scroggle_textsmall8,
            R.id.scroggle_textsmall9,};
    private boolean mResults[] = {false, false, false, false,
            false, false, false, false, false,};
    private List<String> nineLetterWords = new ArrayList<String>();
    static private float angles[] = {315, 0, 45, 270, 999, 90, 225, 180,  135,};
    static ValueEventListener turnListener;

    private ScroggleTile mEntireBoard = new ScroggleTile();
    private ScroggleTile mLargeTiles[] = new ScroggleTile[9];
    private ScroggleTile mSmallTiles[][] = new ScroggleTile[9][9];
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    private int starting = 0;
    private String phaseTwo[] = new String[9];
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        initGame();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Gson g = new Gson();
        View rootView =
                inflater.inflate(R.layout.scroggle_large_board, container, false);
        initViews(rootView);
        mDatabase.child("players").child("player2")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        PlayerModel u = mutableData.getValue(PlayerModel.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        u.setPlayMessage("Player 1 is making the move");
                        u.setNineLetterWords(g.toJson(nineLetterWords));
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
        updateAllTiles();
        return rootView;
    }

    public void initGame() {
        Log.d("UT3", "init game");
        mEntireBoard = new ScroggleTile();
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScroggleTile();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScroggleTile();
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);
        // If the player moves first, set which spots are available
        //setAvailableFromLastMove(starting);

        turnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                PlayerModel p = dataSnapshot.getValue(PlayerModel.class);
                if(p!=null && p.getTurn().equals("false")) {
                    clearAvailable();
                    ((TwoPlayerMainGameP2)getActivity()).hideButtons(this);
                    updateAllTiles();
                }
                else{
                    ((TwoPlayerMainGameP2)getActivity()).showButtons(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mDatabase.child("players").child("player2").addValueEventListener(turnListener);

    }

    private void initViews(View rootView) {
        String output = "";
        try {
            output = Expander.expand(getActivity().getApplicationContext(), '9');
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (output.length() > 0) {
            List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
            Random randomGenerator = new Random();
            int size = outputList.size();
            for (int t = 0; t < 9; t++) {
                int index = randomGenerator.nextInt(size);
                if (!nineLetterWords.contains(outputList.get(index))) {
                    nineLetterWords.add(outputList.get(index));
                    System.out.println(outputList.get(index));
                } else t--;
            }
            Scrambler scr = new Scrambler();
            nineLetterWords = scr.scramble(nineLetterWords);
        }
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            for (int small = 0; small < 9; small++) {
                ImageButton inner = (ImageButton) outer.findViewById
                        (mSmallIds[small]);
                TextView innerText = (TextView) outer.findViewById(mSmallTextIds[small]);
                innerText.setText(String.valueOf(nineLetterWords.get(large).charAt(small)));
                final int fLarge = large;
                final int fSmall = small;
                final ScroggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAvailable(smallTile)) {
                            mLargeTiles[fLarge].setLastClicked(fSmall);
                            List<ScroggleTile> availableList = getNextAvailable(fLarge, fSmall);
                            if (availableList.size() > 0) {
                                for (int t = 0; t < availableList.size(); t++) {
                                    if (!availableList.get(t).isClicked())
                                        addAvailable(availableList.get(t));
                                }
                            }
                            smallTile.setClicked(true);
                            smallTile.updateDrawableState();
                            addLetter(fLarge, nineLetterWords.get(fLarge).charAt(fSmall));
                        }
                    }
                });
            }
        }
    }


    private List<ScroggleTile> getNextAvailable(int large, int small) {
        List<ScroggleTile> availableList = new ArrayList();
        switch (small) {
            case 0:
                clearAvailable();
                availableList.add(mSmallTiles[large][1]);
                availableList.add(mSmallTiles[large][3]);
                availableList.add(mSmallTiles[large][4]);
                break;
            case 1:
                clearAvailable();
                availableList.add(mSmallTiles[large][0]);
                availableList.add(mSmallTiles[large][2]);
                availableList.add(mSmallTiles[large][3]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][5]);
                break;
            case 2:
                clearAvailable();
                availableList.add(mSmallTiles[large][1]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][5]);
                break;
            case 3:
                clearAvailable();
                availableList.add(mSmallTiles[large][0]);
                availableList.add(mSmallTiles[large][1]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][6]);
                availableList.add(mSmallTiles[large][7]);
                break;
            case 4:
                clearAvailable();
                availableList.add(mSmallTiles[large][0]);
                availableList.add(mSmallTiles[large][1]);
                availableList.add(mSmallTiles[large][2]);
                availableList.add(mSmallTiles[large][3]);
                availableList.add(mSmallTiles[large][5]);
                availableList.add(mSmallTiles[large][6]);
                availableList.add(mSmallTiles[large][7]);
                availableList.add(mSmallTiles[large][8]);
                break;
            case 5:
                clearAvailable();
                availableList.add(mSmallTiles[large][1]);
                availableList.add(mSmallTiles[large][2]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][7]);
                availableList.add(mSmallTiles[large][8]);
                break;
            case 6:
                clearAvailable();
                availableList.add(mSmallTiles[large][3]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][7]);
                break;
            case 7:
                clearAvailable();
                availableList.add(mSmallTiles[large][3]);
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][5]);
                availableList.add(mSmallTiles[large][6]);
                availableList.add(mSmallTiles[large][8]);
                break;
            case 8:
                clearAvailable();
                availableList.add(mSmallTiles[large][4]);
                availableList.add(mSmallTiles[large][5]);
                availableList.add(mSmallTiles[large][7]);
                break;
        }
        return availableList;
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        for (int i = 0; i < 9; i++) {
            ScroggleTile o = mLargeTiles[i];
            if (i == small)
                o.setResult(ScroggleTile.Result.SELECTED);
            else o.setResult(ScroggleTile.Result.NEITHER);
        }
        ScroggleTile o = mLargeTiles[small];
        o.setResult(ScroggleTile.Result.SELECTED);
        if (small != -1) {
            if (mLargeTiles[small].getLastClicked() != 10) {
                List<ScroggleTile> availableList = getNextAvailable(small, mLargeTiles[small].getLastClicked());
                if (availableList.size() > 0) {
                    for (int t = 0; t < availableList.size(); t++) {
                        if (!availableList.get(t).isClicked())
                            addAvailable(availableList.get(t));
                    }
                }
            } else {
                for (int j = 0; j < 9; j++) {
                    if (!mSmallTiles[small][j].isClicked())
                        addAvailable(mSmallTiles[small][j]);
                }
            }
        }
    }


    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    private void clearAvailable() {
        for (int i = 0; i < 9; i++) {
            ScroggleTile o = mLargeTiles[i];
            o.setResult(ScroggleTile.Result.NEITHER);
        }
        mAvailable.clear();
    }

    private void addAvailable(ScroggleTile tile) {
        mAvailable.add(tile);
    }

    public boolean isAvailable(ScroggleTile tile) {
        return mAvailable.contains(tile);
    }

    public void removeAvailable(ScroggleTile tile) {
        mAvailable.remove(tile);
    }

    public String getNextTile() {

        if (starting >= 0 && starting <= 7) {
            starting = starting +1;
            if(mLargeTiles[starting - 1].getWord() == null)
                return "";
            else return mLargeTiles[starting - 1].getWord();
        }
        return null;
    }


    public void addLetter(int large, char x) {
        ScroggleTile l = mLargeTiles[large];
        if (l.getWord() != null)
            l.setWord(l.getWord() + String.valueOf(x));
        else
            l.setWord(String.valueOf(x));
    }

    public void updatePrevResults(boolean b) {
        mResults[starting - 1] = b;
    }

    public void updateNextResults(boolean b) {
        mResults[starting + 1] = b;
    }

    public void updateCurResults(boolean b) {
        mResults[starting] = b;
    }

    public String getLastWord() {
        mDatabase.child("players").child("player1").removeEventListener(turnListener);
        return mLargeTiles[starting-1].getWord();
    }

    public List<String> submitWords() {
        List<String> correctWords = new ArrayList<String>();
        View view = mEntireBoard.getView();
        clearAvailable();
        for (int large = 0; large < 9; large++) {
            View outer = view.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            for (int small = 0; small < 9; small++) {
                ScroggleTile smallTile = mSmallTiles[large][small];
                if (!(smallTile.isClicked())) {
                    TextView innerText = (TextView) outer.findViewById(mSmallTextIds[small]);
                    innerText.setText("");
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (mResults[i]) {
                mLargeTiles[i].setResult(ScroggleTile.Result.WIN);
                correctWords.add(mLargeTiles[i].getWord());
            } else
                mLargeTiles[i].setResult(ScroggleTile.Result.LOSS);
        }
        updateAllTiles();
        return correctWords;
    }

    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(starting);
        builder.append(',');
        for (int i = 0; i < 9; i++) {
            builder.append(nineLetterWords.get(i));
            builder.append(',');
        }
        for (int large = 0; large < 9; large++) {
            builder.append(mLargeTiles[large].getWord());
            builder.append(',');
            builder.append(mLargeTiles[large].getLastClicked());
            builder.append(',');
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getResult());
                builder.append(',');
                builder.append(mSmallTiles[large][small].isClicked());
                builder.append(',');

            }
        }
        return builder.toString();
    }

    public long putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        starting = Integer.parseInt(fields[index++]);
        nineLetterWords.clear();
        for (int i = 0; i < 9; i++) {
            String s = fields[index++];
            nineLetterWords.add(i, s);
        }
        for (int large = 0; large < 9; large++) {
            if (fields[index].equals("null")) {
                mLargeTiles[large].setWord(null);
                index++;
            } else
                mLargeTiles[large].setWord(fields[index++]);
            mLargeTiles[large].setLastClicked(Integer.parseInt(fields[index++]));
            for (int small = 0; small < 9; small++) {
                ScroggleTile.Result result = ScroggleTile.Result.valueOf(fields[index++]);
                mSmallTiles[large][small].setResult(result);
                boolean b = Boolean.parseBoolean(fields[index++]);
                mSmallTiles[large][small].setClicked(b);
            }
        }
        loadCells();
        setAvailableFromLastMove(starting);
        updateAllTiles();
        return Long.parseLong(fields[index]);
    }

    private void loadCells(){
        View rootView = mEntireBoard.getView();
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            for (int small = 0; small < 9; small++) {
                ImageButton inner = (ImageButton) outer.findViewById
                        (mSmallIds[small]);
                TextView innerText = (TextView) outer.findViewById(mSmallTextIds[small]);
                innerText.setText(String.valueOf(nineLetterWords.get(large).charAt(small)));
                final int fLarge = large;
                final int fSmall = small;
                final ScroggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAvailable(smallTile)) {
                            mLargeTiles[fLarge].setLastClicked(fSmall);
                            List<ScroggleTile> availableList = getNextAvailable(fLarge, fSmall);
                            if (availableList.size() > 0) {
                                for (int t = 0; t < availableList.size(); t++) {
                                    if (!availableList.get(t).isClicked())
                                        addAvailable(availableList.get(t));
                                }
                            }
                            smallTile.setClicked(true);
                            smallTile.updateDrawableState();
                            addLetter(fLarge, nineLetterWords.get(fLarge).charAt(fSmall));
                        }
                    }
                });
            }
        }
    }

    public void initiatePhaseTwo(int score) {
        clearAvailable();
        for (int large = 0; large < 9; large++) {
            if (mResults[large]) {
                for (int small = 0; small < 9; small++) {
                    ScroggleTile s = mSmallTiles[large][small];
                    if (s.isClicked()) {
                        s.setClicked(false);
                        setListener(s, large, small, score);
                        addAvailable(s);
                    }
                }
            }
        }
        updateAllTiles();
    }

    private void setListener(final ScroggleTile s, final int large, final int small,final int score) {
        ImageButton button = (ImageButton) s.getView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAvailable(s)) {
                    removeAvailableForLarge(large);
                    s.setClicked(true);
                    s.updateDrawableState();
                }
            }
        });
    }

    private void removeAvailableForLarge(int large) {
        for (int small = 0; small < 9; small++) {
            if (isAvailable(mSmallTiles[large][small])) {
                removeAvailable(mSmallTiles[large][small]);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuthInDegress = 0;
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
        }
        if(angles[starting] == (int) azimuthInDegress || starting == 4){
            setAvailableFromLastMove(starting);
            updateAllTiles();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
