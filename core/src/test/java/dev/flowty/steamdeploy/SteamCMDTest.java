package dev.flowty.steamdeploy;

import dev.flowty.steamdeploy.CommandLine.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Exercises {@link SteamCMD}
 */
class SteamCMDTest {

  private static final String USERNAME = System.getenv("STEAM_USER_NAME");
  private static final String AUTH_VDF_B64 = System.getenv("STEAM_AUTH_VDF");

  @BeforeAll
  static void preconditions() {
    Assumptions.assumeTrue(USERNAME != null && !USERNAME.isEmpty(), "missing username");
    Assumptions.assumeTrue(AUTH_VDF_B64 != null && !AUTH_VDF_B64.isEmpty(), "missing auth vdf");
  }

  @EnabledOnOs(OS.LINUX)
  @Test
  void linux() {
    testExecution("" +
      "Steam Console Client (c) Valve Corporation - version _masked_\n" +
      "-- type 'quit' to exit --\n" +
      "Loading Steam API...OK\n" +
      "Logging in using cached credentials.\n" +
      "Logging in user '_masked_] to Steam Public...OK\n" +
      "Waiting for client config...OK\n" +
      "Waiting for user info...OK\n" +
      "Unloading Steam API...OK\n" +
      "Redirecting stderr to '_masked_'\n" +
      "Logging directory: '_masked_'\n" +
      "[  0%] Checking for available updates...\n" +
      "[----] Verifying installation...");
  }

  @EnabledOnOs(OS.WINDOWS)
  @Test
  void windows() {
    testExecution("" +
      "Steam Console Client (c) Valve Corporation - version _masked_\n" +
      "-- type 'quit' to exit --\n" +
      "Loading Steam API...OK\n" +
      "Logging in using cached credentials.\n" +
      "Logging in user '_masked_] to Steam Public...OK\n" +
      "Waiting for client config...OK\n" +
      "Waiting for user info...OK\n" +
      "Unloading Steam API...OK\n" +
      "Redirecting stderr to '_masked_'\n" +
      "Logging directory: '_masked_'\n" +
      "[  0%] Checking for available updates...\n" +
      "[----] Verifying installation...");
  }

  @EnabledOnOs(OS.MAC)
  @Test
  void mac() {
    testExecution("" +
      "Steam Console Client (c) Valve Corporation - version _masked_\n" +
      "-- type 'quit' to exit --\n" +
      "Loading Steam API...OK\n" +
      "Logging in using cached credentials.\n" +
      "Logging in user '_masked_] to Steam Public...OK\n" +
      "Waiting for client config...OK\n" +
      "Waiting for user info...OK\n" +
      "Unloading Steam API...OK\n" +
      "Redirecting stderr to '_masked_'\n" +
      "Logging directory: '_masked_'\n" +
      "[  0%] Checking for available updates...\n" +
      "[----] Verifying installation...");
  }

  /**
   * Shows that we can login and immediately quit
   *
   * @param expected expected stdout content
   */
  private static void testExecution(String expected) {
    Path install = Paths.get("target", "SteamCMDTest", "installation");
    QuietFiles.recursiveDelete(install);

    Result result = new SteamCMD(install)
      .loginAndQuit(new Auth(
        USERNAME,
        InjectableFile.ofB64(AUTH_VDF_B64)));

    Assertions.assertEquals(0, result.status());
    Assertions.assertEquals(expected,
      String.join("\n", result.stdOut())
        .replaceAll("( - version )\\d+", "$1_masked_")
        .replaceAll("(Logging in user ').*] ", "$1_masked_] ")
        .replaceAll("(Redirecting stderr to ').*'", "$1_masked_'")
        .replaceAll("(Logging directory: ').*'", "$1_masked_'")
    );
  }
}