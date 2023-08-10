package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyboard {

    private final String name;

    private final Map<Character, List<String>> adjacencyGraph;

    private final boolean slanted;

    private final int startingPositions;

    private final double averageDegree;

    Keyboard(final String name, final AdjacentGraphBuilder adjacentGraphBuilder) {
        this.name = name;
        this.adjacencyGraph = adjacentGraphBuilder.build();
        this.slanted = adjacentGraphBuilder.isSlanted();
        this.startingPositions = adjacencyGraph.size();
        this.averageDegree = calcAverageDegree(adjacencyGraph);
    }

    private static double calcAverageDegree(final Map<Character, List<String>> adjacencyGraph) {
        double average = 0;
        for (Map.Entry<Character, List<String>> graphRef : adjacencyGraph.entrySet()) {
            List<String> neighbors = graphRef.getValue();
            List<String> results = new ArrayList<>();
            for (String neighbor : neighbors) {
                if (neighbor != null) {
                    results.add(neighbor);
                }
            }
            average += results.size();
        }
        List<Character> keys = new ArrayList<>();
        for (Map.Entry<Character, List<String>> graphRef : adjacencyGraph.entrySet()) {
            keys.add(graphRef.getKey());
        }
        average /= keys.size();
        return average;
    }

    public String getName() {
        return name;
    }

    public Map<Character, List<String>> getAdjacencyGraph() {
        return adjacencyGraph;
    }

    public boolean isSlanted() {
        return slanted;
    }

    public int getStartingPositions() {
        return startingPositions;
    }

    public double getAverageDegree() {
        return averageDegree;
    }

    public static abstract class AdjacentGraphBuilder {

        private static final SplitMatcher WHITESPACE_SPLIT_MATCHER = new SplitMatcher() {
            @Override
            public boolean match(final char c) {
                return Character.isWhitespace(c);
            }
        };

        private static final SplitMatcher NEW_LINE_SPLIT_MATCHER = new SplitMatcher() {
            @Override
            public boolean match(final char c) {
                return c == '\n';
            }
        };

        private final String layout;

        public AdjacentGraphBuilder(final String layout) {
            this.layout = layout;
        }

        /**
         * builds an adjacency graph as a dictionary: {character: [adjacent_characters]}.
         * adjacent characters occur in a clockwise order.
         * for example:
         * on qwerty layout, 'g' maps to ['fF', 'tT', 'yY', 'hH', 'bB', 'vV']
         * on keypad layout, '7' maps to [None, None, None, '=', '8', '5', '4', None]         *
         */
        public Map<Character, List<String>> build() {
            final Map<Position, String> positionTable = buildPositionTable(layout);

            final Map<Character, List<String>> adjacencyGraph = new HashMap<>();
            for (Map.Entry<Position, String> entry : positionTable.entrySet()) {
                for (final char key : entry.getValue().toCharArray()) {
                    final List<String> adjacencies = new ArrayList<>();
                    final Position position = entry.getKey();
                    for (final Position coord : getAdjacentCoords(position)) {
                        adjacencies.add(positionTable.get(coord));
                    }
                    adjacencyGraph.put(key, adjacencies);
                }
            }

            return adjacencyGraph;
        }

        private Map<Position, String> buildPositionTable(final String layout) {
            final Map<Position, String> positionTable = new HashMap<>();

            final List<String> tokens = split(layout, WHITESPACE_SPLIT_MATCHER);
            final int tokenSize = tokens.get(0).length();
            final int xUnit = tokenSize + 1;

            for (String token : tokens) {
                assert token.length() == tokenSize : String.format("token [%s] length mismatch:%n%s", token, layout);
            }

            int y = 1;
            for (final String line : split(layout, NEW_LINE_SPLIT_MATCHER)) {
                // the way I illustrated keys above, each qwerty row is indented one space in from the last
                int slant = calcSlant(y);
                for (final String token : split(line, WHITESPACE_SPLIT_MATCHER)) {
                    int index = line.indexOf(token) - slant;
                    int x = index / xUnit;
                    final int remainder = index % xUnit;
                    assert remainder == 0 : String.format("unexpected x offset [%d] for %s in:%n%s", x, token, layout);
                    positionTable.put(Position.of(x, y), token);
                }

                y++;
            }
            return positionTable;
        }

        protected abstract List<Position> getAdjacentCoords(final Position position);

        private static List<String> split(final String str, final SplitMatcher splitMatcher) {
            final int len = str.length();
            final List<String> list = new ArrayList<>();
            int i = 0, start = 0;
            boolean match = false;
            while (i < len) {
                if (splitMatcher.match(str.charAt(i))) {
                    if (match) {
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
            if (match) {
                list.add(str.substring(start, i));
            }
            return list;
        }

        protected abstract int calcSlant(int y);

        public abstract boolean isSlanted();

        private interface SplitMatcher {
            boolean match(char c);
        }

        static class Position {

            private final int x;

            private final int y;

            private Position(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public static Position of(int x, int y) {
                return new Position(x, y);
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            @Override
            public int hashCode() {
                int result = x;
                result = 31 * result + y;
                return result;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Position)) return false;

                final Position position = (Position) o;

                return x == position.x && y == position.y;
            }

            @Override
            public String toString() {
                return "[" + x + "," + y + ']';
            }
        }
    }

}
