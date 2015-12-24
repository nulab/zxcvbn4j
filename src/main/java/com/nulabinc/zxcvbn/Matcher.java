package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;

import java.util.List;

public interface Matcher {
    public List<Match> execute(String password);
}