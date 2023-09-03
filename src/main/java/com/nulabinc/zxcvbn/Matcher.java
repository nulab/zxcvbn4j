package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.List;

/**
 * Represents a matcher responsible for identifying patterns within passwords.
 *
 * <p>Implementations of this interface provide specific matching strategies to detect various
 * patterns such as dictionary words, sequences, and spatial patterns.
 *
 * @deprecated This interface is deprecated. Use {@link com.nulabinc.zxcvbn.matchers.Matcher}
 *     instead.
 */
@Deprecated
public interface Matcher {
  List<Match> execute(CharSequence password);
}
