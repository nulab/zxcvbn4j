package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.DictionaryGuess;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FeedbackFactory {

  private static final List<String> NAME_DICTIONARIES =
      Arrays.asList("surnames", "male_names", "female_names");

  private FeedbackFactory() {
    throw new IllegalStateException("FeedbackFactory should not be instantiated");
  }

  static Feedback getFeedbackWithoutWarnings(String... suggestions) {
    return new Feedback(null, suggestions);
  }

  static Feedback getEmptyFeedback() {
    return new Feedback(null);
  }

  static Feedback createMatchFeedback(Match match, boolean isSoleMatch) {
    switch (match.pattern) {
      case Dictionary:
        return createDictionaryMatchFeedback(match, isSoleMatch);
      case Spatial:
        return createSpatialMatchFeedback(match);
      case Repeat:
        return createRepeatMatchFeedback(match);
      case Sequence:
        return createSequenceMatchFeedback();
      case Regex:
        return createRegexMatchFeedback(match);
      case Date:
        return createDateMatchFeedback();
      default:
        return getFeedbackWithoutWarnings(Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD);
    }
  }

  private static Feedback createSpatialMatchFeedback(Match match) {
    String warning =
        match.turns == 1
            ? Feedback.SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS
            : Feedback.SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS;
    return new Feedback(
        warning,
        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
        Feedback.SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN);
  }

  private static Feedback createRepeatMatchFeedback(Match match) {
    String warning =
        match.baseToken.length() == 1
            ? Feedback.REPEAT_WARNING_LIKE_AAA
            : Feedback.REPEAT_WARNING_LIKE_ABCABCABC;
    return new Feedback(
        warning,
        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
        Feedback.REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS);
  }

  private static Feedback createSequenceMatchFeedback() {
    return new Feedback(
        Feedback.SEQUENCE_WARNING_LIKE_ABCOR6543,
        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
        Feedback.SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES);
  }

  private static Feedback createRegexMatchFeedback(Match match) {
    String warning =
        "recent_year".equals(match.regexName) ? Feedback.REGEX_WARNING_RECENT_YEARS : null;
    return new Feedback(
        warning,
        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
        Feedback.REGEX_SUGGESTIONS_AVOID_RECENT_YEARS);
  }

  private static Feedback createDateMatchFeedback() {
    return new Feedback(
        Feedback.DATE_WARNING_DATES,
        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
        Feedback.DATE_SUGGESTIONS_AVOID_DATES);
  }

  private static Feedback createDictionaryMatchFeedback(Match match, boolean isSoleMatch) {
    String warning = getWarningBasedOnMatch(match, isSoleMatch);
    List<String> suggestions = generateSuggestions(match);
    return new Feedback(warning, suggestions.toArray(new String[0]));
  }

  private static String getWarningBasedOnMatch(Match match, boolean isSoleMatch) {
    if ("passwords".equals(match.dictionaryName)) {
      return getPasswordWarning(match, isSoleMatch);
    }

    if ("english_wikipedia".equals(match.dictionaryName) && isSoleMatch) {
      return Feedback.DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF;
    }

    if (NAME_DICTIONARIES.contains(match.dictionaryName)) {
      return getNameDictionaryWarning(isSoleMatch);
    }

    return null;
  }

  private static String getPasswordWarning(Match match, boolean isSoleMatch) {
    if (isSoleMatch && !match.l33t && !match.reversed) {
      if (match.rank <= 10) {
        return Feedback.DICTIONARY_WARNING_PASSWORDS_TOP10;
      }
      if (match.rank <= 100) {
        return Feedback.DICTIONARY_WARNING_PASSWORDS_TOP100;
      }
      return Feedback.DICTIONARY_WARNING_PASSWORDS_VERY_COMMON;
    }
    if (match.guessesLog10 <= 4) {
      return Feedback.DICTIONARY_WARNING_PASSWORDS_SIMILAR;
    }
    return null;
  }

  private static String getNameDictionaryWarning(boolean isSoleMatch) {
    if (isSoleMatch) {
      return Feedback.DICTIONARY_WARNING_ETC_NAMES_THEMSELVES;
    }
    return Feedback.DICTIONARY_WARNING_ETC_NAMES_COMMON;
  }

  private static List<String> generateSuggestions(Match match) {
    List<String> suggestions = new ArrayList<>();
    suggestions.add(Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD);

    CharSequence word = match.token;
    WipeableString lower = WipeableString.lowerCase(word);

    if (DictionaryGuess.START_UPPER.matcher(word).find()) {
      suggestions.add(Feedback.DICTIONARY_SUGGESTIONS_CAPITALIZATION);
    }

    if (DictionaryGuess.ALL_UPPER.matcher(word).find() && !lower.equals(word)) {
      suggestions.add(Feedback.DICTIONARY_SUGGESTIONS_ALL_UPPERCASE);
    }

    if (match.reversed && match.tokenLength() >= 4) {
      suggestions.add(Feedback.DICTIONARY_SUGGESTIONS_REVERSED);
    }

    if (match.l33t) {
      suggestions.add(Feedback.DICTIONARY_SUGGESTIONS_L33T);
    }

    lower.wipe();
    return suggestions;
  }
}
