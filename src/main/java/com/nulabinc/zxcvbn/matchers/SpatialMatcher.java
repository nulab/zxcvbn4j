package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.WipeableString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SpatialMatcher extends BaseMatcher {

  private static final Pattern SHIFTED_RX =
      Pattern.compile("[~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?]");

  private static final List<String> EMPTY_ADJACENTS = Collections.emptyList();

  private final Map<String, Keyboard> keyboards;

  public SpatialMatcher(Context context, Map<String, Keyboard> keyboardMap) {
    super(context);
    this.keyboards = new LinkedHashMap<>(keyboardMap);
  }

  public SpatialMatcher(Context context) {
    this(context, context.getKeyboardMap());
  }

  @Override
  public List<Match> execute(CharSequence password) {
    List<Match> matches = new ArrayList<>();
    for (Keyboard keyboard : this.keyboards.values()) {
      matches.addAll(findSpatialMatchesInKeyboard(password, keyboard));
    }
    return this.sorted(matches);
  }

  private List<Match> findSpatialMatchesInKeyboard(CharSequence password, Keyboard keyboard) {
    List<Match> matches = new ArrayList<>();
    int curCharIndex = 0;
    while (curCharIndex < password.length() - 1) {
      curCharIndex = processSpatialMatch(password, keyboard, matches, curCharIndex);
    }
    return matches;
  }

  private int processSpatialMatch(
      CharSequence password, Keyboard keyboard, List<Match> matches, int curCharIndex) {
    int nextCharIndex = curCharIndex + 1;
    int lastDirection = 0;
    int turns = 0;
    int shiftedCount = calculateShiftedCount(keyboard, password.charAt(curCharIndex));
    final Map<Character, List<String>> graph = keyboard.getAdjacencyGraph();
    while (true) {
      char prevChar = password.charAt(nextCharIndex - 1);
      List<String> adjacents = graph.containsKey(prevChar) ? graph.get(prevChar) : EMPTY_ADJACENTS;
      AdjacentSearchResult result = findAdjacent(password, nextCharIndex, adjacents);
      if (result.found) {
        nextCharIndex++;
        shiftedCount += result.shiftedCount;
        if (lastDirection != result.foundDirection) {
          turns++;
          lastDirection = result.foundDirection;
        }
      } else {
        if (nextCharIndex - curCharIndex > 2) {
          matches.add(
              MatchFactory.createSpatialMatch(
                  curCharIndex,
                  nextCharIndex - 1,
                  WipeableString.copy(password, curCharIndex, nextCharIndex),
                  keyboard.getName(),
                  turns,
                  shiftedCount));
        }
        return nextCharIndex;
      }
    }
  }

  private int calculateShiftedCount(Keyboard keyboard, char charAt) {
    return (keyboard.isSlanted() && SHIFTED_RX.matcher(String.valueOf(charAt)).find()) ? 1 : 0;
  }

  private static class AdjacentSearchResult {
    boolean found;
    int foundDirection;
    int shiftedCount;

    AdjacentSearchResult(boolean found, int foundDirection, int shiftedCount) {
      this.found = found;
      this.foundDirection = foundDirection;
      this.shiftedCount = shiftedCount;
    }
  }

  private AdjacentSearchResult findAdjacent(
      CharSequence password, int curCharIndex, List<String> adjacents) {
    int curDirection = -1;
    if (curCharIndex < password.length()) {
      char curChar = password.charAt(curCharIndex);
      String curString = String.valueOf(curChar);
      for (String adj : adjacents) {
        curDirection++;
        int foundAdjacentIndex = adj != null ? adj.indexOf(curString) : -1;
        if (foundAdjacentIndex != -1) {
          return new AdjacentSearchResult(true, curDirection, foundAdjacentIndex == 1 ? 1 : 0);
        }
      }
    }
    return new AdjacentSearchResult(false, 0, 0);
  }
}
