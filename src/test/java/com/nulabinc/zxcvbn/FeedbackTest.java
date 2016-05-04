package com.nulabinc.zxcvbn;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(Parameterized.class)
public class FeedbackTest {
    private String password;
    private String expectedWarning;
    private String[] expectedSuggestions;

    public FeedbackTest(String password, String expectedWarning, String[] expectedSuggestions) {
        this.password = password;
        this.expectedWarning = expectedWarning;
        this.expectedSuggestions = expectedSuggestions;
    }

    @Test
    public void testWarning() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback().withResourceBundle(null);

        Assert.assertEquals("Unexpected warning", expectedWarning, feedback.getWarning());
    }

    @Test
    public void testSuggestions() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback().withResourceBundle(null);

        Assert.assertArrayEquals("Unexpected suggestions", expectedSuggestions, feedback.getSuggestions().toArray());
    }

    @Test
    public void testLocalizedWarningWithResourceBundle() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(Feedback.BUNDLE_NAME);

        String expectedWarningL10n = expectedWarning.length() > 0 ? resourceBundle.getString(expectedWarning) : "";
        Assert.assertEquals("Unexpected warning", expectedWarningL10n, feedback.getWarning());
    }

    @Test
    public void testLocalizedSuggestionsWithResourceBundle() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(Feedback.BUNDLE_NAME);

        String[] expectedSuggestionsL10n = new String[expectedSuggestions.length];
        for (int i = 0; i < expectedSuggestions.length; i++) {
            String expectedSuggestion = expectedSuggestions[i];
            expectedSuggestionsL10n[i] = resourceBundle.getString(expectedSuggestion);
        }
        Assert.assertArrayEquals("Unexpected suggestions", expectedSuggestionsL10n, feedback.getSuggestions().toArray());
    }

    @Test
    public void testLocalizedWarningWithLocale() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();
        Feedback localizedFeedback = feedback.withLocale(Locale.JAPAN);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(Feedback.BUNDLE_NAME, Locale.JAPAN);
        String expectedWarningL10n = expectedWarning.length() > 0 ? resourceBundle.getString(expectedWarning) : "";
        System.out.println(localizedFeedback.getWarning());
        Assert.assertEquals("Unexpected warning", expectedWarningL10n, localizedFeedback.getWarning());
    }

    @Test
    public void testLocalizedSuggestionsWithLocale() {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();
        Feedback localizedFeedback = feedback.withLocale(Locale.JAPAN);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(Feedback.BUNDLE_NAME, Locale.JAPAN);

        String[] expectedSuggestionsL10n = new String[expectedSuggestions.length];
        for (int i = 0; i < expectedSuggestions.length; i++) {
            String expectedSuggestion = expectedSuggestions[i];
            expectedSuggestionsL10n[i] = resourceBundle.getString(expectedSuggestion);
        }
        Assert.assertArrayEquals("Unexpected suggestions", expectedSuggestionsL10n, localizedFeedback.getSuggestions().toArray());
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"bbb", Feedback.REPEAT_WARNING_LIKE_AAA, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS
                }},
                {"testtesttest", Feedback.REPEAT_WARNING_LIKE_ABCABCABC, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.REPEAT_SUGGESTIONS_AVOID_REPEATED_WORDS
                }},
                {"qwertyuiop", Feedback.SPATIAL_WARNING_STRAIGHT_ROWS_OF_KEYS, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN
                }},
                {"qwerfdsa", Feedback.SPATIAL_WARNING_SHORT_KEYBOARD_PATTERNS, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.SPATIAL_SUGGESTIONS_USE_LONGER_KEYBOARD_PATTERN
                }},
                {"justshort", "", new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"", "", new String[]{
                        Feedback.DEFAULT_SUGGESTIONS_USE_FEW_WORDS,
                        Feedback.DEFAULT_SUGGESTIONS_NO_NEED_SYMBOLS,
                }},
                {"efghijk", Feedback.SEQUENCE_WARNING_LIKE_ABCOR6543, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.SEQUENCE_SUGGESTIONS_AVOID_SEQUENCES
                }},
                {new SimpleDateFormat("yyyy").format(new Date()), Feedback.REGEX_WARNING_RECENT_YEARS, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.REGEX_SUGGESTIONS_AVOID_RECENT_YEARS
                }},
                {new SimpleDateFormat("dd-MM-yyyy").format(new Date()), Feedback.DATE_WARNING_DATES, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DATE_SUGGESTIONS_AVOID_DATES
                }},
                {"password", Feedback.DICTIONARY_WARNING_PASSWORDS_TOP10, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"access", Feedback.DICTIONARY_WARNING_PASSWORDS_TOP100, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"psychnaut1", Feedback.DICTIONARY_WARNING_PASSWORDS_VERY_COMMON, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"distinguished", Feedback.DICTIONARY_WARNING_ENGLISH_WIKIPEDIA_ITSELF, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"password8", Feedback.DICTIONARY_WARNING_PASSWORDS_SIMILAR, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"mitchell", Feedback.DICTIONARY_WARNING_ETC_NAMES_THEMSELVES, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD
                }},
                {"Mitchell1", Feedback.DICTIONARY_WARNING_ETC_NAMES_COMMON, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DICTIONARY_SUGGESTIONS_CAPITALIZATION,
                }},
                {"Password", Feedback.DICTIONARY_WARNING_PASSWORDS_TOP10, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DICTIONARY_SUGGESTIONS_CAPITALIZATION
                }},
                {"PASSWORD", Feedback.DICTIONARY_WARNING_PASSWORDS_TOP10, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DICTIONARY_SUGGESTIONS_ALL_UPPERCASE
                }},
                {"remmurd", Feedback.DICTIONARY_WARNING_PASSWORDS_SIMILAR, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DICTIONARY_SUGGESTIONS_REVERSED
                }},
                {"p@ssword", Feedback.DICTIONARY_WARNING_PASSWORDS_SIMILAR, new String[]{
                        Feedback.EXTRA_SUGGESTIONS_ADD_ANOTHER_WORD,
                        Feedback.DICTIONARY_SUGGESTIONS_L33T
                }},
                {"correcthorsebatterystaple", "", new String[0]}
        });
    }

}
