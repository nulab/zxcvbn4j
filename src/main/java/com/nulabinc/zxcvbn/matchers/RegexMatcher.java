package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

public class RegexMatcher extends BaseMatcher {

    private static final Map<String, String> REGEXEN = new HashMap<>();
    static {
        REGEXEN.put("recent_year", "19\\d\\d|200\\d|201\\d");
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        for(Map.Entry<String, String> regexenRef: REGEXEN.entrySet()) {
            String name = regexenRef.getKey();
            java.util.regex.Matcher rxMatch = Pattern.compile(regexenRef.getValue()).matcher(password);
            while(rxMatch.find()){
                String token = rxMatch.group();
                matches.add(MatchFactory.createRegexMatch(
                        rxMatch.start(),
                        rxMatch.start() + token.length() - 1,
                        token,
                        name,
                        rxMatch));
            }
        }
        return this.sorted(matches);
    }
}
