package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.Calendar;

/**
 * Represents a strategy for estimating the number of guesses required to crack a given {@link
 * Match}.
 *
 * <p>Implementations of this interface are expected to evaluate the strength or weakness of a
 * matched pattern within a password and return an estimated guess number.
 *
 * @see Match
 */
public interface Guess {

  /** Cardinality used in brute force attacks. */
  int BRUTEFORCE_CARDINALITY = 10;

  /** Minimum number of guesses when the sub-match contains a single character. */
  int MIN_SUBMATCH_GUESSES_SINGLE_CHAR = 10;

  /** Minimum number of guesses when the sub-match contains multiple characters. */
  int MIN_SUBMATCH_GUESSES_MULTI_CHAR = 50;

  /** The minimum range of years to be considered when evaluating date-based patterns. */
  int MIN_YEAR_SPACE = 20;

  /** Reference year used for date-based pattern evaluations. */
  int REFERENCE_YEAR = Calendar.getInstance().get(Calendar.YEAR);

  /**
   * Evaluates the given {@link Match} and estimates the number of guesses required to crack it.
   *
   * @param match the matched pattern to evaluate.
   * @return the estimated number of guesses required to crack the given match.
   */
  double exec(Match match);
}
