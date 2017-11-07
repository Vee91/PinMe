package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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


/**
 * Created by cvika on 2/14/2017.
 */

public class ScroggleGameFragment extends Fragment {
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

    private ScroggleTile mEntireBoard = new ScroggleTile(this);
    private ScroggleTile mLargeTiles[] = new ScroggleTile[9];
    private ScroggleTile mSmallTiles[][] = new ScroggleTile[9][9];
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    private int starting = 0;
    private String phaseTwo[] = new String[9];

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.scroggle_large_board, container, false);
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    public void initGame() {
        Log.d("UT3", "init game");
        mEntireBoard = new ScroggleTile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScroggleTile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScroggleTile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);
        // If the player moves first, set which spots are available
        setAvailableFromLastMove(starting);
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
            starting++;
            setAvailableFromLastMove(starting);
            updateAllTiles();
            return mLargeTiles[starting - 1].getWord();
        }
        return null;
    }

    public String getPrevTile() {
        if (starting >= 1 && starting <= 8) {
            starting--;
            setAvailableFromLastMove(starting);
            updateAllTiles();
            return mLargeTiles[starting + 1].getWord();
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
        return mLargeTiles[starting].getWord();
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
                    addLetterForPhaseTwo(large, nineLetterWords.get(large).charAt(small), score);
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

    private void addLetterForPhaseTwo(int large, char x, int score) {
        phaseTwo[large] = String.valueOf(x);
        if (mAvailable.size() == 0) {
            getActivity().finish();
            Intent intent = new Intent(getActivity().getApplicationContext(), PhaseTwo.class);
            intent.putExtra("PHASE_TWO", phaseTwo);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }
    }
}
