package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.*;
import com.nulabinc.zxcvbn.matchers.Dictionary;

import java.util.*;

public class Matching {

    private static final Map<String, Map<String, Integer>> BASE_RANKED_DICTIONARIES = new HashMap<>();
    static {
        for (Map.Entry<String, String[]> frequencyListRef : Dictionary.FREQUENCY_LISTS.entrySet()) {
            String name = frequencyListRef.getKey();
            String[] ls = frequencyListRef.getValue();
            BASE_RANKED_DICTIONARIES.put(name, buildRankedDict(ls));
        }
    }

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public Matching() {
        this(new ArrayList<String>());
    }

    public Matching(List<String> orderedList) {
        if (orderedList == null) orderedList = new ArrayList<>();
        this.rankedDictionaries = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> baseRankedDictionary : BASE_RANKED_DICTIONARIES.entrySet()) {
            this.rankedDictionaries.put(
                    baseRankedDictionary.getKey(),
                    baseRankedDictionary.getValue());
        }
        this.rankedDictionaries.put("user_inputs", buildRankedDict(orderedList.toArray(new String[]{})));
    }

    public List<Match> omnimatch(String password) {
        return new OmnibusMatcher(rankedDictionaries).execute(password);
    }

    private static Map<String, Integer> buildRankedDict(String[] orderedList) {
        HashMap<String, Integer> result = new HashMap<>();
        int i = 1; // rank starts at 1, not 0
        for(String word: orderedList) {
            result.put(word, i);
            i++;
        }
        return result;
    }
}