package dev.flowty.steamdeploy;

import dev.flowty.steamdeploy.CommandLine.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Handles creation of and interactions with an installation of <a
 * href="https://developer.valvesoftware.com/wiki/SteamCMD">SteamCMD</a>
 */
public class SteamCMD {

  private static final Logger LOG = LoggerFactory.getLogger(SteamCMD.class);

  private static final Path CONTENT_SUBDIR = Paths.get("content");
  private static final Path SCRIPTS_SUBDIR = Paths.get("scripts");

  private final Platform platform;
  private final Path directory;
  private final Timeouts timeouts = new Timeouts();

  /**
   * Builds a new steam console client, downloading the executables from the standard source URLs if
   * necessary.
   *
   * @param destination The installation directory
   */
  public SteamCMD(Path destination) {
    this(Optional.empty(), destination);
  }

  /**
   * Builds a new steam console client, downloading the executables if necessary
   *
   * @param source      The download URL
   * @param destination The installation directory
   */
  public SteamCMD(Optional<URL> source, Path destination) {
    platform = Platform.fromOsName();
    directory = installTo(source, destination);
  }

  /**
   * Controls command timeout thresholds
   *
   * @param t How to set the timeouts
   * @return {@code this}
   */
  public SteamCMD withTimeouts(Consumer<Timeouts> t) {
    t.accept(timeouts);
    return this;
  }

  public Result loginAndQuit(Auth auth) {
    auth.vdf().ifPresent(vdf -> {
      Path destination = platform.steamHome(directory).resolve("config", "config.vdf");
      LOG.info("INJECTING AUTH VDF TO {}", destination);
      vdf.writeTo(destination);
    });
    LOG.info("Logging in");
    Path full = directory.resolve(platform.steamCmd);
    if (!Files.exists(full)) {
      throw new IllegalStateException(full.toString());
    }
    return CommandLine.here()
      .failingAfter(timeouts.total())
      .toleratingInactivityOf(timeouts.inactivity())
      .run(Stream.of(
          full.toAbsolutePath().toString(),
          "+login", auth.username(), auth.password().orElse(null),
          "+quit")
        .filter(Objects::nonNull)
        .toArray(String[]::new)
      );
  }

  /**
   * Runs an app deployment
   *
   * @param auth     How to authenticate to steam
   * @param source   The directory that contains the application files
   * @param appBuild The application build script
   */
  public Result deploy(Auth auth, Path source, InjectableFile appBuild) {
    ingestApplication(source);
    return build(auth, generateBuildScript(appBuild));
  }

  private Path installTo(Optional<URL> source, Path destination) {
    if (!Files.exists(destination)) {
      LOG.info("Extracting SDK to {}", destination);
      platform.installTo(source, destination);
      platform.prepCMD(destination);
    } else {
      LOG.info("Reusing existing installation at {}", destination);
    }
    return destination;
  }

  private void ingestApplication(Path appDir) {
    Path content = directory.resolve(CONTENT_SUBDIR);
    QuietFiles.recursiveDelete(content);
    LOG.info("Copying application to {}", content);
    try (Stream<Path> files = QuietFiles.walk(appDir)) {
      files.forEach(source -> {
        Path sink = content.resolve(appDir.relativize(source));
        LOG.trace("copying\n  {}\n  {}", source, sink);
        QuietFiles.copy(source, sink);
      });
    }
  }

  private Path generateBuildScript(InjectableFile appBuild) {
    Path buildScript = directory.resolve(SCRIPTS_SUBDIR).resolve("script.vdf");
    LOG.info("generating build script to {}", buildScript);
    appBuild.writeTo(buildScript);
    return buildScript;
  }

  private Result build(Auth auth, Path script) {
    auth.vdf().ifPresent(vdf -> {
      Path destination = platform.steamHome(directory).resolve("config", "config.vdf");
      LOG.info("INJECTING AUTH VDF TO {}", destination);
      vdf.writeTo(destination);
    });

    LOG.info("Building app");
    Path full = directory.resolve(platform.steamCmd);
    if (!Files.exists(full)) {
      throw new IllegalStateException(full.toString());
    }
    Result result = CommandLine.here()
      .failingAfter(timeouts.total())
      .toleratingInactivityOf(timeouts.inactivity())
      .run(Stream.of(
          full.toAbsolutePath().toString(),
          "+login", auth.username(), auth.password().orElse(null),
          "+run_app_build",
          script.toAbsolutePath().toString(),
          "+quit")
        .filter(Objects::nonNull)
        .toArray(String[]::new)
      );

    if (result.status() == 0) {
      LOG.error("Build success!\n  {}",
        String.join("\n  ", result.stdOut()));
    } else {
      LOG.error("Build failure! {}\n  {}",
        result.status(),
        String.join("\n  ", result.stdOut()));
      throw new IllegalStateException("build failed with status " + result.status());
    }
    return result;
  }

}
