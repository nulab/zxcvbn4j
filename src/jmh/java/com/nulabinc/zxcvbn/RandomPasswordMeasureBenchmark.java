package com.nulabinc.zxcvbn;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

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
  Zxcvbn zxcvbn = new Zxcvbn();

  @Setup
  public void setup() {
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
