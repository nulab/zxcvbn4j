package com.nulabinc.zxcvbn;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
public class RandomPasswordMeasureBenchmark {

  @Param({"8", "32", "128", "512", "1024"})
  private int passwordLength;

  private String password;
  Zxcvbn zxcvbn;

  @Setup
  public void setup() throws IOException {
    zxcvbn =
        new ZxcvbnBuilder()
            .dictionaries(StandardDictionaries.loadAllDictionaries())
            .keyboards(StandardKeyboards.loadAllKeyboards())
            .build();

    Random random = new Random(42);
    StringBuilder sb = new StringBuilder(passwordLength);
    for (int i = 0; i < passwordLength; i++) {
      char c = (char) (random.nextInt() % Character.MAX_VALUE);
      sb.append(c);
    }
    password = sb.toString();
  }

  @Benchmark
  public Strength measure() {
    return zxcvbn.measure(password);
  }
}
