package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.Scoring;

import java.util.*;
import java.util.regex.*;

public class DateMatcher extends BaseMatcher {

    private static final int DATE_MAX_YEAR = 2050;
    private static final int DATE_MIN_YEAR = 1000;
    private static final Map<Integer, ArrayList<Integer[]>> DATE_SPLITS = new HashMap<>();
    static {
        DATE_SPLITS.put(4, new ArrayList<Integer[]>(){{ add(new Integer[]{1, 2}); add(new Integer[]{2, 3}); }});
        DATE_SPLITS.put(5, new ArrayList<Integer[]>(){{ add(new Integer[]{1, 3}); add(new Integer[]{2, 3}); }});
        DATE_SPLITS.put(6, new ArrayList<Integer[]>(){{ add(new Integer[]{1, 2}); add(new Integer[]{2, 4}); add(new Integer[]{4, 5});}});
        DATE_SPLITS.put(7, new ArrayList<Integer[]>(){{ add(new Integer[]{1, 3}); add(new Integer[]{2, 3}); add(new Integer[]{4, 5}); add(new Integer[]{4, 6});}});
        DATE_SPLITS.put(8, new ArrayList<Integer[]>(){{ add(new Integer[]{2, 4}); add(new Integer[]{4, 6}); }});
    }

    private final Pattern maybe_date_no_separator = Pattern.compile("^\\d{4,8}$");
    private final Pattern maybe_date_with_separator = Pattern.compile("^(\\d{1,4})([\\s/\\\\_.-])(\\d{1,2})\\2(\\d{1,4})$");

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i <= password.length() - 4; i++) {
            for (int j = i + 3; j <= i + 7; j++) {
                if (j >= password.length()) break;
                String token = password.substring(i, j + 1);
                if (!maybe_date_no_separator.matcher(token).find()) {
                    continue;
                }
                List<Dmy> candidates = new ArrayList<>();
                for(Integer[] date: DATE_SPLITS.get(token.length())) {
                    int k = date[0];
                    int l = date[1];
                    List<Integer> ints = new ArrayList<>();
                    ints.add(Integer.parseInt(token.substring(0, k)));
                    ints.add(Integer.parseInt(token.substring(k, l)));
                    ints.add(Integer.parseInt(token.substring(l)));
                    Dmy dmy = mapIntsToDmy(ints);
                    if (dmy != null) {
                        candidates.add(dmy);
                    }
                }
                if (candidates.isEmpty()) {
                    continue;
                }
                Dmy bestCandidate = candidates.get(0);
                int minDistance = metric(candidates.get(0));
                for (Dmy candidate: candidates.subList(1, candidates.size())) {
                    int distance = metric(candidate);
                    if (distance < minDistance) {
                        bestCandidate = candidate;
                        minDistance = distance;
                    }
                }
                matches.add(MatchFactory.createDateMatch(i, j, token, "", bestCandidate.year, bestCandidate.month, bestCandidate.day));
            }
        }
        for (int i = 0; i <= password.length() - 6; i++) {
            for (int j = i + 5; j <= i + 9; j++) {
                if (j >= password.length()) break;
                String token = password.substring(i, j + 1);
                java.util.regex.Matcher rxMatch = maybe_date_with_separator.matcher(token);
                if (!rxMatch.find()) continue;
                List<Integer> ints = new ArrayList<>();
                ints.add(Integer.parseInt(rxMatch.group(1)));
                ints.add(Integer.parseInt(rxMatch.group(3)));
                ints.add(Integer.parseInt(rxMatch.group(4)));
                Dmy dmy = mapIntsToDmy(ints);
                if (dmy == null) continue;
                matches.add(MatchFactory.createDateMatch(i, j, token, rxMatch.group(2), dmy.year, dmy.month, dmy.day));
            }
        }
        List<Match> targetMatches = new ArrayList<>();
        for (Match match: matches) {
            boolean isSubmatch = false;
            for (Match otherMatch: matches) {
                if (match.equals(otherMatch)) continue;
                if (otherMatch.i <= match.i && otherMatch.j >= match.j) {
                    isSubmatch = true;
                    break;
                }
            }
            if (!isSubmatch) targetMatches.add(match);
        }
        return this.sorted(targetMatches);
    }

    private int metric(Dmy candidate) {
        return Math.abs(candidate.year - Scoring.REFERENCE_YEAR);
    }

    private Dmy mapIntsToDmy(List<Integer> ints) {
        if (ints.get(1) > 31 || ints.get(1) <= 0) {
            return null;
        }
        int over12 = 0;
        int over31 = 0;
        int under1 = 0;
        for(int i: ints) {
            if ((99 < i && i < DATE_MIN_YEAR) || i > DATE_MAX_YEAR) {
                return null;
            }
            if (i > 31) over31 += 1;
            if (i > 12) over12 += 1;
            if (i <= 0) under1 += 1;
        }
        if (over31 >= 2 || over12 == 3 || under1 >= 2) {
            return null;
        }
        Map<Integer, List<Integer>> possibleYearSplits = new HashMap<>();
        possibleYearSplits.put(ints.get(2), ints.subList(0, 1 + 1));
        possibleYearSplits.put(ints.get(0), ints.subList(1, 2 + 1));
        for(Map.Entry<Integer, List<Integer>> possibleYearSplitRef: possibleYearSplits.entrySet()) {
            int y = possibleYearSplitRef.getKey();
            List<Integer> rest = possibleYearSplitRef.getValue();
            if (DATE_MIN_YEAR <= y && y <= DATE_MAX_YEAR) {
                Dm dm = mapIntsToDm(rest);
                if (dm != null) {
                    return new Dmy(dm.day, dm.month, y);
                } else {
                    return null;
                }
            }
        }
        for(Map.Entry<Integer, List<Integer>> possibleYearSplitRef: possibleYearSplits.entrySet()) {
            int y = possibleYearSplitRef.getKey();
            List<Integer> rest = possibleYearSplitRef.getValue();
            Dm dm = mapIntsToDm(rest);
            if (dm != null) {
                y = twoToFourDigitYear(y);
                return new Dmy(dm.day, dm.month, y);
            }
        }
        return null;
    }

    private Dm mapIntsToDm(List<Integer> ints) {
        List<Integer> copy = new ArrayList<Integer>(ints);
        Collections.reverse(copy);
        List<List<Integer>> refs = new ArrayList<>();
        refs.add(ints);
        refs.add(copy);
        for (List<Integer> ref: refs) {
            int d = ref.get(0);
            int m = ref.get(1);
            if ((1 <= d && d <= 31) && (1 <= m && m <= 12)) {
                return new Dm(d, m);
            }
        }
        return null;
    }

    private int twoToFourDigitYear(int year) {
        if (year > 99) {
            return year;
        } else if (year > 50) {
            // 87 -> 1987
            return year + 1900;
        } else {
            // 15 -> 2015
            return year + 2000;
        }
    }


    private static class Dm {
        final int day;
        final int month;
        public Dm(int day, int month) {
            this.day = day;
            this.month = month;
        }
    }
    private static class Dmy extends Dm {
        final int year;
        public Dmy(int day, int month, int year) {
            super(day, month);
            this.year = year;
        }
    }
}
