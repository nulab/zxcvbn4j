package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;

public interface Guess {

    public static final int BRUTEFORCE_CARDINALITY = 10;
    public static final int MIN_SUBMATCH_GUESSES_SINGLE_CHAR = 10;
    public static final int MIN_SUBMATCH_GUESSES_MULTI_CHAR = 50;
    public static final int MIN_YEAR_SPACE = 20;
    public static final int REFERENCE_YEAR = 2000;


    public double exec(Match match);
}
