package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Feedback {

  private static final String DEFAULT_BUNDLE_NAME = "com/nulabinc/zxcvbn/messages";

  private static final ResourceBundle.Control CONTROL =
      ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

  public static final String DEFAULT_SUGGESTIONS_USE_FEW_WORDS =
      "feedback.default.suggestions.useFewWords";
  public static final String DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS =
      "feedback.default.suggestions.noNeedSymbols";
  public static final String EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD =
      "feedback.extra.suggestions.addAnotherWord";
  public static final String SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS =
      "feedback.spatial.warning.straightRowsOfKeys";
  public static final String SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS =
      "feedback.spatial.warning.shortKeyboardPatterns";
  public static final String SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN =
      "feedback.spatial.suggestions.UseLongerKeyboardPattern";
  public static final String REPEAT_WARNING_LIKE_AAA = "feedback.repeat.warning.likeAAA";
  public static final String REPEAT_WARNING_LIKE_ABCABCABC =
      "feedback.repeat.warning.likeABCABCABC";
  public static final String REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS =
      "feedback.repeat.suggestions.avoidRepeatedWords";
  public static final String SEQUENCE_WARNING_LIKE_ABCOR6543 =
      "feedback.sequence.warning.likeABCor6543";
  public static final String SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES =
      "feedback.sequence.suggestions.avoidSequences";
  public static final String REGEX_WARNING_RECENT_YEARS = "feedback.regex.warning.recentYears";
  public static final String REGEX_SUGGESTIONS_AVOID_RECENT_YEARS =
      "feedback.regex.suggestions.avoidRecentYears";
  public static final String DATE_WARNING_DATES = "feedback.date.warning.dates";
  public static final String DATE_SUGGESTIONS_AVOID_DATES = "feedback.date.suggestions.avoidDates";
  public static final String DICTIONARY_WARNING_PASSWORDS_TOP10 =
      "feedback.dictionary.warning.passwords.top10";
  public static final String DICTIONARY_WARNING_PASSWORDS_TOP100 =
      "feedback.dictionary.warning.passwords.top100";
  public static final String DICTIONARY_WARNING_PASSWORDS_VERY_COMMON =
      "feedback.dictionary.warning.passwords.veryCommon";
  public static final String DICTIONARY_WARNING_PASSWORDS_SIMILAR =
      "feedback.dictionary.warning.passwords.similar";
  public static final String DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF =
      "feedback.dictionary.warning.englishWikipedia.itself";
  public static final String DICTIONARY_WARNING_ETC_NAMES_THEMSELVES =
      "feedback.dictionary.warning.etc.namesThemselves";
  public static final String DICTIONARY_WARNING_ETC_NAMES_COMMON =
      "feedback.dictionary.warning.etc.namesCommon";
  public static final String DICTIONARY_SUGGESTIONS_CAPITALIZATION =
      "feedback.dictionary.suggestions.capitalization";
  public static final String DICTIONARY_SUGGESTIONS_ALL_UPPERCASE =
      "feedback.dictionary.suggestions.allUppercase";
  public static final String DICTIONARY_SUGGESTIONS_REVERSED =
      "feedback.dictionary.suggestions.reversed";
  public static final String DICTIONARY_SUGGESTIONS_L33T = "feedback.dictionary.suggestions.l33t";

  private final String warning;
  private final String[] suggestions;

  Feedback(String warning, String... suggestions) {
    this.warning = warning;
    this.suggestions = suggestions;
  }

  public String getWarning() {
    return getWarning(Locale.getDefault());
  }

  public String getWarning(Locale locale) {
    if (this.warning == null) {
      return "";
    }
    ResourceBundle messages = resolveResourceBundle(locale);
    return l10n(messages, this.warning);
  }

  public List<String> getSuggestions() {
    return getSuggestions(Locale.getDefault());
  }

  public List<String> getSuggestions(Locale locale) {
    List<String> suggestionTexts = new ArrayList<>(this.suggestions.length);
    ResourceBundle messages = resolveResourceBundle(locale);
    for (String suggestion : this.suggestions) {
      suggestionTexts.add(l10n(messages, suggestion));
    }
    return suggestionTexts;
  }

  protected ResourceBundle resolveResourceBundle(Locale locale) {
    try {
      return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale, CONTROL);
    } catch (MissingResourceException | UnsupportedOperationException e) {
      // MissingResourceException:
      // Fix for issue of Android refs: https://github.com/nulab/zxcvbn4j/issues/21
      //
      // UnsupportedOperationException:
      // Fix for issue of JDK 9 refs: https://github.com/nulab/zxcvbn4j/issues/45
      // ResourceBundle.Control is not supported in named modules.
      // See https://docs.oracle.com/javase/9/docs/api/java/util/ResourceBundle.html#bundleprovider
      // for more details
      return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale);
    }
  }

  public Feedback withResourceBundle(ResourceBundle messages) {
    return new ResourceBundleFeedback(messages, warning, suggestions);
  }

  public Feedback replaceResourceBundle(Map<Locale, ResourceBundle> messages) {
    return new ReplacedMessagesFeedback(messages, warning, suggestions);
  }

  private String l10n(ResourceBundle messages, String messageId) {
    return messages != null ? messages.getString(messageId) : messageId;
  }

  static Feedback getFeedback(int score, List<Match> sequence) {
    if (sequence.size() == 0) {
      return FeedbackFactory.getFeedbackWithoutWarnings(
          DEFAULT_SUGGESTIONS_USE_FEW_WORDS, DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS);
    }
    if (score > 2) {
      return FeedbackFactory.getEmptyFeedback();
    }
    Match longestMatch = sequence.get(0);
    if (sequence.size() > 1) {
      for (Match match : sequence.subList(1, sequence.size())) {
        if (match.tokenLength() > longestMatch.tokenLength()) {
          longestMatch = match;
        }
      }
    }
    boolean isSoleMatch = sequence.size() == 1;
    return FeedbackFactory.createMatchFeedback(longestMatch, isSoleMatch);
  }

  private static class ResourceBundleFeedback extends Feedback {
    private final ResourceBundle messages;

    private ResourceBundleFeedback(ResourceBundle messages, String warning, String... suggestions) {
      super(warning, suggestions);
      this.messages = messages;
    }

    @Override
    protected ResourceBundle resolveResourceBundle(Locale locale) {
      return messages;
    }
  }

  private static class ReplacedMessagesFeedback extends Feedback {
    private final Map<Locale, ResourceBundle> messages;

    private ReplacedMessagesFeedback(
        Map<Locale, ResourceBundle> messages, String warning, String... suggestions) {
      super(warning, suggestions);
      this.messages = messages;
    }

    @Override
    protected ResourceBundle resolveResourceBundle(Locale locale) {
      try {
        ResourceBundle resource = messages.get(locale);
        if (resource != null) {
          return resource;
        }
        return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale, CONTROL);
      } catch (MissingResourceException | UnsupportedOperationException e) {
        return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale);
      }
    }
  }
}
