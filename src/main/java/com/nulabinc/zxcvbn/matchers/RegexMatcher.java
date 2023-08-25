package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexMatcher extends BaseMatcher {

  private static final Map<String, Pattern> PATTERNS = new HashMap<>();

  static {
    PATTERNS.put("recent_year", Pattern.compile("19\\d\\d|200\\d|201\\d|202\\d"));
  }

  public RegexMatcher(final Context context) {
    super(context);
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    for (Map.Entry<String, Pattern> patternRef : PATTERNS.entrySet()) {
      String name = patternRef.getKey();
      java.util.regex.Matcher matcher = patternRef.getValue().matcher(password);
      while (matcher.find()) {
        CharSequence token = new WipeableString(matcher.group());
        matches.add(
            MatchFactory.createRegexMatch(
                matcher.start(), matcher.start() + token.length() - 1, token, name, matcher));
      }
    }
    return this.sorted(matches);
  }
}
