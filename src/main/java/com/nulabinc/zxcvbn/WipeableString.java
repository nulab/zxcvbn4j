package com.nulabinc.zxcvbn;

import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * A character sequence with many attributes of Strings, but that can have its content wiped.
 */
public class WipeableString implements CharSequence {

    private char[] content;
    private int hash = 0;
    private boolean wiped = false;

    /**
     * Creates a new wipeable string, copying the content from the specified source.
     */
    public WipeableString(CharSequence source) {
        this.content = new char[source.length()];
        for (int n = 0; n < content.length; n++) {
            content[n] = source.charAt(n);
        }
    }

    /**
     * Creates a new wipeable string, copying the content from the specified source.
     */
    public WipeableString(char[] source) {
        this.content = Arrays.copyOf(source,source.length);
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
        return new WipeableString(Arrays.copyOfRange(content,start,end));
    }

    /**
     * Wipe the content of the wipeable string.
     *
     * Overwrites the content buffer with spaces, then replaces the buffer with an empty one.
     */
    public void wipe() {
        Arrays.fill(content,' ');
        hash = 0;
        content = new char[0];
        wiped = true;
    }

    /**
     * Returns a new wipeable string with the specified content forced into lower case.
     */
    public static WipeableString lowerCase(CharSequence source) {
        char[] chars = new char[source.length()];
        for (int n = 0; n < source.length(); n++) {
            chars[n] = Character.toLowerCase(source.charAt(n));
        }
        return new WipeableString(chars);
    }

    /**
     * Returns the position of the first match of the specified character (indexed from 0).
     */
    public int indexOf(char character) {
        for (int n = 0; n < content.length; n++) {
            if (content[n] == character) {
                return n;
            }
        }
        return -1;
    }

    /**
     * Returns the nth Unicode code point.
     */
    public int codePointAt(int index) {
        // Copy the implementation from String
        if ((index < 0) || (index >= content.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAt(content, index, content.length);
    }

    /**
     * Returns true if the wipeable string has been wiped.
     */
    public boolean isWiped() {
        return this.wiped;
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
            char val[] = content;

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
            CharSequence other = (CharSequence)obj;
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
     * The following types can be wiped...
     *   WipeableString
     *   StringBuilder
     *   StringBuffer
     *   CharBuffer (if not readOnly)
     */
    public static void wipeIfPossible(CharSequence text) {
        if (text == null) return;
        if (text instanceof WipeableString) {
            ((WipeableString)text).wipe();
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
        }  else if (text instanceof CharBuffer) {
            if (!((CharBuffer)text).isReadOnly()) {
                for (int n = 0; n < text.length(); n++) {
                    ((CharBuffer) text).put(n, ' ');
                }
            }
        }

    }
}
