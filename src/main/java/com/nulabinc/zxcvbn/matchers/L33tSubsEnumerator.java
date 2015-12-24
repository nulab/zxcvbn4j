package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L33tSubsEnumerator {

    private final Map<Character, Character[]> table;

    private List<ArrayList<Character[]>> subs;

    L33tSubsEnumerator(Map<Character, Character[]> table) {
        this.table = table;
        this.subs = new ArrayList<>();
        this.subs.add(new ArrayList<Character[]>());
    }

    List<Map<Character, Character>> enumerate() {
        Character[] keys = this.table.keySet().toArray(new Character[]{});
        this.helper(keys);
        List<Map<Character, Character>> subDicts = new ArrayList<>();
        for (ArrayList<Character[]> sub: this.subs) {
            Map<Character, Character> subDict = new HashMap<>();
            for (Character[] ref: sub) {
                subDict.put(ref[0], ref[1]);
            }
            subDicts.add(subDict);
        }
        return subDicts;
    }

    private List<ArrayList<Character[]>> dedup(List<ArrayList<Character[]>> subs) {
        List<ArrayList<Character[]>> deduped = new ArrayList<>();
        Map<String, Boolean> members = new HashMap<>();
        for (ArrayList<Character[]> sub: subs) {
            Map<Integer, Character[]> assoc = new HashMap<>();
            for (int i = 0; i < sub.size(); i++) {
                assoc.put(i, sub.get(i));
            }
            List<String> strings = new ArrayList<>();
            for (Map.Entry<Integer, Character[]> entry: assoc.entrySet()) {
                strings.add(entry.getValue()+","+entry.getKey());
            }
            StringBuilder builder = new StringBuilder();
            for(String str : strings) {
                builder.append(str).append("-");
            }
            String label = builder.substring(0, builder.length() - 1);
            if (!members.containsKey(label)) {
                members.put(label, true);
                deduped.add(sub);
            }
        }
        return deduped;
    }

    private void helper(Character[] keys) {
        if (keys.length == 0) return;
        Character firstKey = keys[0];
        List<Character> restKeys = new ArrayList<>();
        for (int i = 1; i < keys.length; i++) {
            restKeys.add(keys[i]);
        }
        List<ArrayList<Character[]>> nextSubs = new ArrayList<>();
        for (Character l33tChr: this.table.get(firstKey)) {
            for (ArrayList<Character[]> sub: this.subs) {
                int dupL33tIndex = -1;
                for (int i = 0; i < sub.size(); i++) {
                    if (sub.get(i)[0] == l33tChr) {
                        dupL33tIndex = i;
                        break;
                    }
                }
                if (dupL33tIndex == -1) {
                    ArrayList<Character[]> subExtension = new ArrayList<>(sub);
                    subExtension.add(new Character[]{l33tChr, firstKey});
                    nextSubs.add(subExtension);
                } else {
                    ArrayList<Character[]> subAlternative = new ArrayList<>(sub);
                    subAlternative.remove(dupL33tIndex);
                    subAlternative.add(new Character[]{l33tChr, firstKey});
                    nextSubs.add(sub);
                    nextSubs.add(subAlternative);
                }
            }
        }
        this.subs = dedup(nextSubs);
        helper(restKeys.toArray(new Character[]{}));
    }
}
