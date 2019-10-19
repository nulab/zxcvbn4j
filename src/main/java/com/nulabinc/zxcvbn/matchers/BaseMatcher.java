package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Matcher;
import com.nulabinc.zxcvbn.WipeableString;

import java.util.*;

public abstract class BaseMatcher implements Matcher {

    protected List<Match> sorted(List<Match> matches) {
        Collections.sort(matches, new Comparator<Match>() {
            @Override
            public int compare(Match o1, Match o2) {
                int c = o1.i - o2.i;
                if (c != 0) {
                    return c;
                } else {
                    return (o1.j - o2.j);
                }
            }
        });
        return matches;
    }

    protected CharSequence translate(CharSequence string, Map<Character, Character> chrMap) {
        List<Character> characters = new ArrayList<>();
        for (int n = 0; n < string.length(); n++) {
            char chr = string.charAt(n);
            characters.add(chrMap.containsKey(chr) ? chrMap.get(chr) : chr);
        }
        StringBuilder sb = new StringBuilder();
        for (char c: characters) {
            sb.append(c);
        }
        WipeableString result = new WipeableString(sb);
        WipeableString.wipeIfPossible(sb);
        return result;
    }

    protected List<Match> extend(List<Match> lst, List<Match> lst2) {
        lst.addAll(lst2);
        return lst;
    }

}