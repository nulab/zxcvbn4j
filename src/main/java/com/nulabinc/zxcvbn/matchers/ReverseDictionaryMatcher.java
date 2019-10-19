package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.WipeableString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverseDictionaryMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public ReverseDictionaryMatcher(Map<String, Map<String, Integer>> rankedDictionaries) {
        if (rankedDictionaries == null) rankedDictionaries = new HashMap<>();
        this.rankedDictionaries = rankedDictionaries;
    }

    @Override
    public List<Match> execute(CharSequence password) {
        CharSequence reversedPassword =  WipeableString.reversed(password);
        List<Match> matches = new ArrayList<>();
        for (Match match: new DictionaryMatcher(this.rankedDictionaries).execute(reversedPassword)) {
            matches.add(MatchFactory.createReversedDictionaryMatch(
                    password.length() - 1 - match.j,
                    password.length() - 1 - match.i,
                    WipeableString.reversed(match.token),
                    match.matchedWord,
                    match.rank,
                    match.dictionaryName));
        }
        return this.sorted(matches);
    }
}
