package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Match;

public class BruteforceGuess extends BaseGuess {

  protected BruteforceGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    double guesses = calculateBruteforceGuesses(match.tokenLength());
    double minGuesses = calculateMinGuesses(match.tokenLength());
    return Math.max(guesses, minGuesses);
  }

  private double calculateBruteforceGuesses(int tokenLength) {
    double guesses = Math.pow(BRUTEFORCE_CARDINALITY, tokenLength);
    return Double.isInfinite(guesses) ? Double.MAX_VALUE : guesses;
  }

  private double calculateMinGuesses(int tokenLength) {
    return tokenLength == 1
        ? MIN_SUBMATCH_GUESSES_SINGLE_CHAR + 1
        : MIN_SUBMATCH_GUESSES_MULTI_CHAR + 1;
  }
}
