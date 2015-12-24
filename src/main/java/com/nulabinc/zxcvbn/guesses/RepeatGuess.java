package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;

public class RepeatGuess extends BaseGuess {

    @Override
    public double exec(Match match) {
        return match.baseGuesses * match.repeatCount;
    }
}
