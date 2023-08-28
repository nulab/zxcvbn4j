package com.nulabinc.zxcvbn;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TimeEstimates {

  private static final double GUESSES_PER_HOUR = 100.0 / 3600.0;
  private static final double GUESSES_ONLINE_NO_THROTTLING = 10;
  private static final double GUESSES_OFFLINE_SLOW_HASHING = 1e4;
  private static final double GUESSES_OFFLINE_FAST_HASHING = 1e10;
  private static final int DELTA = 5;
  private static final double MINUTE = 60.0;
  private static final double HOUR = MINUTE * 60;
  private static final double DAY = HOUR * 24;
  private static final double MONTH = DAY * 31;
  private static final double YEAR = MONTH * 12;
  private static final double CENTURY = YEAR * 100;

  public static AttackTimes estimateAttackTimes(double guesses) {
    AttackTimes.CrackTimeSeconds crackTimeSeconds =
        new AttackTimes.CrackTimeSeconds(
            divide(guesses, GUESSES_PER_HOUR),
            guesses / GUESSES_ONLINE_NO_THROTTLING,
            guesses / GUESSES_OFFLINE_SLOW_HASHING,
            guesses / GUESSES_OFFLINE_FAST_HASHING);
    AttackTimes.CrackTimesDisplay crackTimesDisplay =
        new AttackTimes.CrackTimesDisplay(
            displayTime(crackTimeSeconds.getOnlineThrottling100perHour()),
            displayTime(crackTimeSeconds.getOnlineNoThrottling10perSecond()),
            displayTime(crackTimeSeconds.getOfflineSlowHashing1e4perSecond()),
            displayTime(crackTimeSeconds.getOfflineFastHashing1e10PerSecond()));
    return new AttackTimes(crackTimeSeconds, crackTimesDisplay, guessesToScore(guesses));
  }

  public static int guessesToScore(double guesses) {
    if (guesses < 1e3 + DELTA) {
      return 0;
    }
    if (guesses < 1e6 + DELTA) {
      return 1;
    }
    if (guesses < 1e8 + DELTA) {
      return 2;
    }
    if (guesses < 1e10 + DELTA) {
      return 3;
    }
    return 4;
  }

  public static String displayTime(final double seconds) {
    if (seconds < 1) {
      return "less than a second";
    }
    if (seconds < MINUTE) {
      return format(seconds, "%s second");
    }
    if (seconds < HOUR) {
      return format(divide(seconds, MINUTE), "%s minute");
    }
    if (seconds < DAY) {
      return format(divide(seconds, HOUR), "%s hour");
    }
    if (seconds < MONTH) {
      return format(divide(seconds, DAY), "%s day");
    }
    if (seconds < YEAR) {
      return format(divide(seconds, MONTH), "%s month");
    }
    if (seconds < CENTURY) {
      return format(divide(seconds, YEAR), "%s year");
    }
    return "centuries";
  }

  private static String format(double number, String text) {
    return String.format(text, Math.round(number)) + (number != 1 ? "s" : "");
  }

  private static double divide(double dividend, double divisor) {
    BigDecimal dividendDecimal = BigDecimal.valueOf(dividend);
    BigDecimal divisorDecimal = BigDecimal.valueOf(divisor);
    return dividendDecimal.divide(divisorDecimal, RoundingMode.HALF_DOWN).doubleValue();
  }
}
