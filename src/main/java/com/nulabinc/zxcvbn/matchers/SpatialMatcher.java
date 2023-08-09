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

  private final Pattern shiftedRx =
      Pattern.compile("[~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?]");

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
      extend(matches, spatialMatchHelper(password, keyboard));
    }
    return this.sorted(matches);
  }

  private List<Match> spatialMatchHelper(CharSequence password, Keyboard keyboard) {
    List<Match> matches = new ArrayList<>();
    int i = 0;
    while (i < password.length() - 1) {
      int j = i + 1;
      int lastDirection = 0;
      int turns = 0;
      int shiftedCount;
      if (keyboard.isSlanted() && shiftedRx.matcher(String.valueOf(password.charAt(i))).find()) {
        shiftedCount = 1;
      } else {
        shiftedCount = 0;
      }
      final Map<Character, List<String>> graph = keyboard.getAdjacencyGraph();
      while (true) {
        Character prevChar = password.charAt(j - 1);
        boolean found = false;
        int foundDirection;
        int curDirection = -1;
        List<String> adjacents =
            graph.containsKey(prevChar) ? graph.get(prevChar) : Collections.<String>emptyList();
        if (j < password.length()) {
          Character curChar = password.charAt(j);
          for (String adj : adjacents) {
            curDirection += 1;
            if (adj != null && adj.contains(String.valueOf(curChar))) {
              found = true;
              foundDirection = curDirection;
              if (adj.indexOf(String.valueOf(curChar)) == 1) {
                shiftedCount += 1;
              }
              if (lastDirection != foundDirection) {
                turns += 1;
                lastDirection = foundDirection;
              }
              break;
            }
          }
        }
        if (found) {
          j += 1;
        } else {
          if (j - i > 2) {
            matches.add(
                MatchFactory.createSpatialMatch(
                    i,
                    j - 1,
                    WipeableString.copy(password, i, j),
                    keyboard.getName(),
                    turns,
                    shiftedCount));
          }
          i = j;
          break;
        }
      }
    }
    return matches;
  }
}
