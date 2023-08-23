package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SequenceGuess extends BaseGuess {

  private static final Set<Character> START_POINTS =
      new HashSet<>(Arrays.asList('a', 'A', 'z', 'Z', '0', '1', '9'));
  private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

  public SequenceGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    final char firstChar = match.token.charAt(0);
    double baseGuesses = determineBaseGuesses(firstChar);
    if (!match.ascending) {
      baseGuesses *= 2;
    }
    return baseGuesses * match.tokenLength();
  }

  private double determineBaseGuesses(char firstChar) {
    if (START_POINTS.contains(firstChar)) {
      return 4;
    } else if (DIGIT_PATTERN.matcher(String.valueOf(firstChar)).find()) {
      return 10;
    } else {
      return 26;
    }
  }
}
