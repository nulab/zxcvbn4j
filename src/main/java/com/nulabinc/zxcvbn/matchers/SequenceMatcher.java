package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SequenceMatcher extends BaseMatcher {

    private static final int MAX_DELTA = 5;

    private static Match update(String password, int i, int j, Integer delta) {
        Match match = null;
        if ((j - i) > 1 || (delta != null && Math.abs(delta) == 1)) {
            String token;
            if (0 < Math.abs(delta) && Math.abs(delta) <= MAX_DELTA) {
                token = password.substring(i, j + 1);
                final String sequenceName;
                final int sequenceSpace;
                if (Pattern.compile("^[a-z]+$").matcher(token).find()) {
                    sequenceName = "lower";
                    sequenceSpace = 26;
                } else if (Pattern.compile("^[A-Z]+$").matcher(token).find()) {
                    sequenceName = "upper";
                    sequenceSpace = 26;
                } else if (Pattern.compile("^\\d+$").matcher(token).find()) {
                    sequenceName = "digits";
                    sequenceSpace = 10;
                } else {
                    // conservatively stick with roman alphabet size.
                    // (this could be improved)
                    sequenceName = "unicode";
                    sequenceSpace = 26;
                }
                match = MatchFactory.createSequenceMatch(i, j, token, sequenceName, sequenceSpace, delta > 0);
            }
        }
        return match;
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        // Identifies sequences by looking for repeated differences in unicode codepoint.
        // this allows skipping, such as 9753, and also matches some extended unicode sequences
        // such as Greek and Cyrillic alphabets.
        //
        // for example, consider the input 'abcdb975zy'
        //
        // password: a   b   c   d   b    9   7   5   z   y
        // index:    0   1   2   3   4    5   6   7   8   9
        // delta:      1   1   1  -2  -41  -2  -2  69   1
        //
        // expected result:
        // [(i, j, delta), ...] = [(0, 3, 1), (5, 7, -2), (8, 9, 1)]
        if (password == null || password.length() == 1) return matches;
        int i = 0;
        Integer lastDelta = null;
        for (int k = 1; k < password.length(); k++) {
            int delta = password.codePointAt(k) - password.codePointAt(k - 1);
            if (lastDelta == null) {
                lastDelta = delta;
            }
            if (delta == lastDelta) continue;
            int j = k - 1;
            Match match = update(password, i, j, lastDelta);
            if (match != null) matches.add(match);
            i = j;
            lastDelta = delta;
        }
        Match match = update(password, i, password.length() - 1, lastDelta);
        if (match != null) matches.add(match);
        return matches;
    }
}
