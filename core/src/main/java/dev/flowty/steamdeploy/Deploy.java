package dev.flowty.steamdeploy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class Deploy implements Runnable {

  private static final String RUNTIME_IMAGE_DIRECTORY_PROPERTY = "runtime.image.directory";

  private final Properties systemProperties;
  private final Properties projectProperties;
  private final Properties userProperties;


  public Deploy(Properties systemProperties, Properties projectProperties,
      Properties userProperties) {
    this.systemProperties = systemProperties;
    this.projectProperties = projectProperties;
    this.userProperties = userProperties;
  }

  private String getProperty(String name) {
    return Stream.of(userProperties, projectProperties, systemProperties)
        .map(p -> p.getProperty(name))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void run() {
    try {

      String user = getString("steam.user");
      String password = System.getenv("STEAM_PASSWORD");
      String vdfBase64 = System.getenv("STEAM_VDF");

      if ("true".equals(getProperty("steam.cached.creds"))) {
        password = null;
        vdfBase64 = null;
      } else if (password == null && vdfBase64 == null) {
        throw new IllegalStateException("Need at least one of STEAM_PASSWORD or STEAM_VDF");
      }
      if (password != null && vdfBase64 != null) {
        throw new IllegalStateException("Need at most one of STEAM_PASSWORD or STEAM_VDF");
      }

      int appId = getID("steam.app.id");
      int depotId = getID("steam.depot.id");
      boolean preview = "true".equals(System.getProperty("steam.build.preview"));

      Path runtimeImageDir = Paths.get(
          Optional.ofNullable(getProperty(RUNTIME_IMAGE_DIRECTORY_PROPERTY)).orElseThrow(
              () -> new IllegalStateException(
                  "Set project property '" + RUNTIME_IMAGE_DIRECTORY_PROPERTY
                      + "' to point to the runtime image")));
//      LOG.info("Validating application at {}", runtimeImageDir);
      if (!Files.isDirectory(runtimeImageDir)) {
        throw new IllegalArgumentException("bad runtime image at " + runtimeImageDir);
      }

      SteamCMD sdk = new SteamCMD(runtimeImageDir.getParent().resolve("steam"));

      sdk.injectVDF(vdfBase64);

      sdk.ingestApplication(runtimeImageDir);

      Path script = sdk.generateBuildScript(
          getProperty("steam.build.desc"), appId, depotId, preview);

      sdk.build(user, password, script);

//      LOG.info("We're done here");

    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private String getString(String property) {
    String value = getProperty(property);
    if (value == null) {
      throw new IllegalArgumentException("missing '" + property + "' property");
    }
    return value;
  }

  private int getID(String property) {
    String value = getString(property);
    if (!value.matches("\\d+")) {
      throw new IllegalArgumentException("bad '" + property + "' property '" + value + "'");
    }
    return Integer.parseInt(value);
  }


}
