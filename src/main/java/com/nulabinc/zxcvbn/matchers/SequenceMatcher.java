package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceMatcher extends BaseMatcher {

    private static final Map<String, String> SEQUENCES = new HashMap<>();
    static {
        SEQUENCES.put("lower", "abcdefghijklmnopqrstuvwxyz");
        SEQUENCES.put("upper", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        SEQUENCES.put("digits", "0123456789");
    }

    protected int mod(int n, int m) {
        return ((n % m) + m) % m; //  mod impl that works for negative numbers
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        for (Map.Entry<String, String> sequenceRef: SEQUENCES.entrySet()) {
            String sequenceName = sequenceRef.getKey();
            String sequence = sequenceRef.getValue();
            for (int direction: new Integer[]{1, -1}) {
                int i = 0;
                while ( i < password.length()) {
                    if (sequence.indexOf(password.charAt(i)) < 0) {
                        i += 1;
                        continue;
                    }
                    int j = i + 1;
                    int sequencePosition = sequence.indexOf(password.charAt(i));
                    while (j < password.length()) {
                        // mod by sequence length to allow sequences to wrap around: xyzabc
                        int nextSequencePosition = mod(sequencePosition + direction, sequence.length());
                        if (!(sequence.indexOf(password.charAt(j)) == nextSequencePosition)) {
                            break;
                        }
                        j += 1;
                        sequencePosition = nextSequencePosition;
                    }
                    j -= 1;
                    if (j - i + 1 > 1) {
                        matches.add(MatchFactory.createSequenceMatch(i, j, password.substring(i, j + 1), sequenceName, sequence.length(), direction == 1));
                    }
                    i = j + 1;
                }
            }
        }
        return this.sorted(matches);
    }
}
