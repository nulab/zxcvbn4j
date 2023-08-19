package com.nulabinc.zxcvbn.matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {

  private final String name;

  private final List<String> frequencies;

  private final Map<String, Integer> rankedDictionary;

  public Dictionary(String name, List<String> frequencies) {
    this.name = name;
    this.frequencies = frequencies;
    this.rankedDictionary = toRankedDictionary(frequencies);
  }

  private Map<String, Integer> toRankedDictionary(final List<String> frequencies) {
    Map<String, Integer> result = new HashMap<>();
    int i = 1; // rank starts at 1, not 0
    for (String word : frequencies) {
      result.put(word, i);
      i++;
    }
    return result;
  }

  public String getName() {
    return name;
  }

  public List<String> getFrequencies() {
    return frequencies;
  }

  public Map<String, Integer> getRankedDictionary() {
    return rankedDictionary;
  }
}
