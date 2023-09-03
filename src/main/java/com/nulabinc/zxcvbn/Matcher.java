package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import java.util.List;

/**
 * @deprecated This interface is deprecated. Use {@link com.nulabinc.zxcvbn.matchers.Matcher}
 *     instead.
 */
@Deprecated
public interface Matcher {
  List<Match> execute(CharSequence password);
}
