package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Match;

public class RepeatGuess extends BaseGuess {

    public RepeatGuess(final Context context) {
        super(context);
    }

    @Override
    public double exec(Match match) {
        return match.baseGuesses * match.repeatCount;
    }
}
