package com.nulabinc.zxcvbn;

@SuppressWarnings("java:S115")
public enum Pattern {
  Bruteforce("bruteforce"),
  Dictionary("dictionary"),
  Spatial("spatial"),
  Repeat("repeat"),
  Sequence("sequence"),
  Regex("regex"),
  Date("date");

  private final String value;

  Pattern(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
