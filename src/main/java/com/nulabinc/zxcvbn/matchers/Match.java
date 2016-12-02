package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {

    public final Pattern pattern;
    public final int i;
    public final int j;
    public final String token;
    public final String matchedWord;
    public final int rank;
    public final String dictionaryName;
    public final boolean reversed;
    public final boolean l33t;
    public final Map<Character, Character> sub;
    public final String subDisplay;
    public final String sequenceName;
    public final int sequenceSpace;
    public final boolean ascending;
    public final String regexName;
    public final java.util.regex.Matcher regexMatch;
    public final String baseToken;
    public final List<Match> baseMatches;
    public final int repeatCount;
    public final String graph;
    public final int turns;
    public final Integer shiftedCount;
    public final String separator;
    public final int year;
    public final int month;
    public final int day;

    public Double baseGuesses;
    public Double guesses;
    public Double guessesLog10;

    private Match(Builder builder) {
        this.pattern = builder.pattern;
        this.i = builder.i;
        this.j = builder.j;
        this.token = builder.token;
        this.matchedWord = builder.matchedWord;
        this.rank = builder.rank;
        this.dictionaryName = builder.dictionaryName;
        this.reversed = builder.reversed;
        this.l33t = builder.l33t;
        if (builder.sub == null) builder.sub = new HashMap<>();
        this.sub = builder.sub;
        this.subDisplay = builder.subDisplay;
        this.sequenceName = builder.sequenceName;
        this.sequenceSpace = builder.sequenceSpace;
        this.ascending = builder.ascending;
        this.regexName = builder.regexName;
        this.regexMatch = builder.regexMatch;
        this.baseToken = builder.baseToken;
        this.baseGuesses = builder.baseGuesses;
        if (builder.baseMatches == null) builder.baseMatches = new ArrayList<>();
        this.baseMatches = builder.baseMatches;
        this.repeatCount = builder.repeatCount;
        this.graph = builder.graph;
        this.turns = builder.turns;
        this.shiftedCount = builder.shiftedCount;
        this.separator = builder.separator;
        this.year = builder.year;
        this.month = builder.month;
        this.day = builder.day;
        this.guesses = builder.guesses;
        this.guessesLog10 = builder.guessesLog10;
    }

    public static class Builder {

        private final Pattern pattern;
        private final int i;
        private final int j;
        private final String token;

        private String matchedWord;
        private int rank;
        private String dictionaryName;
        private boolean reversed;
        private boolean l33t;
        private Map<Character, Character> sub;
        private String subDisplay;
        private String sequenceName;
        private int sequenceSpace;
        private boolean ascending;
        private String regexName;
        private java.util.regex.Matcher regexMatch;
        private String baseToken;
        private double baseGuesses;
        private List<Match> baseMatches;
        private int repeatCount;
        private String graph;
        private int turns;
        private int shiftedCount;
        private String separator;
        private int year;
        private int month;
        private int day;

        private Double guesses;
        private Double guessesLog10;

        public Builder(Pattern pattern, int i, int j, String token) {
            this.pattern = pattern;
            this.i = i;
            this.j = j;
            this.token = token;
        }

        public Builder matchedWord(String matchedWord) {
            this.matchedWord = matchedWord;
            return this;
        }

        public Builder rank(int rank) {
            this.rank = rank;
            return this;
        }

        public Builder dictionaryName(String dictionaryName) {
            this.dictionaryName = dictionaryName;
            return this;
        }

        public Builder reversed(boolean reversed) {
            this.reversed = reversed;
            return this;
        }

        public Builder l33t(boolean l33t) {
            this.l33t = l33t;
            return this;
        }

        public Builder sub(Map<Character, Character> sub) {
            this.sub = sub;
            return this;
        }

        public Builder subDisplay(String subDisplay) {
            this.subDisplay = subDisplay;
            return this;
        }

        public Builder sequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
            return this;
        }

        public Builder sequenceSpace(int sequenceSpace) {
            this.sequenceSpace = sequenceSpace;
            return this;
        }

        public Builder ascending(boolean ascending) {
            this.ascending = ascending;
            return this;
        }

        public Builder regexName(String regexName) {
            this.regexName = regexName;
            return this;
        }

        public Builder regexMatch(java.util.regex.Matcher regexMatch) {
            this.regexMatch = regexMatch;
            return this;
        }

        public Builder baseToken(String baseToken) {
            this.baseToken = baseToken;
            return this;
        }

        public Builder baseGuesses(double baseGuesses) {
            this.baseGuesses = baseGuesses;
            return this;
        }

        public Builder baseMatches(List<Match> baseMatches) {
            this.baseMatches = baseMatches;
            return this;
        }

        public Builder repeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
            return this;
        }

        public Builder graph(String graph) {
            this.graph = graph;
            return this;
        }

        public Builder turns(int turns) {
            this.turns = turns;
            return this;
        }

        public Builder shiftedCount(int shiftedCount) {
            this.shiftedCount = shiftedCount;
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder month(int month) {
            this.month = month;
            return this;
        }

        public Builder day(int day) {
            this.day = day;
            return this;
        }

        public Builder guesses(Double guesses) {
            this.guesses = guesses;
            return this;
        }

        public Builder guessesLog10(Double guessesLog10) {
            this.guessesLog10 = guessesLog10;
            return this;
        }

        public Match build() {
            return new Match(this);
        }
    }

}
