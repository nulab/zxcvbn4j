package com.nulabinc.zxcvbn;

import java.math.BigDecimal;

public class TimeEstimates {

    public static final String ESTIMATES_LESSTHANASECOND = "estimates.lessThanASecond";
    public static final String ESTIMATES_SECONDS = "estimates.seconds";
    public static final String ESTIMATES_MINUTES = "estimates.minutes";
    public static final String ESTIMATES_HOURS = "estimates.hours";
    public static final String ESTIMATES_DAYS = "estimates.days";
    public static final String ESTIMATES_MONTHS = "estimates.months";
    public static final String ESTIMATES_YEARS = "estimates.years";
    public static final String ESTIMATES_CENTURIES = "estimates.centuries";



    public static AttackTimes estimateAttackTimes(double guesses) {
        AttackTimes.CrackTimeSeconds crackTimeSeconds = new AttackTimes.CrackTimeSeconds(
                divide(guesses, 100.0 / 3600.0),
                guesses / 10,
                guesses / 1e4,
                guesses / 1e10
        );
        AttackTimes.CrackTimesDisplay crackTimesDisplay = new AttackTimes.CrackTimesDisplay(
                displayTime(crackTimeSeconds.getOnlineThrottling100perHour()),
                displayTime(crackTimeSeconds.getOnlineNoThrottling10perSecond()),
                displayTime(crackTimeSeconds.getOfflineSlowHashing1e4perSecond()),
                displayTime(crackTimeSeconds.getOfflineFastHashing1e10PerSecond())
        );
        return new AttackTimes(crackTimeSeconds, crackTimesDisplay, guessesToScore(guesses));
    }

    public static int guessesToScore(double guesses) {
        int DELTA = 5;
        if (guesses < 1e3 + DELTA) return 0;
        else if (guesses < 1e6 + DELTA) return 1;
        else if (guesses < 1e8 + DELTA) return 2;
        else if (guesses < 1e10 + DELTA) return 3;
        else return 4;
    }

    public static String displayTime(final double seconds) {
        final Double minute = 60.0;
        final Double hour = minute * 60;
        final Double day = hour * 24;
        final Double month = day * 31;
        final Double year = month * 12;
        final Double century = year * 100;
        if (seconds < 1) return format(null, ESTIMATES_LESSTHANASECOND);
        else if (seconds < minute) return format(seconds, "%s " + ESTIMATES_SECONDS);
        else if (seconds < hour) return format(divide(seconds, minute), "%s " + ESTIMATES_MINUTES);
        else if (seconds < day) return format(divide(seconds, hour), "%s " + ESTIMATES_HOURS);
        else if (seconds < month) return format(divide(seconds, day), "%s " + ESTIMATES_DAYS);
        else if (seconds < year) return format(divide(seconds, month), "%s " + ESTIMATES_MONTHS);
        else if (seconds < century) return format(divide(seconds, year), "%s " + ESTIMATES_YEARS);
        else return format(null, ESTIMATES_CENTURIES);
    }

    private static String format(Double number, String text) {
        if (number != null) {
            text = String.format(text, Math.round(number)) + (number != 1 ? "s" : "");
        }
        return text;
    }

    private static double divide(double dividend, double divisor) {
        BigDecimal dividendDecimal = new BigDecimal(dividend);
        BigDecimal divisorDecimal = new BigDecimal(divisor);
        return dividendDecimal.divide(divisorDecimal, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }
}
