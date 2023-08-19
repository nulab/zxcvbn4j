package com.nulabinc.zxcvbn;

import static junit.framework.TestCase.*;

import java.nio.CharBuffer;
import org.junit.Test;

public class WipeableStringTest {

  @Test
  public void testHashCode() {
    assertEquals("testing".hashCode(), new WipeableString("testing").hashCode());
  }

  @Test
  public void testEquals() {
    assertTrue(new WipeableString("hello").equals("hello"));
    assertFalse(new WipeableString("goodbye").equals("hello"));
  }

  @Test
  public void testLowerCase() {
    assertEquals("abc", WipeableString.lowerCase("ABC").toString());
    assertEquals("abc", WipeableString.lowerCase("abc").toString());
    assertEquals("abcxyz", WipeableString.lowerCase("abcXYZ").toString());
    assertEquals("", WipeableString.lowerCase("").toString());
  }

  @Test
  public void testReversed() {
    assertEquals("CBA", WipeableString.reversed("ABC").toString());
    assertEquals("", WipeableString.reversed("").toString());
    assertEquals("X", WipeableString.reversed("X").toString());
  }

  @Test
  public void testWipeIfPossible() {
    testWipeIfPossible(new StringBuffer("password"), "wiping StringBuffer");
    testWipeIfPossible(new StringBuilder("password"), "wiping StringBuilder");
    testWipeIfPossible(CharBuffer.wrap("password".toCharArray()), "wiping CharBuffer");
  }

  private void testWipeIfPossible(CharSequence text, String message) {
    assertEquals(message + " (before)", "password", text.toString());
    WipeableString.wipeIfPossible(text);
    assertEquals(message, "", text.toString().trim());
  }

  @Test
  public void testWipeStrength() {
    Strength strength = new Zxcvbn().measure(new WipeableString("pa55w0rd"));

    assertEquals("pa55w0rd", strength.getPassword().toString());
    assertEquals("pa55w0rd", strength.getSequence().get(0).token.toString());

    strength.wipe();

    assertEquals("", strength.getPassword().toString());
    assertEquals("", strength.getSequence().get(0).token.toString());
  }

  @Test
  public void testParseInt() {
    assertEquals(1, WipeableString.parseInt("1"));
    assertEquals(1, WipeableString.parseInt("+1"));
    assertEquals(1, WipeableString.parseInt("+01"));
    assertEquals(1928, WipeableString.parseInt("1928"));
    assertEquals(19369, WipeableString.parseInt("00019369"));
    assertEquals(-101, WipeableString.parseInt("-101"));
    assertEquals(5, WipeableString.parseInt("101", 2));
  }

  @Test
  public void testWipeStrengthWithStringPassword() {
    Strength strength = new Zxcvbn().measure("pa55w0rd");

    assertEquals("pa55w0rd", strength.getPassword().toString());

    strength.wipe();

    assertEquals("string passwords cannot be wiped", "pa55w0rd", strength.getPassword().toString());
  }

  @Test
  public void testParseIntWithTrailingSpaces() {
    assertEquals(2001, WipeableString.parseInt("2001 "));
    assertEquals(1, WipeableString.parseInt("1 "));
    assertEquals(2001, WipeableString.parseInt("2001     "));
  }

  @Test
  public void testParseIntWithTrailingCRLF() {
    assertEquals(
        2001,
        WipeableString.parseInt(
            CharBuffer.wrap(new char[] {'2', '0', '0', '1', (char) 13, (char) 10})));
  }

  @Test
  public void testParseIntWithTrailingCR() {
    assertEquals(
        2001, WipeableString.parseInt(CharBuffer.wrap(new char[] {'2', '0', '0', '1', (char) 13})));
  }

  @Test
  public void testParseIntWithTrailingLF() {
    assertEquals(
        2001, WipeableString.parseInt(CharBuffer.wrap(new char[] {'2', '0', '0', '1', (char) 10})));
  }
}
