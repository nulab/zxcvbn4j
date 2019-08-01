package com.nulabinc.zxcvbn;

import org.junit.Test;

import java.nio.CharBuffer;

import static junit.framework.TestCase.*;

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
        assertEquals("abc",  WipeableString.lowerCase("abc").toString());
        assertEquals("abcxyz",  WipeableString.lowerCase("abcXYZ").toString());
    }

    @Test
    public void testWipeIfPossible() {
        testWipeIfPossible(new StringBuffer("password"), "wiping StringBuffer");
        testWipeIfPossible(new StringBuilder("password"), "wiping StringBuilder");
        testWipeIfPossible(CharBuffer.wrap("password".toCharArray()), "wiping CharBuffer");
    }

    private void testWipeIfPossible(CharSequence text, String message) {
        assertEquals(message+" (before)", "password", text.toString());
        WipeableString.wipeIfPossible(text);
        assertEquals(message, "", text.toString().trim());
    }

    @Test
    public void testWipeStrength() {
        Strength strength = new Zxcvbn().measure(new WipeableString("pa55w0rd"));

        assertEquals("pa55w0rd", strength.getPassword().toString());
        assertEquals("pa55w0rd", strength.getSequence().get(0).token.toString());

        strength.wipe();

        assertEquals( "", strength.getPassword().toString());
        assertEquals("", strength.getSequence().get(0).token.toString());
    }

    @Test
    public void testWipeStrengthWithStringPassword() {
        Strength strength = new Zxcvbn().measure("pa55w0rd");

        assertEquals("pa55w0rd", strength.getPassword().toString());

        strength.wipe();

        assertEquals("string passwords cannot be wiped","pa55w0rd", strength.getPassword().toString());
    }
}
