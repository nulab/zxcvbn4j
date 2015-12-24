package com.nulabinc.zxcvbn.matchers;

import java.util.*;

public class L33tMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public L33tMatcher() {
        this(new HashMap<String, Map<String, Integer>>());
    }

    public L33tMatcher(Map<String, Map<String, Integer>> rankedDictionaries) {
        if (rankedDictionaries == null) rankedDictionaries = new HashMap<>();
        this.rankedDictionaries = rankedDictionaries;
    }

    private static final Map<Character, Character[]> L33T_TABLE = new HashMap<>();
    static {
        L33T_TABLE.put('a', new Character[]{'4', '@'});
        L33T_TABLE.put('b', new Character[]{'8'});
        L33T_TABLE.put('c', new Character[]{'(', '{', '[', '<'});
        L33T_TABLE.put('e', new Character[]{'3'});
        L33T_TABLE.put('g', new Character[]{'6', '9'});
        L33T_TABLE.put('i', new Character[]{'1', '!', '|'});
        L33T_TABLE.put('l', new Character[]{'1', '|', '7'});
        L33T_TABLE.put('o', new Character[]{'0'});
        L33T_TABLE.put('s', new Character[]{'$', '5'});
        L33T_TABLE.put('t', new Character[]{'+', '7'});
        L33T_TABLE.put('x', new Character[]{'%'});
        L33T_TABLE.put('z', new Character[]{'2'});
    }

    public HashMap<Character, Character[]> relevantL33tSubTable(String password) {
        return relevantL33tSubTable(password, L33T_TABLE);
    }

    public HashMap<Character, Character[]> relevantL33tSubTable(String password, Map<Character, Character[]> table) {
        HashMap<Character, Boolean> passwordChars = new HashMap<>();
        for(Character chr: password.toCharArray()) passwordChars.put(chr, true);
        HashMap<Character, Character[]> subTable = new HashMap<>();
        for(Map.Entry<Character, Character[]> l33tRowRef: table.entrySet()) {
            Character letter = l33tRowRef.getKey();
            Character[] subs = l33tRowRef.getValue();
            List<Character> relevantSubs = new ArrayList<>();
            for(Character sub: subs) if (passwordChars.containsKey(sub)) relevantSubs.add(sub);
            if (relevantSubs.size() > 0) subTable.put(letter, relevantSubs.toArray(new Character[]{}));
        }
        return subTable;
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        HashMap<Character, Character[]> subTable = relevantL33tSubTable(password);
        L33tSubsEnumerator l33tSubs = new L33tSubsEnumerator(subTable);
        List<Map<Character, Character>> subs =  l33tSubs.enumerate();
        for (Map<Character, Character> sub: subs) {
            if (sub.isEmpty()) break;
            String subbedPassword = translate(password, sub);
            for (Match match: new DictionaryMatcher(rankedDictionaries).execute(subbedPassword)) {
                String token = password.substring(match.i, match.j + 1);
                if (token.toLowerCase().equals(match.matchedWord)) continue;
                Map<Character, Character> matchSub = new HashMap<>();
                for (Map.Entry<Character, Character> subRef: sub.entrySet()) {
                    Character subbedChr = subRef.getKey();
                    Character chr = subRef.getValue();
                    if (token.indexOf(subbedChr) != -1) {
                        matchSub.put(subbedChr, chr);
                    }
                }
                List<String> subDisplays = new ArrayList<>();
                for (Map.Entry<Character, Character> matchSubRef: matchSub.entrySet()) {
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

            }
        }
        List<Match> lst = new ArrayList<>();
        for(Match match: matches) if (match.token.length() > 1) lst.add(match);
        return this.sorted(lst);
    }

}
