package com.nulabinc.zxcvbn;

public enum Pattern {
  @SuppressWarnings("java:S115")
  Bruteforce("bruteforce"),
  @SuppressWarnings("java:S115")
  Dictionary("dictionary"),
  @SuppressWarnings("java:S115")
  Spatial("spatial"),
  @SuppressWarnings("java:S115")
  Repeat("repeat"),
  @SuppressWarnings("java:S115")
  Sequence("sequence"),
  @SuppressWarnings("java:S115")
  Regex("regex"),
  @SuppressWarnings("java:S115")
  Date("date");

  private final String value;

  private Pattern(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
