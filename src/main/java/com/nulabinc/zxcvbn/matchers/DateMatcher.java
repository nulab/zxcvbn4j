package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.Scoring;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/*
 * a "date" is recognized as:
 * any 3-tuple that starts or ends with a 2- or 4-digit year,
 * with 2 or 0 separator chars (1.1.91 or 1191),
 * maybe zero-padded (01-01-91 vs 1-1-91),
 * a month between 1 and 12,
 * a day between 1 and 31.
 *
 * note: this isn't true date parsing in that "feb 31st" is allowed,
 * this doesn't check for leap years, etc.
 *
 * recipe:
 * start with regex to find maybe-dates, then attempt to map the integers
 * onto month-day-year to filter the maybe-dates into dates.
 * finally, remove matches that are substrings of other matches to reduce noise.
 *
 * note: instead of using a lazy or greedy regex to find many dates over the full string,
 * this uses a ^...$ regex against every substring of the password -- less performant but leads
 * to every possible date match.
 * */
public class DateMatcher extends BaseMatcher {

  private static final int DATE_MAX_YEAR = 2050;
  private static final int DATE_MIN_YEAR = 1000;
  private static final int[][][] DATE_SPLITS = new int[9][][];
  private static final Pattern MAYBE_DATE_NO_SEPARATOR = Pattern.compile("^\\d{4,8}$");
  private static final Pattern MAYBE_DATE_WITH_SEPARATOR =
      Pattern.compile(
          "^"
              +
              // day, month, year
              "(\\d{1,4})"
              +
              // separator
              "([\\s/\\\\_.-])"
              +
              // day, month
              "(\\d{1,2})"
              +
              // same separator
              "\\2"
              +
              // day, month, year
              "(\\d{1,4})"
              + "$");

  static {
    DATE_SPLITS[4] =
        // for length-4 strings, e.g. 1191 or 9111, two ways to split:
        new int[][] {
          new int[] {1, 2}, // 1 1 91 (2nd split starts at index 1, 3rd at index 2)
          new int[] {2, 3} // 91 1 1
        };
    DATE_SPLITS[5] =
        new int[][] {
          new int[] {1, 3}, // 1 11 91
          new int[] {2, 3} // 11 1 91
        };
    DATE_SPLITS[6] =
        new int[][] {
          new int[] {1, 2}, // 1 1 1991
          new int[] {2, 4}, // 11 11 91
          new int[] {4, 5} // 1991 1 1
        };
    DATE_SPLITS[7] =
        new int[][] {
          new int[] {1, 3}, // 1 11 1991
          new int[] {2, 3}, // 11 1 1991
          new int[] {4, 5}, // 1991 1 11
          new int[] {4, 6} // 1991 11 1
        };
    DATE_SPLITS[8] =
        new int[][] {
          new int[] {2, 4}, // 11 11 1991
          new int[] {4, 6} // 1991 11 11
        };
  }

  public DateMatcher(final Context context) {
    super(context);
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    findDatesWithoutSeparator(password, matches);
    findDatesWithSeparator(password, matches);
    return filterSubMatches(matches);
  }

  private void findDatesWithoutSeparator(CharSequence password, List<Match> matches) {
    // dates without separators are between length 4 '1191' and 8 '11111991'
    for (int startIndex = 0; startIndex <= password.length() - 4; startIndex++) {
      for (int endIndex = startIndex + 3; endIndex <= startIndex + 7; endIndex++) {
        if (endIndex >= password.length()) {
          break;
        }
        WipeableString token = WipeableString.copy(password, startIndex, endIndex + 1);
        if (!MAYBE_DATE_NO_SEPARATOR.matcher(token).find()) {
          token.wipe();
          continue;
        }
        extractDateCandidates(matches, startIndex, endIndex, token);
      }
    }
  }

  private void extractDateCandidates(
      List<Match> matches, int startIndex, int endIndex, WipeableString token) {
    List<Dmy> candidates = generateDateCandidates(token);
    if (candidates.isEmpty()) {
      token.wipe();
      return;
    }
    Dmy bestCandidate = selectBestDateCandidate(candidates);
    matches.add(
        MatchFactory.createDateMatch(
            startIndex,
            endIndex,
            token,
            "",
            bestCandidate.year,
            bestCandidate.month,
            bestCandidate.day));
  }

  private List<Dmy> generateDateCandidates(WipeableString token) {
    List<Dmy> candidates = new ArrayList<>();
    for (int[] date : DATE_SPLITS[token.length()]) {
      int[] extractedInts = extractIntsFromToken(token, date);
      if (extractedInts != null) {
        Dmy dmy = mapIntsToDmy(extractedInts);
        if (dmy != null) {
          candidates.add(dmy);
        }
      }
    }
    return candidates;
  }

  private int[] extractIntsFromToken(WipeableString token, int[] date) {
    int[] ints = new int[3];
    try {
      ints[0] = WipeableString.parseInt(token.subSequence(0, date[0]));
      ints[1] = WipeableString.parseInt(token.subSequence(date[0], date[1]));
      ints[2] = WipeableString.parseInt(token.subSequence(date[1], token.length()));
      return ints;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Dmy selectBestDateCandidate(List<Dmy> candidates) {
    Dmy bestCandidate = candidates.get(0);
    int minDistance = metric(candidates.get(0));
    for (Dmy candidate : candidates.subList(1, candidates.size())) {
      int distance = metric(candidate);
      if (distance < minDistance) {
        bestCandidate = candidate;
        minDistance = distance;
      }
    }
    return bestCandidate;
  }

  private void findDatesWithSeparator(CharSequence password, List<Match> matches) {
    // dates with separators are between length 6 '1/1/91' and 10 '11/11/1991'
    for (int startIndex = 0; startIndex <= password.length() - 6; startIndex++) {
      for (int endIndex = startIndex + 5; endIndex <= startIndex + 9; endIndex++) {
        if (endIndex >= password.length()) {
          break;
        }
        WipeableString token = WipeableString.copy(password, startIndex, endIndex + 1);
        java.util.regex.Matcher rxMatch = MAYBE_DATE_WITH_SEPARATOR.matcher(token);
        if (!rxMatch.find()) {
          token.wipe();
          continue;
        }
        int[] extractedInts = extractIntsFromMatcher(rxMatch);
        if (extractedInts != null) {
          Dmy dmy = mapIntsToDmy(extractedInts);
          if (dmy == null) {
            token.wipe();
            continue;
          }
          matches.add(
              MatchFactory.createDateMatch(
                  startIndex, endIndex, token, rxMatch.group(2), dmy.year, dmy.month, dmy.day));
        }
      }
    }
  }

  private int[] extractIntsFromMatcher(java.util.regex.Matcher matcher) {
    int[] ints = new int[3];
    try {
      ints[0] = WipeableString.parseInt(matcher.group(1));
      ints[1] = WipeableString.parseInt(matcher.group(3));
      ints[2] = WipeableString.parseInt(matcher.group(4));
    } catch (NumberFormatException e) {
      return null;
    }
    return ints;
  }

  private List<Match> filterSubMatches(List<Match> matches) {
    List<Match> targetMatches = new ArrayList<>();
    for (Match match : matches) {
      boolean isSubMatch = false;
      for (Match otherMatch : matches) {
        if (match.equals(otherMatch)) {
          continue;
        }
        if (otherMatch.i <= match.i && otherMatch.j >= match.j) {
          isSubMatch = true;
          break;
        }
      }
      if (!isSubMatch) {
        targetMatches.add(match);
      }
    }
    return this.sorted(targetMatches);
  }

  private int metric(Dmy candidate) {
    return Math.abs(candidate.year - Scoring.REFERENCE_YEAR);
  }

  private Dmy mapIntsToDmy(int[] ints) {
    if (ints[1] > 31 || ints[1] <= 0) {
      return null;
    }
    int over12 = 0;
    int over31 = 0;
    int under1 = 0;
    for (int i : ints) {
      if ((99 < i && i < DATE_MIN_YEAR) || i > DATE_MAX_YEAR) {
        return null;
      }
      if (i > 31) {
        over31 += 1;
      }
      if (i > 12) {
        over12 += 1;
      }
      if (i <= 0) {
        under1 += 1;
      }
    }
    if (over31 >= 2 || over12 == 3 || under1 >= 2) {
      return null;
    }
    int[][] possibleYearSplits = {
      {ints[2], ints[0], ints[1]},
      {ints[0], ints[1], ints[2]}
    };
    for (int[] split : possibleYearSplits) {
      int y = split[0];
      int[] rest = new int[] {split[1], split[2]};
      if (DATE_MIN_YEAR <= y && y <= DATE_MAX_YEAR) {
        Dm dm = mapIntsToDm(rest);
        if (dm != null) {
          return new Dmy(dm.day, dm.month, y);
        } else {
          return null;
        }
      }
    }
    for (int[] split : possibleYearSplits) {
      int[] rest = new int[] {split[1], split[2]};
      Dm dm = mapIntsToDm(rest);
      if (dm != null) {
        int y = twoToFourDigitYear(split[0]);
        return new Dmy(dm.day, dm.month, y);
      }
    }
    return null;
  }

  private Dm mapIntsToDm(int[] ints) {
    int[] copy = Arrays.copyOf(ints, ints.length);
    reverse(copy);
    for (int[] ref : new int[][] {ints, copy}) {
      int d = ref[0];
      int m = ref[1];
      if ((1 <= d && d <= 31) && (1 <= m && m <= 12)) {
        return new Dm(d, m);
      }
    }
    return null;
  }

  private void reverse(int[] array) {
    for (int i = 0; i < array.length / 2; i++) {
      int temp = array[i];
      array[i] = array[array.length - 1 - i];
      array[array.length - 1 - i] = temp;
    }
  }

  private int twoToFourDigitYear(int year) {
    if (year > 99) {
      return year;
    } else if (year > 50) {
      // 87 -> 1987
      return year + 1900;
    } else {
      // 15 -> 2015
      return year + 2000;
    }
  }

  private static class Dm {
    final int day;
    final int month;

    public Dm(int day, int month) {
      this.day = day;
      this.month = month;
    }
  }

  private static class Dmy extends Dm {
    final int year;

    public Dmy(int day, int month, int year) {
      super(day, month);
      this.year = year;
    }
  }
}
