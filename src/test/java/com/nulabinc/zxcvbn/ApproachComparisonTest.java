package com.nulabinc.zxcvbn;

import static java.nio.CharBuffer.wrap;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * These tests compare the output from different approaches for calculating password strength.
 *
 * <p>The approaches include measuring String passwords with Java, measuring CharSequence passwords
 * in Java, and using the JavaScript version. The measure method with an empty list as the second
 * parameter is also compared. All versions should produce the same results.
 *
 * <p>The list of password to do the comparisons with is loaded from passwords.txt in the
 * test/resources folder.
 */
@RunWith(Parameterized.class)
public class ApproachComparisonTest {

  private static ScriptEngine engine;

  private final CharSequence password;

  private Strength charSequenceStrength;
  private Strength stringStrength;
  private Strength stringInputsStrength;
  private JavaScriptStrength jsStrength;

  public ApproachComparisonTest(CharSequence password) {
    this.password = wrap(password);

    Zxcvbn zxcvbn = new Zxcvbn();

    calculateAndRecordStrengthUsingAllMethods(password, zxcvbn);
  }

  private void calculateAndRecordStrengthUsingAllMethods(CharSequence password, Zxcvbn zxcvbn) {
    charSequenceStrength = zxcvbn.measure(new WipeableString(password));

    stringStrength = zxcvbn.measure(password.toString());

    stringInputsStrength = zxcvbn.measure(password, Collections.<String>emptyList());

    jsStrength = invokeJsVersion(password);
  }

  // =================================================================================//

  @Test
  public void keyValuesAreNotNull() {
    assertNotNull(password);
    assertNotNull(stringStrength);
    assertNotNull(charSequenceStrength);
    assertNotNull(stringInputsStrength);
    assertNotNull(jsStrength);
  }

  @Test
  public void passwordStrengthMatchesStringStrength() {
    assertEquals(stringStrength.getScore(), charSequenceStrength.getScore());
  }

  @Test
  public void passwordStrengthMatchesStringInputsStrength() {
    assertEquals(stringStrength.getScore(), stringInputsStrength.getScore());
  }

  @Test
  public void charsequenceAttackTimeMatchesStringAttackTime() {
    assertEquals(
        stringStrength.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond(),
        charSequenceStrength.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond());
  }

  @Test
  public void charsequenceStrengthPasswordMatchesStringStrengthPassword() {
    assertEquals(
        stringStrength.getPassword().toString(), charSequenceStrength.getPassword().toString());
  }

  @Test
  public void strengthPasswordMatchesInput() {
    assertEquals(password.toString(), charSequenceStrength.getPassword().toString());
  }

  @Test
  public void strengthScoreMatchesJavascript() {
    assertEquals(jsStrength.getScore(), charSequenceStrength.getScore());
  }

  @Test
  public void strengthPasswordMatchesJavascript() {
    assertEquals(jsStrength.getPassword(), charSequenceStrength.getPassword().toString());
  }

  @Test
  public void charsequenceSuggestionsMatchStringSuggestions() {
    assertStringListsAreEqual(
        stringStrength.getFeedback().getSuggestions(),
        charSequenceStrength.getFeedback().getSuggestions());
  }

  @Test
  public void charsequenceGuessesMatchesStringGuesses() {
    assertEquals(stringStrength.getGuessesLog10(), charSequenceStrength.getGuessesLog10(), 0.1);
  }

  // =================================================================================//

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> data() throws IOException {
    List<Object[]> passwords = new LinkedList<>();
    passwords.add(new Object[] {""});
    try (InputStream data = ApproachComparisonTest.class.getResourceAsStream("/passwords.txt")) {
      BufferedReader in = new BufferedReader(new InputStreamReader(data));
      String line;
      while ((line = in.readLine()) != null) {
        if (line.trim().length() > 0) {
          passwords.add(new Object[] {line});
        }
      }
    }
    return passwords;
  }

  public void assertStringListsAreEqual(List<String> expectedStrings, List<String> actualStrings) {
    Collections.sort(expectedStrings);
    Collections.sort(actualStrings);

    assertEquals(expectedStrings.size(), actualStrings.size());

    for (int n = 0; n < expectedStrings.size(); n++) {
      assertEquals(expectedStrings.get(n), actualStrings.get(n));
    }
  }

  static class JavaScriptStrength {
    private final Map<String, Object> values;

    public JavaScriptStrength(Map<String, Object> values) {
      this.values = values;
    }

    public int getScore() {
      Object score = values.get("score");
      // nashorn returns int, rhino returns double
      if (score instanceof Double) {
        return ((Double) score).intValue();
      } else {
        return (int) score;
      }
    }

    public String getPassword() {
      return (String) values.get("password");
    }
  }

  @SuppressWarnings("unchecked")
  public JavaScriptStrength invokeJsVersion(CharSequence password) {
    engine.put("pwd", password.toString());
    try {
      return new JavaScriptStrength((Map<String, Object>) engine.eval("zxcvbn(pwd);"));
    } catch (ScriptException e) {
      fail("Error invoking JavaScript version for password " + password);
      return null;
    }
  }

  @BeforeClass
  public static void beforeClass() {
    engine = new JSScriptEngineBuilder().build();
  }
}
