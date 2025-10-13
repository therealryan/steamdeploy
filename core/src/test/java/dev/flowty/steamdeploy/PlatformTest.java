package dev.flowty.steamdeploy;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link Platform}
 */
class PlatformTest {

  /**
   * Demonstrates download and extraction behaviour
   */
  @Test
  void windows() {
    test(Platform.WINDOWS,
        "\n"
            + "steamcmd.exe");
  }

  /**
   * Demonstrates download and extraction behaviour
   */
  @Test
  void linux() {
    test(Platform.LINUX,
        "\n"
            + "linux32\n"
            + "linux32/crashhandler.so\n"
            + "linux32/libstdc++.so.6\n"
            + "linux32/steamcmd\n"
            + "linux32/steamerrorreporter\n"
            + "steamcmd.sh");
  }

  /**
   * Demonstrates download and extraction behaviour
   */
  @Test
  void macos() {
    test(Platform.MAC,
        "\n"
            + "Frameworks\n"
            + "Frameworks/Breakpad.framework\n"
            + "Frameworks/Breakpad.framework/Breakpad\n"
            + "Frameworks/Breakpad.framework/Headers\n"
            + "Frameworks/Breakpad.framework/Resources\n"
            + "Frameworks/Breakpad.framework/Versions\n"
            + "Frameworks/Breakpad.framework/Versions/A\n"
            + "Frameworks/Breakpad.framework/Versions/A/Breakpad\n"
            + "Frameworks/Breakpad.framework/Versions/A/Headers\n"
            + "Frameworks/Breakpad.framework/Versions/A/Headers/Breakpad.h\n"
            + "Frameworks/Breakpad.framework/Versions/A/Headers/BreakpadDefines.h\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/Info.plist\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/Inspector\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/breakpadUtilities.dylib\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Info.plist\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/MacOS\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/MacOS/crash_report_sender\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/PkgInfo\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/Breakpad.nib\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/crash_report_sender.icns\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/goArrow.png\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj/InfoPlist.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj/Localizable.strings\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/_CodeSignature\n"
            + "Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/_CodeSignature/CodeResources\n"
            + "Frameworks/Breakpad.framework/Versions/A/_CodeSignature\n"
            + "Frameworks/Breakpad.framework/Versions/A/_CodeSignature/CodeResources\n"
            + "Frameworks/Breakpad.framework/Versions/Current\n"
            + "crashhandler.dylib\n"
            + "steamcmd\n"
            + "steamcmd.sh");
  }

  private static void test(Platform platform, String expected) {
    try {
      Path destination = Paths.get(
          "target", "PlatformTest", platform.name().toLowerCase());
      SteamCMD.recursiveDelete(destination);
      Files.createDirectories(destination);

      platform.installTo(destination);

      try (Stream<Path> paths = Files.walk(destination)) {
        Assertions.assertEquals(expected,
            paths.map(destination::relativize)
                .map(Path::toString)
                .map(s -> s.replace('\\', '/'))
                .sorted()
                .collect(joining("\n")));
      }
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }
}