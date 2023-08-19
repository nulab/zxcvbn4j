package com.nulabinc.zxcvbn;

import static org.junit.Assert.assertNotNull;

import com.nulabinc.zxcvbn.io.ClasspathResource;
import com.nulabinc.zxcvbn.matchers.AlignedKeyboardLoader;
import com.nulabinc.zxcvbn.matchers.DictionaryLoader;
import com.nulabinc.zxcvbn.matchers.SlantedKeyboardLoader;
import java.io.IOException;
import org.junit.*;

public class ZxcvbnBuilderTest {

  @Test
  public void testBuild1() {
    Zxcvbn zxcvbn = new ZxcvbnBuilder().build();
    assertNotNull(zxcvbn);
  }

  @Test
  public void testBuild2() throws IOException {
    // This way is same as "new Zxcvbn();"
    Zxcvbn zxcvbn =
        new ZxcvbnBuilder()
            .dictionaries(StandardDictionaries.loadAllDictionaries())
            .keyboards(StandardKeyboards.loadAllKeyboards())
            .build();
    assertNotNull(zxcvbn);
  }

  @Test
  public void testBuild3() throws IOException {
    Zxcvbn zxcvbn =
        new ZxcvbnBuilder()
            .dictionary(StandardDictionaries.ENGLISH_WIKIPEDIA_LOADER.load())
            .dictionary(StandardDictionaries.PASSWORDS_LOADER.load())
            .keyboard(StandardKeyboards.QWERTY_LOADER.load())
            .keyboard(StandardKeyboards.DVORAK_LOADER.load())
            .build();
    assertNotNull(zxcvbn);
  }

  @Test
  public void testBuild4() throws IOException {
    Zxcvbn zxcvbn =
        new ZxcvbnBuilder()
            .dictionary(
                new DictionaryLoader(
                        "us_tv_and_film",
                        new ClasspathResource(
                            "/com/nulabinc/zxcvbn/matchers/dictionaries/us_tv_and_film.txt"))
                    .load())
            .keyboard(
                new SlantedKeyboardLoader(
                        "qwerty",
                        new ClasspathResource("/com/nulabinc/zxcvbn/matchers/keyboards/qwerty.txt"))
                    .load())
            .keyboard(
                new AlignedKeyboardLoader(
                        "keypad",
                        new ClasspathResource("/com/nulabinc/zxcvbn/matchers/keyboards/keypad.txt"))
                    .load())
            .build();
    assertNotNull(zxcvbn);
  }
}
