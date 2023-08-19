package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class L33tSubDict implements Iterable<Map<Character, Character>> {

  private final List<Map<Character, Character>> subDicts;

  L33tSubDict(Map<Character, List<Character>> table) {
    this.subDicts = buildSubDicts(table);
  }

  private List<Map<Character, Character>> buildSubDicts(
      final Map<Character, List<Character>> table) {
    final Set<List<String>> initialSubs = new LinkedHashSet<>();
    initialSubs.add(new ArrayList<String>());
    final Set<List<String>> subs = helper(table, table.keySet().iterator(), initialSubs);

    List<Map<Character, Character>> subDicts = new ArrayList<>();
    for (List<String> sub : subs) {
      Map<Character, Character> subDict = new HashMap<>();
      for (CharSequence ref : sub) {
        subDict.put(ref.charAt(0), ref.charAt(1));
      }
      subDicts.add(subDict);
    }
    return subDicts;
  }

  private Set<List<String>> helper(
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

    return helper(table, keysIterator, nextSubs);
  }

  @Override
  public Iterator<Map<Character, Character>> iterator() {
    return subDicts.iterator();
  }
}
