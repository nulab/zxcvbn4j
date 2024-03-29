package com.nulabinc.zxcvbn;

public class AttackTimes {

  private CrackTimeSeconds crackTimeSeconds;
  private CrackTimesDisplay crackTimesDisplay;
  private int score;

  public AttackTimes(
      CrackTimeSeconds crackTimeSeconds, CrackTimesDisplay crackTimesDisplay, int score) {
    this.crackTimeSeconds = crackTimeSeconds;
    this.crackTimesDisplay = crackTimesDisplay;
    this.score = score;
  }

  public CrackTimeSeconds getCrackTimeSeconds() {
    return crackTimeSeconds;
  }

  /**
   * Sets the crack time in seconds.
   *
   * @param crackTimeSeconds The crack time in seconds.
   * @deprecated It is recommended to initialize using the constructor.
   */
  @Deprecated
  public void setCrackTimeSeconds(CrackTimeSeconds crackTimeSeconds) {
    this.crackTimeSeconds = crackTimeSeconds;
  }

  public CrackTimesDisplay getCrackTimesDisplay() {
    return crackTimesDisplay;
  }

  /**
   * Sets the display representation for the crack times.
   *
   * @param crackTimesDisplay The display values for crack times.
   * @deprecated It is recommended to initialize using the constructor.
   */
  @Deprecated
  public void setCrackTimesDisplay(CrackTimesDisplay crackTimesDisplay) {
    this.crackTimesDisplay = crackTimesDisplay;
  }

  public int getScore() {
    return score;
  }

  /**
   * Sets the score value.
   *
   * @param score The score value.
   * @deprecated It is recommended to initialize using the constructor.
   */
  @Deprecated
  public void setScore(int score) {
    this.score = score;
  }

  public static class CrackTimeSeconds {
    private double onlineThrottling100perHour;
    private double onlineNoThrottling10perSecond;
    private double offlineSlowHashing1e4perSecond;
    private double offlineFastHashing1e10PerSecond;

    public CrackTimeSeconds(
        double onlineThrottling100perHour,
        double onlineNoThrottling10perSecond,
        double offlineSlowHashing1e4perSecond,
        double offlineFastHashing1e10PerSecond) {
      this.onlineThrottling100perHour = onlineThrottling100perHour;
      this.onlineNoThrottling10perSecond = onlineNoThrottling10perSecond;
      this.offlineSlowHashing1e4perSecond = offlineSlowHashing1e4perSecond;
      this.offlineFastHashing1e10PerSecond = offlineFastHashing1e10PerSecond;
    }

    public double getOnlineThrottling100perHour() {
      return onlineThrottling100perHour;
    }

    /**
     * Sets the time required to crack a password with online throttling at 100 attempts per hour.
     *
     * @param onlineThrottling100perHour Time in seconds for online throttling at 100 attempts per
     *     hour.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOnlineThrottling100perHour(double onlineThrottling100perHour) {
      this.onlineThrottling100perHour = onlineThrottling100perHour;
    }

    public double getOnlineNoThrottling10perSecond() {
      return onlineNoThrottling10perSecond;
    }

    /**
     * Sets the time required to crack a password with online attacks without throttling at 10
     * attempts per second.
     *
     * @param onlineNoThrottling10perSecond Time in seconds for online attacks without throttling at
     *     10 attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOnlineNoThrottling10perSecond(double onlineNoThrottling10perSecond) {
      this.onlineNoThrottling10perSecond = onlineNoThrottling10perSecond;
    }

    public double getOfflineSlowHashing1e4perSecond() {
      return offlineSlowHashing1e4perSecond;
    }

    /**
     * Sets the time required to crack a password with offline slow hashing at 1e4 attempts per
     * second.
     *
     * @param offlineSlowHashing1e4perSecond Time in seconds for offline slow hashing at 1e4
     *     attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOfflineSlowHashing1e4perSecond(double offlineSlowHashing1e4perSecond) {
      this.offlineSlowHashing1e4perSecond = offlineSlowHashing1e4perSecond;
    }

    public double getOfflineFastHashing1e10PerSecond() {
      return offlineFastHashing1e10PerSecond;
    }

    /**
     * Sets the time required to crack a password with offline fast hashing at 1e10 attempts per
     * second.
     *
     * @param offlineFastHashing1e10PerSecond Time in seconds for offline fast hashing at 1e10
     *     attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOfflineFastHashing1e10PerSecond(double offlineFastHashing1e10PerSecond) {
      this.offlineFastHashing1e10PerSecond = offlineFastHashing1e10PerSecond;
    }
  }

  public static class CrackTimesDisplay {
    private String onlineThrottling100perHour;
    private String onlineNoThrottling10perSecond;
    private String offlineSlowHashing1e4perSecond;
    private String offlineFastHashing1e10PerSecond;

    public CrackTimesDisplay(
        String onlineThrottling100perHour,
        String onlineNoThrottling10perSecond,
        String offlineSlowHashing1e4perSecond,
        String offlineFastHashing1e10PerSecond) {
      this.onlineThrottling100perHour = onlineThrottling100perHour;
      this.onlineNoThrottling10perSecond = onlineNoThrottling10perSecond;
      this.offlineSlowHashing1e4perSecond = offlineSlowHashing1e4perSecond;
      this.offlineFastHashing1e10PerSecond = offlineFastHashing1e10PerSecond;
    }

    public String getOnlineThrottling100perHour() {
      return onlineThrottling100perHour;
    }

    /**
     * Sets the display representation for the time required to crack a password with online
     * throttling at 100 attempts per hour.
     *
     * @param onlineThrottling100perHour Display representation for online throttling at 100
     *     attempts per hour.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOnlineThrottling100perHour(String onlineThrottling100perHour) {
      this.onlineThrottling100perHour = onlineThrottling100perHour;
    }

    public String getOnlineNoThrottling10perSecond() {
      return onlineNoThrottling10perSecond;
    }

    /**
     * Sets the display representation for the time required to crack a password with online attacks
     * without throttling at 10 attempts per second.
     *
     * @param onlineNoThrottling10perSecond Display representation for online attacks without
     *     throttling at 10 attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOnlineNoThrottling10perSecond(String onlineNoThrottling10perSecond) {
      this.onlineNoThrottling10perSecond = onlineNoThrottling10perSecond;
    }

    public String getOfflineSlowHashing1e4perSecond() {
      return offlineSlowHashing1e4perSecond;
    }

    /**
     * Sets the display representation for the time required to crack a password with offline slow
     * hashing at 1e4 attempts per second.
     *
     * @param offlineSlowHashing1e4perSecond Display representation for offline slow hashing at 1e4
     *     attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOfflineSlowHashing1e4perSecond(String offlineSlowHashing1e4perSecond) {
      this.offlineSlowHashing1e4perSecond = offlineSlowHashing1e4perSecond;
    }

    public String getOfflineFastHashing1e10PerSecond() {
      return offlineFastHashing1e10PerSecond;
    }

    /**
     * Sets the display representation for the time required to crack a password with offline fast
     * hashing at 1e10 attempts per second.
     *
     * @param offlineFastHashing1e10PerSecond Display representation for offline fast hashing at
     *     1e10 attempts per second.
     * @deprecated This method is deprecated. It is recommended to initialize using the constructor.
     */
    @Deprecated
    public void setOfflineFastHashing1e10PerSecond(String offlineFastHashing1e10PerSecond) {
      this.offlineFastHashing1e10PerSecond = offlineFastHashing1e10PerSecond;
    }
  }
}
