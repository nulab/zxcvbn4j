package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.WipeableString;
import com.nulabinc.zxcvbn.matchers.Match;
import java.util.regex.Pattern;

import java.util.Map;

public class DictionaryGuess extends BaseGuess {

    public final static Pattern START_UPPER = Pattern.compile("^[A-Z][^A-Z]+$");
    private final static Pattern END_UPPER = Pattern.compile("^[^A-Z]+[A-Z]$");
    public final static Pattern ALL_UPPER = Pattern.compile("^[^a-z]+$");
    private final static Pattern ALL_LOWER = Pattern.compile("^[^A-Z]+$");

    @Override
    public double exec(Match match) {
        match.baseGuesses = (double) match.rank;
        int uppercaseVariations = uppercaseVariations(match);
        int l33tVariations = l33tVariations(match);
        int reversedVariations = match.reversed ? 2 : 1;
        return match.rank * uppercaseVariations * l33tVariations * reversedVariations;
    }

    public int uppercaseVariations(Match match) {
        CharSequence word = match.token;
        WipeableString lowercaseWord = WipeableString.lowerCase(word);
        if (ALL_LOWER.matcher(word).find(0) || lowercaseWord.equals(word)) return 1;
        for(Pattern pattern: new Pattern[] { START_UPPER, END_UPPER, ALL_UPPER })
            if (pattern.matcher(word).find()) return 2;
        int u = 0;
        int l = 0;
        for (int n = 0; n < word.length(); n++) {
            l +=  Character.isLowerCase(word.charAt(n)) ? 1 : 0;
            u +=  Character.isUpperCase(word.charAt(n)) ? 1 : 0;
        }
        int variations = 0;
        for (int i = 1; i <= Math.min(u, l); i++) variations += nCk(u + l, i);
        lowercaseWord.wipe();
        return variations;
    }

    public int l33tVariations(Match match) {
        if (!match.l33t) return 1;
        int variations = 1;
        for (Map.Entry<Character, Character> subRef : match.sub.entrySet()) {
            Character subbed = subRef.getKey();
            Character unsubbed = subRef.getValue();
            int s = 0;
            int u = 0;
            WipeableString lower = WipeableString.lowerCase(match.token);
            for (char chr: lower.charArray()) {
                if (chr == subbed) s++;
                if (chr == unsubbed) u++;
            }
            if (s == 0 || u == 0) {
                variations *= 2;
            } else {
                int p = Math.min(u, s);
                int possibilities = 0;
                for (int i = 1; i <= p; i++) possibilities += nCk(u + s, i);
                variations *= possibilities;
            }
        }
        return variations;
    }
}
