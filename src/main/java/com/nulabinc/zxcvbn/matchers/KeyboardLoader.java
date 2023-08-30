package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.io.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class KeyboardLoader {

  private final String name;

  private final Resource resource;

  protected KeyboardLoader(final String name, final Resource resource) {
    this.name = name;
    this.resource = resource;
  }

  public Keyboard load() throws IOException {
    InputStream inputStream = resource.getInputStream();
    String layout = loadAsString(inputStream);
    return new Keyboard(name, buildAdjacentGraphBuilder(layout));
  }

  protected abstract Keyboard.AdjacentGraphBuilder buildAdjacentGraphBuilder(final String layout);

  private static String loadAsString(final InputStream input) {
    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
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
}
