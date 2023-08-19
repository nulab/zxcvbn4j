package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.OmnibusMatcher;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Matching {

  private final Context context;

  protected final Map<String, Map<String, Integer>> rankedDictionaries;

  public Matching(Context context, List<String> orderedList) {
    this.context = context;

    final Map<String, Dictionary> dictionaryMap = new LinkedHashMap<>(context.getDictionaryMap());
    dictionaryMap.put("user_inputs", new Dictionary("user_inputs", orderedList));
    this.rankedDictionaries = buildRankedDictionaryMap(dictionaryMap);
  }

  public List<Match> omnimatch(CharSequence password) {
    return new OmnibusMatcher(context, rankedDictionaries).execute(password);
  }

  private static Map<String, Map<String, Integer>> buildRankedDictionaryMap(
      Map<String, Dictionary> dictionaryMap) {
    Map<String, Map<String, Integer>> rankedDictionaries = new HashMap<>();
    for (Dictionary dictionary : dictionaryMap.values()) {
      rankedDictionaries.put(dictionary.getName(), dictionary.getRankedDictionary());
    }
    return rankedDictionaries;
  }
}
