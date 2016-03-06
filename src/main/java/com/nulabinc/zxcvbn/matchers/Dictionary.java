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

    private static final String RESOURCES_PACKAGE_PATH = "/com/nulabinc/zxcvbn/";

    private static final String EXT = ".txt";

    private static String buildResourcePath(String filename) {
        return RESOURCES_PACKAGE_PATH + filename + EXT;
    }

    private static final Map<String, Integer> DICTIONARY_PARAMS;
    static {
        DICTIONARY_PARAMS = new HashMap<>();
        DICTIONARY_PARAMS.put("us_tv_and_film", 30000);
        DICTIONARY_PARAMS.put("english_wikipedia", 30000);
        DICTIONARY_PARAMS.put("passwords", 30000);
        DICTIONARY_PARAMS.put("surnames", 10000);
        DICTIONARY_PARAMS.put("male_names", null);
        DICTIONARY_PARAMS.put("female_names", null);
    }

    public static final Map<String, String[]> FREQUENCY_LISTS;
    static {
        Map<String, Map<String, Long>> unfilteredFreqLists = parseFrequencyLists();
        FREQUENCY_LISTS = filterFrequencyLists(unfilteredFreqLists);
    }

    private static Map<String, Map<String, Long>> parseFrequencyLists() {

        Map<String, Map<String, Long>> freqLists = new HashMap<>();

        for (String filename:  DICTIONARY_PARAMS.keySet()) {

            final String freqListName = filename;

            if (!DICTIONARY_PARAMS.containsKey(freqListName)) {
                String msg = "Warning: %s appears in directory but not in DICTIONARY settings. Excluding.";
                System.out.println(String.format(msg, freqListName));
                continue;
            }

            Map<String, Long> tokenToRank = new HashMap<>();
            try(InputStream is = Dictionary.class.getResourceAsStream(buildResourcePath(filename));
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));) {
                String line;
                long rank = 0;
                while ((line = br.readLine()) != null) {
                    rank++;  // rank starts at 1
                    String token = line.split(" ")[0];
                    tokenToRank.put(token, rank);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while reading " + filename);
            }
            freqLists.put(freqListName, tokenToRank);
        }

        return freqLists;
    }

    private static boolean isRareAndShort(String token, long rank) {
        return rank >= Math.pow(10, token.length());
    }


    private static boolean hasCommaOrDoubleQuote(String token) {
        // hax, switch to csv or similar if this excludes too much.
        // simple comma joining has the advantage of being easy to process
        // client-side w/o needing a lib, and so far this only excludes a few
        // very high-rank tokens eg 'ps8,000' at rank 74868 from wikipedia list.
        if (token.indexOf(",") != -1 || token.indexOf("\"") != -1) {
            return true;
        }
        return false;
    }

    private static Map<String, String[]> filterFrequencyLists(Map<String, Map<String, Long>> freqLists) {
        // filters frequency data according to:
        // - filter out short tokens if they are too rare.
        // - filter out tokens if they already appear in another dict
        // at lower rank.
        // - cut off final freq_list at limits set in DICTIONARY_PARAMS, if any.

        Map<String, Map<String, Long>> filteredTokenAndRank = new HashMap<>();
        Map<String, Long> tokenCount = new HashMap<>();
        for(String name: freqLists.keySet()) {
            filteredTokenAndRank.put(name, new HashMap<String, Long>());
            tokenCount.put(name, Long.valueOf(0));
        }

        // maps token -> lowest token rank across all freq lists
        Map<String, Long> minimumRank = new HashMap<>();
        // maps token -> freq list name with lowest token rank
        Map<String, String> minimumName = new HashMap<>();

        for (Map.Entry<String, Map<String, Long>> freqRef: freqLists.entrySet()) {
            String name = freqRef.getKey();
            Map<String, Long> tokenToRank = freqRef.getValue();

            for (Map.Entry<String, Long> tokenToRankRef: tokenToRank.entrySet()) {
                String token = tokenToRankRef.getKey();
                long rank = tokenToRankRef.getValue();

                if (!minimumRank.containsKey(token)) {
                    minimumRank.put(token, rank);
                    minimumName.put(token, name);
                } else {
                    long minRank = minimumRank.get(token);
                    if (rank < minRank) {
                        minimumRank.put(token, rank);
                        minimumName.put(token, name);
                    }
                }
            }
        }

        for (Map.Entry<String, Map<String, Long>> freqRef: freqLists.entrySet()) {
            String name = freqRef.getKey();
            Map<String, Long> tokenToRank = freqRef.getValue();

            for (Map.Entry<String, Long> tokenToRankRef : tokenToRank.entrySet()) {
                String token = tokenToRankRef.getKey();
                long rank = tokenToRankRef.getValue();

                if (!minimumName.get(token).equals(name)) {
                    continue;
                }
                if (isRareAndShort(token, rank)
                        || hasCommaOrDoubleQuote(token)) {
                    continue;
                }
                filteredTokenAndRank.get(name).put(token, rank);
                tokenCount.put(name, (tokenCount.get(name) + 1));
            }
        }
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Long>> filteredTokenAndRankRef:
                filteredTokenAndRank.entrySet()) {

            String name = filteredTokenAndRankRef.getKey();
            Map<String, Long> tokenRankPairs = filteredTokenAndRankRef.getValue();

            List<Map.Entry<String, Long>> entries = new ArrayList<>(tokenRankPairs.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            Integer cutoffLimit = DICTIONARY_PARAMS.get(name);
            if (cutoffLimit != null && tokenRankPairs.size() > cutoffLimit) {
                entries = entries.subList(0, cutoffLimit);
            }
            List<String> tr = new ArrayList<>();
            for (Map.Entry<String, Long> tokenRankPairRef: entries) {
                tr.add(tokenRankPairRef.getKey());
            }
            // discard rank post-sort
            result.put(name, tr.toArray(new String[]{}));
        }
        return result;
    }
}