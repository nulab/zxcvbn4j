package com.nulabinc.zxcvbn;

public enum Pattern {
    Bruteforce("bruteforce"),
    Dictionary("dictionary"),
    Spatial("spatial"),
    Repeat("repeat"),
    Sequence("sequence"),
    Regex("regex"),
    Date("date");

    private final String value;

    private Pattern(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
