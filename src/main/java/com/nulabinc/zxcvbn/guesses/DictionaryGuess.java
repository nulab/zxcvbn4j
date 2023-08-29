package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.AbstractMap;
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
    return (double) match.rank * uppercaseVariations * l33tVariations * reversedVariations;
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
      variations += calculateBinomialCoefficient(upperCount + lowerCount, i);
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
      totalVariations *= calculateSubstitutionVariation(substitution, lowercaseToken);
    }
    return totalVariations;
  }

  private static int calculateSubstitutionVariation(
      Map.Entry<Character, Character> substitution, WipeableString token) {
    Character substitutedChar = substitution.getKey();
    Character originalChar = substitution.getValue();
    AbstractMap.SimpleImmutableEntry<Integer, Integer> counts =
        countCharOccurrences(token, substitutedChar, originalChar);
    int substitutedCount = counts.getKey();
    int originalCount = counts.getValue();
    if (substitutedCount == 0 || originalCount == 0) {
      return 2;
    }
    return calculatePossibleCombinations(originalCount, substitutedCount);
  }

  private static AbstractMap.SimpleImmutableEntry<Integer, Integer> countCharOccurrences(
      WipeableString str, char char1, char char2) {
    int count1 = 0;
    int count2 = 0;
    for (char currentChar : str.charArray()) {
      if (currentChar == char1) {
        count1++;
      }
      if (currentChar == char2) {
        count2++;
      }
    }
    return new AbstractMap.SimpleImmutableEntry<>(count1, count2);
  }

  private static int calculatePossibleCombinations(int originalCount, int substitutedCount) {
    int minCount = Math.min(originalCount, substitutedCount);
    int possibleCombinations = 0;
    for (int i = 1; i <= minCount; i++) {
      possibleCombinations += calculateBinomialCoefficient(originalCount + substitutedCount, i);
    }
    return possibleCombinations;
  }
}
