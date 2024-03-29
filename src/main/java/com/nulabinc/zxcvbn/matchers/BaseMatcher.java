package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Context;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseMatcher implements Matcher {

  private final Context context;

  protected BaseMatcher(Context context) {
    this.context = context;
  }

  protected Context getContext() {
    return context;
  }

  protected List<Match> sorted(List<Match> matches) {
    Collections.sort(matches, new MatchComparator());
    return matches;
  }

  private static class MatchComparator implements Comparator<Match>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Match o1, Match o2) {
      int c = o1.i - o2.i;
      if (c != 0) {
        return c;
      } else {
        return (o1.j - o2.j);
      }
    }
  }
}
