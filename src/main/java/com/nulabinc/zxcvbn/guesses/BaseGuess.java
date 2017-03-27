package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Guess;

public abstract class BaseGuess implements Guess {

    protected static int nCk(int n, int k) {
        // http://blog.plover.com/math/choose.html
        if (k > n) return 0;
        if (k == 0) return 1;
        int r = 1;
        for (int d = 1; d <= k; d++) {
            r *= n;
            r /= d;
            n -= 1;
        }
        return r;
    }

}
