package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.Collections;
import java.util.List;

public class MatchSequence {

  private final List<Match> sequence;
  private final double guesses;

  public MatchSequence(List<Match> sequence, double guesses) {
    this.sequence = Collections.unmodifiableList(sequence);
    this.guesses = guesses;
  }

  public List<Match> getSequence() {
    return sequence;
  }

  public double getGuesses() {
    return guesses;
  }
}
