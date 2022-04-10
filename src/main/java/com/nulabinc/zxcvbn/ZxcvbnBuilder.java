package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Keyboard;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZxcvbnBuilder {

    private final Map<String, Dictionary> dictionaryMap = new LinkedHashMap<>();

    private final Map<String, Keyboard> keyboardMap = new LinkedHashMap<>();

    public Zxcvbn build() throws IOException {
        return new Zxcvbn(buildContext());
    }

    Context buildContext() {
        try {
            if (this.dictionaryMap.isEmpty() && this.keyboardMap.isEmpty()) {
                for (Dictionary dictionary : StandardDictionaries.loadAllDictionaries()) {
                    this.dictionaryMap.put(dictionary.getName(), dictionary);
                }
                for (Keyboard keyboard : StandardKeyboards.loadAllKeyboards()) {
                    this.keyboardMap.put(keyboard.getName(), keyboard);
                }
            }

            Map<String, Dictionary> dictionaryMap = new LinkedHashMap<>();
            for (Dictionary dictionary : this.dictionaryMap.values()) {
                dictionaryMap.put(dictionary.getName(), dictionary);
            }

            Map<String, Keyboard> keyboardMap = new LinkedHashMap<>();
            for (Keyboard keyboard : this.keyboardMap.values()) {
                keyboardMap.put(keyboard.getName(), keyboard);
            }

            return new Context(dictionaryMap, keyboardMap);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
