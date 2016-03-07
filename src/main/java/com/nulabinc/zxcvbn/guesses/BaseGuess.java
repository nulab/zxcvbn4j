package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Guess;
import com.nulabinc.zxcvbn.matchers.Keyboard;

import java.util.List;
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

}
