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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    testLogin();
  }

  @EnabledOnOs(OS.WINDOWS)
  @Test
  void windows() {
    testLogin();
  }

  @EnabledOnOs(OS.MAC)
  @Test
  void mac() {
    testLogin();
  }

  /**
   * Shows that we can login and immediately quit
   *
   * @param expected expected stdout content
   */
  private static void testLogin(String expected) {
    Path install = Paths.get("target", "SteamCMDTest", "installation");
    QuietFiles.recursiveDelete(install);

    Result result = new SteamCMD(install)
      .loginAndQuit(new Auth(USERNAME, InjectableFile.ofB64(AUTH_VDF_B64)));

    Assertions.assertEquals(0, result.status());
    Matcher loginMatcher = Pattern.compile("Logging in user .* to Steam Public...OK")
      .matcher(String.join("\n", result.stdOut()));
    Assertions.assertTrue(loginMatcher.find(), "failed to find successful login");
  }
}