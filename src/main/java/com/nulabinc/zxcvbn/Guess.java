package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.Calendar;

public interface Guess {

  public static final int BRUTEFORCE_CARDINALITY = 10;
  public static final int MIN_SUBMATCH_GUESSES_SINGLE_CHAR = 10;
  public static final int MIN_SUBMATCH_GUESSES_MULTI_CHAR = 50;
  public static final int MIN_YEAR_SPACE = 20;
  public static final int REFERENCE_YEAR = Calendar.getInstance().get(Calendar.YEAR);

  public double exec(Match match);
}
