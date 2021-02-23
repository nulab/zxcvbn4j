package com.nulabinc.zxcvbn;

import java.util.*;

import com.nulabinc.zxcvbn.matchers.CustomDictionary;
import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.OmnibusMatcher;

public class Matching {

    private static final Map<String, Map<String, Integer>> BASE_RANKED_DICTIONARIES = buildRankedDicts(Dictionary.FREQUENCY_LISTS);

    protected final Map<String, Map<String, Integer>> rankedDictionaries;

    public Matching() {
        this(null);
    }

    public Matching(List<String> orderedList) {
        this(BASE_RANKED_DICTIONARIES, orderedList);
    }

    public Matching(List<CustomDictionary> customDictionaries, List<String> orderedList) {
        this(buildRankedDicts(customDictionaries), orderedList);
    }

    protected Matching(Map<String, Map<String, Integer>> rankedDictionaries, List<String> orderedList) {
        if (rankedDictionaries != null) {
            this.rankedDictionaries = new HashMap<>(rankedDictionaries);
        } else {
            this.rankedDictionaries = new HashMap<>();
        }
        Map<String, Integer> rankedUserInputs;
        if (orderedList != null && !orderedList.isEmpty()) {
            rankedUserInputs = buildRankedDict(orderedList);
        } else {
            rankedUserInputs = Collections.emptyMap();
        }
        this.rankedDictionaries.put("user_inputs", rankedUserInputs);
    }

    public List<Match> omnimatch(CharSequence password) {
        return new OmnibusMatcher(rankedDictionaries).execute(password);
    }

    protected static Map<String, Map<String, Integer>> buildRankedDicts(List<CustomDictionary> customDictionaries) {
        Map<String, Map<String, Integer>> rankedDicts = new HashMap<>(BASE_RANKED_DICTIONARIES);
        for (CustomDictionary customDictionary : customDictionaries) {
            rankedDicts.putAll(buildRankedDicts(customDictionary.getFrequencyLists()));
        }
        return rankedDicts;
    }

    protected static Map<String, Map<String, Integer>> buildRankedDicts(Map<String, String[]> frequencyLists) {
        Map<String, Map<String, Integer>> rankedDicts = new HashMap<>();
        for (Map.Entry<String, String[]> frequencyListRef : frequencyLists.entrySet()) {
            String name = frequencyListRef.getKey();
            String[] ls = frequencyListRef.getValue();
            rankedDicts.put(name, buildRankedDict(Arrays.asList(ls)));
        }
        return rankedDicts;
    }

    protected static Map<String, Integer> buildRankedDict(List<String> orderedList) {
        HashMap<String, Integer> result = new HashMap<>();
        int i = 1; // rank starts at 1, not 0
        for(String word: orderedList) {
            result.put(word, i);
            i++;
        }
        return result;
    }
}
