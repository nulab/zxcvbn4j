package com.nulabinc.zxcvbn.matchers;

import com.nulabinc.zxcvbn.io.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DictionaryLoader {

  private final String name;

  private final Resource resource;

  public DictionaryLoader(final String name, final Resource resource) {
    this.name = name;
    this.resource = resource;
  }

  public Dictionary load() throws IOException {
    List<String> words = new ArrayList<>();
    // Reasons for not using StandardCharsets
    // refs: https://github.com/nulab/zxcvbn4j/issues/62
    try (final InputStream inputStream = resource.getInputStream();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        final BufferedReader br = new BufferedReader(inputStreamReader)) {
      String line;
      while ((line = br.readLine()) != null) {
        words.add(line);
      }
    } catch (IOException e) {
      throw new DictionaryLoadException("Error while reading " + name, e);
    }
    return new Dictionary(name, words);
  }

  static class DictionaryLoadException extends IOException {

    DictionaryLoadException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
