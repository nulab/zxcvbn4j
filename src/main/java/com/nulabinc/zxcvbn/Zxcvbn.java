package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Zxcvbn {

  private final Context context;

  public Zxcvbn() {
    try {
      context = StandardContext.build();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  Zxcvbn(Context context) {
    this.context = context;
  }

  public Strength measure(CharSequence password) {
    return measure(password, null);
  }

  public Strength measure(CharSequence password, List<String> sanitizedInputs) {
    if (password == null) {
      throw new IllegalArgumentException("Password is null.");
    }
    List<String> lowerSanitizedInputs;
    if (sanitizedInputs != null && !sanitizedInputs.isEmpty()) {
      lowerSanitizedInputs = new ArrayList<>(sanitizedInputs.size());
      for (String sanitizedInput : sanitizedInputs) {
        lowerSanitizedInputs.add(sanitizedInput.toLowerCase(Locale.getDefault()));
      }
    } else {
      lowerSanitizedInputs = Collections.emptyList();
    }
    long start = time();
    Matching matching = new Matching(context, lowerSanitizedInputs);
    List<Match> matches = matching.omnimatch(password);
    Scoring scoring = new Scoring(context);
    MatchSequence matchSequence = scoring.calculateMostGuessableMatchSequence(password, matches);
    long end = time() - start;
    return new Strength(password, matchSequence.getGuesses(), matchSequence.getSequence(), end);
  }

  private long time() {
    return System.nanoTime();
  }
}
