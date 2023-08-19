package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.io.Resource;

public class SlantedKeyboardLoader extends KeyboardLoader {

  public SlantedKeyboardLoader(final String name, final Resource inputStreamSource) {
    super(name, inputStreamSource);
  }

  @Override
  protected Keyboard.AdjacentGraphBuilder buildAdjacentGraphBuilder(final String layout) {
    return new SlantedAdjacentGraphBuilder(layout);
  }
}
