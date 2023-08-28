package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class L33tMatcher extends BaseMatcher {

  private final Map<String, Map<String, Integer>> rankedDictionaries;
  private static final Map<Character, List<Character>> L33T_TABLE;

  static {
    Map<Character, List<Character>> table = new HashMap<>();
    table.put('a', Arrays.asList('4', '@'));
    table.put('b', Collections.singletonList('8'));
    table.put('c', Arrays.asList('(', '{', '[', '<'));
    table.put('e', Collections.singletonList('3'));
    table.put('g', Arrays.asList('6', '9'));
    table.put('i', Arrays.asList('1', '!', '|'));
    table.put('l', Arrays.asList('1', '|', '7'));
    table.put('o', Collections.singletonList('0'));
    table.put('s', Arrays.asList('$', '5'));
    table.put('t', Arrays.asList('+', '7'));
    table.put('x', Collections.singletonList('%'));
    table.put('z', Collections.singletonList('2'));
    L33T_TABLE = Collections.unmodifiableMap(table);
  }

  public L33tMatcher(Context context, Map<String, Map<String, Integer>> rankedDictionaries) {
    super(context);
    this.rankedDictionaries = rankedDictionaries;
  }

  public Map<Character, List<Character>> relevantL33tSubTable(CharSequence password) {
    return relevantL33tSubTable(password, L33T_TABLE);
  }

  public Map<Character, List<Character>> relevantL33tSubTable(
      CharSequence password, Map<Character, List<Character>> table) {
    HashSet<Character> passwordChars = new HashSet<>();
    for (int n = 0; n < password.length(); n++) {
      passwordChars.add(password.charAt(n));
    }
    Map<Character, List<Character>> subTable = new HashMap<>();
    for (Map.Entry<Character, List<Character>> l33tRowRef : table.entrySet()) {
      Character letter = l33tRowRef.getKey();
      List<Character> subs = l33tRowRef.getValue();
      List<Character> relevantSubs = new ArrayList<>();
      for (Character sub : subs) {
        if (passwordChars.contains(sub)) {
          relevantSubs.add(sub);
        }
      }
      if (!relevantSubs.isEmpty()) {
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
    DictionaryMatcher dictionaryMatcher =
        new DictionaryMatcher(this.getContext(), rankedDictionaries);

    for (Map<Character, Character> sub : l33tSubs) {
      if (sub.isEmpty()) {
        break; // corner case: password has no relevant subs.
      }
      CharSequence subbedPassword = decodeL33tSpeak(password, sub);

      for (Match match : dictionaryMatcher.execute(subbedPassword)) {

        WipeableString token = WipeableString.copy(password, match.i, match.j + 1);
        WipeableString lower = WipeableString.lowerCase(token);
        if (lower.equals(match.matchedWord)) {
          token.wipe();
          lower.wipe();
          continue;
        }

        Map<Character, Character> matchSub = extractMatchSub(token, sub);
        String subDisplay = generateSubDisplay(matchSub);

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
    return filterMatches(matches);
  }

  private Map<Character, Character> extractMatchSub(
      WipeableString token, Map<Character, Character> sub) {
    Map<Character, Character> matchSub = new HashMap<>();
    for (Map.Entry<Character, Character> subRef : sub.entrySet()) {
      Character subbedChr = subRef.getKey();
      Character chr = subRef.getValue();
      if (token.indexOf(subbedChr) != -1) {
        matchSub.put(subbedChr, chr);
      }
    }
    return matchSub;
  }

  private String generateSubDisplay(Map<Character, Character> matchSub) {
    List<String> subDisplays = new ArrayList<>();
    for (Map.Entry<Character, Character> matchSubRef : matchSub.entrySet()) {
      Character k = matchSubRef.getKey();
      Character v = matchSubRef.getValue();
      subDisplays.add(String.format("%s -> %s", k, v));
    }
    return Arrays.toString(subDisplays.toArray(new String[0]));
  }

  private List<Match> filterMatches(List<Match> matches) {
    List<Match> filteredMatches = new ArrayList<>();
    for (Match match : matches) {
      if (match.tokenLength() > 1) {
        filteredMatches.add(match);
      }
    }
    return this.sorted(filteredMatches);
  }

  private CharSequence decodeL33tSpeak(
      CharSequence password, Map<Character, Character> l33tToRegularMapping) {
    StringBuilder sb = new StringBuilder(password.length());
    for (int charIndex = 0; charIndex < password.length(); charIndex++) {
      char curChar = password.charAt(charIndex);
      Character replacement = l33tToRegularMapping.get(curChar);
      sb.append(replacement != null ? replacement : curChar);
    }
    WipeableString result = new WipeableString(sb);
    WipeableString.wipeIfPossible(sb);
    return result;
  }
}
