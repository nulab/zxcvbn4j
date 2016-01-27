package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.DictionaryGuess;
import com.nulabinc.zxcvbn.matchers.Match;

import java.util.*;

public class Feedback {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("com/nulabinc/zxcvbn/messages");

    public static final Message DEFAULT_SUGGESTIONS_USE_FEW_WORDS = new Message("feedback.default.suggestions.useFewWords");
    public static final Message DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS = new Message("feedback.default.suggestions.noNeedSymbols");
    public static final Message EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD = new Message("feedback.extra.suggestions.addAnotherWord");
    public static final Message SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS = new Message("feedback.spatial.warning.straightRowsOfKeys");
    public static final Message SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS = new Message("feedback.spatial.warning.shortKeyboardPatterns");
    public static final Message SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN = new Message("feedback.spatial.suggestions.UseLongerKeyboardPattern");
    public static final Message REPEAT_WARNING_LIKE_AAA = new Message("feedback.repeat.warning.likeAAA");
    public static final Message REPEAT_WARNING_LIKE_ABCABCABC = new Message("feedback.repeat.warning.likeABCABCABC");
    public static final Message REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS = new Message("feedback.repeat.suggestions.avoidRepeatedWords");
    public static final Message SEQUENCE_WARNING_LIKE_ABCOR6543 = new Message("feedback.sequence.warning.likeABCor6543");
    public static final Message SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES = new Message("feedback.sequence.suggestions.avoidSequences");
    public static final Message REGEX_WARNING_RECENT_YEARS = new Message("feedback.regex.warning.recentYears");
    public static final Message REGEX_SUGGESTIONS_AVOID_RECENT_YEARS = new Message("feedback.regex.suggestions.avoidRecentYears");
    public static final Message DATE_WARNING_DATES = new Message("feedback.date.warning.dates");
    public static final Message DATE_SUGGESTIONS_AVOID_DATES = new Message("feedback.date.suggestions.avoidDates");
    public static final Message DICTIONARY_WARNING_PASSWORDS_TOP10 = new Message("feedback.dictionary.warning.passwords.top10");
    public static final Message DICTIONARY_WARNING_PASSWORDS_TOP100 = new Message("feedback.dictionary.warning.passwords.top100");
    public static final Message DICTIONARY_WARNING_PASSWORDS_VERY_COMMON = new Message("feedback.dictionary.warning.passwords.veryCommon");
    public static final Message DICTIONARY_WARNING_PASSWORDS_SIMILAR = new Message("feedback.dictionary.warning.passwords.similar");
    public static final Message DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF = new Message("feedback.dictionary.warning.englishWikipedia.itself");
    public static final Message DICTIONARY_WARNING_ETC_NAMES_THEMSELVES = new Message("feedback.dictionary.warning.etc.namesThemselves");
    public static final Message DICTIONARY_WARNING_ETC_NAMES_COMMON = new Message("feedback.dictionary.warning.etc.namesCommon");
    public static final Message DICTIONARY_SUGGESTIONS_CAPITALIZATION = new Message("feedback.dictionary.suggestions.capitalization");
    public static final Message DICTIONARY_SUGGESTIONS_ALL_UPPERCASE = new Message("feedback.dictionary.suggestions.allUppercase");
    public static final Message DICTIONARY_SUGGESTIONS_REVERSED = new Message("feedback.dictionary.suggestions.reversed");
    public static final Message DICTIONARY_SUGGESTIONS_L33T = new Message("feedback.dictionary.suggestions.l33t");

    private ResourceBundle messages;
    private Message warning;
    private Message[] suggestions;

    private Feedback(Message warning, Message... suggestions) {
        this.messages = MESSAGES;
        this.warning = warning;
        this.suggestions = suggestions;
    }

    public String getWarning() {
        return warning != null ? warning.getLocalized(messages) : "";
    }

    public Message getWarningMessage() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = new Message(warning);
    }

    public List<String> getSuggestions() {
        List<String> suggestions;
        if (this.suggestions != null) {
            suggestions = new ArrayList<>(this.suggestions.length);
            for (Message suggestion : this.suggestions) {
                suggestions.add(suggestion.getLocalized(messages));
            }
        } else {
            suggestions = Collections.emptyList();
        }
        return suggestions;
    }

    public Message[] getSuggestionMessages() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        if (suggestions != null) {
            this.suggestions = new Message[suggestions.size()];
            for (int i = 0; i < suggestions.size(); i++) {
                String suggestion = suggestions.get(i);
                this.suggestions[i] = new Message(suggestion);
            }
        } else {
            this.suggestions = null;
        }
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

    private static Feedback getFeedbackWithoutWarnings(Message... suggestions) {
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
        Message warning = null;
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

        List<Message> suggestions = new ArrayList<>();
        suggestions.add(EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD);

        String word = match.token;
        if (DictionaryGuess.START_UPPER.matcher(word).find()) {
            suggestions.add(DICTIONARY_SUGGESTIONS_CAPITALIZATION);
        } else if (DictionaryGuess.ALL_UPPER.matcher(word).find()) {
            suggestions.add(DICTIONARY_SUGGESTIONS_ALL_UPPERCASE);
        }
        if (match.reversed && match.token.length() >= 4) {
            suggestions.add(DICTIONARY_SUGGESTIONS_REVERSED);
        }
        if (match.l33t) {
            suggestions.add(DICTIONARY_SUGGESTIONS_L33T);
        }
        return new Feedback(warning, suggestions.toArray(new Message[suggestions.size()]));
    }

    public static class Message {
        final private String id;

        private Message(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getLocalized(ResourceBundle messages) {
            return messages.getString(id);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": " + id;
        }
    }
}
