package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.Keyboard;

public class SpatialGuess extends BaseGuess {

    private static final int KEYBOARD_AVERAGE_DEGREE = calcAverageDegree(Keyboard.ADJACENCY_GRAPHS.get("qwerty"));
    private static final int KEYPAD_AVERAGE_DEGREE = calcAverageDegree(Keyboard.ADJACENCY_GRAPHS.get("keypad"));
    private static final int KEYBOARD_STARTING_POSITIONS = Keyboard.ADJACENCY_GRAPHS.get("qwerty").size();
    private static final int KEYPAD_STARTING_POSITIONS =  Keyboard.ADJACENCY_GRAPHS.get("keypad").size();

    @Override
    public double exec(Match match) {
        int s;
        int d;
        if ("qwerty".equals(match.graph) || "dvorak".equals(match.graph)) {
            s = KEYBOARD_STARTING_POSITIONS;
            d = KEYBOARD_AVERAGE_DEGREE;
        } else {
            s = KEYPAD_STARTING_POSITIONS;
            d = KEYPAD_AVERAGE_DEGREE;
        }
        double guesses = 0;
        int l = match.token.length();
        int t = match.turns;
        for (int i = 2; i <= l; i++) {
            int possibleTurns = Math.min(t, i - 1);
            for (int j = 1; j < possibleTurns; j++) guesses += nCk(i - 1, j - 1) * s * Math.pow(d, j);
        }
        if (match.shiftedCount != null) {
            int shiftedCount = match.shiftedCount;
            int unshiftedCount = match.token.length() - match.shiftedCount;
            if (shiftedCount == 0 || unshiftedCount == 0) {
                guesses *= 2;
            } else {
                int shiftedVariations = 0;
                for (int i = 1; i <= Math.min(shiftedCount, unshiftedCount); i++) {
                    shiftedVariations += nCk(shiftedCount + unshiftedCount, i);
                    guesses *= shiftedVariations;
                }
            }
        }
        return guesses;
    }
}
