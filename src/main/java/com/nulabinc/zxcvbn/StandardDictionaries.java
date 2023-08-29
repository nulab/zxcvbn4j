package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.io.ClasspathResource;
import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.DictionaryLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandardDictionaries {

  @SuppressWarnings("squid:S1075")
  private static final String BASE_PATH = "/com/nulabinc/zxcvbn/matchers/dictionaries/";

  public static final String US_TV_AND_FILM = "us_tv_and_film";

  public static final String ENGLISH_WIKIPEDIA = "english_wikipedia";

  public static final String PASSWORDS = "passwords";

  public static final String SURNAMES = "surnames";

  public static final String MALE_NAMES = "male_names";

  public static final String FEMALE_NAMES = "female_names";

  public static final DictionaryLoader US_TV_AND_FILM_LOADER =
      new DictionaryLoader(US_TV_AND_FILM, new ClasspathResource(BASE_PATH + "us_tv_and_film.txt"));

  public static final DictionaryLoader ENGLISH_WIKIPEDIA_LOADER =
      new DictionaryLoader(
          ENGLISH_WIKIPEDIA, new ClasspathResource(BASE_PATH + "english_wikipedia.txt"));

  public static final DictionaryLoader PASSWORDS_LOADER =
      new DictionaryLoader(PASSWORDS, new ClasspathResource(BASE_PATH + "passwords.txt"));

  public static final DictionaryLoader SURNAMES_LOADER =
      new DictionaryLoader(SURNAMES, new ClasspathResource(BASE_PATH + "surnames.txt"));

  public static final DictionaryLoader MALE_NAMES_LOADER =
      new DictionaryLoader(MALE_NAMES, new ClasspathResource(BASE_PATH + "male_names.txt"));

  public static final DictionaryLoader FEMALE_NAMES_LOADER =
      new DictionaryLoader(FEMALE_NAMES, new ClasspathResource(BASE_PATH + "female_names.txt"));

  private StandardDictionaries() {
    throw new IllegalStateException("StandardDictionaries should not be instantiated");
  }

  private static final DictionaryLoader[] ALL_LOADERS = {
    US_TV_AND_FILM_LOADER,
    ENGLISH_WIKIPEDIA_LOADER,
    PASSWORDS_LOADER,
    SURNAMES_LOADER,
    MALE_NAMES_LOADER,
    FEMALE_NAMES_LOADER
  };

  public static List<Dictionary> loadAllDictionaries() throws IOException {
    List<Dictionary> dictionaries = new ArrayList<>();
    for (DictionaryLoader dictionaryLoader : ALL_LOADERS) {
      dictionaries.add(dictionaryLoader.load());
    }
    return dictionaries;
  }
}
