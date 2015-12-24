package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;

import java.util.List;

public class Strength {

    private String password;
    private double guesses;
    private double guessesLog10;
    private AttackTimes.CrackTimeSeconds crackTimeSeconds;
    private AttackTimes.CrackTimesDisplay crackTimesDisplay;
    private int score;
    private Feedback feedback;
    private List<Match> sequence;
    private long calcTime;

    public Strength() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getGuesses() {
        return guesses;
    }

    public void setGuesses(double guesses) {
        this.guesses = guesses;
    }

    public double getGuessesLog10() {
        return guessesLog10;
    }

    public void setGuessesLog10(double guessesLog10) {
        this.guessesLog10 = guessesLog10;
    }

    public AttackTimes.CrackTimeSeconds getCrackTimeSeconds() {
        return crackTimeSeconds;
    }

    public void setCrackTimeSeconds(AttackTimes.CrackTimeSeconds crackTimeSeconds) {
        this.crackTimeSeconds = crackTimeSeconds;
    }

    public AttackTimes.CrackTimesDisplay getCrackTimesDisplay() {
        return crackTimesDisplay;
    }

    public void setCrackTimesDisplay(AttackTimes.CrackTimesDisplay crackTimesDisplay) {
        this.crackTimesDisplay = crackTimesDisplay;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public List<Match> getSequence() {
        return sequence;
    }

    public void setSequence(List<Match> sequence) {
        this.sequence = sequence;
    }

    public long getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(long calcTime) {
        this.calcTime = calcTime;
    }
}
