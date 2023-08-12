package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L33tMatcher extends BaseMatcher {

  private final Map<String, Map<String, Integer>> rankedDictionaries;

  public L33tMatcher(Context context, Map<String, Map<String, Integer>> rankedDictionaries) {
    super(context);
    this.rankedDictionaries = rankedDictionaries;
  }

  private static final Map<Character, List<Character>> L33T_TABLE;

  static {
    Map<Character, List<Character>> l33tTable = new HashMap<>();
    l33tTable.put('a', Arrays.asList('4', '@'));
    l33tTable.put('b', Arrays.asList('8'));
    l33tTable.put('c', Arrays.asList('(', '{', '[', '<'));
    l33tTable.put('e', Arrays.asList('3'));
    l33tTable.put('g', Arrays.asList('6', '9'));
    l33tTable.put('i', Arrays.asList('1', '!', '|'));
    l33tTable.put('l', Arrays.asList('1', '|', '7'));
    l33tTable.put('o', Arrays.asList('0'));
    l33tTable.put('s', Arrays.asList('$', '5'));
    l33tTable.put('t', Arrays.asList('+', '7'));
    l33tTable.put('x', Arrays.asList('%'));
    l33tTable.put('z', Arrays.asList('2'));
    L33T_TABLE = l33tTable;
  }

  public Map<Character, List<Character>> relevantL33tSubTable(CharSequence password) {
    return relevantL33tSubTable(password, L33T_TABLE);
  }

  public Map<Character, List<Character>> relevantL33tSubTable(
      CharSequence password, Map<Character, List<Character>> table) {
    HashMap<Character, Boolean> passwordChars = new HashMap<>();
    for (int n = 0; n < password.length(); n++) {
      passwordChars.put(password.charAt(n), true);
    }
    Map<Character, List<Character>> subTable = new HashMap<>();
    for (Map.Entry<Character, List<Character>> l33tRowRef : table.entrySet()) {
      Character letter = l33tRowRef.getKey();
      List<Character> subs = l33tRowRef.getValue();
      List<Character> relevantSubs = new ArrayList<>();
      for (Character sub : subs) {
        if (passwordChars.containsKey(sub)) {
          relevantSubs.add(sub);
        }
      }
      if (relevantSubs.size() > 0) {
        subTable.put(letter, relevantSubs);
      }
    }
    return subTable;
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    Map<Character, List<Character>> subTable = relevantL33tSubTable(password);
    L33tSubDict l33tSubs = new L33tSubDict(subTable);
    for (Map<Character, Character> sub : l33tSubs) {
      if (sub.isEmpty()) break;
      CharSequence subbedPassword = translate(password, sub);
      for (Match match :
          new DictionaryMatcher(this.getContext(), rankedDictionaries).execute(subbedPassword)) {
        WipeableString token = WipeableString.copy(password, match.i, match.j + 1);
        WipeableString lower = WipeableString.lowerCase(token);
        if (lower.equals(match.matchedWord)) {
          token.wipe();
          lower.wipe();
          continue;
        }
        Map<Character, Character> matchSub = new HashMap<>();
        for (Map.Entry<Character, Character> subRef : sub.entrySet()) {
          Character subbedChr = subRef.getKey();
          Character chr = subRef.getValue();
          if (token.indexOf(subbedChr) != -1) {
            matchSub.put(subbedChr, chr);
          }
        }
        List<String> subDisplays = new ArrayList<>();
        for (Map.Entry<Character, Character> matchSubRef : matchSub.entrySet()) {
          Character k = matchSubRef.getKey();
          Character v = matchSubRef.getValue();
          subDisplays.add(String.format("%s -> %s", k, v));
        }
        String subDisplay = Arrays.toString(subDisplays.toArray(new String[] {}));
        matches.add(
            MatchFactory.createDictionaryL33tMatch(
                match.i,
                match.j,
                token,
                match.matchedWord,
                match.rank,
                match.dictionaryName,
                match.reversed,
                matchSub,
                subDisplay));
        // Don't wipe token as the Match needs it
        lower.wipe();
      }
    }
    List<Match> lst = new ArrayList<>();
    for (Match match : matches) if (match.tokenLength() > 1) lst.add(match);
    return this.sorted(lst);
  }
}
