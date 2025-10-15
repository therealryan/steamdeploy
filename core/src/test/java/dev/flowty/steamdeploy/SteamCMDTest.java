package dev.flowty.steamdeploy;

import static java.nio.charset.StandardCharsets.UTF_8;

import dev.flowty.steamdeploy.CommandLine.Result;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * Exercises {@link SteamCMD}
 */
class SteamCMDTest {

  @EnabledOnOs(OS.LINUX)
  @Test
  void linux() {
    testBuild("");
  }

  private static void testBuild(String expected) {
    Path appDir = Paths.get("target", "SteamCMDTest", "application");
    QuietFiles.recursiveDelete(appDir);
    QuietFiles.createDirectories(appDir);
    QuietFiles.write(appDir.resolve("file.txt"), "game content".getBytes(UTF_8));

    Path install = Paths.get("target", "SteamCMDTest", "installation");
    QuietFiles.recursiveDelete(install);

    String username = System.getenv("STEAM_USER_NAME");
    String vdfb64 = System.getenv("STEAM_AUTH_VDF");
    Auth auth = new Auth(username, InjectableFile.ofB64(vdfb64));
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