package com.nulabinc.zxcvbn;

public enum Pattern {
  @SuppressWarnings("squid:S115")
  Bruteforce("bruteforce"),
  @SuppressWarnings("squid:S115")
  Dictionary("dictionary"),
  @SuppressWarnings("squid:S115")
  Spatial("spatial"),
  @SuppressWarnings("squid:S115")
  Repeat("repeat"),
  @SuppressWarnings("squid:S115")
  Sequence("sequence"),
  @SuppressWarnings("squid:S115")
  Regex("regex"),
  @SuppressWarnings("squid:S115")
  Date("date");

  private final String value;

  private Pattern(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
