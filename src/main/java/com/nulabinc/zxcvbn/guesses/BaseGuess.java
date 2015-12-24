package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Guess;

import java.util.Map;

public abstract class BaseGuess implements Guess {

    protected static int nCk(int n, int k) {
        // http://blog.plover.com/math/choose.html
        if (k > n) return 0;
        int r = 1;
        for (int d = 1; d <= k; d++) {
            r *= n;
            r /= d;
            n -= 1;
        }
        return r;
    }

    protected static int calcAverageDegree(Map<Character, String[]> graph) {
        int average = 0;
        for (Map.Entry<Character, String[]> graphRef: graph.entrySet()) {
            Character key = graphRef.getKey();
            String[] neighbors = graphRef.getValue();
            for (String neighbor: neighbors) if (neighbor != null) average += 1;
            average /= graph.size();
        }
        return average;
    }
}
