package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.EstimateGuess;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.MatchFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Scoring {

  public static final int REFERENCE_YEAR = Calendar.getInstance().get(Calendar.YEAR);

  public static final int MIN_GUESSES_BEFORE_GROWING_SEQUENCE = 10000;

  public static final long JS_NUMBER_MAX = 9007199254740991L;

  private final Context context;

  public static double log10(double n) {
    return Math.log(n) / Math.log(10);
  }

  public Scoring(Context context) {
    this.context = context;
  }

  public Strength mostGuessableMatchSequence(CharSequence password, List<Match> matches) {
    return mostGuessableMatchSequence(password, matches, false);
  }

  public Strength mostGuessableMatchSequence(
      CharSequence password, List<Match> matches, boolean excludeAdditive) {
    List<List<Match>> matchesByEndPosition = groupMatchesByEndPosition(password.length(), matches);
    Optimal optimal = computeOptimal(context, password, matchesByEndPosition, excludeAdditive);
    List<Match> optimalMatchSequence = unwindOptimal(password.length(), optimal);
    double guesses =
        password.length() == 0
            ? 1
            : optimal.getOverallMetric(password.length() - 1, optimalMatchSequence.size());
    Strength strength = new Strength();
    strength.setPassword(password);
    strength.setGuesses(guesses);
    strength.setGuessesLog10(log10(guesses));
    strength.setSequence(optimalMatchSequence);
    return strength;
  }

  private static List<List<Match>> groupMatchesByEndPosition(int length, List<Match> matches) {
    final List<List<Match>> matchesByEndPosition = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      matchesByEndPosition.add(new ArrayList<Match>());
    }
    for (Match match : matches) {
      matchesByEndPosition.get(match.j).add(match);
    }
    for (List<Match> lst : matchesByEndPosition) {
      Collections.sort(lst, new MatchStartPositionComparator());
    }
    return matchesByEndPosition;
  }

  private static Optimal computeOptimal(
      Context context,
      CharSequence password,
      List<List<Match>> matchesByEndPosition,
      boolean excludeAdditive) {
    int length = password.length();
    Optimal optimal = new Optimal(length);
    for (int k = 0; k < length; k++) {
      for (Match m : matchesByEndPosition.get(k)) {
        if (m.i > 0) {
          for (Map.Entry<Integer, Match> entry : optimal.getBestMatchesAt(m.i - 1).entrySet()) {
            int l = entry.getKey();
            updateOptimal(context, password, m, l + 1, optimal, excludeAdditive);
          }
        } else {
          updateOptimal(context, password, m, 1, optimal, excludeAdditive);
        }
      }
      updateBruteforceMatches(context, password, k, optimal, excludeAdditive);
    }
    return optimal;
  }

  private static void updateOptimal(
      Context context,
      CharSequence password,
      Match match,
      int l,
      Optimal optimal,
      boolean excludeAdditive) {

    double guesses = calculateGuesses(context, password, match, l, optimal);
    double metrics = calculateMetrics(l, guesses, excludeAdditive);

    if (shouldUpdateMetrics(match, l, metrics, optimal)) {
      optimal.putToBestMatches(match.j, l, match);
      optimal.putToTotalGuesses(match.j, l, guesses);
      optimal.putToOverallMetrics(match.j, l, metrics);
    }
  }

  private static double calculateGuesses(
      Context context, CharSequence password, Match match, int l, Optimal optimal) {
    double guesses = new EstimateGuess(context, password).exec(match);
    if (l > 1) {
      guesses *= optimal.getTotalGuess(match.i - 1, l - 1);
    }
    return handleInfinity(guesses);
  }

  private static double calculateMetrics(int l, double guesses, boolean excludeAdditive) {
    double metrics = factorial(l) * guesses;
    metrics = handleInfinity(metrics);

    if (!excludeAdditive) {
      metrics += Math.pow(MIN_GUESSES_BEFORE_GROWING_SEQUENCE, (double) l - 1);
      metrics = handleInfinity(metrics);
    }

    return metrics;
  }

  private static double handleInfinity(double value) {
    return Double.isInfinite(value) ? Double.MAX_VALUE : value;
  }

  private static boolean shouldUpdateMetrics(Match match, int l, double metrics, Optimal optimal) {
    Map<Integer, Double> overallMetrics = optimal.getOverallMetricsAt(match.j);
    for (Map.Entry<Integer, Double> competing : overallMetrics.entrySet()) {
      if (competing.getKey() > l) {
        continue;
      }
      if (competing.getValue() <= metrics) {
        return false;
      }
    }
    return true;
  }

  private static void updateBruteforceMatches(
      Context context,
      CharSequence password,
      int endIndex,
      Optimal optimal,
      boolean excludeAdditive) {

    Match match = makeBruteforceMatch(password, 0, endIndex);
    updateOptimal(context, password, match, 1, optimal, excludeAdditive);

    for (int startIndex = 1; startIndex <= endIndex; startIndex++) {
      match = makeBruteforceMatch(password, startIndex, endIndex);
      Map<Integer, Match> previousBestMatches = optimal.getBestMatchesAt(startIndex - 1);

      for (Map.Entry<Integer, Match> entry : previousBestMatches.entrySet()) {
        int matchLength = entry.getKey();
        Match lastMatch = entry.getValue();
        if (lastMatch.pattern != Pattern.Bruteforce) {
          updateOptimal(context, password, match, matchLength + 1, optimal, excludeAdditive);
        }
      }
    }
  }

  private static List<Match> unwindOptimal(int passwordLength, Optimal optimal) {
    List<Match> optimalMatchSequence = new ArrayList<>();
    if (passwordLength <= 0) {
      return optimalMatchSequence;
    }

    int lastIndex = passwordLength - 1;
    Map<Integer, Double> metricsForLastIndex = optimal.getOverallMetricsAt(lastIndex);

    int optimalLength = getOptimalMatchLength(metricsForLastIndex);

    while (lastIndex >= 0) {
      Match currentMatch = optimal.getBestMatch(lastIndex, optimalLength);
      optimalMatchSequence.add(0, currentMatch);
      lastIndex = currentMatch.i - 1;
      optimalLength--;
    }
    return optimalMatchSequence;
  }

  private static int getOptimalMatchLength(Map<Integer, Double> metrics) {
    int optimalLength = 0;
    Double minMetric = Double.POSITIVE_INFINITY;

    for (Map.Entry<Integer, Double> candidate : metrics.entrySet()) {
      Double currentMetric = candidate.getValue();
      if (currentMetric < minMetric) {
        optimalLength = candidate.getKey();
        minMetric = currentMetric;
      }
    }

    return optimalLength;
  }

  private static Match makeBruteforceMatch(CharSequence password, int i, int j) {
    return MatchFactory.createBruteforceMatch(i, j, password.subSequence(i, j + 1));
  }

  private static long factorial(int n) {
    if (n < 2) {
      return 1;
    }
    if (n > 19) {
      return JS_NUMBER_MAX;
    }
    long f = 1;
    for (int i = 2; i <= n; i++) {
      f *= i;
    }
    return f;
  }

  private static class MatchStartPositionComparator implements Comparator<Match>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Match m1, Match m2) {
      return m1.i - m2.i;
    }
  }
}
