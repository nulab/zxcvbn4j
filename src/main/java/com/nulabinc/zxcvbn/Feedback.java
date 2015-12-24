package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.DictionaryGuess;
import com.nulabinc.zxcvbn.matchers.Match;

import java.util.*;

public class Feedback {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("com/nulabinc/zxcvbn/messages");

    private String warning;
    private List<String> suggestions;

    private Feedback(String warning, List<String> suggestions) {
        this.warning = warning;
        if (suggestions == null) suggestions = new ArrayList<>();
        this.suggestions = suggestions;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    static Feedback getFeedback(int score, List<Match> sequence) {
        if (sequence.size() == 0) {
            return new Feedback("", new ArrayList<String>() {
                {
                    add(MESSAGES.getString("feedback.default.suggestions.useFewWords"));
                    add(MESSAGES.getString("feedback.default.suggestions.noNeedSymbols"));
                }
            });
        }
        if (score > 2) return new Feedback("", new ArrayList<String>());
        Match longestMatch = sequence.get(0);
        if (sequence.size() > 1) {
            for (Match match: sequence.subList(1, sequence.size() - 1)) {
                if (match.token.length() > longestMatch.token.length()) longestMatch = match;
            }
        }
        Feedback feedback = getMatchFeedback(longestMatch, sequence.size() == 1);
        final String extraFeedback = MESSAGES.getString("feedback.extra.suggestions.addAnotherWord");
        if (feedback != null) {
            feedback.suggestions.add(0, extraFeedback);
        } else {
            feedback = new Feedback("", new ArrayList<String>() {
                {
                    add(extraFeedback);
                }
            });
        }
        return feedback;
    };

    private static Feedback getMatchFeedback(Match match, boolean isSoleMatch) {
        switch (match.pattern) {
            case Dictionary:
                return getDictionaryMatchFeedback(match, isSoleMatch);
            case Spatial:
                return new Feedback(match.turns == 1
                                ? MESSAGES.getString("feedback.spatial.warning.straightRowsOfKeys")
                                : MESSAGES.getString("feedback.spatial.warning.shortKeyboardPatterns"),
                        new ArrayList<String>() {{ add(MESSAGES.getString("feedback.spatial.suggestions.UseLongerKeyboardPattern")); }}
                );
            case Repeat:
                return new Feedback(match.baseToken.length() == 1
                                ? MESSAGES.getString("feedback.repeat.warning.likeAAA")
                                : MESSAGES.getString("feedback.repeat.warning.likeABCABCABC"),
                        new ArrayList<String>() { { add(MESSAGES.getString("feedback.repeat.suggestions.avoidRepeatedWords")); } }
                );
            case Sequence:
                return new Feedback(MESSAGES.getString("feedback.sequence.warning.likeABCor6543"),
                        new ArrayList<String>() { { add(MESSAGES.getString("feedback.sequence.suggestions.avoidSequences")); } }
                );
            case Regex:
                return new Feedback("recent_year".equals(match.regexName)
                                ? MESSAGES.getString("feedback.regex.warning.recentYears")
                                : "",
                        new ArrayList<String>() { { add(MESSAGES.getString("feedback.regex.suggestions.avoidRecentYears")); } }
                );
            case Date:
                return new Feedback(
                        MESSAGES.getString("feedback.date.warning.dates"),
                        new ArrayList<String>() { { add(MESSAGES.getString("feedback.date.suggestions.avoidDates")); } }
                );
            default:
                return null;
        }
    }

    private static Feedback getDictionaryMatchFeedback(Match match, boolean isSoleMatch) {
        String warning = "";
        if ("passwords".equals(match.dictionaryName)) {
            if (isSoleMatch && !match.l33t && !match.reversed) {
                if (match.rank <= 10) {
                    warning = MESSAGES.getString("feedback.dictionary.warning.passwords.top10");
                } else if (match.rank <= 100) {
                    warning = MESSAGES.getString("feedback.dictionary.warning.passwords.top100");
                } else {
                    warning = MESSAGES.getString("feedback.dictionary.warning.passwords.veryCommon");
                }
            } else if (match.guessesLog10 <= 4) {
                warning = MESSAGES.getString("feedback.dictionary.warning.passwords.similar");
            }
        } else if ("english_wikipedia".equals(match.dictionaryName)) {
            if (isSoleMatch) {
                warning = MESSAGES.getString("feedback.dictionary.warning.englishWikipedia.itself");
            }
        } else if (Arrays.asList(new String[]{"surnames", "male_names", "female_names"}).contains(match.dictionaryName)) {
            if (isSoleMatch) {
                warning = MESSAGES.getString("feedback.dictionary.warning.etc.namesThemselves");
            } else {
                warning = MESSAGES.getString("feedback.dictionary.warning.etc.namesCommon");
            }
        } else {
            warning = "";
        }
        List<String> suggestions = new ArrayList<>();
        String word = match.token;
        if (DictionaryGuess.START_UPPER.matcher(word).find()) {
            suggestions.add(MESSAGES.getString("feedback.dictionary.suggestions.capitalization"));
        } else if (DictionaryGuess.ALL_UPPER.matcher(word).find()) {
            suggestions.add(MESSAGES.getString("feedback.dictionary.suggestions.allUppercase"));
        }
        if (match.reversed && match.token.length() >= 4) {
            suggestions.add(MESSAGES.getString("feedback.dictionary.suggestions.reversed"));
        }
        if (match.l33t) {
            suggestions.add(MESSAGES.getString("feedback.dictionary.suggestions.l33t"));
        }
        return new Feedback(warning, suggestions);
    }

}
