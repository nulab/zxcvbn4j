package com.nulabinc.zxcvbn.matchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyboard {

    public static final Keyboard QWERTY =
            new Keyboard("qwerty", new SlantedAdjacentGraphBuilder(loadAsString("keyboards/qwerty.txt")));

    public static final Keyboard DVORAK =
            new Keyboard("dvorak", new SlantedAdjacentGraphBuilder(loadAsString("keyboards/dvorak.txt")));

    public static final Keyboard JIS =
            new Keyboard("jis", new SlantedAdjacentGraphBuilder(loadAsString("keyboards/jis.txt")));

    public static final Keyboard KEYPAD =
            new Keyboard("keypad", new AlignedAdjacentAdjacentGraphBuilder(loadAsString("keyboards/keypad.txt")));

    public static final Keyboard MAC_KEYPAD =
            new Keyboard("mac_keypad", new AlignedAdjacentAdjacentGraphBuilder(loadAsString("keyboards/mac_keypad.txt")));

    public static final List<Keyboard> ALL_KEYBOARDS = Arrays.asList(QWERTY, DVORAK, JIS, KEYPAD, MAC_KEYPAD);

    private final String name;

    private final Map<Character, List<String>> adjacencyGraph;

    private final boolean slanted;

    private final int startingPositions;

    private final double averageDegree;

    private Keyboard(final String name, final AdjacentGraphBuilder adjacentGraphBuilder) {
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

    private static String loadAsString(final String name) {
        try (final InputStream input = Keyboard.class.getResourceAsStream(name);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            final StringBuilder sb = new StringBuilder(1024 * 4);
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }
            return sb.toString();
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Keyboard of(final String graph) {
        for (Keyboard keyboard : ALL_KEYBOARDS) {
            if (keyboard.getName().equals(graph)) {
                return keyboard;
            }
        }
        throw new IllegalArgumentException("Illegal graph " + graph);
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

    static abstract class AdjacentGraphBuilder {

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
                assert token.length() == tokenSize : String.format("token [%s] length mismatch:\n%s", token, layout);
            }

            int y = 1;
            for (final String line : split(layout, NEW_LINE_SPLIT_MATCHER)) {
                // the way I illustrated keys above, each qwerty row is indented one space in from the last
                int slant = calcSlant(y);
                for (final String token : split(line, WHITESPACE_SPLIT_MATCHER)) {
                    int index = line.indexOf(token) - slant;
                    int x = index / xUnit;
                    final int remainder = index % xUnit;
                    assert remainder == 0 : String.format("unexpected x offset [%d] for %s in:\n%s", x, token, layout);
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
    }

    static class SlantedAdjacentGraphBuilder extends AdjacentGraphBuilder {

        public SlantedAdjacentGraphBuilder(final String layout) {
            super(layout);
        }

        /**
         * returns the six adjacent coordinates on a standard keyboard, where each row is slanted to the
         * right from the last. adjacencies are clockwise, starting with key to the left, then two keys
         * above, then right key, then two keys below. (that is, only near-diagonal keys are adjacent,
         * so g's coordinate is adjacent to those of t,y,b,v, but not those of r,u,n,c.)
         */
        @Override
        protected List<Position> getAdjacentCoords(final Position position) {
            return Arrays.asList(
                    Position.of(position.getX() - 1, position.getY()),
                    Position.of(position.getX(), position.getY() - 1),
                    Position.of(position.getX() + 1, position.getY() - 1),
                    Position.of(position.getX() + 1, position.getY()),
                    Position.of(position.getX(), position.getY() + 1),
                    Position.of(position.getX() - 1, position.getY() + 1));
        }

        @Override

        public boolean isSlanted() {
            return true;
        }

        @Override
        protected int calcSlant(int y) {
            return y - 1;
        }
    }

    static class AlignedAdjacentAdjacentGraphBuilder extends AdjacentGraphBuilder {

        public AlignedAdjacentAdjacentGraphBuilder(final String layout) {
            super(layout);
        }

        @Override
        public boolean isSlanted() {
            return false;
        }

        @Override
        protected int calcSlant(int y) {
            return 0;
        }

        /**
         * returns the nine clockwise adjacent coordinates on a keypad, where each row is vert aligned.
         */
        @Override
        protected List<Position> getAdjacentCoords(final Position position) {
            return Arrays.asList(
                    Position.of(position.getX() - 1, position.getY()),
                    Position.of(position.getX() - 1, position.getY() - 1),
                    Position.of(position.getX(), position.getY() - 1),
                    Position.of(position.getX() + 1, position.getY() - 1),
                    Position.of(position.getX() + 1, position.getY()),
                    Position.of(position.getX() + 1, position.getY() + 1),
                    Position.of(position.getX(), position.getY() + 1),
                    Position.of(position.getX() - 1, position.getY() + 1));
        }
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
