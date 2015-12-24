package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.*;
import com.nulabinc.zxcvbn.matchers.Dictionary;

import java.util.*;

public class Matching {

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public Matching() {
        this(new ArrayList<String>());
    }

    public Matching(List<String> orderedList) {
        if (orderedList == null) new ArrayList<>();
        this.rankedDictionaries = new HashMap<>();
        for (Map.Entry<String, String[]> frequencyListRef : Dictionary.FREQUENCY_LISTS.entrySet()) {
            String name = frequencyListRef.getKey();
            String[] ls = frequencyListRef.getValue();
            this.rankedDictionaries.put(name, buildRankedDict(ls));
        }
        this.rankedDictionaries.put("user_inputs", buildRankedDict(orderedList.toArray(new String[]{})));
    }

    public List<Match> omnimatch(String password) {
        return new OmnibusMatcher(rankedDictionaries).execute(password);
    }

    private Map<String, Integer> buildRankedDict(String[] orderedList) {
        HashMap<String, Integer> result = new HashMap<>();
        int i = 1; // rank starts at 1, not 0
        for(String word: orderedList) {
            result.put(word, i);
            i++;
        }
        return result;
    }
}