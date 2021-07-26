package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {

    private static final String PACKAGE_PATH = "com/nulabinc/zxcvbn/matchers/";
    private static final String DICTIONARY_FILES_LOCATION = PACKAGE_PATH + "dictionarys/";
    private static final String DICTIONARY_FILES_EXT = ".txt";
    private static final String CONFIG_FILE_PATH = PACKAGE_PATH + ".dictionaries";
    private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();
    private static final ArrayList<String> dictionaryList = new ArrayList<>(Arrays.asList(
            "us_tv_and_film",
            "english_wikipedia",
            "passwords",
            "surnames",
            "male_names",
            "female_names"
    ));

    private static String buildResourcePath(String filename) {
        return DICTIONARY_FILES_LOCATION + filename + DICTIONARY_FILES_EXT;
    }

    public static final Map<String, String[]> FREQUENCY_LISTS;

    static {
        initListFromConfigIfSupplied();
        FREQUENCY_LISTS = read();
    }

    private static Map<String, String[]> read() {
        Map<String, String[]> freqLists = new HashMap<>();
        for (String dictionaryName : dictionaryList) {
            List<String> words = RESOURCE_LOADER.getEntriesFromFile(buildResourcePath(dictionaryName), false);
            freqLists.put(dictionaryName, words.toArray(new String[]{}));
        }
        return freqLists;
    }

    private static void initListFromConfigIfSupplied() {
        List<String> configEntries = RESOURCE_LOADER.getEntriesFromFile(CONFIG_FILE_PATH, true);
        if (configEntries != null) {
            dictionaryList.clear();
            dictionaryList.addAll(configEntries);
        }
    }
}