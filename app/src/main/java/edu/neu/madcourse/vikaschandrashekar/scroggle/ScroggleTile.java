package edu.neu.madcourse.vikaschandrashekar.scroggle;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;



/**
 * Created by cvika on 2/14/2017.
 */

public class ScroggleTile {


    public ScroggleTile() {

    }

    public enum Result {
        WIN, LOSS , NEITHER, SELECTED
    }

    // These levels are defined in the drawable definitions
    private static final int TILE_AVAILABLE = 0;
    private static final int TILE_SELECTED = 1;
    private static final int TILE_DISABLED = 2;

    private String word;
    private ScroggleGameFragment mGame;
    private Result mResult = Result.NEITHER;
    private boolean clicked = false;
    private View mView;
    private ScroggleTile mSubTiles[];
    private int lastClicked = 10;

    public ScroggleTile(ScroggleGameFragment game) {
        this.mGame = game;
    }

    public ScroggleTile(PhaseTwoFragment g){
        mGame = null;
    }

    public View getView() {
        return mView;
    }

    public void setWord(String word){
        this.word = word;
    }

    public String getWord(){
        return this.word;
    }

    public void setClicked(boolean clicked){
        this.clicked = clicked;
    }

    public boolean isClicked(){
        return this.clicked;
    }

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result owner) {
        this.mResult = owner;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public ScroggleTile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(ScroggleTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void updateDrawableState() {
        if (mView == null) return;
        int level = getBackgroundLevel();
        if (mView.getBackground() != null) {
            mView.getBackground().setLevel(level);
        }
        int level1 = getLevel();
        if (mView instanceof ImageButton) {
            Drawable drawable = ((ImageButton) mView).getDrawable();
            drawable.setLevel(level1);
        }
    }

    private int getBackgroundLevel() {
        switch (mResult){
            case WIN :
                return 2;
            case LOSS:
                return 1;
            case NEITHER:
                return 0;
            case SELECTED:
                return 3;
        }
        return 0;
    }

    private int getLevel() {
        if(!this.isClicked()){
            return 0;
        }
        return 1;
    }

    public int evaluate() {
        return 0;
    }

    public int getLastClicked() {
        return lastClicked;
    }

    public void setLastClicked(int lastClicked) {
        this.lastClicked = lastClicked;
    }
}
