package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Zxcvbn {

    private final Context context;

    public Zxcvbn() {
        try {
            context = StandardContext.build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    Zxcvbn(Context context) {
        this.context = context;
    }

    public Strength measure(CharSequence password) {
        return measure(password, null);
    }

    public Strength measure(CharSequence password, List<String> sanitizedInputs) {
        if (password == null) {
            throw new IllegalArgumentException("Password is null.");
        }
        List<String> lowerSanitizedInputs;
        if (sanitizedInputs != null && !sanitizedInputs.isEmpty()) {
            lowerSanitizedInputs = new ArrayList<>(sanitizedInputs.size());
            for (String sanitizedInput : sanitizedInputs) {
                lowerSanitizedInputs.add(sanitizedInput.toLowerCase(Locale.getDefault()));
            }
        } else {
            lowerSanitizedInputs = Collections.emptyList();
        }
        long start = time();
        Matching matching = createMatching(lowerSanitizedInputs);
        List<Match> matches = matching.omnimatch(password);
        Scoring scoring = new Scoring(this.context);
        Strength strength = scoring.mostGuessableMatchSequence(password, matches);
        strength.setCalcTime(time() - start);
        AttackTimes attackTimes = TimeEstimates.estimateAttackTimes(strength.getGuesses());
        strength.setCrackTimeSeconds(attackTimes.getCrackTimeSeconds());
        strength.setCrackTimesDisplay(attackTimes.getCrackTimesDisplay());
        strength.setScore(attackTimes.getScore());
        strength.setFeedback(Feedback.getFeedback(strength.getScore(), strength.getSequence()));
        return strength;
    }

    protected Matching createMatching(List<String> lowerSanitizedInputs) {
        return new Matching(this.context, lowerSanitizedInputs);
    }

    private long time() {
        return System.nanoTime();
    }

}
