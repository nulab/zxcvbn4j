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
        Map<String, Map<String, Long>> unfiltered_freq_lists = parse_frequency_lists();
        FREQUENCY_LISTS = filter_frequency_lists(unfiltered_freq_lists);
    }

    private static Map<String, Map<String, Long>> parse_frequency_lists() {

        Map<String, Map<String, Long>> freq_lists = new HashMap<>();

        for (String filename:  DICTIONARY_PARAMS.keySet()) {

            final String freq_list_name = filename;

            if (!DICTIONARY_PARAMS.containsKey(freq_list_name)) {
                String msg = "Warning: %s appears in directory but not in DICTIONARY settings. Excluding.";
                System.out.println(String.format(msg, freq_list_name));
                continue;
            }

            Map<String, Long> token_to_rank = new HashMap<>();
            try(InputStream is = Dictionary.class.getResourceAsStream(buildResourcePath(filename));
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));) {
                String line;
                long rank = 0;
                while ((line = br.readLine()) != null) {
                    rank++;  // rank starts at 1
                    String token = line.split(" ")[0];
                    token_to_rank.put(token, rank);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while reading " + filename);
            }
            freq_lists.put(freq_list_name, token_to_rank);
        }

        return freq_lists;
    }

    private static boolean is_rare_and_short(String token, long rank) {
        return rank >= Math.pow(10, token.length());
    }


    private static boolean has_comma_or_double_quote(String token) {
        // hax, switch to csv or similar if this excludes too much.
        // simple comma joining has the advantage of being easy to process
        // client-side w/o needing a lib, and so far this only excludes a few
        // very high-rank tokens eg 'ps8,000' at rank 74868 from wikipedia list.
        if (token.indexOf(",") != -1 || token.indexOf("\"") != -1) {
            return true;
        }
        return false;
    }

    private static Map<String, String[]> filter_frequency_lists(Map<String, Map<String, Long>> freq_lists) {
        // filters frequency data according to:
        // - filter out short tokens if they are too rare.
        // - filter out tokens if they already appear in another dict
        // at lower rank.
        // - cut off final freq_list at limits set in DICTIONARY_PARAMS, if any.

        Map<String, Map<String, Long>> filtered_token_and_rank = new HashMap<>();
        Map<String, Long> token_count = new HashMap<>();
        for(String name: freq_lists.keySet()) {
            filtered_token_and_rank.put(name, new HashMap<String, Long>());
            token_count.put(name, Long.valueOf(0));
        }

        // maps token -> lowest token rank across all freq lists
        Map<String, Long> minimum_rank = new HashMap<>();
        // maps token -> freq list name with lowest token rank
        Map<String, String> minimum_name = new HashMap<>();

        for (Map.Entry<String, Map<String, Long>> freq_ref: freq_lists.entrySet()) {
            String name = freq_ref.getKey();
            Map<String, Long> token_to_rank = freq_ref.getValue();

            for (Map.Entry<String, Long> token_to_rank_ref: token_to_rank.entrySet()) {
                String token = token_to_rank_ref.getKey();
                long rank = token_to_rank_ref.getValue();

                if (!minimum_rank.containsKey(token)) {
                    minimum_rank.put(token, rank);
                    minimum_name.put(token, name);
                } else {
                    long min_rank = minimum_rank.get(token);
                    if (rank < min_rank) {
                        minimum_rank.put(token, rank);
                        minimum_name.put(token, name);
                    }
                }
            }
        }

        for (Map.Entry<String, Map<String, Long>> freq_ref: freq_lists.entrySet()) {
            String name = freq_ref.getKey();
            Map<String, Long> token_to_rank = freq_ref.getValue();

            for (Map.Entry<String, Long> token_to_rank_ref : token_to_rank.entrySet()) {
                String token = token_to_rank_ref.getKey();
                long rank = token_to_rank_ref.getValue();

                if (!minimum_name.get(token).equals(name)) {
                    continue;
                }
                if (is_rare_and_short(token, rank)
                        || has_comma_or_double_quote(token)) {
                    continue;
                }
                filtered_token_and_rank.get(name).put(token, rank);
                token_count.put(name, (token_count.get(name) + 1));
            }
        }
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Long>> filtered_token_and_rank_ref:
                filtered_token_and_rank.entrySet()) {

            String name = filtered_token_and_rank_ref.getKey();
            Map<String, Long> token_rank_pairs = filtered_token_and_rank_ref.getValue();

            List<Map.Entry<String, Long>> entries = new ArrayList<>(token_rank_pairs.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            Integer cutoff_limit = DICTIONARY_PARAMS.get(name);
            if (cutoff_limit != null && token_rank_pairs.size() > cutoff_limit) {
                entries = entries.subList(0, cutoff_limit);
            }
            List<String> tr = new ArrayList<>();
            for (Map.Entry<String, Long> token_rank_pair_ref: entries) {
                tr.add(token_rank_pair_ref.getKey());
            }
            // discard rank post-sort
            result.put(name, tr.toArray(new String[]{}));
        }
        return result;
    }
}