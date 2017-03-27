package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.Keyboard;

public class SpatialGuess extends BaseGuess {

    @Override
    public double exec(Match match) {
        Keyboard keyboard = Keyboard.of(match.graph);
        int s = keyboard.getStartingPositions();
        double d = keyboard.getAverageDegree();
        double guesses = 0;
        int l = match.token.length();
        int t = match.turns;
        for (int i = 2; i <= l; i++) {
            int possibleTurns = Math.min(t, i - 1);
            for (int j = 1; j <= possibleTurns; j++) {
                guesses += nCk(i - 1, j - 1) * s * Math.pow(d, j);
            }
        }
        if (match.shiftedCount != null && match.shiftedCount > 0) {
            int shiftedCount = match.shiftedCount;
            int unshiftedCount = match.token.length() - match.shiftedCount;
            if (shiftedCount == 0 || unshiftedCount == 0) {
                guesses *= 2;
            } else {
                int shiftedVariations = 0;
                for (int i = 1; i <= Math.min(shiftedCount, unshiftedCount); i++) {
                    shiftedVariations += nCk(shiftedCount + unshiftedCount, i);
                }
                guesses *= shiftedVariations;
            }
        }
        return guesses;
    }
}
