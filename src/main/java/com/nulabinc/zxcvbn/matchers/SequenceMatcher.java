package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SequenceMatcher extends BaseMatcher {

  private static final int MAX_CODE_POINT_DIFF = 5;
  private static final int MIN_VALID_SEQUENCE_LENGTH = 1;
  private static final Pattern LOWERCASE_PATTERN = Pattern.compile("^[a-z]+$");
  private static final Pattern UPPERCASE_PATTERN = Pattern.compile("^[A-Z]+$");
  private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d+$");

  public SequenceMatcher(final Context context) {
    super(context);
  }

  /**
   * Identifies sequence by looking for repeated differences in unicode codepoint. this allows
   * skipping, such as 9753, and also matches some extended unicode sequences such as Greek and
   * Cyrillic alphabets.
   *
   * <p>for example, consider the input 'abcdb975zy'
   *
   * <p>password: a b c d b 9 7 5 z y index: 0 1 2 3 4 5 6 7 8 9 delta: 1 1 1 -2 -41 -2 -2 69 1
   *
   * <p>expected result: [(i, j, delta), ...] = [(0, 3, 1), (5, 7, -2), (8, 9, 1)]
   */
  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    if (password == null || password.length() == 1) {
      return matches;
    }
    // Initial value. This value itself is not used in actual data processing.
    int lastCodePointDiff = 0;
    WipeableString wipeable = new WipeableString(password);
    int startIndex = 0;

    for (int curIndex = 1; curIndex < password.length(); curIndex++) {
      int codePointDiff = wipeable.codePointAt(curIndex) - wipeable.codePointAt(curIndex - 1);
      if (curIndex == 1) { // is first iteration
        lastCodePointDiff = codePointDiff;
      }
      if (codePointDiff == lastCodePointDiff) {
        continue;
      }

      int endIndex = curIndex - 1;
      addMatchIfPresent(password, matches, startIndex, endIndex, lastCodePointDiff);
      startIndex = endIndex;
      lastCodePointDiff = codePointDiff;
    }

    wipeable.wipe();
    addMatchIfPresent(password, matches, startIndex, password.length() - 1, lastCodePointDiff);

    return matches;
  }

  private Match createSequenceMatch(
      CharSequence password, int startIndex, int endIndex, int codePointDiff) {
    if (!isValidSequenceLength(endIndex, startIndex, codePointDiff)
        || !isValidCodePointDiffValue(codePointDiff)) {
      return null;
    }

    CharSequence token = WipeableString.copy(password, startIndex, endIndex + 1);
    SequenceType sequenceType = determineSequenceType(token);
    return MatchFactory.createSequenceMatch(
        startIndex,
        endIndex,
        token,
        sequenceType.getName(),
        sequenceType.getSpace(),
        codePointDiff > 0);
  }

  private boolean isValidSequenceLength(int endIndex, int startIndex, int codePointDiff) {
    return (endIndex - startIndex) > MIN_VALID_SEQUENCE_LENGTH
        || Math.abs(codePointDiff) == MIN_VALID_SEQUENCE_LENGTH;
  }

  private boolean isValidCodePointDiffValue(int codePointDiff) {
    return Math.abs(codePointDiff) <= MAX_CODE_POINT_DIFF;
  }

  private SequenceType determineSequenceType(CharSequence token) {
    if (isLowercase(token)) {
      return SequenceType.LOWER;
    } else if (isUppercase(token)) {
      return SequenceType.UPPER;
    } else if (isDigits(token)) {
      return SequenceType.DIGITS;
    } else {
      return SequenceType.UNICODE;
    }
  }

  private boolean isLowercase(CharSequence token) {
    return LOWERCASE_PATTERN.matcher(token).matches();
  }

  private boolean isUppercase(CharSequence token) {
    return UPPERCASE_PATTERN.matcher(token).matches();
  }

  private boolean isDigits(CharSequence token) {
    return DIGIT_PATTERN.matcher(token).matches();
  }

  private void addMatchIfPresent(
      CharSequence password, List<Match> matches, int startIndex, int endIndex, int codePointDiff) {
    Match match = createSequenceMatch(password, startIndex, endIndex, codePointDiff);
    if (match != null) {
      matches.add(match);
    }
  }

  private enum SequenceType {
    LOWER("lower", 26),
    UPPER("upper", 26),
    DIGITS("digits", 10),
    // conservatively stick with roman alphabet size. (this could be improved)
    UNICODE("unicode", 26);

    private final String name;
    private final int space;

    SequenceType(String name, int space) {
      this.name = name;
      this.space = space;
    }

    public String getName() {
      return name;
    }

    public int getSpace() {
      return space;
    }
  }
}
