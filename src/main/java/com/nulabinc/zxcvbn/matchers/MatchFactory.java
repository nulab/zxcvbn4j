package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Pattern;

import java.util.List;
import java.util.Map;

public class MatchFactory {

    private MatchFactory() { }

    public static Match createBruteforceMatch(int i, int j, String token) {
        return new Match.Builder(Pattern.Bruteforce, i, j, token).build();
    }

    public static Match createDictionaryMatch(int i, int j, String token, String matchedWord, int rank, String dictionaryName) {
        return new Match.Builder(Pattern.Dictionary, i, j, token)
                .matchedWord(matchedWord)
                .rank(rank)
                .dictionaryName(dictionaryName)
                .reversed(false)
                .l33t(false)
                .build();
    }

    public static Match createReversedDictionaryMatch(int i, int j, String token, String matchedWord, int rank, String dictionaryName) {
        return new Match.Builder(Pattern.Dictionary, i, j, token)
                .matchedWord(matchedWord)
                .rank(rank)
                .dictionaryName(dictionaryName)
                .reversed(true)
                .l33t(false)
                .build();
    }

    public static Match createDictionaryL33tMatch(int i, int j, String token, String matchedWord, int rank, String dictionaryName, boolean reversed, Map<Character, Character> sub, String subDisplay) {
        return new Match.Builder(Pattern.Dictionary, i, j, token)
                .matchedWord(matchedWord)
                .rank(rank)
                .dictionaryName(dictionaryName)
                .reversed(reversed)
                .sub(sub)
                .subDisplay(subDisplay)
                .l33t(true)
                .build();
    }

    public static Match createSpatialMatch(int i, int j, String token, String graph, int turns, int shiftedCount) {
        return new Match.Builder(Pattern.Spatial, i, j, token)
                .graph(graph)
                .turns(turns)
                .shiftedCount(shiftedCount)
                .build();
    }

    public static Match createRepeatMatch(int i, int j, String token, String baseToken, double baseGuesses, List<Match> baseMatches, int repeatCount) {
        return new Match.Builder(Pattern.Repeat, i, j, token)
                .baseToken(baseToken)
                .baseGuesses(baseGuesses)
                .baseMatches(baseMatches)
                .repeatCount(repeatCount)
                .build();
    }

    public static Match createSequenceMatch(int i, int j, String token, String sequenceName, int sequenceSpace, boolean ascending) {
        return new Match.Builder(Pattern.Sequence, i, j, token)
                .sequenceName(sequenceName)
                .sequenceSpace(sequenceSpace)
                .ascending(ascending)
                .build();
    }

    public static Match createRegexMatch(int i, int j, String token, String regexName, java.util.regex.Matcher regexMatch) {
        return new Match.Builder(Pattern.Regex, i, j, token)
                .regexName(regexName)
                .regexMatch(regexMatch)
                .build();
    }

    public static Match createDateMatch(int i, int j, String token, String separator, int year, int month, int day) {
        return new Match.Builder(Pattern.Date, i, j, token)
                .separator(separator)
                .year(year)
                .month(month)
                .day(day)
                .build();
    }
}
