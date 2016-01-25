package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.*;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchingTest {

    private static List<String[]> genpws(String pattern, List<String> prefixes, List<String> suffixes) {
        List<String> pres = prefixes.subList(0, prefixes.size());
        List<String> sufs = suffixes.subList(0, suffixes.size());
        List<String[]> result = new ArrayList<>();
        for (String pre : pres) {
            for (String suf : sufs) {
                int i = pre.length();
                int j = pre.length() + pattern.length() - 1;
                result.add(new String[]{pre + pattern + suf, String.valueOf(i), String.valueOf(j)});
            }
        }
        return result;
    }

    private static void checkMatches(String prefix, List<Match> matches, Pattern patternName, List<String> patterns, List<Integer[]> ijs, Map<String, List<?>> props) {
        List<Pattern> patternNames = new ArrayList<>();
        for (int i = 0; i < patterns.size(); i++) patternNames.add(patternName);
        boolean is_equal_len_args = patternNames.size() == patterns.size() && patterns.size() == ijs.size();
        for (Map.Entry<String, List<?>> propRef : props.entrySet()) {
            List<?> lst = propRef.getValue();
            is_equal_len_args = is_equal_len_args && (lst.size() == patterns.size());
        }
        if (!is_equal_len_args) throw new IllegalArgumentException("unequal argument lists to check_matches");
        String msg = String.format("%s: matches.length == %s", prefix, patterns.size());
        assertEquals(msg, matches.size(), patterns.size());
        for (int k = 0; k < patterns.size(); k++) {
            Match match = matches.get(k);
            Pattern pattern_name = patternNames.get(k);
            String pattern = patterns.get(k);
            Integer[] ij = ijs.get(k);
            int i = ij[0];
            int j = ij[1];
            msg = String.format("%s: matches[%s].pattern == '%s'", prefix, k, pattern_name);
            assertEquals(msg, match.pattern, pattern_name);
            msg = String.format("%s: matches[%s] should have i of %s", prefix, k, i);
            assertEquals(msg, match.i, i);
            msg = String.format("%s: matches[%s] should have j of %s", prefix, k, j);
            assertEquals(msg, match.j, j);
            msg = String.format("%s: matches[%s].token == '%s'", prefix, k, pattern);
            assertEquals(msg, match.token, pattern);
            for (Map.Entry<String, List<?>> propRef : props.entrySet()) {
                String name = propRef.getKey();
                List<?> lst = propRef.getValue();
                Object a = field(match, name);
                msg = String.format("%s: matches[%s].token == '%s'", prefix, k, pattern);
                assertEquals(msg, lst.get(k), a);
            }
        }
    }

    private static Object field(Match target, String name) {
        try {
            return Match.class.getField(name).get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Match> dm(String password, Map<String, Map<String, Integer>> testDicts) {
        return new DictionaryMatcher(testDicts).execute(password);
    }

    @Test
    public void testDictionaryMatching() throws Exception {
        Map<String, Map<String, Integer>> testDicts = new HashMap<>();
        testDicts.put("d1", new HashMap<String, Integer>() {{
            put("motherboard", 1);
            put("mother", 2);
            put("board", 3);
            put("abcd", 4);
            put("cdef", 5);
        }});
        testDicts.put("d2", new HashMap<String, Integer>() {{
            put("z", 1);
            put("8", 2);
            put("99", 3);
            put("$", 4);
            put("asdf1234&*", 5);
        }});
        {
            List<Match> matches = dm("motherboard", testDicts);
            List<String> patterns = Arrays.asList("mother", "motherboard", "board");
            List<Integer[]> ijs = new ArrayList<>();
            ijs.add(new Integer[]{0, 5});
            ijs.add(new Integer[]{0, 10});
            ijs.add(new Integer[]{6, 10});
            String msg = "matches words that contain other words";
            Map<String, List<?>> props = new HashMap<>();
            props.put("matchedWord", Arrays.asList("mother", "motherboard", "board"));
            props.put("dictionaryName", Arrays.asList("d1", "d1", "d1"));
            props.put("rank", Arrays.asList(2, 1, 3));

            checkMatches(msg, matches, Pattern.Dictionary, patterns, ijs, props);
        }
        {
            List<Match> matches = dm("abcdef", testDicts);
            List<String> patterns = Arrays.asList("abcd", "cdef");
            List<Integer[]> ijs = new ArrayList<>();
            ijs.add(new Integer[]{0, 3});
            ijs.add(new Integer[]{2, 5});
            String msg = "matches multiple words when they overlap";
            Map<String, List<?>> props = new HashMap<>();
            props.put("matchedWord", Arrays.asList("abcd", "cdef"));
            props.put("dictionaryName", Arrays.asList("d1", "d1"));
            props.put("rank", Arrays.asList(4, 5));

            checkMatches(msg, matches, Pattern.Dictionary, patterns, ijs, props);
        }
        {
            List<Match> matches = dm("BoaRdZ", testDicts);
            List<String> patterns = Arrays.asList("board", "z");
            List<Integer[]> ijs = new ArrayList<>();
            ijs.add(new Integer[]{0, 4});
            ijs.add(new Integer[]{5, 5});
            String msg = "ignores uppercasing";
            Map<String, List<?>> props = new HashMap<>();
            props.put("matchedWord", Arrays.asList("board", "z"));
            props.put("dictionaryName", Arrays.asList("d1", "d2"));
            props.put("rank", Arrays.asList(3, 1));

            checkMatches(msg, matches, Pattern.Dictionary, patterns, ijs, props);
        }
    }

    @Test
    public void testReverseDictionaryMatching() throws Exception {
        Map<String, Map<String, Integer>> testDicts = new HashMap<>();
        testDicts.put("d1", new HashMap<String, Integer>() {{
            put("123", 1);
            put("321", 2);
            put("456", 3);
            put("654", 4);
        }});

        String password = "0123456789";
        List<Match> matches = new ReverseDictionaryMatcher(testDicts).execute(password);
        String msg = "matches against reversed words";

        List<String> patterns = Arrays.asList("123", "456");
        List<Integer[]> ijs = new ArrayList<>();
        ijs.add(new Integer[]{1, 3});
        ijs.add(new Integer[]{4, 6});

        Map<String, List<?>> props = new HashMap<>();
        props.put("matchedWord", Arrays.asList("321", "654"));
        props.put("reversed", Arrays.asList(true, true));
        props.put("dictionaryName", Arrays.asList("d1", "d1"));
        props.put("rank", Arrays.asList(2, 4));

        checkMatches(msg, matches, Pattern.Dictionary, patterns, ijs, props);
    }

    @Test
    public void testL33tMatching() throws Exception {
        Map<Character, Character[]> testTable = new HashMap<>();
        testTable.put('a', new Character[]{'4', '@'});
        testTable.put('c', new Character[]{'(', '{', '[', '<'});
        testTable.put('g', new Character[]{'6', '9'});
        testTable.put('o', new Character[]{'0'});
        for (Object[] param : new Object[][]{
                {"", new HashMap<Character, Character[]>()},
                {"abcdefgo123578!#$&*)]}>", new HashMap<Character, Character[]>()},
                {"a", new HashMap<Character, Character[]>()},
                {"4", new HashMap<Character, Character[]>() {{
                    put('a', new Character[]{'4'});
                }}},
                {"4@", new HashMap<Character, Character[]>() {{
                    put('a', new Character[]{'4', '@'});
                }}},
                {"4({60", new HashMap<Character, Character[]>() {{
                    put('a', new Character[]{'4'});
                    put('c', new Character[]{'(', '{'});
                    put('g', new Character[]{'6'});
                    put('o', new Character[]{'0'});
                }}}
        }) {
            String pw = (String) param[0];
            Map<Character, Character[]> expected = (Map<Character, Character[]>) param[1];
            String msg = "reduces l33t table to only the substitutions that a password might be employing";
            assertEquals(msg, expected.size(), new L33tMatcher().relevantL33tSubTable(pw, testTable).size());
        }

    }

    @Test
    public void testSpatialMatching() throws Exception {
        {
            for (String password : new String[]{"", "/", "qw", "*/"}) {
                String msg = "doesn't match 1- and 2-character spatial patterns";
                assertEquals(msg, new SpatialMatcher().execute(password).size(), 0);
            }
            Map<String, Map<Character, String[]>> graphs = new HashMap<>();
            graphs.put("qwerty", Keyboard.ADJACENCY_GRAPHS.get("qwerty"));
            final String pattern = "6tfGHJ";
            List<Match> matches = new SpatialMatcher(graphs).execute("rz!" + pattern + "%z");
            String msg = "matches against spatial patterns surrounded by non-spatial patterns";
            checkMatches(
                    msg,
                    matches,
                    Pattern.Spatial,
                    new ArrayList<String>() {{
                        add(pattern);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{3, 3 + pattern.length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("graph", new ArrayList<String>() {{
                            add("qwerty");
                        }});
                        put("turns", new ArrayList<Integer>() {{
                            add(2);
                        }});
                        put("shiftedCount", new ArrayList<Integer>() {{
                            add(3);
                        }});
                    }});
        }
        for (Object[] testParam: new Object[][] {
                { "12345",        "qwerty",     1, 0 },
                { "@WSX",         "qwerty",     1, 4 },
                { "6tfGHJ",       "qwerty",     2, 3 },
                { "hGFd",         "qwerty",     0, 2 },
                { "/;p09876yhn",  "qwerty",     3, 0},
                { "Xdr%",         "qwerty",     1, 2 },
                { "159-",         "keypad",     1, 0 },
                { "*84",          "keypad",     1, 0 },
                { "/8520",        "keypad",     1, 0 },
                { "369",          "keypad",     1, 0 },
                { "/963.",        "mac_keypad", 1, 0 },
                { "*-632.0214",   "mac_keypad", 9, 0 },
                { "aoEP%yIxkjq:", "dvorak",     4, 5 },
                { ";qoaOQ:Aoq;a", "dvorak",    11, 4 }
        }) {
            final String pattern = (String) testParam[0];
            final String keyboard = (String) testParam[1];
            final int turns = (int) testParam[2];
            final int shifts = (int) testParam[3];

            Map<String, Map<Character, String[]>> graphs = new HashMap<>();
            graphs.put(keyboard, Keyboard.ADJACENCY_GRAPHS.get(keyboard));
            List<Match> matches = new SpatialMatcher(graphs).execute(pattern);
            String msg = String.format("matches %s as a %s pattern", pattern, keyboard);
            checkMatches(
                    msg,
                    matches,
                    Pattern.Spatial,
                    new ArrayList<String>() {{
                        add(pattern);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, pattern.length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("graph", new ArrayList<String>() {{
                            add(keyboard);
                        }});
                        put("turns", new ArrayList<Integer>() {{
                            add(turns);
                        }});
                        put("shiftedCount", new ArrayList<Integer>() {{
                            add(shifts);
                        }});
                    }});
        }
    }

    @Test
    public void testSequenceMatching() throws Exception {
        for (String password : new String[]{"", "a", "1"}) {
            String msg = String.format("doesn't match length-%s sequences", password.length());
            assertEquals(msg, new SequenceMatcher().execute(password).size(), 0);
        }
        List<Match> matches = new SequenceMatcher().execute("abcbabc");
        String msg = "matches overlapping patterns";
        checkMatches(
                msg,
                matches,
                Pattern.Sequence,
                new ArrayList<String>() {{
                    add("abc");
                    add("cba");
                    add("abc");
                }},
                new ArrayList<Integer[]>() {{
                    add(new Integer[]{0, 2});
                    add(new Integer[]{2, 4});
                    add(new Integer[]{4, 6});
                }},
                new HashMap<String, List<?>>() {{
                    put("ascending", new ArrayList<Boolean>() {{
                        add(true);
                        add(false);
                        add(true);
                    }});
                }});
    }

    @Test
    public void testRepeatMatching() throws Exception {
        for (String password : new String[]{"", "#"}) {
            String msg = String.format("doesn't match length-%s repeat patterns", password.length());
            assertEquals(msg, new RepeatMatcher().execute(password).size(), 0);
        }
        List<String> prefixes = Arrays.asList("@", "y4@");
        List<String> suffixes = Arrays.asList("u", "u%7");
        final String pattern = "&&&&&";
        for (String[] pws : genpws(pattern, prefixes, suffixes)) {
            String password = pws[0];
            final int i = Integer.valueOf(pws[1]);
            final int j = Integer.valueOf(pws[2]);
            List<Match> matches = new RepeatMatcher().execute(password);
            String msg = "matches embedded repeat patterns";
            checkMatches(
                    msg,
                    matches,
                    Pattern.Repeat,
                    new ArrayList<String>() {{
                        add(pattern);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{i, j});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("baseToken", new ArrayList<String>() {{
                            add("&");
                        }});
                    }});
        }
    }

    @Test
    public void testRegexMatching() throws Exception {
        {
            List<Match> matches = new RegexMatcher().execute("1922");
            String msg = "matches 1922 as a recent_year pattern";
            checkMatches(
                    msg,
                    matches,
                    Pattern.Regex,
                    new ArrayList<String>() {{
                        add("1922");
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, "1922".length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("regexName", new ArrayList<String>() {{
                            add("recent_year");
                        }});
                    }}
            );
        }
        {
            List<Match> matches = new RegexMatcher().execute("2017");
            String msg = "matches 2017 as a recent_year pattern";
            checkMatches(
                    msg,
                    matches,
                    Pattern.Regex,
                    new ArrayList<String>() {{
                        add("2017");
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, "2017".length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("regexName", new ArrayList<String>() {{
                            add("recent_year");
                        }});
                    }}
            );
        }
    }

    @Test
    public void testDateMatching() throws Exception {
        for (final String sep : new String[]{"", " ", "-", "/", "\\", "_", "."}) {
            final String password = String.format("13%s2%s1921", sep, sep);
            List<Match> matches = new DateMatcher().execute(password);
            String msg = String.format("matches dates that use '%s' as a separator", sep);
            checkMatches(
                    msg,
                    matches,
                    Pattern.Date,
                    new ArrayList<String>() {{
                        add(password);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, password.length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("separator", new ArrayList<String>() {{
                            add(sep);
                        }});
                        put("year", new ArrayList<Integer>() {{
                            add(1921);
                        }});
                        put("month", new ArrayList<Integer>() {{
                            add(2);
                        }});
                        put("day", new ArrayList<Integer>() {{
                            add(13);
                        }});
                    }});
        }
        for (final String order : new String[]{"mdy", "dmy", "ymd", "ydm"}) {
            final String password = order
                    .replace("y", "88")
                    .replace("m", "8")
                    .replace("d", "8");
            List<Match> matches = new DateMatcher().execute(password);
            String msg = String.format("matches dates with '%s' format", order);
            checkMatches(
                    msg,
                    matches,
                    Pattern.Date,
                    new ArrayList<String>() {{
                        add(password);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, password.length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("separator", new ArrayList<String>() {{
                            add("");
                        }});
                        put("year", new ArrayList<Integer>() {{
                            add(1988);
                        }});
                        put("month", new ArrayList<Integer>() {{
                            add(8);
                        }});
                        put("day", new ArrayList<Integer>() {{
                            add(8);
                        }});
                    }});
        }
        {
            final String password = "111504";
            List<Match> matches = new DateMatcher().execute(password);
            String msg = "matches the date with year closest to REFERENCE_YEAR when ambiguous";
            checkMatches(
                    msg,
                    matches,
                    Pattern.Date,
                    new ArrayList<String>() {{
                        add(password);
                    }},
                    new ArrayList<Integer[]>() {{
                        add(new Integer[]{0, password.length() - 1});
                    }},
                    new HashMap<String, List<?>>() {{
                        put("separator", new ArrayList<String>() {{
                            add("");
                        }});
                        put("year", new ArrayList<Integer>() {{
                            add(2004);
                        }});
                        put("month", new ArrayList<Integer>() {{
                            add(11);
                        }});
                        put("day", new ArrayList<Integer>() {{
                            add(15);
                        }});
                    }});
        }
    }

    @Test
    public void testOmnimatch() throws Exception {
        assertEquals(0, new Matching(new ArrayList<String>()).omnimatch("").size());
        String password = "r0sebudmaelstrom11/20/91aaaa";
        List<Match> matches = new Matching(new ArrayList<String>()).omnimatch(password);
        Map<Pattern, Integer[]> testMatches = new HashMap<>();
        testMatches.put(Pattern.Dictionary, new Integer[]{0, 6});
        testMatches.put(Pattern.Dictionary, new Integer[]{7, 15});
        testMatches.put(Pattern.Date, new Integer[]{16, 23});
        testMatches.put(Pattern.Repeat, new Integer[]{24, 27});
        for (Map.Entry<Pattern, Integer[]> testMatch : testMatches.entrySet()) {
            Pattern patternName = testMatch.getKey();
            int i = testMatch.getValue()[0];
            int j = testMatch.getValue()[1];
            boolean included = false;
            for (Match match : matches) {
                if (match.i == i && match.j == j && match.pattern == patternName) included = true;
            }
            String msg = String.format("for %s, matches a %s pattern at [%s, %s]", password, patternName.value(), i, j);
            assertTrue(msg, included);
        }
    }

}
