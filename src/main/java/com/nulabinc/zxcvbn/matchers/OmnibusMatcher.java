package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OmnibusMatcher extends BaseMatcher {

    private final Map<String, Map<String, Integer>> dictionaryMap;

    public OmnibusMatcher(Context context, Map<String, Map<String, Integer>> dictionaries) {
        super(context);
        if (dictionaries == null) {
            this.dictionaryMap = new HashMap<>();
        } else {
            this.dictionaryMap = dictionaries;
        }
    }

    @Override
    public List<Match> execute(CharSequence password) {
        List<Matcher> matchers = new ArrayList<>();
        matchers.add(new DictionaryMatcher(this.getContext(), dictionaryMap));
        matchers.add(new ReverseDictionaryMatcher(this.getContext(), dictionaryMap));
        matchers.add(new L33tMatcher(this.getContext(), dictionaryMap));
        matchers.add(new SpatialMatcher(this.getContext()));
        matchers.add(new RepeatMatcher(this.getContext()));
        matchers.add(new SequenceMatcher(this.getContext()));
        matchers.add(new RegexMatcher(this.getContext()));
        matchers.add(new DateMatcher(this.getContext()));
        List<Match> matches = new ArrayList<>();
        for (Matcher matcher: matchers) matches.addAll(matcher.execute(password));
        return this.sorted(matches);
    }

}
