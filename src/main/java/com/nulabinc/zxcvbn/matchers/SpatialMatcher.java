package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SpatialMatcher extends BaseMatcher {


    private final static List<String> KEY_LAYOUTS = Arrays.asList(new String[]{"qwerty","dvorak","jis"});

    private final Pattern shiftedRx = Pattern.compile("[~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?]");

    private final Map<String, Map<Character, String[]>> graphs;

    public SpatialMatcher(Map<String, Map<Character, String[]>> graphs) {
        this.graphs = graphs;
    }

    public SpatialMatcher() {
        this(Keyboard.ADJACENCY_GRAPHS);
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        for(Map.Entry<String, Map<Character, String[]>> graphEntry: graphs.entrySet()) {
            String graphName = graphEntry.getKey();
            Map<Character, String[]> graph = graphEntry.getValue();
            extend(matches, spatialMatchHelper(password, graph, graphName));
        }
        return this.sorted(matches);
    }


    private List<Match> spatialMatchHelper(String password, Map<Character, String[]> graph, String graphName) {
        List<Match> matches = new ArrayList<>();
        int i = 0;
        while (i < password.length() - 1) {
            int j = i + 1;
            int lastDirection = 0;
            int turns = 0;
            int shiftedCount;
            if ( KEY_LAYOUTS.contains(graphName) && shiftedRx.matcher(String.valueOf(password.charAt(i))).find()) {
                shiftedCount = 1;
            } else {
                shiftedCount = 0;
            }
            while(true) {
                Character prevChar = password.charAt(j-1);
                boolean found = false;
                int foundDirection = -1;
                int curDirection = -1;
                String[] adjacents = graph.containsKey(prevChar) ? graph.get(prevChar) : new String[]{};
                if (j < password.length()) {
                    Character curChar = password.charAt(j);
                    for(String adj: adjacents) {
                        curDirection += 1;
                        if (adj != null && adj.indexOf(String.valueOf(curChar)) != -1) {
                            found = true;
                            foundDirection = curDirection;
                            if (adj.indexOf(String.valueOf(curChar)) == 1) {
                                shiftedCount += 1;
                            }
                            if (lastDirection != foundDirection) {
                                turns += 1;
                                lastDirection = foundDirection;
                            }
                            break;
                        }
                    }
                }
                if (found) {
                    j += 1;
                } else {
                    if (j - i > 2) {
                        matches.add(MatchFactory.createSpatialMatch(
                                i,
                                j - 1,
                                password.substring(i, j),
                                graphName,
                                turns,
                                shiftedCount));
                    }
                    i = j;
                    break;
                }
            }
        }
        return matches;
    }
}
