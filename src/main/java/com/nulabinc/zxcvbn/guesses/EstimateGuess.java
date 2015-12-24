package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Guess;
import com.nulabinc.zxcvbn.Scoring;
import com.nulabinc.zxcvbn.matchers.Match;

public class EstimateGuess extends BaseGuess {

    private final String password;

    public EstimateGuess(String password) {
        this.password = password;
    }

    @Override
    public double exec(Match match) {
        if (match.guesses != null) return match.guesses;
        int minGuesses = 1;
        if (match.token.length() < password.length()) {
            minGuesses = match.token.length() == 1 ? MIN_SUBMATCH_GUESSES_SINGLE_CHAR : MIN_SUBMATCH_GUESSES_MULTI_CHAR;
        }
        final Guess guess;
        switch (match.pattern) {
            case Bruteforce: guess = new BruteforceGuess(); break;
            case Dictionary: guess = new DictionaryGuess(); break;
            case Spatial: guess = new SpatialGuess(); break;
            case Repeat: guess = new RepeatGuess(); break;
            case Sequence: guess = new SequenceGuess(); break;
            case Regex: guess = new RegexGuess(); break;
            case Date: guess = new DateGuess(); break;
            default: guess = null; break;
        }
        double guesses = guess != null ? guess.exec(match) : 0;
        match.guesses = Math.max(guesses, minGuesses);
        match.guessesLog10 = Scoring.log10(match.guesses);
        return  match.guesses;
    }
}
