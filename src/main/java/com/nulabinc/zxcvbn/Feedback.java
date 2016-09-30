package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.DictionaryGuess;
import com.nulabinc.zxcvbn.matchers.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Feedback {

    private static final String DEFAULT_BUNDLE_NAME = "com/nulabinc/zxcvbn/messages";

    private static final ResourceBundle.Control CONTROL =
            ResourceBundle.Control.getNoFallbackControl(
                    ResourceBundle.Control.FORMAT_DEFAULT);

    public static final String DEFAULT_SUGGESTIONS_USE_FEW_WORDS = "feedback.default.suggestions.useFewWords";
    public static final String DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS = "feedback.default.suggestions.noNeedSymbols";
    public static final String EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD = "feedback.extra.suggestions.addAnotherWord";
    public static final String SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS = "feedback.spatial.warning.straightRowsOfKeys";
    public static final String SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS = "feedback.spatial.warning.shortKeyboardPatterns";
    public static final String SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN = "feedback.spatial.suggestions.UseLongerKeyboardPattern";
    public static final String REPEAT_WARNING_LIKE_AAA = "feedback.repeat.warning.likeAAA";
    public static final String REPEAT_WARNING_LIKE_ABCABCABC = "feedback.repeat.warning.likeABCABCABC";
    public static final String REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS = "feedback.repeat.suggestions.avoidRepeatedWords";
    public static final String SEQUENCE_WARNING_LIKE_ABCOR6543 = "feedback.sequence.warning.likeABCor6543";
    public static final String SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES = "feedback.sequence.suggestions.avoidSequences";
    public static final String REGEX_WARNING_RECENT_YEARS = "feedback.regex.warning.recentYears";
    public static final String REGEX_SUGGESTIONS_AVOID_RECENT_YEARS = "feedback.regex.suggestions.avoidRecentYears";
    public static final String DATE_WARNING_DATES = "feedback.date.warning.dates";
    public static final String DATE_SUGGESTIONS_AVOID_DATES = "feedback.date.suggestions.avoidDates";
    public static final String DICTIONARY_WARNING_PASSWORDS_TOP10 = "feedback.dictionary.warning.passwords.top10";
    public static final String DICTIONARY_WARNING_PASSWORDS_TOP100 = "feedback.dictionary.warning.passwords.top100";
    public static final String DICTIONARY_WARNING_PASSWORDS_VERY_COMMON = "feedback.dictionary.warning.passwords.veryCommon";
    public static final String DICTIONARY_WARNING_PASSWORDS_SIMILAR = "feedback.dictionary.warning.passwords.similar";
    public static final String DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF = "feedback.dictionary.warning.englishWikipedia.itself";
    public static final String DICTIONARY_WARNING_ETC_NAMES_THEMSELVES = "feedback.dictionary.warning.etc.namesThemselves";
    public static final String DICTIONARY_WARNING_ETC_NAMES_COMMON = "feedback.dictionary.warning.etc.namesCommon";
    public static final String DICTIONARY_SUGGESTIONS_CAPITALIZATION = "feedback.dictionary.suggestions.capitalization";
    public static final String DICTIONARY_SUGGESTIONS_ALL_UPPERCASE = "feedback.dictionary.suggestions.allUppercase";
    public static final String DICTIONARY_SUGGESTIONS_REVERSED = "feedback.dictionary.suggestions.reversed";
    public static final String DICTIONARY_SUGGESTIONS_L33T = "feedback.dictionary.suggestions.l33t";

    final private String warning;
    final private String[] suggestions;

    private Feedback(String warning, String... suggestions) {
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
        } catch (MissingResourceException e) {
            // Fix for issue of Android refs: https://github.com/nulab/zxcvbn4j/issues/21
            return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale);
        }
    }

    public Feedback withResourceBundle(ResourceBundle messages) {
        return new ResourceBundleFeedback(messages, warning, suggestions);
    }

    private String l10n(ResourceBundle messages, String messageId) {
        return messages != null ? messages.getString(messageId) : messageId;
    }

    static Feedback getFeedback(int score, List<Match> sequence) {
        if (sequence.size() == 0) {
            return getFeedbackWithoutWarnings(
                    DEFAULT_SUGGESTIONS_USE_FEW_WORDS,
                    DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS);
        }
        if (score > 2) {
            return getEmptyFeedback();
        }
        Match longestMatch = sequence.get(0);
        if (sequence.size() > 1) {
            for (Match match : sequence.subList(1, sequence.size() - 1)) {
                if (match.token.length() > longestMatch.token.length()) longestMatch = match;
            }
        }

        return getMatchFeedback(longestMatch, sequence.size() == 1);
    }

    private static Feedback getFeedbackWithoutWarnings(String... suggestions) {
        return new Feedback(null, suggestions);
    }

    private static Feedback getEmptyFeedback() {
        return new Feedback(null);
    }

    private static Feedback getMatchFeedback(Match match, boolean isSoleMatch) {
        switch (match.pattern) {
            case Dictionary:
                return getDictionaryMatchFeedback(match, isSoleMatch);
            case Spatial:
                return new Feedback(match.turns == 1
                        ? SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS
                        : SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS,
                        EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN
                );
            case Repeat:
                return new Feedback(match.baseToken.length() == 1
                        ? REPEAT_WARNING_LIKE_AAA
                        : REPEAT_WARNING_LIKE_ABCABCABC,
                        EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS
                );
            case Sequence:
                return new Feedback(SEQUENCE_WARNING_LIKE_ABCOR6543,
                        EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES
                );
            case Regex:
                return new Feedback("recent_year".equals(match.regexName)
                        ? REGEX_WARNING_RECENT_YEARS
                        : null,
                        EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        REGEX_SUGGESTIONS_AVOID_RECENT_YEARS
                );
            case Date:
                return new Feedback(
                        DATE_WARNING_DATES,
                        EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        DATE_SUGGESTIONS_AVOID_DATES
                );
            default:
                return getFeedbackWithoutWarnings(EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD);
        }
    }

    private static Feedback getDictionaryMatchFeedback(Match match, boolean isSoleMatch) {
        String warning = null;
        if ("passwords".equals(match.dictionaryName)) {
            if (isSoleMatch && !match.l33t && !match.reversed) {
                if (match.rank <= 10) {
                    warning = DICTIONARY_WARNING_PASSWORDS_TOP10;
                } else if (match.rank <= 100) {
                    warning = DICTIONARY_WARNING_PASSWORDS_TOP100;
                } else {
                    warning = DICTIONARY_WARNING_PASSWORDS_VERY_COMMON;
                }
            } else if (match.guessesLog10 <= 4) {
                warning = DICTIONARY_WARNING_PASSWORDS_SIMILAR;
            }
        } else if ("english_wikipedia".equals(match.dictionaryName)) {
            if (isSoleMatch) {
                warning = DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF;
            }
        } else if (Arrays.asList(new String[]{"surnames", "male_names", "female_names"}).contains(match.dictionaryName)) {
            if (isSoleMatch) {
                warning = DICTIONARY_WARNING_ETC_NAMES_THEMSELVES;
            } else {
                warning = DICTIONARY_WARNING_ETC_NAMES_COMMON;
            }
        }

        List<String> suggestions = new ArrayList<>();
        suggestions.add(EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD);

        String word = match.token;
        if (DictionaryGuess.START_UPPER.matcher(word).find()) {
            suggestions.add(DICTIONARY_SUGGESTIONS_CAPITALIZATION);
        } else if (DictionaryGuess.ALL_UPPER.matcher(word).find() && !word.toLowerCase().equals(word)) {
            suggestions.add(DICTIONARY_SUGGESTIONS_ALL_UPPERCASE);
        }
        if (match.reversed && match.token.length() >= 4) {
            suggestions.add(DICTIONARY_SUGGESTIONS_REVERSED);
        }
        if (match.l33t) {
            suggestions.add(DICTIONARY_SUGGESTIONS_L33T);
        }
        return new Feedback(warning, suggestions.toArray(new String[suggestions.size()]));
    }

    private static class ResourceBundleFeedback extends Feedback {
        private ResourceBundle messages;

        private ResourceBundleFeedback(ResourceBundle messages, String warning, String... suggestions) {
            super(warning, suggestions);
            this.messages = messages;
        }

        @Override
        protected ResourceBundle resolveResourceBundle(Locale locale) {
            return messages;
        }
    }
}
