package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OmnibusMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> dictionaries;

    public OmnibusMatcher(Map<String, Map<String, Integer>> dictionaries) {
        if (dictionaries == null) dictionaries = new HashMap<>();
        this.dictionaries = dictionaries;
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        List<Matcher> matchers = new ArrayList<>();
        matchers.add(new DictionaryMatcher(dictionaries));
        matchers.add(new ReverseDictionaryMatcher(dictionaries));
        matchers.add(new L33tMatcher(dictionaries));
        matchers.add(new SpatialMatcher());
        matchers.add(new RepeatMatcher());
        matchers.add(new SequenceMatcher());
        matchers.add(new RegexMatcher());
        matchers.add(new DateMatcher());
        for (Matcher matcher: matchers) matches.addAll(matcher.execute(password));
        return this.sorted(matches);
    }
}
