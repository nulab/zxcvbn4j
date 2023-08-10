package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.Matcher;
import com.nulabinc.zxcvbn.WipeableString;

import java.io.Serializable;
import java.util.*;

public abstract class BaseMatcher implements Matcher {

    private final Context context;

    protected BaseMatcher(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected List<Match> sorted(List<Match> matches) {
        Collections.sort(matches, new MatchComparator());
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

    private static class MatchComparator implements Comparator<Match>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Match o1, Match o2) {
            int c = o1.i - o2.i;
            if (c != 0) {
                return c;
            } else {
                return (o1.j - o2.j);
            }
        }
    }

}