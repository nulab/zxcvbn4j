package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.DateMatcher;
import com.nulabinc.zxcvbn.matchers.DictionaryMatcher;
import com.nulabinc.zxcvbn.matchers.Keyboard;
import com.nulabinc.zxcvbn.matchers.L33tMatcher;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.RegexMatcher;
import com.nulabinc.zxcvbn.matchers.RepeatMatcher;
import com.nulabinc.zxcvbn.matchers.ReverseDictionaryMatcher;
import com.nulabinc.zxcvbn.matchers.SequenceMatcher;
import com.nulabinc.zxcvbn.matchers.SpatialMatcher;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class MatchingTest {

    private static void assertMatches(String prefix, Pattern expectedPattern, ExpectedMatch[] expectedMatches, List<Match> actualMatches) {
        String msg = String.format("%s: matches.length == %s", prefix, expectedMatches.length);
        assertEquals(msg, expectedMatches.length, actualMatches.size());
        for (int k = 0; k < expectedMatches.length; k++) {
            ExpectedMatch expectedMatch = expectedMatches[k];
            Match actualMatch = actualMatches.get(k);

            msg = String.format("%s: matches[%s].pattern == '%s'", prefix, k, expectedPattern);
            assertEquals(msg, expectedPattern, actualMatch.pattern);

            msg = String.format("%s: matches[%s] should start at %s", prefix, k, expectedMatch.start);
            assertEquals(msg, expectedMatch.start, actualMatch.i);

            msg = String.format("%s: matches[%s] should end at %s", prefix, k, expectedMatch.end);
            assertEquals(msg, expectedMatch.end, actualMatch.j);

            msg = String.format("%s: matches[%s].token == '%s'", prefix, k, expectedMatch.token);
            assertEquals(msg, expectedMatch.token, actualMatch.token.toString());

            for (String fieldName : expectedMatch.fields.keySet()) {
                Object expectedValue = expectedMatch.fields.get(fieldName);
                Object actualValue;
                try {
                    actualValue = Match.class.getField(fieldName).get(actualMatch);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                msg = String.format("%s: matches[%s].%s == '%s'", prefix, k, fieldName, expectedValue);
                if ((expectedValue instanceof String) && (actualValue instanceof CharSequence)) {
                    // If comparing a CharSequence to a String, convert the CharSequence to a String first
                    // because String.equals doesn't consider it equal if it isn't a String itself.
                    assertEquals(msg, expectedValue, actualValue.toString());
                } else {
                    assertEquals(msg, expectedValue, actualValue);
                }
            }
        }
    }

    private static Map<String, Integer> dictionary(String... words) {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            dictionary.put(word, i + 1);
        }
        return dictionary;
    }

    @RunWith(Parameterized.class)
    public static class DictionaryMatchingTest {
        private final Map<String, Map<String, Integer>> dictionaries = new HashMap<>();
        private final String password;
        private final String message;
        private final ExpectedMatch[] expectedMatches;

        public DictionaryMatchingTest(String password, String message, ExpectedMatch[] expectedMatches) {
            this.password = password;
            this.message = message;
            this.expectedMatches = expectedMatches;
        }

        @Before
        public void setUp() throws Exception {
            dictionaries.put("d1", dictionary(
                    "motherboard",
                    "mother",
                    "board",
                    "abcd",
                    "cdef"
            ));
            dictionaries.put("d2", dictionary(
                    "z",
                    "8",
                    "99",
                    "$",
                    "asdf1234&*"
            ));
        }

        @Test
        public void testDictionaryMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new DictionaryMatcher(context, dictionaries).execute(password);
            assertMatches(message, Pattern.Dictionary, expectedMatches, actualMatches);
        }

        @Parameterized.Parameters(name = "\"{0}\": {1}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"motherboard", "matches words that contain other words", new ExpectedMatch[]{
                            new ExpectedMatch("mother", 0, 5).matchedWord("mother").dictionaryName("d1").rank(2),
                            new ExpectedMatch("motherboard", 0, 10).matchedWord("motherboard").dictionaryName("d1").rank(1),
                            new ExpectedMatch("board", 6, 10).matchedWord("board").dictionaryName("d1").rank(3)
                    }},
                    {"abcdef", "matches multiple words when they overlap", new ExpectedMatch[]{
                            new ExpectedMatch("abcd", 0, 3).matchedWord("abcd").dictionaryName("d1").rank(4),
                            new ExpectedMatch("cdef", 2, 5).matchedWord("cdef").dictionaryName("d1").rank(5)
                    }},
                    {"BoaRdZ", "ignores uppercasing", new ExpectedMatch[]{
                            new ExpectedMatch("BoaRd", 0, 4).matchedWord("board").dictionaryName("d1").rank(3),
                            new ExpectedMatch("Z", 5, 5).matchedWord("z").dictionaryName("d2").rank(1)
                    }}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class L33tMatchingTest {
        private final Map<Character, List<Character>> testTable = new HashMap<Character, List<Character>>() {{
            put('a', Arrays.asList('4', '@'));
            put('c', Arrays.asList('(', '{', '[', '<'));
            put('g', Arrays.asList('6', '9'));
            put('o', Arrays.asList('0'));
        }};

        private final String password;
        private final Map<Character, Character[]> expected;

        public L33tMatchingTest(String password, Map<Character, Character[]> expected) {
            this.password = password;
            this.expected = expected;
        }

        @Test
        public void testL33tMatching() throws IOException {
            Context context = new ZxcvbnBuilder().buildContext();
            String msg = "reduces l33t table to only the substitutions that a password might be employing";
            assertEquals(msg, expected.size(), new L33tMatcher(context, Collections.<String, Map<String, Integer>>emptyMap()).relevantL33tSubTable(password, testTable).size());
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"", new HashMap<Character, Character[]>()},
                    {"abcdefgo123578!#$&*)]}>", new HashMap<Character, Character[]>()},
                    {"a", new HashMap<Character, Character[]>()},
                    {"4", new HashMap<Character, Character[]>() {{
                        put('a', new Character[]{'4'});
                    }}},
                    {"4@", new HashMap<Character, Character[]>() {{
                        put('a', new Character[]{'4', '@'});
                    }}},
                    {"4({60", new HashMap<Character, Character[]>() {{
                        put('a', new Character[]{'4'});
                        put('c', new Character[]{'(', '{'});
                        put('g', new Character[]{'6'});
                        put('o', new Character[]{'0'});
                    }}}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class SpatialMatchingSimpleTest {
        final String password;

        public SpatialMatchingSimpleTest(String password) {
            this.password = password;
        }

        @Test
        public void testSpatialMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            String msg = "doesn't match 1- and 2-character spatial patterns";
            assertEquals(msg, 0, new SpatialMatcher(context).execute(password).size());
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {""},
                    {"/"},
                    {"qw"},
                    {"*/"}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class SpatialMatchingTest {
        final String token;
        final Keyboard keyboard;
        final int turns;
        final int shifts;

        public SpatialMatchingTest(String token, Keyboard keyboard, int turns, int shifts) {
            this.token = token;
            this.keyboard = keyboard;
            this.turns = turns;
            this.shifts = shifts;
        }

        @Test
        public void testSpatialMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new SpatialMatcher(context, Collections.singletonMap(keyboard.getName(), keyboard)).execute(token);
            String msg = String.format("matches %s as a %s token", token, keyboard);
            assertMatches(msg, Pattern.Spatial, new ExpectedMatch[]{
                            new ExpectedMatch(token).graph(keyboard.getName()).turns(turns).shiftedCount(shifts)},
                    actualMatches);
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() throws IOException {
            final Keyboard qwerty = StandardKeyboards.QWERTY_LOADER.load();
            final Keyboard keypad = StandardKeyboards.KEYPAD_LOADER.load();
            final Keyboard macKeypad = StandardKeyboards.MAC_KEYPAD_LOADER.load();
            final Keyboard dvorak = StandardKeyboards.DVORAK_LOADER.load();
            return Arrays.asList(new Object[][]{
                    {"12345", qwerty, 1, 0},
                    {"@WSX", qwerty, 1, 4},
                    {"6tfGHJ", qwerty, 2, 3},
                    {"hGFd", qwerty, 0, 2},
                    {"/;p09876yhn", qwerty, 3, 0},
                    {"Xdr%", qwerty, 1, 2},
                    {"159-", keypad, 1, 0},
                    {"*84", keypad, 1, 0},
                    {"/8520", keypad, 1, 0},
                    {"369", keypad, 1, 0},
                    {"/963.", macKeypad, 1, 0},
                    {"*-632.0214", macKeypad, 9, 0},
                    {"aoEP%yIxkjq:", dvorak, 4, 5},
                    {";qoaOQ:Aoq;a", dvorak, 11, 4}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class SequenceMatchingSimpleTest {
        private final String password;

        public SequenceMatchingSimpleTest(String password) {
            this.password = password;
        }

        @Test
        public void testSequenceMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            String msg = String.format("doesn't match length-%s sequences", password.length());
            assertEquals(msg, new SequenceMatcher(context).execute(password).size(), 0);
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {""},
                    {"a"},
                    {"1"}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class RepeatMatchingSimpleTest {
        private final String password;
        private final int size;

        public RepeatMatchingSimpleTest(String password, int size) {
            this.password = password;
            this.size = size;
        }

        @Test
        public void testRepeatMatchingSimple() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            String msg = String.format("doesn't match length-%s repeat patterns", password.length());
            assertEquals(msg, new RepeatMatcher(context).execute(password).size(), size);
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"", 0},
                    {"#", 0},
                    {"abababababbbbbbbbbbbbb", 2},
                    {"abababababbbbbbbbbbbb", 2},
                    {"abababababbbbbbbbbbb", 2}

            });
        }
    }

    @RunWith(Parameterized.class)
    public static class RepeatMatchingTest {
        private final String password;
        private final ExpectedMatch expectedMatch;

        public RepeatMatchingTest(String password, ExpectedMatch expectedMatch) {
            this.password = password;
            this.expectedMatch = expectedMatch;
        }

        @Test
        public void testRepeatMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new RepeatMatcher(context).execute(password);
            assertMatches("matches embedded repeat patterns", Pattern.Repeat, new ExpectedMatch[]{expectedMatch}, actualMatches);
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            final String pattern = "&&&&&";

            List<String> prefixes = Arrays.asList("@", "y4@");
            List<String> suffices = Arrays.asList("u", "u%7");
            List<Object[]> result = new ArrayList<>();
            for (String prefix : prefixes) {
                for (String suffix : suffices) {
                    int i = prefix.length();
                    int j = prefix.length() + pattern.length() - 1;
                    String password = prefix + pattern + suffix;
                    result.add(new Object[]{password, new ExpectedMatch(pattern, i, j).baseToken("&")});
                }
            }
            return result;
        }
    }

    @RunWith(Parameterized.class)
    public static class DateMatchingTest {
        private final String password;
        private final String message;
        private final ExpectedMatch expectedMatch;

        public DateMatchingTest(String password, String message, ExpectedMatch expectedMatch) {
            this.password = password;
            this.message = message;
            this.expectedMatch = expectedMatch;
        }

        @Test
        public void testDateMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new DateMatcher(context).execute(password);
            assertMatches(message, Pattern.Date, new ExpectedMatch[]{expectedMatch}, actualMatches);
        }

        @Parameterized.Parameters(name = "{1}")
        public static Collection<Object[]> data() {
            List<Object[]> data = new ArrayList<>();

            for (final String separator : new String[]{"", " ", "-", "/", "\\", "_", "."}) {
                final String password = String.format("13%s2%s1921", separator, separator);
                data.add(new Object[]{
                        password,
                        String.format("matches dates that use '%s' as a separator", separator),
                        new ExpectedMatch(password).separator(separator).year(1921).month(2).day(13)
                });
            }

            for (final String order : new String[]{"mdy", "dmy", "ymd", "ydm"}) {
                final String password = order
                        .replace("y", "88")
                        .replace("m", "8")
                        .replace("d", "8");
                data.add(new Object[]{
                        password,
                        String.format("matches dates with '%s' format", order),
                        new ExpectedMatch(password).separator("").year(1988).month(8).day(8)
                });
            }

            data.add(new Object[]{
                    "111504",
                    "matches the date with year closest to REFERENCE_YEAR when ambiguous",
                    new ExpectedMatch("111504").separator("").year(2004).month(11).day(15)
            });

            return data;
        }
    }

    public static class RestMatchingTest {

        @Test
        public void testReverseDictionaryMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            ReverseDictionaryMatcher reverseDictionaryMatcher = new ReverseDictionaryMatcher(context, new HashMap<String, Map<String, Integer>>() {{
                put("d1", dictionary(
                        "123",
                        "321",
                        "456",
                        "654"
                ));
            }});

            String password = "0123456789";
            List<Match> actualMatches = reverseDictionaryMatcher.execute(password);

            ExpectedMatch[] expectedMatches = new ExpectedMatch[]{
                    new ExpectedMatch("123", 1, 3).matchedWord("321").reversed(true).dictionaryName("d1").rank(2),
                    new ExpectedMatch("456", 4, 6).matchedWord("654").reversed(true).dictionaryName("d1").rank(4)
            };
            assertMatches("matches against reversed words", Pattern.Dictionary, expectedMatches, actualMatches);
        }

        @Test
        public void testSpatialMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            final Keyboard keyboard = StandardKeyboards.QWERTY_LOADER.load();
            final String token = "6tfGHJ";
            List<Match> actualMatches = new SpatialMatcher(context, Collections.singletonMap(keyboard.getName(), keyboard))
                    .execute("rz!" + token + "%z");
            String msg = "matches against spatial patterns surrounded by non-spatial patterns";
            ExpectedMatch[] expectedMatches = new ExpectedMatch[]{
                    new ExpectedMatch(token, 3, 3 + token.length() - 1).graph(keyboard.getName()).turns(2).shiftedCount(3)
            };
            assertMatches(msg, Pattern.Spatial, expectedMatches, actualMatches);
        }

        @Test
        public void testSequenceMatching() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new SequenceMatcher(context).execute("abcbabc");
            ExpectedMatch[] expectedMatches = new ExpectedMatch[]{
                    new ExpectedMatch("abc", 0, 2).ascending(true),
                    new ExpectedMatch("cba", 2, 4).ascending(false),
                    new ExpectedMatch("abc", 4, 6).ascending(true)
            };
            assertMatches("matches overlapping patterns", Pattern.Sequence, expectedMatches, actualMatches);
        }

        @Test
        public void testRegexMatchingPastYear() throws Exception {
            testRegexMatching("1922");
        }

        @Test
        public void testRegexMatchingFutureYear() throws Exception {
            testRegexMatching("2017");
        }

        private void testRegexMatching(String year) throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            List<Match> actualMatches = new RegexMatcher(context).execute(year);
            assertMatches(
                    "matches " + year + " as a recent_year token",
                    Pattern.Regex,
                    new ExpectedMatch[]{new ExpectedMatch(year).regexName("recent_year")},
                    actualMatches);
        }

        @Test
        public void testOmnimatch() throws Exception {
            Context context = new ZxcvbnBuilder().buildContext();
            assertEquals(0, new Matching(context, new ArrayList<String>()).omnimatch("").size());
            String password = "r0sebudmaelstrom11/20/91aaaa";
            List<Match> matches = new Matching(context, new ArrayList<String>()).omnimatch(password);
            Map<Pattern, Integer[]> testMatches = new HashMap<>();
            //testMatches.put(Pattern.Dictionary, new Integer[]{0, 6});
            testMatches.put(Pattern.Dictionary, new Integer[]{7, 15});
            testMatches.put(Pattern.Date, new Integer[]{16, 23});
            testMatches.put(Pattern.Repeat, new Integer[]{24, 27});
            for (Map.Entry<Pattern, Integer[]> testMatch : testMatches.entrySet()) {
                Pattern patternName = testMatch.getKey();
                int i = testMatch.getValue()[0];
                int j = testMatch.getValue()[1];
                boolean included = false;
                for (Match match : matches) {
                    if (match.i == i && match.j == j && match.pattern == patternName) {
                        included = true;
                        break;
                    }
                }
                String msg = String.format("for %s, matches a %s token at [%s, %s]", password, patternName.value(), i, j);
                assertTrue(msg, included);
            }
        }
    }

    private static class ExpectedMatch {
        String token;
        int start;
        int end;
        Map<String, Object> fields = new HashMap<>();

        public ExpectedMatch(String token) {
            this(token, 0, token.length() - 1);
        }

        public ExpectedMatch(String token, int start, int end) {
            this.token = token;
            this.start = start;
            this.end = end;
        }

        public ExpectedMatch matchedWord(String matchedWord) {
            fields.put("matchedWord", matchedWord);
            return this;
        }

        public ExpectedMatch dictionaryName(String dictionaryName) {
            fields.put("dictionaryName", dictionaryName);
            return this;
        }

        public ExpectedMatch rank(int rank) {
            fields.put("rank", rank);
            return this;
        }

        public ExpectedMatch reversed(boolean reversed) {
            fields.put("reversed", reversed);
            return this;
        }

        public ExpectedMatch graph(String graph) {
            fields.put("graph", graph);
            return this;
        }

        public ExpectedMatch turns(int turns) {
            fields.put("turns", turns);
            return this;
        }

        public ExpectedMatch shiftedCount(int shiftedCount) {
            fields.put("shiftedCount", shiftedCount);
            return this;
        }

        public ExpectedMatch ascending(boolean ascending) {
            fields.put("ascending", ascending);
            return this;
        }

        public ExpectedMatch baseToken(String baseToken) {
            fields.put("baseToken", baseToken);
            return this;
        }

        public ExpectedMatch regexName(String regexName) {
            fields.put("regexName", regexName);
            return this;
        }

        public ExpectedMatch separator(String separator) {
            fields.put("separator", separator);
            return this;
        }

        public ExpectedMatch year(int year) {
            fields.put("year", year);
            return this;
        }

        public ExpectedMatch month(int month) {
            fields.put("month", month);
            return this;
        }

        public ExpectedMatch day(int day) {
            fields.put("day", day);
            return this;
        }
    }
}
