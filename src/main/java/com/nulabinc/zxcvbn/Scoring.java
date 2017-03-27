package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.guesses.*;
import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.matchers.MatchFactory;

import java.util.*;

public class Scoring {

    public static final int REFERENCE_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    public static final int MIN_GUESSES_BEFORE_GROWING_SEQUENCE = 10000;

    public static double log10(double n) {
        return Math.log(n) / Math.log(10);
    }

    public static Strength mostGuessableMatchSequence(String password, List<Match> matches) {
        return mostGuessableMatchSequence(password, matches, false);
    }

    public static Strength mostGuessableMatchSequence(String password, List<Match> matches, boolean excludeAdditive) {
        final int n = password.length();
        final List<List<Match>> matchesByJ = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            matchesByJ.add(new ArrayList<Match>());
        }
        for (Match m : matches) {
            matchesByJ.get(m.j).add(m);
        }
        for(List<Match> lst : matchesByJ) {
            Collections.sort(lst, new Comparator<Match>() {
                @Override
                public int compare(Match m1, Match m2) {
                    return m1.i - m2.i;
                }
            });
        }
        final Optimal optimal = new Optimal(n);
        for (int k = 0; k < n; k++) {
            for(Match m :matchesByJ.get(k)) {
                if (m.i > 0) {
                    for(Map.Entry<Integer, Match> entry : optimal.m.get(m.i - 1).entrySet()) {
                        int l = entry.getKey();
                        update(password, m, l + 1, optimal, excludeAdditive);
                    }
                } else {
                    update(password, m, 1, optimal, excludeAdditive);
                }
            }
            bruteforceUpdate(password, k, optimal, excludeAdditive);
        }
        List<Match> optimalMatchSequence = unwind(n, optimal);
        Integer optimalL = optimalMatchSequence.size();
        double guesses = password.length() == 0 ? 1 : optimal.g.get(n - 1).get(optimalL);
        Strength strength = new Strength();
        strength.setPassword(password);
        strength.setGuesses(guesses);
        strength.setGuessesLog10(log10(guesses));
        strength.setSequence(optimalMatchSequence);
        return strength;
    }

    private static void update(String password, Match m, int l, Optimal optimal, boolean excludeAdditive) {
        int k = m.j;
        double pi = new EstimateGuess(password).exec(m);
        if (l > 1) {
            pi *= optimal.pi.get(m.i - 1).get(l - 1);
        }
        if (Double.isInfinite(pi)) {
            pi = Double.MAX_VALUE;
        }
        double g = factorial(l) * pi;
        if (Double.isInfinite(g)) {
            g = Double.MAX_VALUE;
        }
        if (!excludeAdditive) {
            g += Math.pow(MIN_GUESSES_BEFORE_GROWING_SEQUENCE, l - 1);
            if (Double.isInfinite(g)) {
                g = Double.MAX_VALUE;
            }
        }
        for (Map.Entry<Integer, Double> competing : optimal.g.get(k).entrySet()) {
            if (competing.getKey() > l) {
                continue;
            }
            if (competing.getValue() <= g) {
                return;
            }
        }
        optimal.g.get(k).put(l, g);
        optimal.m.get(k).put(l, m);
        optimal.pi.get(k).put(l, pi);
    }

    private static void bruteforceUpdate(String password, int k, Optimal optimal, boolean excludeAdditive) {
        Match m = makeBruteforceMatch(password, 0, k);
        update(password, m, 1, optimal, excludeAdditive);
        for (int i = 1; i <= k; i++) {
            m = makeBruteforceMatch(password, i, k);
            for (Map.Entry<Integer, Match> entry : optimal.m.get(i - 1).entrySet()) {
                int l = entry.getKey();
                Match last_m = entry.getValue();
                if (last_m.pattern == Pattern.Bruteforce) {
                    continue;
                } else {
                    update(password, m, l + 1, optimal, excludeAdditive);
                }
            }
        }

    }

    private static List<Match> unwind(int n, Optimal optimal) {
        List<Match> optimalMatchSequence = new ArrayList<>();
        int k = n - 1;
        if (0 <= k) {
            Integer l = null;
            Double g = Double.POSITIVE_INFINITY;
            for (Map.Entry<Integer, Double> candidate : optimal.g.get(k).entrySet()) {
                if (candidate.getValue() < g) {
                    l = candidate.getKey();
                    g = candidate.getValue();
                }
            }
            while (k >= 0) {
                Match m = optimal.m.get(k).get(l);
                optimalMatchSequence.add(0, m);
                k = m.i - 1;
                l--;
            }
        }
        return optimalMatchSequence;
    }

    private static Match makeBruteforceMatch(String password, int i, int j) {
        return MatchFactory.createBruteforceMatch(i, j, password.substring(i, j + 1));
    }

    private static int factorial(int n) {
        if (n < 2) return 1;
        int f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }

    private static class Optimal {

        public final List<Map<Integer, Match>> m = new ArrayList<>();

        public final List<Map<Integer, Double>> pi = new ArrayList<>();

        public final List<Map<Integer, Double>> g = new ArrayList<>();

        public Optimal(int n) {
            for (int i = 0; i < n; i++) {
                m.add(new HashMap<Integer, Match>());
                pi.add(new HashMap<Integer, Double>());
                g.add(new HashMap<Integer, Double>());
            }
        }
    }

}
