package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Match;

public class DateGuess extends BaseGuess {

    public DateGuess(final Context context) {
        super(context);
    }

    @Override
    public double exec(Match match) {
        double yearSpace = Math.max(Math.abs(match.year - REFERENCE_YEAR), MIN_YEAR_SPACE);
        double guesses = yearSpace * 365;
        if (match.separator != null && !match.separator.isEmpty()) guesses *= 4;
        return guesses;
    }
}
