package com.nulabinc.zxcvbn.matchers;

import java.util.List;

/**
 * Represents a matcher responsible for identifying patterns within passwords.
 *
 * <p>Implementations of this interface provide specific matching strategies to detect various
 * patterns such as dictionary words, sequences, and spatial patterns.
 *
 * @see Match
 */
public interface Matcher {
  /**
   * Analyzes the given password and returns a list of detected patterns as {@link Match} objects.
   *
   * @param password the password to analyze for patterns.
   * @return a list of matches identifying patterns found within the password.
   */
  List<Match> execute(CharSequence password);
}
