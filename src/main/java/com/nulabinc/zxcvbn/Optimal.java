package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Optimal {

  private final List<Map<Integer, Match>> bestMatches = new ArrayList<>();

  private final List<Map<Integer, Double>> totalGuesses = new ArrayList<>();

  private final List<Map<Integer, Double>> overallMetrics = new ArrayList<>();

  Optimal(int length) {
    for (int i = 0; i < length; i++) {
      bestMatches.add(new HashMap<Integer, Match>());
      totalGuesses.add(new HashMap<Integer, Double>());
      overallMetrics.add(new HashMap<Integer, Double>());
    }
  }

  Match putToBestMatches(int index, Integer key, Match value) {
    return bestMatches.get(index).put(key, value);
  }

  Double putToTotalGuesses(int index, Integer key, Double value) {
    return totalGuesses.get(index).put(key, value);
  }

  Double putToOverallMetrics(int index, Integer key, Double value) {
    return overallMetrics.get(index).put(key, value);
  }

  Map<Integer, Match> getBestMatchesAt(int index) {
    return bestMatches.get(index);
  }

  Map<Integer, Double> getTotalGuessAt(int index) {
    return totalGuesses.get(index);
  }

  Map<Integer, Double> getOverallMetricsAt(int index) {
    return overallMetrics.get(index);
  }

  Match getBestMatch(int index, int key) {
    return getBestMatchesAt(index).get(key);
  }

  Double getTotalGuess(int index, int key) {
    return getTotalGuessAt(index).get(key);
  }

  Double getOverallMetric(int index, int key) {
    return getOverallMetricsAt(index).get(key);
  }
}
