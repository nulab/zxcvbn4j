package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;

public class BruteforceGuess extends BaseGuess {

    @Override
    public double exec(Match match) {
        double guesses = Math.pow(BRUTEFORCE_CARDINALITY, match.token.length());
        if (Double.isInfinite(guesses)) {
            guesses = Double.MAX_VALUE;
        }
        double minGuesses = match.token.length() == 1 ? MIN_SUBMATCH_GUESSES_SINGLE_CHAR + 1 : MIN_SUBMATCH_GUESSES_MULTI_CHAR + 1;
        return Math.max(guesses, minGuesses);
    }
}
