package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.*;
import com.nulabinc.zxcvbn.matchers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ScoringTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private final double EPSILON = 1e-10; // truncate to 10th decimal place

    private double truncateFloat(double d) { return  Math.round(d / EPSILON) * EPSILON; }

    private void approxEqual(double actual, double expected, String msg) {
        assertEquals(msg, truncateFloat(actual), truncateFloat(expected), 0.0);
    }

    @Test
    public void testNck() throws Exception {
        BaseGuess obj = new BaseGuess() {
            @Override
            public double exec(Match match) {
                return 0;
            }
        };
        Method method = BaseGuess.class.getDeclaredMethod("nCk", int.class, int.class);
        method.setAccessible(true);
        for(Integer[] param: new Integer[][] {
                { 0,  0, 1 },
                { 1,  0, 1 },
                { 5,  0, 1 },
                { 0,  1, 0 },
                { 0,  5, 0 },
                { 2,  1, 2 },
                { 4,  2, 6 },
                { 33, 7, 4272048 }
        }) {
            int n = param[0];
            int k = param[1];
            int result = param[2];
            String msg = String.format("nCk(%s, %s) == %s", n, k, result);
            assertEquals(msg, result, method.invoke(obj, n, k));
        }
    }

    @Test
    public void testCalcGuesses() throws Exception {
        {
            Match match = new Match.Builder(Pattern.Dictionary, 0, 8, "password").guesses(1.0).build();
            String msg = "estimate_guesses returns cached guesses when available";
            assertEquals(msg, new EstimateGuess("password").exec(match), 1, 0.0);
        }
        {
            Match match = MatchFactory.createDateMatch(0, 0, "1977", "/", 1977, 7, 14);
            String msg = msg = "estimate_guesses delegates based on pattern";
            assertEquals(msg, new EstimateGuess("1977").exec(match), new DateGuess().exec(match), 0.0);
        }
    }

    @Test
    public void  testRepeatGuesses() throws Exception {
        for(String[] param: new String[][]{
                { "aa", "a", "2" },
                { "999", "9",  "3" },
                { "$$$$", "$",  "4"},
                { "abab", "ab", "2"},
                { "batterystaplebatterystaplebatterystaple", "batterystaple", "3"}
        }) {
            String token = param[0];
            String baseToken = param[1];
            int repeatCount = Integer.valueOf(param[2]);
            double baseGuesses = Scoring.mostGuessableMatchSequence(
                    baseToken, new Matching().omnimatch(baseToken)).getGuesses();
            Match match = new Match.Builder(Pattern.Date, 0, 0, token)
                    .baseToken(baseToken)
                    .baseGuesses(baseGuesses)
                    .repeatCount(repeatCount)
                    .build();
            double expectedGuesses = baseGuesses * repeatCount;
            String msg = String.format("the repeat pattern '%s' has guesses of %s", token, expectedGuesses);
            assertEquals(msg, expectedGuesses, new RepeatGuess().exec(match), 0.0);
        }
    }

    @Test
    public void  testSequenceGuesses() throws Exception {
        for (String[] param: new String[][] {
                { "ab",   "true",  String.valueOf(4 * 2) },
                { "XYZ",  "true",  String.valueOf(26 * 3) },
                { "4567", "true",  String.valueOf(10 * 4) },
                { "7654", "false", String.valueOf(10 * 4 * 2) },
                { "ZYX",  "false", String.valueOf(4 * 3 * 2) }
        }) {
            String token = param[0];
            boolean ascending = Boolean.valueOf(param[1]);
            int guesses = Integer.valueOf(param[2]);
            Match match = new Match.Builder(Pattern.Sequence, 0, 0, token).ascending(ascending).build();
            String msg = String.format("the sequence pattern '%s' has guesses of %s", token, guesses);
            assertEquals(msg, guesses, new SequenceGuess().exec(match), 0.0);
        }
    }

    @Test
    public void  testDictionaryGuesses() throws Exception {
        {
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "aaaa").rank(32).build();
            String msg = "base guesses == the rank";
            assertEquals(msg, 32, new DictionaryGuess().exec(match), 0.0);
        }
        {
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "AAAaaa").rank(32).build();
            String msg = "extra guesses are added for capitalization";
            assertEquals(msg, 32 * new DictionaryGuess().uppercaseVariations(match), new DictionaryGuess().exec(match), 0.0);
        }
        {
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "aaa").reversed(true).rank(32).build();
            String msg = "guesses are doubled when word is reversed";
            assertEquals(msg, 32 * 2, new DictionaryGuess().exec(match), 0.0);
        }
        {
            Map<Character, Character> sub = new HashMap<>();
            sub.put('@','a');
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "aaa@@@").sub(sub).l33t(true).rank(32).build();
            String msg = "extra guesses are added for common l33t substitutions";
            assertEquals(msg, 32 * new DictionaryGuess().l33tVariations(match), new DictionaryGuess().exec(match), 0.0);
        }
        {
            Map<Character, Character> sub = new HashMap<>();
            sub.put('@','a');
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "AaA@@@").sub(sub).l33t(true).rank(32).build();
            String msg = "extra guesses are added for both capitalization and common l33t substitutions";
            int expected = 32 * new DictionaryGuess().l33tVariations(match) * new DictionaryGuess().uppercaseVariations(match);
            assertEquals(msg, expected, new DictionaryGuess().exec(match), 0.0);
        }
    }

    @Test
    public void  testUppercaseVariants() throws Exception {
        BaseGuess baseGuess = new BaseGuess() {
            @Override
            public double exec(Match match) {
                return 0;
            }
        };
        Method method = BaseGuess.class.getDeclaredMethod("nCk", int.class, int.class);
        method.setAccessible(true);
        DictionaryGuess dictionaryGuess = new DictionaryGuess();
        Method uppercaseVariationsMethod = DictionaryGuess.class.getDeclaredMethod("uppercaseVariations", Match.class);
        uppercaseVariationsMethod.setAccessible(true);
        for (Object[] param : new Object[][]{
                { "", 0 },
                { "a", 1 },
                { "A", 2 },
                { "abcdef", 1 },
                { "Abcdef", 2 },
                { "abcdeF", 2 },
                { "ABCDEF", 2 },
                { "aBcdef", (int) method.invoke(baseGuess,6,1) },
                { "aBcDef", (int) method.invoke(baseGuess,6,1) + (int) method.invoke(baseGuess,6,2) },
                { "ABCDEf", (int) method.invoke(baseGuess,6,1) },
                { "aBCDEf", (int) method.invoke(baseGuess,6,1) + (int) method.invoke(baseGuess,6,2) },
                { "ABCdef", (int) method.invoke(baseGuess,6,1) + (int) method.invoke(baseGuess,6,2) + (int) method.invoke(baseGuess,6,3) },
        }){
            String word = (String) param[0];
            int variants = (int) param[1];
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, word).sub(new HashMap<Character, Character>()).l33t(true).build();
            String msg = String.format("guess multiplier of %s is %s", word, variants);
            assertEquals(msg, variants, uppercaseVariationsMethod.invoke(dictionaryGuess, match));
        }
    }

    @Test
    public void  testL33tVariants() throws Exception {
        BaseGuess baseGuess = new BaseGuess() {
            @Override
            public double exec(Match match) {
                return 0;
            }
        };
        Method method = BaseGuess.class.getDeclaredMethod("nCk", int.class, int.class);
        method.setAccessible(true);
        {
            Match match = MatchFactory.createDictionaryMatch(0, 0, "", "", 0, "");
            assertEquals(
                    "1 variant for non-l33t matches",
                    1.0,
                    new DictionaryGuess().l33tVariations(match),
                    0.0);
        }
        {
            for (Object[] param : new Object[][]{
                    {"", 1, new HashMap<Character, Character>() {
                    }},
                    {"a", 1, new HashMap<Character, Character>() {
                    }},
                    {"4", 2, new HashMap<Character, Character>() {{
                        put('4', 'a');
                    }}},
                    {"4pple", 2, new HashMap<Character, Character>() {{
                        put('4', 'a');
                    }}},
                    {"abcet", 1, new HashMap<Character, Character>() {
                    }},
                    {"4bcet", 2, new HashMap<Character, Character>() {{
                        put('4', 'a');
                    }}},
                    {"a8cet", 2, new HashMap<Character, Character>() {{
                        put('8', 'b');
                    }}},
                    {"abce+", 2, new HashMap<Character, Character>() {{
                        put('+', 't');
                    }}},
                    {"48cet", 4, new HashMap<Character, Character>() {{
                        put('4', 'a');
                        put('8', 'b');
                    }}},
                    {"a4a4aa", (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1), new HashMap<Character, Character>() {{
                        put('4', 'a');
                    }}},
                    {"4a4a44", (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1), new HashMap<Character, Character>() {{
                        put('4', 'a');
                    }}},
                    {"a44att+",
                            ((int) method.invoke(baseGuess, 4, 2) + (int) method.invoke(baseGuess, 4, 1)) * (int) method.invoke(baseGuess, 3, 1),
                            new HashMap<Character, Character>() {{
                                put('4', 'a');
                                put('+', 't');
                            }}}
            }) {
                String word = (String) param[0];
                int variants = (int) param[1];
                Map<Character, Character> sub = (Map<Character, Character>) param[2];
                Match match = new Match.Builder(Pattern.Dictionary, 0, 0, word).sub(sub).l33t(!sub.isEmpty()).build();
                String msg = String.format("extra l33t guesses of %s is %s", word, variants);
                assertEquals(msg, variants, new DictionaryGuess().l33tVariations(match));
            }
        }
        {
            Map<Character, Character> sub = new HashMap<>();
            sub.put('4','a');
            Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "Aa44aA").sub(sub).l33t(true).build();
            int variants = (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1);
            String msg = "capitalization doesn't affect extra l33t guesses calc";
            assertEquals(msg, variants, new DictionaryGuess().l33tVariations(match));
        }
    }
}
