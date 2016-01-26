package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public DictionaryMatcher(Map<String, Map<String, Integer>> rankedDictionaries) {
        if (rankedDictionaries == null) rankedDictionaries = new HashMap<>();
        this.rankedDictionaries = rankedDictionaries;
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        int len = password.length();
        String passwordLower = password.toLowerCase();
        for (Map.Entry<String, Map<String, Integer>> rankedDictionaryRef: this.rankedDictionaries.entrySet()) {
            String dictionaryName = rankedDictionaryRef.getKey();
            Map<String, Integer> rankedDict = rankedDictionaryRef.getValue();
            for(int i = 0; i < len; i++) {
                for(int j = i; j < len; j++) {
                    String word = passwordLower.substring(i, j + 1);
                    if (rankedDict.containsKey(word)) {
                        int rank = rankedDict.get(word);
                        String token = password.substring(i, j + 1);
                        matches.add(MatchFactory.createDictionaryMatch(i, j, token, word, rank, dictionaryName));
                    }
                }
            }
        }
        return this.sorted(matches);
    }
}
