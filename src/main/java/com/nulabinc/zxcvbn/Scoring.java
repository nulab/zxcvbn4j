package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.*;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.MatchFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scoring {

    public static final int REFERENCE_YEAR = 2000;

    public static final int MIN_GUESSES_BEFORE_GROWING_SEQUENCE = 10000;

    public static double log10(double n) {
        return Math.log(n) / Math.log(10);
    }

    public static Strength mostGuessableMatchSequence(String password, List<Match> matches) {
        List<List<Double>> optimalProduct = new ArrayList<>();
        List<List<Match>> backPointers = new ArrayList<>();
        int maxL = 0;
        int optimalL = 0;
        double optimalScore = 0;
        for (int k = 0; k < password.length(); k++) {
            backPointers.add(new ArrayList<Match>());
            optimalProduct.add(new ArrayList<Double>());
            optimalScore = Double.POSITIVE_INFINITY;
            for (int prevL = 0; prevL <= maxL ; prevL++) {
                boolean considerBruteforce = true;
                int bfJ = k;
                int bfI = 0;
                int newL = 0;
                if (prevL == 0) {
                    bfI = 0;
                    newL = 1;
                } else if (indexExists(backPointers, k - 1)
                        && indexExists(backPointers.get(k - 1), prevL)) {
                    if (Pattern.Bruteforce == backPointers.get(k - 1).get(prevL).pattern) {
                        bfI = backPointers.get(k - 1).get(prevL).i;
                        newL = prevL;
                    } else {
                        bfI = k;
                        newL = prevL + 1;
                    }
                } else {
                    considerBruteforce = false;
                }
                if (considerBruteforce) {
                    Match bfMatch = makeBruteforceMatch(password, bfI, bfJ);
                    int prevJ = k - bfMatch.token.length(); // end of preceeding match
                    double candidateProduct = new EstimateGuess(password).exec(bfMatch);
                    if (newL > 1) candidateProduct *= optimalProduct.get(prevJ).get(newL - 1);
                    double candidateScore = score(candidateProduct, newL, false);
                    if (candidateScore < optimalScore) {
                        optimalScore = candidateScore;
                        addOrSet(optimalProduct.get(k), newL, candidateProduct);
                        optimalL = newL;
                        maxL = Math.max(maxL, newL);
                        addOrSet(backPointers.get(k), newL, bfMatch);
                    }
                }
                for (Match match: matches) {
                    if (match.j != k) continue;
                    int i = match.i;
                    int j = match.j;
                    if (prevL == 0) {
                        if (i != 0) continue;
                    } else {
                        if (!indexExists(optimalProduct, i - 1)
                                || !indexExists(optimalProduct.get(i - 1), prevL)) {
                            continue;
                        }
                    }
                    double candidateProduct = new EstimateGuess(password).exec(match);
                    if (prevL > 0) {
                        candidateProduct *= optimalProduct.get(i-1).get(prevL);
                    }
                    double candidateScore = score(candidateProduct, prevL + 1, false);
                    if (candidateScore < optimalScore) {
                        optimalScore = candidateScore;
                        addOrSet(optimalProduct.get(k), prevL + 1, candidateScore);
                        optimalL = prevL + 1;
                        maxL = Math.max(maxL, prevL + 1);
                        addOrSet(backPointers.get(k), prevL + 1, match);
                    }
                }
            }
        }
        List<Match> matchSequence = new ArrayList<>();
        int l = optimalL;
        int k = password.length() - 1;
        while(k >= 0) {
            Match match = backPointers.get(k).get(l);
            matchSequence.add(match);
            k = match.i - 1;
            l -= 1;
        }
        Collections.reverse(matchSequence);
        double guesses = password.length() == 0 ? 1 : optimalScore;
        Strength strength = new Strength();
        strength.setPassword(password);
        strength.setGuesses(guesses);
        strength.setGuessesLog10(log10(guesses));
        strength.setSequence(matchSequence);
        return strength;
    }

    private static boolean indexExists(final List list, final int index) {
        return index >= 0 && index < list.size() && list.get(index) != null;
    }

    private static<T> void addOrSet(final List<T> list, final int index, T element) {
        if (!indexExists(list, index)) {
            int diff =  index - (list.size() - 1);
            for (int i = 0; i < diff; i++) {
                list.add(null);
            }
        }
        list.set(index, element);
    }

    private static Match makeBruteforceMatch(String password, int i, int j) {
        return MatchFactory.createBruteforceMatch(i, j, password.substring(i, j + 1));
    }

    private static double score(double guessProduct, int sequenceLength, boolean excludeAdditive) {
        double result = factorial(sequenceLength) * guessProduct;
        if (!excludeAdditive) result += Math.pow(MIN_GUESSES_BEFORE_GROWING_SEQUENCE, sequenceLength - 1);
        return result;
    }

    private static int factorial(int n) {
        if (n < 2) return 1;
        int f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }
}
