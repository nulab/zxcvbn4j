package com.nulabinc.zxcvbn;

import java.nio.CharBuffer;
import java.util.Arrays;

/** A character sequence with many attributes of Strings, but that can have its content wiped. */
public class WipeableString implements CharSequence {

  private char[] content;
  private int hash = 0;
  private boolean wiped = false;

  /** Creates a new wipeable string, copying the content from the specified source. */
  public WipeableString(CharSequence source) {
    this.content = new char[source.length()];
    for (int n = 0; n < content.length; n++) {
      content[n] = source.charAt(n);
    }
  }

  /** Creates a new wipeable string, copying the content from the specified source. */
  public WipeableString(char[] source) {
    this.content = Arrays.copyOf(source, source.length);
  }

  @Override
  public int length() {
    return content == null ? 0 : content.length;
  }

  @Override
  public char charAt(int index) {
    return content[index];
  }

  @Override
  public WipeableString subSequence(int start, int end) {
    return new WipeableString(Arrays.copyOfRange(content, start, end));
  }

  /**
   * Wipe the content of the wipeable string.
   *
   * <p>Overwrites the content buffer with spaces, then replaces the buffer with an empty one.
   */
  public void wipe() {
    Arrays.fill(content, ' ');
    hash = 0;
    content = new char[0];
    wiped = true;
  }

  /** Returns a new wipeable string with the specified content forced into lower case. */
  public static WipeableString lowerCase(CharSequence source) {
    if (source == null) {
      throw new IllegalArgumentException("source is null");
    }

    char[] chars = new char[source.length()];
    for (int n = 0; n < source.length(); n++) {
      chars[n] = Character.toLowerCase(source.charAt(n));
    }
    return new WipeableString(chars);
  }

  /**
   * Returns a new wipeable string with the specified content but with the order of the characters
   * reversed.
   */
  public static WipeableString reversed(CharSequence source) {
    if (source == null) {
      throw new IllegalArgumentException("source is null");
    }
    int length = source.length();
    char[] chars = new char[length];
    for (int n = 0; n < source.length(); n++) {
      chars[n] = source.charAt(length - n - 1);
    }
    return new WipeableString(chars);
  }

  /** Returns a copy of a portion of a character sequence as a wipeable string. */
  public static WipeableString copy(CharSequence source, int start, int end) {
    return new WipeableString(source.subSequence(start, end));
  }

  /** Returns the position of the first match of the specified character (indexed from 0). */
  public int indexOf(char character) {
    for (int n = 0; n < content.length; n++) {
      if (content[n] == character) {
        return n;
      }
    }
    return -1;
  }

  /** Returns the nth Unicode code point. */
  public int codePointAt(int index) {
    // Copy the implementation from String
    if ((index < 0) || (index >= content.length)) {
      throw new StringIndexOutOfBoundsException(index);
    }
    return Character.codePointAt(content, index, content.length);
  }

  /** Returns true if the wipeable string has been wiped. */
  public boolean isWiped() {
    return this.wiped;
  }

  /** Returns a copy of the content as a char array. */
  public char[] charArray() {
    return Arrays.copyOf(content, content.length);
  }

  /**
   * Trims whitespace from a CharSequence.
   *
   * <p>If there is no trailing whitespace then the original value is returned. If there is trailing
   * whitespace then the content (without that trailing whitespace) is copied into a new
   * WipeableString.
   */
  static CharSequence trimTrailingWhitespace(CharSequence s) {
    if (!Character.isWhitespace(s.charAt(s.length() - 1))) {
      return s;
    }

    int length = s.length();

    while (length > 0 && Character.isWhitespace(s.charAt(length - 1))) {
      length--;
    }

    return WipeableString.copy(s, 0, length);
  }

  /** A version of Integer.parse(String) that accepts CharSequence as parameter. */
  public static int parseInt(CharSequence s) throws NumberFormatException {
    return parseInt(s, 10);
  }

  /** A version of Integer.parse(String) that accepts CharSequence as parameter. */
  @SuppressWarnings("squid:S3776")
  public static int parseInt(CharSequence s, int radix) throws NumberFormatException {
    if (s == null) {
      throw new NumberFormatException("null");
    }

    s = trimTrailingWhitespace(s);

    if (radix < Character.MIN_RADIX) {
      throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
    }

    if (radix > Character.MAX_RADIX) {
      throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
    }

    int result = 0;
    boolean negative = false;
    int i = 0;
    int len = s.length();
    int limit = -Integer.MAX_VALUE;
    int multmin;
    int digit;

    if (len > 0) {
      char firstChar = s.charAt(0);
      if (firstChar < '0') { // Possible leading "+" or "-"
        if (firstChar == '-') {
          negative = true;
          limit = Integer.MIN_VALUE;
        } else if (firstChar != '+') {
          throw numberFormatException(s);
        }
        if (len == 1) { // Cannot have lone "+" or "-"
          throw numberFormatException(s);
        }
        i++;
      }
      multmin = limit / radix;
      while (i < len) {
        // Accumulating negatively avoids surprises near MAX_VALUE
        digit = Character.digit(s.charAt(i++), radix);
        if (digit < 0) {
          throw numberFormatException(s);
        }
        if (result < multmin) {
          throw numberFormatException(s);
        }
        result *= radix;
        if (result < limit + digit) {
          throw numberFormatException(s);
        }
        result -= digit;
      }
    } else {
      throw numberFormatException(s);
    }
    return negative ? result : -result;
  }

  private static NumberFormatException numberFormatException(CharSequence s) {
    return new NumberFormatException("For input string: \"" + s + "\"");
  }

  @Override
  public String toString() {
    return new String(content);
  }

  @Override
  public int hashCode() {
    // Reproduce the same hash as String
    int h = hash;
    if (h == 0 && content.length > 0) {
      char[] val = content;

      for (int i = 0; i < content.length; i++) {
        h = 31 * h + val[i];
      }
      hash = h;
    }
    return h;
  }

  @Override
  public boolean equals(Object obj) {
    // Use an algorithm that matches any CharSequence (including Strings) with identical content.
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof CharSequence) {
      CharSequence other = (CharSequence) obj;
      if (other.length() != length()) {
        return false;
      }
      for (int n = 0; n < length(); n++) {
        if (charAt(n) != other.charAt(n)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Wipes the content of the specified character sequence if possible.
   *
   * <p>The following types can be wiped... WipeableString StringBuilder StringBuffer CharBuffer (if
   * not readOnly)
   */
  public static void wipeIfPossible(CharSequence text) {
    if (text == null) {
      return;
    }
    if (text instanceof WipeableString) {
      ((WipeableString) text).wipe();
    } else if (text instanceof StringBuilder) {
      for (int n = 0; n < text.length(); n++) {
        ((StringBuilder) text).setCharAt(n, ' ');
      }
      ((StringBuilder) text).setLength(0);
    } else if (text instanceof StringBuffer) {
      for (int n = 0; n < text.length(); n++) {
        ((StringBuffer) text).setCharAt(n, ' ');
      }
      ((StringBuffer) text).setLength(0);
    } else if (text instanceof CharBuffer && !((CharBuffer) text).isReadOnly()) {
      for (int n = 0; n < text.length(); n++) {
        ((CharBuffer) text).put(n, ' ');
      }
    }
  }
}
