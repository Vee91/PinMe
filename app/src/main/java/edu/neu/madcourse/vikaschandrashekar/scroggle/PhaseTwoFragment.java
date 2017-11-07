package edu.neu.madcourse.vikaschandrashekar.scroggle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvika on 2/17/2017.
 */

public class PhaseTwoFragment extends Fragment {

    static private int mSmallIds[] = {R.id.scroggle_small1, R.id.scroggle_small2, R.id.scroggle_small3,
            R.id.scroggle_small4, R.id.scroggle_small5, R.id.scroggle_small6, R.id.scroggle_small7, R.id.scroggle_small8,
            R.id.scroggle_small9,};
    static private int mSmallTextIds[] = {R.id.scroggle_textsmall1, R.id.scroggle_textsmall2, R.id.scroggle_textsmall3,
            R.id.scroggle_textsmall4, R.id.scroggle_textsmall5, R.id.scroggle_textsmall6, R.id.scroggle_textsmall7, R.id.scroggle_textsmall8,
            R.id.scroggle_textsmall9,};

    private ScroggleTile mEntireBoard = new ScroggleTile(this);
    private ScroggleTile mTiles[] = new ScroggleTile[9];
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    int currentTile = 10;

    private String word = "";
    private String phaseTwoWords[] = new String[9];

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
                inflater.inflate(R.layout.phase_two_fragment, container, false);
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    private void initGame() {
        phaseTwoWords = getActivity().getIntent().getStringArrayExtra("PHASE_TWO");
        mEntireBoard = new ScroggleTile(this);
        for (int i = 0; i < 9; i++) {
            mTiles[i] = new ScroggleTile(this);
        }
        mEntireBoard.setSubTiles(mTiles);
        setAvailableFromLastMove(currentTile);
    }

    private void initViews(View rootView) {
        mEntireBoard.setView(rootView);
        for (int small = 0; small < 9; small++) {
            ImageButton inner = (ImageButton) rootView.findViewById
                    (mSmallIds[small]);
            TextView innerText = (TextView) rootView.findViewById(mSmallTextIds[small]);
            if (phaseTwoWords[small] != null)
                innerText.setText(phaseTwoWords[small]);
            final ScroggleTile smallTile = mTiles[small];
            final int fNext = small;
            smallTile.setView(inner);
            inner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAvailable(smallTile)) {
                        setAvailableFromLastMove(fNext);
                        setOtherClicks();
                        smallTile.setClicked(true);
                        smallTile.updateDrawableState();
                        addLetter(phaseTwoWords[fNext]);
                        updateAllTiles();
                    }
                }
            });
        }

    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small == 10) {
            for (int dest = 0; dest < 9; dest++) {
                if (phaseTwoWords[dest] != null) {
                    ScroggleTile tile = mTiles[dest];
                    tile.setClicked(false);
                    addAvailable(tile);
                }
            }
        } else {
            switch (small) {
                case 0:
                    clearAvailable();
                    addAvailable(mTiles[1]);
                    addAvailable(mTiles[3]);
                    addAvailable(mTiles[4]);
                    break;
                case 1:
                    clearAvailable();
                    addAvailable(mTiles[0]);
                    addAvailable(mTiles[2]);
                    addAvailable(mTiles[3]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[5]);
                    break;
                case 2:
                    clearAvailable();
                    addAvailable(mTiles[1]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[5]);
                    break;
                case 3:
                    clearAvailable();
                    addAvailable(mTiles[0]);
                    addAvailable(mTiles[1]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[6]);
                    addAvailable(mTiles[7]);
                    break;
                case 4:
                    clearAvailable();
                    addAvailable(mTiles[0]);
                    addAvailable(mTiles[1]);
                    addAvailable(mTiles[2]);
                    addAvailable(mTiles[3]);
                    addAvailable(mTiles[5]);
                    addAvailable(mTiles[6]);
                    addAvailable(mTiles[7]);
                    addAvailable(mTiles[8]);
                    break;
                case 5:
                    clearAvailable();
                    addAvailable(mTiles[1]);
                    addAvailable(mTiles[2]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[7]);
                    addAvailable(mTiles[8]);
                    break;
                case 6:
                    clearAvailable();
                    addAvailable(mTiles[3]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[7]);
                    break;
                case 7:
                    clearAvailable();
                    addAvailable(mTiles[3]);
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[5]);
                    addAvailable(mTiles[6]);
                    addAvailable(mTiles[8]);
                    break;
                case 8:
                    clearAvailable();
                    addAvailable(mTiles[4]);
                    addAvailable(mTiles[5]);
                    addAvailable(mTiles[7]);
                    break;
            }
        }
    }

    private void setOtherClicks() {
        for (int i = 0; i < 9; i++) {
            ScroggleTile s = mTiles[i];
            s.setClicked(false);
        }
    }

    private void addLetter(String s) {
        word += s;
        System.out.println(word);
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int small = 0; small < 9; small++) {
            mTiles[small].updateDrawableState();
        }
    }

    public String getWord(){
        setAvailableFromLastMove(10);
        updateAllTiles();
        return word;
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

    public String[] getPhaseTwoWords() {
        return phaseTwoWords;
    }

    public void setPhaseTwoWords(String[] phaseTwoWords) {
        this.phaseTwoWords = phaseTwoWords;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
