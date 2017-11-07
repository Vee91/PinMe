package edu.neu.madcourse.vikaschandrashekar.multiplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvika on 3/5/2017.
 */

public class PlayerModel {

    private String name;
    // yes or no
    private String isPlaying;
    // status of the game
    // ex player found a word in a cell
    private String statusMessage;
    // says whether player is playing or not
    private String playMessage;
    private String score;
    private String playerToken;
    // represents which player turn it is 1 or 2
    private String turn;
    private String nineLetterWords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(String isPlaying) {
        this.isPlaying = isPlaying;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getPlayMessage() {
        return playMessage;
    }

    public void setPlayMessage(String playMessage) {
        this.playMessage = playMessage;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getNineLetterWords() {
        return nineLetterWords;
    }

    public void setNineLetterWords(String nineLetterWords) {
        this.nineLetterWords = nineLetterWords;
    }
}