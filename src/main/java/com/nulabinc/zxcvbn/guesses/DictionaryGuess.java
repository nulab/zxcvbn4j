package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.Map;
import java.util.regex.Pattern;

public class DictionaryGuess extends BaseGuess {

  public static final Pattern START_UPPER = Pattern.compile("^[A-Z][^A-Z]+$");
  private static final Pattern END_UPPER = Pattern.compile("^[^A-Z]+[A-Z]$");
  public static final Pattern ALL_UPPER = Pattern.compile("^[^a-z]+$");
  private static final Pattern ALL_LOWER = Pattern.compile("^[^A-Z]+$");

  public DictionaryGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    match.baseGuesses = (double) match.rank;
    int uppercaseVariations = uppercaseVariations(match);
    int l33tVariations = l33tVariations(match);
    int reversedVariations = match.reversed ? 2 : 1;
    return match.rank * uppercaseVariations * l33tVariations * reversedVariations;
  }

  public int uppercaseVariations(Match match) {
    CharSequence token = match.token;
    WipeableString lowercaseToken = WipeableString.lowerCase(token);
    if (ALL_LOWER.matcher(token).find(0) || lowercaseToken.equals(token)) {
      return 1;
    }
    if (START_UPPER.matcher(token).find()
        || END_UPPER.matcher(token).find()
        || ALL_UPPER.matcher(token).find()) {
      return 2;
    }

    int upperCount = 0;
    int lowerCount = 0;
    for (int i = 0; i < token.length(); i++) {
      lowerCount += Character.isLowerCase(token.charAt(i)) ? 1 : 0;
      upperCount += Character.isUpperCase(token.charAt(i)) ? 1 : 0;
    }
    int variations = 0;
    for (int i = 1; i <= Math.min(upperCount, lowerCount); i++) {
      variations += nCk(upperCount + lowerCount, i);
    }
    lowercaseToken.wipe();
    return variations;
  }

  public int l33tVariations(Match match) {
    if (!match.l33t) {
      return 1;
    }
    int totalVariations = 1;
    WipeableString lowercaseToken = WipeableString.lowerCase(match.token);
    for (Map.Entry<Character, Character> substitution : match.sub.entrySet()) {
      Character substitutedChar = substitution.getKey();
      Character originalChar = substitution.getValue();
      int substitutedCount = 0;
      int originalCount = 0;
      for (char currentChar : lowercaseToken.charArray()) {
        if (currentChar == substitutedChar) {
          substitutedCount++;
        }
        if (currentChar == originalChar) {
          originalCount++;
        }
      }
      if (substitutedCount == 0 || originalCount == 0) {
        totalVariations *= 2;
      } else {
        int minCount = Math.min(originalCount, substitutedCount);
        int possibleCombinations = 0;
        for (int i = 1; i <= minCount; i++) {
          possibleCombinations += nCk(originalCount + substitutedCount, i);
        }
        totalVariations *= possibleCombinations;
      }
    }
    return totalVariations;
  }
}
