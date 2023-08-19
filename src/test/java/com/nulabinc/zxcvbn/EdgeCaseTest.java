package com.nulabinc.zxcvbn;

import org.junit.Test;

public class EdgeCaseTest {

  /**
   * Reproduce Issue #49 from GitHub.
   *
   * <p>This should fail if the trimTrailingWhitespace call is removed from
   * WipeableString.parseInt(s,radix).
   */
  @Test
  public void testWindowsNewlineInDate() {
    StringBuilder buf = new StringBuilder("PW2001");
    buf.append((char) 10);
    buf.append((char) 13);
    buf.append("0101");
    new Zxcvbn().measure(buf.toString());
  }

  @Test
  public void testUnixNewlineInDate() {
    StringBuilder buf = new StringBuilder("PW2001");
    buf.append((char) 13);
    buf.append("0101");
    new Zxcvbn().measure(buf.toString());
  }

  @Test
  public void testSpaceAfterDate() {
    new Zxcvbn().measure("PW2009 ");
  }

  /** Try to reproduce GitHub issue #34 */
  @Test
  public void testJustFourDigitNumber() {
    new Zxcvbn().measure("8604 ");
  }
}
