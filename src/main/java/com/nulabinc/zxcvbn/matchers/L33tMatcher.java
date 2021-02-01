package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.WipeableString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L33tMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public L33tMatcher() {
        this(new HashMap<String, Map<String, Integer>>());
    }

    public L33tMatcher(Map<String, Map<String, Integer>> rankedDictionaries) {
        if (rankedDictionaries == null) {
            this.rankedDictionaries = new HashMap<>();
        } else {
            this.rankedDictionaries = rankedDictionaries;
        }
    }

    private static final Map<Character, List<Character>> L33T_TABLE = new HashMap<>();

    static {
        L33T_TABLE.put('a', Arrays.asList('4', '@'));
        L33T_TABLE.put('b', Arrays.asList('8'));
        L33T_TABLE.put('c', Arrays.asList('(', '{', '[', '<'));
        L33T_TABLE.put('e', Arrays.asList('3'));
        L33T_TABLE.put('g', Arrays.asList('6', '9'));
        L33T_TABLE.put('i', Arrays.asList('1', '!', '|'));
        L33T_TABLE.put('l', Arrays.asList('1', '|', '7'));
        L33T_TABLE.put('o', Arrays.asList('0'));
        L33T_TABLE.put('s', Arrays.asList('$', '5'));
        L33T_TABLE.put('t', Arrays.asList('+', '7'));
        L33T_TABLE.put('x', Arrays.asList('%'));
        L33T_TABLE.put('z', Arrays.asList('2'));
    }

    public Map<Character, List<Character>> relevantL33tSubTable(CharSequence password) {
        return relevantL33tSubTable(password, L33T_TABLE);
    }

    public Map<Character, List<Character>> relevantL33tSubTable(CharSequence password, Map<Character, List<Character>> table) {
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
            for (Match match : new DictionaryMatcher(rankedDictionaries).execute(subbedPassword)) {
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
                String subDisplay = Arrays.toString(subDisplays.toArray(new String[]{}));
                matches.add(MatchFactory.createDictionaryL33tMatch(
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
