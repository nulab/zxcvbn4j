package com.nulabinc.zxcvbn;

import static org.junit.Assert.assertEquals;

import com.nulabinc.zxcvbn.guesses.BaseGuess;
import com.nulabinc.zxcvbn.guesses.DateGuess;
import com.nulabinc.zxcvbn.guesses.DictionaryGuess;
import com.nulabinc.zxcvbn.guesses.EstimateGuess;
import com.nulabinc.zxcvbn.guesses.RepeatGuess;
import com.nulabinc.zxcvbn.guesses.SequenceGuess;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.MatchFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Enclosed.class)
public class ScoringTest {

  @RunWith(Parameterized.class)
  public static class NckTest {
    private final int n;
    private final int k;
    private final int expected;

    public NckTest(int n, int k, int expected) {
      this.n = n;
      this.k = k;
      this.expected = expected;
    }

    @Test
    public void testNck() throws Exception {
      Context context = StandardContext.build();
      BaseGuess obj =
          new BaseGuess(context) {
            @Override
            public double exec(Match match) {
              return 0;
            }
          };
      Method method =
          BaseGuess.class.getDeclaredMethod("calculateBinomialCoefficient", int.class, int.class);
      method.setAccessible(true);

      String msg = String.format("nCk(%s, %s) == %s", n, k, expected);
      assertEquals(msg, expected, method.invoke(obj, n, k));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
      return Arrays.asList(
          new Object[][] {
            {0, 0, 1},
            {1, 0, 1},
            {5, 0, 1},
            {0, 1, 0},
            {0, 5, 0},
            {2, 1, 2},
            {4, 2, 6},
            {33, 7, 4272048}
          });
    }
  }

  @RunWith(Parameterized.class)
  public static class RepeatGuessesTest {
    private final String token;
    private final String baseToken;
    private final int repeatCount;

    public RepeatGuessesTest(String token, String baseToken, int repeatCount) {
      this.token = token;
      this.baseToken = baseToken;
      this.repeatCount = repeatCount;
    }

    @Test
    public void testRepeatGuesses() throws Exception {
      Context context = StandardContext.build();
      Scoring scoring = new Scoring(context);
      double baseGuesses =
          scoring
              .mostGuessableMatchSequence(
                  baseToken,
                  new Matching(context, Collections.<String>emptyList()).omnimatch(baseToken))
              .getGuesses();
      Match match =
          new Match.Builder(Pattern.Repeat, 0, 0, token)
              .baseToken(baseToken)
              .baseGuesses(baseGuesses)
              .repeatCount(repeatCount)
              .build();
      double expectedGuesses = baseGuesses * repeatCount;
      String msg =
          String.format("the repeat pattern '%s' has guesses of %s", token, expectedGuesses);
      assertEquals(msg, expectedGuesses, new RepeatGuess(context).exec(match), 0.0);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
      return Arrays.asList(
          new Object[][] {
            {"aa", "a", 2},
            {"999", "9", 3},
            {"$$$$", "$", 4},
            {"abab", "ab", 2},
            {"batterystaplebatterystaplebatterystaple", "batterystaple", 3}
          });
    }
  }

  @RunWith(Parameterized.class)
  public static class SequenceGuessesTest {
    private final String token;
    private final boolean ascending;
    private final int expectedGuesses;

    public SequenceGuessesTest(String token, boolean ascending, int expectedGuesses) {
      this.token = token;
      this.ascending = ascending;
      this.expectedGuesses = expectedGuesses;
    }

    @Test
    public void testSequenceGuesses() throws Exception {
      Context context = StandardContext.build();
      Match match = new Match.Builder(Pattern.Sequence, 0, 0, token).ascending(ascending).build();
      String msg =
          String.format("the sequence pattern '%s' has guesses of %s", token, expectedGuesses);
      assertEquals(msg, expectedGuesses, new SequenceGuess(context).exec(match), 0.0);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
      return Arrays.asList(
          new Object[][] {
            {"ab", true, 4 * 2},
            {"XYZ", true, 26 * 3},
            {"4567", true, 10 * 4},
            {"7654", false, 10 * 4 * 2},
            {"ZYX", false, 4 * 3 * 2}
          });
    }
  }

  public static class DictionaryGuessesTest {

    @Test
    public void testDictionaryGuessesSameWithRank() throws Exception {
      Context context = StandardContext.build();
      Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "aaaa").rank(32).build();
      String msg = "base guesses == the rank";
      assertEquals(msg, 32, new DictionaryGuess(context).exec(match), 0.0);
    }

    @Test
    public void testDictionaryGuessesCapitalization() throws Exception {
      Context context = StandardContext.build();
      Match match = new Match.Builder(Pattern.Dictionary, 0, 0, "AAAaaa").rank(32).build();
      String msg = "extra guesses are added for capitalization";
      assertEquals(
          msg,
          32 * new DictionaryGuess(context).uppercaseVariations(match),
          new DictionaryGuess(context).exec(match),
          0.0);
    }

    @Test
    public void testDictionaryGuessesReverse() throws Exception {
      Context context = StandardContext.build();
      Match match =
          new Match.Builder(Pattern.Dictionary, 0, 0, "aaa").reversed(true).rank(32).build();
      String msg = "guesses are doubled when word is reversed";
      assertEquals(msg, 32 * 2, new DictionaryGuess(context).exec(match), 0.0);
    }

    @Test
    public void testDictionaryGuesses133t() throws Exception {
      Context context = StandardContext.build();
      Map<Character, Character> sub = new HashMap<>();
      sub.put('@', 'a');
      Match match =
          new Match.Builder(Pattern.Dictionary, 0, 0, "aaa@@@")
              .sub(sub)
              .l33t(true)
              .rank(32)
              .build();
      String msg = "extra guesses are added for common l33t substitutions";
      assertEquals(
          msg,
          32 * new DictionaryGuess(context).l33tVariations(match),
          new DictionaryGuess(context).exec(match),
          0.0);
    }

    @Test
    public void testDictionaryGuessesMixed() throws Exception {
      Context context = StandardContext.build();
      Map<Character, Character> sub = new HashMap<>();
      sub.put('@', 'a');
      Match match =
          new Match.Builder(Pattern.Dictionary, 0, 0, "AaA@@@")
              .sub(sub)
              .l33t(true)
              .rank(32)
              .build();
      String msg = "extra guesses are added for both capitalization and common l33t substitutions";
      int expected =
          32
              * new DictionaryGuess(context).l33tVariations(match)
              * new DictionaryGuess(context).uppercaseVariations(match);
      assertEquals(msg, expected, new DictionaryGuess(context).exec(match), 0.0);
    }
  }

  @RunWith(Parameterized.class)
  public static class UppercaseVariantsTest {
    private final String word;
    private final int variants;

    public UppercaseVariantsTest(String word, int variants) {
      this.word = word;
      this.variants = variants;
    }

    @Test
    public void testUppercaseVariants() throws Exception {
      Context context = StandardContext.build();
      DictionaryGuess dictionaryGuess = new DictionaryGuess(context);
      Method uppercaseVariationsMethod =
          DictionaryGuess.class.getDeclaredMethod("uppercaseVariations", Match.class);
      uppercaseVariationsMethod.setAccessible(true);

      Match match =
          new Match.Builder(Pattern.Dictionary, 0, 0, word)
              .sub(new HashMap<Character, Character>())
              .l33t(true)
              .build();
      String msg = String.format("guess multiplier of %s is %s", word, variants);
      assertEquals(msg, variants, uppercaseVariationsMethod.invoke(dictionaryGuess, match));
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws Exception {
      Context context = StandardContext.build();
      BaseGuess baseGuess =
          new BaseGuess(context) {
            @Override
            public double exec(Match match) {
              return 0;
            }
          };
      Method method =
          BaseGuess.class.getDeclaredMethod("calculateBinomialCoefficient", int.class, int.class);
      method.setAccessible(true);
      return Arrays.asList(
          new Object[][] {
            {"", 1},
            {"a", 1},
            {"A", 2},
            {"abcdef", 1},
            {"Abcdef", 2},
            {"abcdeF", 2},
            {"ABCDEF", 2},
            {"aBcdef", method.invoke(baseGuess, 6, 1)},
            {"aBcDef", (int) method.invoke(baseGuess, 6, 1) + (int) method.invoke(baseGuess, 6, 2)},
            {"ABCDEf", method.invoke(baseGuess, 6, 1)},
            {"aBCDEf", (int) method.invoke(baseGuess, 6, 1) + (int) method.invoke(baseGuess, 6, 2)},
            {
              "ABCdef",
              (int) method.invoke(baseGuess, 6, 1)
                  + (int) method.invoke(baseGuess, 6, 2)
                  + (int) method.invoke(baseGuess, 6, 3)
            },
          });
    }
  }

  @RunWith(Parameterized.class)
  public static class L33tVariantsTest {
    private final String word;
    private final int variants;
    private final Map<Character, Character> sub;

    public L33tVariantsTest(String word, int variants, Map<Character, Character> sub) {
      this.word = word;
      this.variants = variants;
      this.sub = sub;
    }

    @Test
    public void testL33tVariants() throws Exception {
      Context context = StandardContext.build();
      Match match =
          new Match.Builder(Pattern.Dictionary, 0, 0, word).sub(sub).l33t(!sub.isEmpty()).build();
      String msg = String.format("extra l33t guesses of %s is %s", word, variants);
      assertEquals(msg, variants, new DictionaryGuess(context).l33tVariations(match));
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws Exception {
      Context context = StandardContext.build();
      BaseGuess baseGuess =
          new BaseGuess(context) {
            @Override
            public double exec(Match match) {
              return 0;
            }
          };
      Method method =
          BaseGuess.class.getDeclaredMethod("calculateBinomialCoefficient", int.class, int.class);
      method.setAccessible(true);

      return Arrays.asList(
          new Object[][] {
            {"", 1, Collections.emptyMap()},
            {"a", 1, Collections.emptyMap()},
            {"4", 2, Collections.singletonMap('4', 'a')},
            {"4pple", 2, Collections.singletonMap('4', 'a')},
            {"abcet", 1, Collections.emptyMap()},
            {"4bcet", 2, Collections.singletonMap('4', 'a')},
            {"a8cet", 2, Collections.singletonMap('8', 'b')},
            {"abce+", 2, Collections.singletonMap('+', 't')},
            {
              "48cet",
              4,
              new HashMap<Character, Character>() {
                {
                  put('4', 'a');
                  put('8', 'b');
                }
              }
            },
            {
              "a4a4aa",
              (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1),
              Collections.singletonMap('4', 'a')
            },
            {
              "4a4a44",
              (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1),
              Collections.singletonMap('4', 'a')
            },
            {
              "a44att+",
              ((int) method.invoke(baseGuess, 4, 2) + (int) method.invoke(baseGuess, 4, 1))
                  * (int) method.invoke(baseGuess, 3, 1),
              new HashMap<Character, Character>() {
                {
                  put('4', 'a');
                  put('+', 't');
                }
              }
            },
            {
              "Aa44aA",
              (int) method.invoke(baseGuess, 6, 2) + (int) method.invoke(baseGuess, 6, 1),
              Collections.singletonMap('4', 'a')
            }
          });
    }
  }

  public static class RestScoringTest {
    @Test
    public void testCalcGuessesPassword() throws Exception {
      Context context = StandardContext.build();
      Match match = new Match.Builder(Pattern.Dictionary, 0, 8, "password").guesses(1.0).build();
      String msg = "estimate_guesses returns cached guesses when available";
      assertEquals(msg, 1, new EstimateGuess(context, "password").exec(match), 0.0);
    }

    @Test
    public void testCalcGuessesYear() throws Exception {
      Context context = StandardContext.build();
      Match match = MatchFactory.createDateMatch(0, 0, "1977", "/", 1977, 7, 14);
      String msg = "estimate_guesses delegates based on pattern";
      assertEquals(
          msg,
          new EstimateGuess(context, "1977").exec(match),
          new DateGuess(context).exec(match),
          0.0);
    }

    @Test
    public void testL33tVariants() throws Exception {
      Context context = StandardContext.build();
      Match match = MatchFactory.createDictionaryMatch(0, 0, "", "", 0, "");
      assertEquals(
          "1 variant for non-l33t matches",
          1.0,
          new DictionaryGuess(context).l33tVariations(match),
          0.0);
    }
  }

  @RunWith(Parameterized.class)
  public static class DateGuessTest {

    private final String token;
    private final int i;
    private final int j;
    private final String separator;
    private final int year;
    private final int month;
    private final int day;
    private final double expectedGuesses;

    public DateGuessTest(
        String token,
        int i,
        int j,
        String separator,
        int year,
        int month,
        int day,
        double expectedGuesses) {
      this.token = token;
      this.i = i;
      this.j = j;
      this.separator = separator;
      this.year = year;
      this.month = month;
      this.day = day;
      this.expectedGuesses = expectedGuesses;
    }

    @Test
    public void testDateGuess() throws Exception {
      Context context = StandardContext.build();
      Match match = MatchFactory.createDateMatch(i, j, token, separator, year, month, day);
      String msg = String.format("the date pattern '%s' has guesses of %s", token, expectedGuesses);
      assertEquals(msg, expectedGuesses, new DateGuess(context).exec(match), 0.0);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
      return Arrays.asList(
          new Object[][] {
            {"23525", 0, 4, "", 2025, 5, 23, 7300.0},
          });
    }
  }
}
