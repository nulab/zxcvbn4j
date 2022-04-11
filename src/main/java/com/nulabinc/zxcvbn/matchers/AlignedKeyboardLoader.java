package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.io.Resource;

public class AlignedKeyboardLoader extends KeyboardLoader {

    public AlignedKeyboardLoader(final String name, final Resource inputStreamSource) {
        super(name, inputStreamSource);
    }

    @Override
    protected Keyboard.AdjacentGraphBuilder buildAdjacentGraphBuilder(final String layout) {
        return new AlignedAdjacentGraphBuilder(layout);
    }

}
