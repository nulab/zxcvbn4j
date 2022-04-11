package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.Guess;
import com.nulabinc.zxcvbn.Scoring;
import com.nulabinc.zxcvbn.matchers.Match;

public class EstimateGuess extends BaseGuess {

    private final CharSequence password;

    public EstimateGuess(Context context, CharSequence password) {
        super(context);
        this.password = password;
    }

    @Override
    public double exec(Match match) {
        if (match.guesses != null) return match.guesses;
        int minGuesses = 1;
        if (match.tokenLength() < password.length()) {
            minGuesses = match.tokenLength() == 1 ? MIN_SUBMATCH_GUESSES_SINGLE_CHAR : MIN_SUBMATCH_GUESSES_MULTI_CHAR;
        }
        final Guess guess;
        switch (match.pattern) {
            case Bruteforce: guess = new BruteforceGuess(this.getContext()); break;
            case Dictionary: guess = new DictionaryGuess(this.getContext()); break;
            case Spatial: guess = new SpatialGuess(this.getContext()); break;
            case Repeat: guess = new RepeatGuess(this.getContext()); break;
            case Sequence: guess = new SequenceGuess(this.getContext()); break;
            case Regex: guess = new RegexGuess(this.getContext()); break;
            case Date: guess = new DateGuess(this.getContext()); break;
            default: guess = null; break;
        }
        double guesses = guess != null ? guess.exec(match) : 0;
        match.guesses = Math.max(guesses, minGuesses);
        match.guessesLog10 = Scoring.log10(match.guesses);
        return  match.guesses;
    }
}
