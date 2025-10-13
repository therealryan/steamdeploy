package dev.flowty.steamdeploy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * Encapsulates the platform-specific bits of steamcmd:
 * <ul>
 *   <li>Where to download it</li>
 *   <li>How to extract that download</li>
 *   <li>Where the executable lives inside the download</li>
 *   <li>How to prepare the executable for use</li>
 *   <li>Where to look for the config file where authentication is cached</li>
 * </ul>
 */
enum Platform {

  LINUX("https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz",
    Platform::extractTarGz,
    Paths.get("steamcmd.sh")) {
    @Override
    public void prepCMD(Path cmdDir) {
      CommandLine.here().run("chmod", "+x",
        cmdDir.resolve(steamCmd)
          .toAbsolutePath().toString());
      CommandLine.here().run("chmod", "+x",
        cmdDir.resolve("linux32", "steamcmd")
          .toAbsolutePath().toString());
    }

    @Override
    public Path steamHome(Path installation) {
      return Paths.get(System.getProperty("user.home"))
        .resolve(".steam", "steam");
    }
  },

  MAC("https://steamcdn-a.akamaihd.net/client/installer/steamcmd_osx.tar.gz",
    Platform::extractTarGz,
    Paths.get("steamcmd")) {
    @Override
    public void prepCMD(Path cmdDir) {
      CommandLine.here()
        .run("chmod", "+x",
          cmdDir.resolve(steamCmd).toAbsolutePath().toString());
      CommandLine.in(cmdDir)
        .run("bash", "steamcmd.sh", "+quit");
    }

    @Override
    public Path steamHome(Path installation) {
      return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Steam");
    }
  },

  WINDOWS("https://steamcdn-a.akamaihd.net/client/installer/steamcmd.zip",
    Platform::extractZip,
    Paths.get("steamcmd.exe")) {
    @Override
    public Path steamHome(Path installation) {
      return installation;
    }

    @Override
    public void prepCMD(Path cmdDir) {
      // for mysterious reasons the first invocation of steamcmd, where it
      // updates itself, will exit with status 7
      // let's get that out of the way here where we don't care about the
      // exit status.
      CommandLine.here().run(
        cmdDir.resolve(steamCmd).toAbsolutePath().toString(),
        "+quit");
    }
  };

  public final URL source;
  private final BiConsumer<URL, Path> extractor;
  public final Path steamCmd;

  Platform(String source,
           BiConsumer<URL, Path> extractor,
           Path steamCmd) {
    try {
      this.source = new URI(source).toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
    this.extractor = extractor;
    this.steamCmd = steamCmd;
  }

  public void installTo(Path destination) {
    extractor.accept(source, destination);
  }

  /**
   * @param installation Where <code>steamcmd</code> lives
   * @return Where we should look for <code>config/config.vdf</code>
   */
  public abstract Path steamHome(Path installation);

  public void prepCMD(Path cmdDir) {
    // default to no-op
  }

  public static Platform fromOsName() {
    String osName = System.getProperty("os.name");
    for (Platform p : values()) {
      if (osName.toUpperCase().contains(p.name())) {
        return p;
      }
    }
    throw new IllegalStateException("Failed to detect platform from os.name '" + osName + "'");
  }

  private static void extractZip(URL zip, Path destination) {
    try (InputStream in = zip.openStream();
         ZipInputStream zin = new ZipInputStream(in);) {
      String canonDestination = destination.toFile().getCanonicalPath() + File.separator;
      ZipEntry ze;
      while ((ze = zin.getNextEntry()) != null) {
        Path sink = destination.resolve(ze.getName());
        if (!sink.toFile().getCanonicalPath().startsWith(canonDestination)) {
          throw new IllegalStateException("zipzlip " + ze.getName());
        }
        if (ze.isDirectory()) {
          Files.createDirectories(sink);
        } else {
          Files.createDirectories(sink.getParent());
          try (OutputStream out = Files.newOutputStream(sink)) {
            zin.transferTo(out);
          }
        }
      }
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  private static void extractTarGz(URL targz, Path destination) {
    try (InputStream in = targz.openStream();
         GzipCompressorInputStream gz = new GzipCompressorInputStream(in);
         TarArchiveInputStream tar = new TarArchiveInputStream(gz)) {
      String canonDestination = destination.toFile().getCanonicalPath() + File.separator;
      TarArchiveEntry tae;
      while ((tae = tar.getNextEntry()) != null) {
        if (tae.isFile()) {
          Path sink = destination.resolve(tae.getName());
          if (!sink.toFile().getCanonicalPath().startsWith(canonDestination)) {
            throw new IllegalStateException("zipslip " + tae.getName());
          }
          Files.createDirectories(sink.getParent());
          try (OutputStream out = Files.newOutputStream(sink)) {
            tar.transferTo(out);
          }
        }
      }

    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }
}