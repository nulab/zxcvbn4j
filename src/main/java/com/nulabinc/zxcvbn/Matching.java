package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.OmnibusMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matching {

    private static final Map<String, Map<String, Integer>> BASE_RANKED_DICTIONARIES = new HashMap<>();
    static {
        for (Map.Entry<String, String[]> frequencyListRef : Dictionary.FREQUENCY_LISTS.entrySet()) {
            String name = frequencyListRef.getKey();
            String[] ls = frequencyListRef.getValue();
            BASE_RANKED_DICTIONARIES.put(name, buildRankedDict(Arrays.asList(ls)));
        }
    }

    private final Map<String, Map<String, Integer>> rankedDictionaries;

    public Matching() {
        this(null);
    }

    public Matching(List<String> orderedList) {
        this.rankedDictionaries = new HashMap<>(BASE_RANKED_DICTIONARIES);
        Map<String, Integer> rankedUserInputs;
        if (orderedList != null && !orderedList.isEmpty()) {
            rankedUserInputs = buildRankedDict(orderedList);
        } else {
            rankedUserInputs = Collections.emptyMap();
        }
        this.rankedDictionaries.put("user_inputs", rankedUserInputs);
    }

    public List<Match> omnimatch(String password) {
        return new OmnibusMatcher(rankedDictionaries).execute(password);
    }

    private static Map<String, Integer> buildRankedDict(List<String> orderedList) {
        HashMap<String, Integer> result = new HashMap<>();
        int i = 1; // rank starts at 1, not 0
        for(String word: orderedList) {
            result.put(word, i);
            i++;
        }
        return result;
    }
}
