package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Match;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SequenceGuess extends BaseGuess {

    private final static List<Character> START_POINTS = Arrays.asList('a', 'A', 'z', 'Z', '0', '1', '9');

    public SequenceGuess(final Context context) {
        super(context);
    }

    @Override
    public double exec(Match match) {
        final char firstChr = match.token.charAt(0);
        double baseGuesses;
        if (START_POINTS.contains(firstChr)) {
            baseGuesses = 4;
        } else {
            if (Pattern.compile("\\d").matcher(String.valueOf(firstChr)).find()) {
                baseGuesses = 10;
            } else {
                baseGuesses = 26;
            }
        }
        if (!match.ascending) baseGuesses *= 2;
        return baseGuesses * match.tokenLength();
    }
}
