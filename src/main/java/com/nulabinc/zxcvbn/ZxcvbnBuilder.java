package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Keyboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZxcvbnBuilder {

    private final Map<String, Dictionary> dictionaryMap = new LinkedHashMap<>();

    private final Map<String, Keyboard> keyboardMap = new LinkedHashMap<>();

    public Zxcvbn build() {
        return new Zxcvbn(new Context(dictionaryMap, keyboardMap));
    }

    public ZxcvbnBuilder dictionary(final Dictionary dictionary) {
        this.dictionaryMap.put(dictionary.getName(), dictionary);
        return this;
    }

    public ZxcvbnBuilder dictionaries(final List<Dictionary> dictionaries) {
        for (Dictionary dictionary : dictionaries) {
            this.dictionary(dictionary);
        }
        return this;
    }

    public ZxcvbnBuilder keyboard(final Keyboard keyboard) {
        this.keyboardMap.put(keyboard.getName(), keyboard);
        return this;
    }

    public ZxcvbnBuilder keyboards(final List<Keyboard> keyboards) {
        for (Keyboard keyboard : keyboards) {
            this.keyboard(keyboard);
        }
        return this;
    }

}
