package com.nulabinc.zxcvbn.matchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Dictionary {

    private static final String RESOURCES_PACKAGE_PATH = "/com/nulabinc/zxcvbn/matchers/dictionarys/";

    private static final String EXT = ".txt";

    private static String buildResourcePath(String filename) {
        return RESOURCES_PACKAGE_PATH + filename + EXT;
    }

    private static final String[] DICTIONARY_PARAMS = {
            "us_tv_and_film",
            "english_wikipedia",
            "passwords",
            "surnames",
            "male_names",
            "female_names"
    };

    public static final Map<String, String[]> FREQUENCY_LISTS;
    static {
        FREQUENCY_LISTS = read();
    }

    private static Map<String, String[]> read() {
        Map<String, String[]> freqLists = new HashMap<>();
        for (String filename:  DICTIONARY_PARAMS) {
            List<String> words = new ArrayList<>();
            try(InputStream is = Dictionary.class.getResourceAsStream(buildResourcePath(filename));
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));) {
                String line;
                while ((line = br.readLine()) != null) {
                    words.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while reading " + filename);
            }
            freqLists.put(filename, words.toArray(new String[]{}));
        }
        return freqLists;
    }
}