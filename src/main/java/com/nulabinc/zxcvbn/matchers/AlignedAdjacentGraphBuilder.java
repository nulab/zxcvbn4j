package com.nulabinc.zxcvbn.matchers;

import java.util.Arrays;
import java.util.List;

public class AlignedAdjacentGraphBuilder extends Keyboard.AdjacentGraphBuilder {

  public AlignedAdjacentGraphBuilder(final String layout) {
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
