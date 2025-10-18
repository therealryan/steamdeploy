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

import static java.nio.charset.StandardCharsets.UTF_8;

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
    testBuild("");
  }

  @EnabledOnOs(OS.WINDOWS)
  @Test
  void windows() {
    testBuild("");
  }

  private static void testBuild(String expected) {
    Path appDir = Paths.get("target", "SteamCMDTest", "application");
    QuietFiles.recursiveDelete(appDir);
    QuietFiles.createDirectories(appDir);
    QuietFiles.write(appDir.resolve("file.txt"), "game content".getBytes(UTF_8));

    Path install = Paths.get("target", "SteamCMDTest", "installation");
    QuietFiles.recursiveDelete(install);

    Auth auth = new Auth(USERNAME, InjectableFile.ofB64(AUTH_VDF_B64));
    InjectableFile appBuild = InjectableFile.appBuild(
      123, "description", false, true, 456);
    Result result = new SteamCMD(install)
      .deploy(auth,
        appDir,
        appBuild);

    Assertions.assertEquals(0, result.status());
    Assertions.assertEquals(expected, result.stdOut());
  }
}