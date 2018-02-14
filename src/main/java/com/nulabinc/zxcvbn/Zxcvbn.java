package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zxcvbn {

    public Zxcvbn() {
    }

    public Strength measure(String password) {
        return measure(password, null);
    }

    public Strength measure(String password, List<String> sanitizedInputs) {
        if (password == null) {
            throw new IllegalArgumentException("Password is null.");
        }
        List<String> lowerSanitizedInputs;
        if (sanitizedInputs != null && !sanitizedInputs.isEmpty()) {
            lowerSanitizedInputs = new ArrayList<>(sanitizedInputs.size());
            for (String sanitizedInput : sanitizedInputs) {
                lowerSanitizedInputs.add(sanitizedInput.toLowerCase());
            }
        } else {
            lowerSanitizedInputs = Collections.emptyList();
        }
        long start = time();
        Matching matching = createMatching(lowerSanitizedInputs);
        List<Match> matches = matching.omnimatch(password);
        Strength strength = Scoring.mostGuessableMatchSequence(password, matches);
        strength.setCalcTime(time() - start);
        AttackTimes attackTimes = TimeEstimates.estimateAttackTimes(strength.getGuesses());
        strength.setCrackTimeSeconds(attackTimes.getCrackTimeSeconds());
        strength.setCrackTimesDisplay(attackTimes.getCrackTimesDisplay());
        strength.setScore(attackTimes.getScore());
        strength.setFeedback(Feedback.getFeedback(strength.getScore(), strength.getSequence()));
        return strength;
    }

    protected Matching createMatching(List<String> lowerSanitizedInputs) {
        return new Matching(lowerSanitizedInputs);
    }

    private long time() {
        return System.nanoTime();
    }

}
