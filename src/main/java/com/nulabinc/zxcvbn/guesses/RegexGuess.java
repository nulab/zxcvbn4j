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

  protected RegexGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    if (CHAR_CLASS_BASES.containsKey(match.regexName)) {
      return Math.pow(CHAR_CLASS_BASES.get(match.regexName), match.tokenLength());
    } else if ("recent_year".equals(match.regexName)) {
      double yearSpace = Math.abs(parseInt(match.token) - REFERENCE_YEAR);
      yearSpace = Math.max(yearSpace, MIN_YEAR_SPACE);
      return yearSpace;
    }
    return 0;
  }

  private static int parseInt(CharSequence s) {
    int result = 0;
    try {
      result = WipeableString.parseInt(s);
    } catch (NumberFormatException e) {
      // ignore
    }
    return result;
  }
}
