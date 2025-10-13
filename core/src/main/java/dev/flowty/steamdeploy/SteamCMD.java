package dev.flowty.steamdeploy;

import dev.flowty.steamdeploy.CommandLine.Result;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handles creation of and interactions with an installation of <a
 * href="https://developer.valvesoftware.com/wiki/SteamCMD">SteamCMD</a>
 */
public class SteamCMD {

  private static final Path CONTENT_SUBDIR = Paths.get("content");
  private static final Path SCRIPTS_SUBDIR = Paths.get("scripts");

  private final Platform platform;
  private final Path directory;

  public SteamCMD(Path destination) {
    platform = Platform.fromOsName();
    directory = extractTo(destination);
  }

  private Path extractTo(Path destination) {
    if (!Files.exists(destination)) {
//      LOG.info("Extracting SDK to {}", destination);
      platform.installTo(destination);
      platform.prepCMD(destination);
    }
    return destination;
  }

  public void injectVDF(String vdfBase64) {
    if (vdfBase64 != null) {
      Path config = platform.steamHome(directory).resolve("config", "config.vdf");
//      LOG.info("Injecting auth to {}", config.toAbsolutePath());
      try {
        Files.createDirectories(config.getParent());
        Files.write(config,
            Base64.getDecoder().decode(vdfBase64.replaceAll("\n", "")));
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }
    }
  }

  public void ingestApplication(Path appDir) {
    Path content = directory.resolve(CONTENT_SUBDIR);
    recursiveDelete(content);
//    LOG.info("Copying application to {}", content);
    try (Stream<Path> files = Files.walk(appDir)) {
      files.forEach(source -> {
        Path sink = content.resolve(appDir.relativize(source));
//        LOG.trace("copying\n  {}\n  {}", source, sink);
        try {
          Files.copy(source, sink);
        } catch (IOException ioe) {
          throw new UncheckedIOException(ioe);
        }
      });
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  public Path generateBuildScript(String description, int appId, int depotId, boolean preview) {
    Path buildScript = directory.resolve(SCRIPTS_SUBDIR).resolve("script.vdf");
//    LOG.info("generating build script to {} for app {} depot {}", buildScript, appId, depotId);
    try {
      Files.createDirectories(buildScript.getParent());
      Files.write(buildScript, buildScript(description, appId, depotId, preview));
      return buildScript;
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  public void build(String user, String password, Path script) {
//    LOG.info("Building app");
    Path full = directory.resolve(platform.steamCmd);
    if (!Files.exists(full)) {
      throw new IllegalStateException(full.toString());
    }
    Result result = CommandLine.here()
        .failingAfter(Duration.ofMinutes(5))
        .toleratingInactivityOf(Duration.ofMinutes(2))
        .run(Stream.of(
                full.toAbsolutePath().toString(),
                "+login", user, password,
                "+run_app_build",
                script.toAbsolutePath().toString(),
                "+quit")
            .filter(Objects::nonNull)
            .toArray(String[]::new)
        );

    if (result.status() == 0) {
//      LOG.error("Build success!\n  {}",
//          String.join("\n  ", result.stdOut()));
    } else {
//      LOG.error("Build failure! {}\n  {}",
//          result.status(),
//          String.join("\n  ", result.stdOut()));
      throw new IllegalStateException("build failed with status " + result.status());
    }
  }

  private static byte[] buildScript(String desc, int appId, int depotId, boolean preview) {
    return String.format("\"AppBuild\"\n"
                + "{\n"
                + "        \"AppID\" \"%s\" // your AppID\n"
                + "        \"Desc\" \"%s\"\n"
                + "        \"verbose\" \"0\" // spew more build details in console\n"
                + "        \"preview\" \"%s\" // make this a preview build only, nothing is uploaded\n"
                + "        \"ContentRoot\" \"..\\content\\\" // root content folder, relative to location of this file\n"
                + "        \"BuildOutput\" \"..\\output\\\" // build output folder for build logs and build cache files\n"
                + "        \"Depots\"\n" + "        {\n" + "                \"%s\" // your DepotID\n"
                + "                {\n" + "                        \"FileMapping\"\n"
                + "                        {\n"
                + "                                \"LocalPath\" \"*\" // all files from contentroot folder\n"
                + "                                \"DepotPath\" \".\" // mapped into the root of the depot\n"
                + "                                \"recursive\" \"1\" // include all subfolders\n"
                + "                        }\n" + "                }\n" + "        }\n" + "}\n",
            appId,
            desc,
            preview ? "1" : "0",
            depotId)
        .getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Deletes a file or directory
   *
   * @param path The path of the file/dir to delete
   */
   static void recursiveDelete(Path path) {
    try {
      if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
        // we're done here
      } else if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
        try (Stream<Path> children = Files.list(path)) {
          children.forEach(SteamCMD::recursiveDelete);
        }
        Files.delete(path);
      } else {
        Files.delete(path);
      }
    } catch (IOException ioe) {
      throw new UncheckedIOException("Failed to delete " + path, ioe);
    }
  }
}
