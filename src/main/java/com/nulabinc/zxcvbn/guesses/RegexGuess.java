package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.HashMap;
import java.util.Map;

public class RegexGuess extends BaseGuess {

  private static final Map<String, Integer> CHAR_CLASS_BASES = new HashMap<>();

  static {
    CHAR_CLASS_BASES.put("alpha_lower", 26);
    CHAR_CLASS_BASES.put("alpha_upper", 26);
    CHAR_CLASS_BASES.put("alpha", 52);
    CHAR_CLASS_BASES.put("alphanumeric", 62);
    CHAR_CLASS_BASES.put("digits", 10);
    CHAR_CLASS_BASES.put("symbols", 33);
  }

  private static final String RECENT_YEAR = "recent_year";

  protected RegexGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    if (CHAR_CLASS_BASES.containsKey(match.regexName)) {
      return calculateCharClassGuesses(match);
    }
    if (RECENT_YEAR.equals(match.regexName)) {
      return calculateYearSpace(match.token);
    }
    return 0;
  }

  private double calculateCharClassGuesses(Match match) {
    return Math.pow(CHAR_CLASS_BASES.get(match.regexName), match.tokenLength());
  }

  private double calculateYearSpace(CharSequence token) {
    double yearSpace = Math.abs(parseInt(token) - REFERENCE_YEAR);
    return Math.max(yearSpace, MIN_YEAR_SPACE);
  }

  private static int parseInt(CharSequence s) {
    try {
      return WipeableString.parseInt(s);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
