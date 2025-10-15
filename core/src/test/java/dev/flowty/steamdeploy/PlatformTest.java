package dev.flowty.steamdeploy;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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
        """
            
            linux32
            linux32/crashhandler.so
            linux32/libstdc++.so.6
            linux32/steamcmd
            linux32/steamerrorreporter
            steamcmd.sh""");
  }

  /**
   * Demonstrates download and extraction behaviour
   */
  @Test
  void macos() {
    test(Platform.MAC,
        """
            
            Frameworks
            Frameworks/Breakpad.framework
            Frameworks/Breakpad.framework/Breakpad
            Frameworks/Breakpad.framework/Headers
            Frameworks/Breakpad.framework/Resources
            Frameworks/Breakpad.framework/Versions
            Frameworks/Breakpad.framework/Versions/A
            Frameworks/Breakpad.framework/Versions/A/Breakpad
            Frameworks/Breakpad.framework/Versions/A/Headers
            Frameworks/Breakpad.framework/Versions/A/Headers/Breakpad.h
            Frameworks/Breakpad.framework/Versions/A/Headers/BreakpadDefines.h
            Frameworks/Breakpad.framework/Versions/A/Resources
            Frameworks/Breakpad.framework/Versions/A/Resources/Info.plist
            Frameworks/Breakpad.framework/Versions/A/Resources/Inspector
            Frameworks/Breakpad.framework/Versions/A/Resources/breakpadUtilities.dylib
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Info.plist
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/MacOS
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/MacOS/crash_report_sender
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/PkgInfo
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/Breakpad.nib
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/English.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/crash_report_sender.icns
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/da.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/de.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/es.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/fr.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/goArrow.png
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/it.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/ja.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/nl.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/no.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sl.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/sv.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj/InfoPlist.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/Resources/tr.lproj/Localizable.strings
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/_CodeSignature
            Frameworks/Breakpad.framework/Versions/A/Resources/crash_report_sender.app/Contents/_CodeSignature/CodeResources
            Frameworks/Breakpad.framework/Versions/A/_CodeSignature
            Frameworks/Breakpad.framework/Versions/A/_CodeSignature/CodeResources
            Frameworks/Breakpad.framework/Versions/Current
            crashhandler.dylib
            steamcmd
            steamcmd.sh""");
  }

  private static void test(Platform platform, String expected) {
    try {
      Path destination = Paths.get(
          "target", "PlatformTest", platform.name().toLowerCase());
      QuietFiles.recursiveDelete(destination);
      Files.createDirectories(destination);

      platform.installTo(Optional.empty(), destination);

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