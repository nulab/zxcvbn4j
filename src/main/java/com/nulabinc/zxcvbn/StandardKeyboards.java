package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.io.ClasspathResource;
import com.nulabinc.zxcvbn.matchers.AlignedKeyboardLoader;
import com.nulabinc.zxcvbn.matchers.Keyboard;
import com.nulabinc.zxcvbn.matchers.KeyboardLoader;
import com.nulabinc.zxcvbn.matchers.SlantedKeyboardLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandardKeyboards {

    private static final String RESOURCES_PACKAGE_PATH = "/com/nulabinc/zxcvbn/matchers/keyboards/";

    public static final String QWERTY = "qwerty";

    public static final String DVORAK = "dvorak";

    public static final String JIS = "jis";

    public static final String KEYPAD = "keypad";

    public static final String MAC_KEYPAD = "mac_keypad";

    public static final KeyboardLoader QWERTY_LOADER = new SlantedKeyboardLoader(QWERTY, new ClasspathResource(RESOURCES_PACKAGE_PATH + "qwerty.txt"));

    public static final KeyboardLoader DVORAK_LOADER = new SlantedKeyboardLoader(DVORAK, new ClasspathResource(RESOURCES_PACKAGE_PATH + "dvorak.txt"));

    public static final KeyboardLoader JIS_LOADER = new SlantedKeyboardLoader(JIS, new ClasspathResource(RESOURCES_PACKAGE_PATH + "jis.txt"));

    public static final KeyboardLoader KEYPAD_LOADER = new AlignedKeyboardLoader(KEYPAD, new ClasspathResource(RESOURCES_PACKAGE_PATH + "keypad.txt"));

    public static final KeyboardLoader MAC_KEYPAD_LOADER = new AlignedKeyboardLoader(MAC_KEYPAD, new ClasspathResource(RESOURCES_PACKAGE_PATH + "mac_keypad.txt"));

    private static final KeyboardLoader[] ALL_LOADERS = new KeyboardLoader[]{QWERTY_LOADER, DVORAK_LOADER, JIS_LOADER, KEYPAD_LOADER, MAC_KEYPAD_LOADER};

    public static List<Keyboard> loadAllKeyboards() throws IOException {
        List<Keyboard> keyboards = new ArrayList<>();
        for (KeyboardLoader keyboardLoader : ALL_LOADERS) {
            keyboards.add(keyboardLoader.load());
        }
        return keyboards;
    }
}
