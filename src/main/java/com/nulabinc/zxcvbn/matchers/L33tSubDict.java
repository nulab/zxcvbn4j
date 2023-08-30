package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class L33tSubDict implements Iterable<Map<Character, Character>> {

  private final List<Map<Character, Character>> l33tSubDictionaries;

  L33tSubDict(Map<Character, List<Character>> table) {
    this.l33tSubDictionaries = buildSubDictionaries(table);
  }

  private List<Map<Character, Character>> buildSubDictionaries(
      final Map<Character, List<Character>> table) {
    final Set<List<String>> initialSubs = new LinkedHashSet<>();
    initialSubs.add(new ArrayList<String>());
    final Set<List<String>> allCombinations =
        generateCombinationsRecursively(table, table.keySet().iterator(), initialSubs);

    List<Map<Character, Character>> subDictionaries = new ArrayList<>();
    for (List<String> combination : allCombinations) {
      Map<Character, Character> subDictionary = new HashMap<>();
      for (CharSequence pair : combination) {
        subDictionary.put(pair.charAt(0), pair.charAt(1));
      }
      subDictionaries.add(subDictionary);
    }
    return subDictionaries;
  }

  private Set<List<String>> generateCombinationsRecursively(
      final Map<Character, List<Character>> table,
      final Iterator<Character> keysIterator,
      final Set<List<String>> subs) {
    if (!keysIterator.hasNext()) {
      return subs;
    }

    Character key = keysIterator.next();
    Set<List<String>> nextSubs = new LinkedHashSet<>();
    for (Character l33tChr : table.get(key)) {
      for (List<String> sub : subs) {
        boolean found = false;
        for (int i = 0; i < sub.size(); i++) {
          if (sub.get(i).charAt(0) == l33tChr) {
            List<String> subAlternative = new ArrayList<>(sub);
            subAlternative.remove(i);
            subAlternative.add(String.valueOf(new char[] {l33tChr, key}));
            nextSubs.add(sub);
            nextSubs.add(subAlternative);
            found = true;
            break;
          }
        }
        if (!found) {
          List<String> subExtension = new ArrayList<>(sub);
          subExtension.add(String.valueOf(new char[] {l33tChr, key}));
          nextSubs.add(subExtension);
        }
      }
    }

    return generateCombinationsRecursively(table, keysIterator, nextSubs);
  }

  @Override
  public Iterator<Map<Character, Character>> iterator() {
    return l33tSubDictionaries.iterator();
  }
}
