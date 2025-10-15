package dev.flowty.steamdeploy;

import java.time.Duration;

/**
 * Command timeout thresholds
 */
public class Timeouts {

  private Duration inactivity = Duration.ofMinutes(2);
  private Duration total = Duration.ofMinutes(5);

  Duration inactivity() {
    return inactivity;
  }

  /**
   * @param inactivity How long no stdout activity will be tolerated for, before an error is raised
   */
  public void inactivity(Duration inactivity) {
    this.inactivity = inactivity;
  }

  Duration total() {
    return total;
  }

  /**
   * @param total How long to wait for command completion, before an error is raised
   */
  public void total(Duration total) {
    this.total = total;
  }
}
