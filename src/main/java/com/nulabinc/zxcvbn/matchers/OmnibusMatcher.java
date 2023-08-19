package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OmnibusMatcher extends BaseMatcher {

  private final List<Matcher> matchers = new ArrayList<>();

  public OmnibusMatcher(Context context, Map<String, Map<String, Integer>> dictionaries) {
    super(context);
    if (dictionaries == null) {
      dictionaries = new HashMap<>();
    }
    matchers.add(new DictionaryMatcher(getContext(), dictionaries));
    matchers.add(new ReverseDictionaryMatcher(getContext(), dictionaries));
    matchers.add(new L33tMatcher(getContext(), dictionaries));
    matchers.add(new SpatialMatcher(getContext()));
    matchers.add(new RepeatMatcher(getContext()));
    matchers.add(new SequenceMatcher(getContext()));
    matchers.add(new RegexMatcher(getContext()));
    matchers.add(new DateMatcher(getContext()));
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    for (Matcher matcher : matchers) {
      matches.addAll(matcher.execute(password));
    }
    return sorted(matches);
  }
}
