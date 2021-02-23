package com.nulabinc.zxcvbn.matchers;

import java.util.HashMap;
import java.util.Map;

public class CustomDictionary {

    private final String resourcePackagePath;
    private final String[] filenames;

    private volatile Map<String, String[]> frequencyLists = null;

    public CustomDictionary(String resourcePackagePath, String... filenames) {
        this.resourcePackagePath = resourcePackagePath;
        this.filenames = filenames;
    }

    public Map<String, String[]> getFrequencyLists() {
        if (frequencyLists == null) {
            synchronized (this) {
                if (frequencyLists == null) {
                    Map<String, String[]> lists = new HashMap<>();
                    for (String filename : filenames) {
                        lists.put(filename, Dictionary.read(resourcePackagePath, filename));
                    }
                    frequencyLists = lists;
                }
            }
        }
        return frequencyLists;
    }
}
