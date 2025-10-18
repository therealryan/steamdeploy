package dev.flowty.steamdeploy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * File content that can be injected into a file structure
 */
public interface InjectableFile {

  void writeTo(Path destination);

  /**
   * @param source file content source
   * @return VDF file that may be written to the file system
   */
  static InjectableFile from(Path source) {
    return destination -> {
      try {
        Files.createDirectories(destination.getParent());
        Files.copy(source, destination);
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }
    };
  }

  /**
   * @param content file content
   * @return VDF file that may be written to the file system
   */
  static InjectableFile of(String content) {
    return destination -> {
      try {
        Files.createDirectories(destination.getParent());
        Files.writeString(destination, content);
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }
    };
  }

  /**
   * Handles a common usecase for authentication VDF files, where they get stored in base64-encoded
   * form as an environment variable in CI
   *
   * @param base64content base64-encoded file content
   * @return VDF file that may be written to the file system
   */
  static InjectableFile ofB64(String base64content) {
    return of(Optional.of(base64content)
      .map(lines -> lines.replaceAll("\n", ""))
      .map(b64 -> Base64.getDecoder().decode(b64))
      .map(bytes -> new String(bytes, UTF_8))
      .orElseThrow());
  }

  /**
   * Builds a simple <code>AppBuild</code> VDF file
   *
   * @param appId       The steam application ID
   * @param description A description for the build
   * @param verbose     {@code true} to enable more build logs
   * @param preview     {@code true} to build the app but not actually upload it
   * @param depotId     The depot ID to upload to
   * @return <code>AppBuild</code> VDF content
   */
  static InjectableFile appBuild(int appId, String description, boolean verbose, boolean preview,
                                 int depotId) {
    return of(new VDF("AppBuild")
      .v("AppId", String.valueOf(appId))
      .v("Desc", description)
      // spew more build details in console
      .v("verbose", verbose ? "1" : "0")
      // make this a preview build only, nothing is uploaded
      .v("preview", preview ? "1" : "0")
      // root content folder, relative to location of this file
      .v("ContentRoot", "..\\content\\")
      // build output folder for build logs and build cache files
      .v("BuildOutput", "..\\output\\")
      .o("Depots", ds -> ds
        .o(String.valueOf(depotId), d -> d
          .o("FileMapping", f -> f
            // all files from contentroot folder
            .v("LocalPath", "*")
            // mapped into the root of the depot
            .v("DepotPath", ".")
            // include all subfolders
            .v("recursive", "1")
          )
        )
      )
      .toString());
  }
}
