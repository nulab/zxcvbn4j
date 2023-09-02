package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.MatchSequence;
import com.nulabinc.zxcvbn.Matching;
import com.nulabinc.zxcvbn.Scoring;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RepeatMatcher extends BaseMatcher {

  private static final Pattern GREEDY_PATTERN = Pattern.compile("(.+)\\1+");
  private static final Pattern LAZY_PATTERN = Pattern.compile("(.+?)\\1+");
  private static final Pattern LAZY_ANCHORED_PATTERN = Pattern.compile("^(.+?)\\1+$");

  private final Scoring scoring;
  private final Matching matching;

  public RepeatMatcher(final Context context) {
    super(context);
    this.scoring = new Scoring(context);
    this.matching = new Matching(context, new ArrayList<String>());
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    int passwordLength = password.length();
    int lastIndex = 0;

    while (lastIndex < passwordLength) {
      java.util.regex.Matcher greedyMatcher =
          createRegionMatcher(GREEDY_PATTERN, password, lastIndex, passwordLength);
      java.util.regex.Matcher lazyMatcher =
          createRegionMatcher(LAZY_PATTERN, password, lastIndex, passwordLength);

      if (!greedyMatcher.find()) {
        break;
      }

      ChosenMatch chosenMatch = chooseMatch(greedyMatcher, lazyMatcher);
      Match repeatMatch =
          createRepeatMatch(
              chosenMatch.baseToken, chosenMatch.matchResult, chosenMatch.start, chosenMatch.end);
      matches.add(repeatMatch);
      lastIndex = chosenMatch.end + 1;
    }

    return matches;
  }

  private java.util.regex.Matcher createRegionMatcher(
      Pattern pattern, CharSequence password, int start, int end) {
    java.util.regex.Matcher matcher = pattern.matcher(password);
    matcher.region(start, end);
    return matcher;
  }

  private ChosenMatch chooseMatch(
      java.util.regex.Matcher greedyMatcher, java.util.regex.Matcher lazyMatcher) {

    String greedyMatchResult = greedyMatcher.group(0);
    String lazyMatchResult = lazyMatcher.find() ? lazyMatcher.group(0) : "";
    boolean isGreedyLonger = greedyMatchResult.length() > lazyMatchResult.length();

    String matchResult;
    CharSequence baseToken;
    int start;
    int end;

    if (isGreedyLonger) {
      matchResult = greedyMatchResult;
      baseToken = deriveBaseTokenFromGreedyMatchResult(greedyMatchResult);
      start = greedyMatcher.start(0);
      end = start + greedyMatchResult.length() - 1;
    } else {
      matchResult = lazyMatchResult;
      baseToken = lazyMatcher.group(1);
      start = lazyMatcher.start(0);
      end = start + lazyMatchResult.length() - 1;
    }
    return new ChosenMatch(matchResult, baseToken, start, end);
  }

  private CharSequence deriveBaseTokenFromGreedyMatchResult(String greedyMatchResult) {
    java.util.regex.Matcher lazyAnchoredMatcher = LAZY_ANCHORED_PATTERN.matcher(greedyMatchResult);
    return lazyAnchoredMatcher.find() ? lazyAnchoredMatcher.group(1) : greedyMatchResult;
  }

  private Match createRepeatMatch(CharSequence baseToken, String matchResult, int start, int end) {
    List<Match> omnimatch = matching.omnimatch(baseToken);
    MatchSequence baseAnalysis = scoring.calculateMostGuessableMatchSequence(baseToken, omnimatch);
    CharSequence wipeableBaseToken = new WipeableString(baseToken);
    int repeatCount = matchResult.length() / wipeableBaseToken.length();
    return MatchFactory.createRepeatMatch(
        start,
        end,
        matchResult,
        wipeableBaseToken,
        baseAnalysis.getGuesses(),
        baseAnalysis.getSequence(),
        repeatCount);
  }

  private static class ChosenMatch {
    final String matchResult;
    final CharSequence baseToken;
    final int start;
    final int end;

    public ChosenMatch(String matchResult, CharSequence baseToken, int start, int end) {
      this.matchResult = matchResult;
      this.baseToken = baseToken;
      this.start = start;
      this.end = end;
    }
  }
}
