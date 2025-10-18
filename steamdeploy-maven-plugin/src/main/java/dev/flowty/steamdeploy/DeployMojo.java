package dev.flowty.steamdeploy;

import dev.flowty.steamdeploy.CommandLine.Result;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Deploys an application to steam
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployMojo extends AbstractMojo {

  /**
   * Optional: The URL from which to download the steamCMD executable, if the defaults don't work
   * for you
   */
  @Parameter(property = "deploy.source")
  private String source;

  /**
   * Optional: The directory in which to install the steamCMD executable
   */
  @Parameter(property = "deploy.install", defaultValue = "target/steamcmd")
  private String install;

  /**
   * The steam user name.
   */
  @Parameter(property = "deploy.user", required = true)
  private String user;

  private String password = System.getenv("STEAM_PASSWORD");

  private String authVDF = System.getenv("STEAM_AUTH_VDF");

  /**
   * The directory that holds the application to deploy
   */
  @Parameter( property = "deploy.application", required = true)
  private String application;

  /**
   * Optional: The path to the appBuild install script for your deployed application. If this is
   * supplied then all of:
   * <ul>
   *   <li><code>appId</code></li>
   *   <li><code>depotId</code></li>
   *   <li><code>description</code></li>
   *   <li><code>verbose</code></li>
   *   <li><code>preview</code></li>
   * </ul>
   * are forbidden
   */
  @Parameter(property = "deploy.script")
  private String script;

  /**
   * Optional: The steam application ID. <code>depotId</code> is also required.
   */
  @Parameter(property = "deploy.appId")
  private Integer appId;

  /**
   * Optional: The steam depot ID. <code>appId</code> is also required.
   */
  @Parameter(property = "deploy.depotId")
  private Integer depotId;

  /**
   * Optional: A description for the build.
   */
  @Parameter(property = "deploy.description")
  private String description;

  /**
   * Optional: Controls verbose logging for the steam deployment
   */
  @Parameter(property = "deploy.verbose")
  private Boolean verbose;

  /**
   * Optional: Skips the upload of compiled application content
   */
  @Parameter(property = "deploy.preview")
  private Boolean preview;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Auth auth = buildAuth();
    InjectableFile appBuild = appBuild();
    SteamCMD steamCMD = new SteamCMD(source(), Paths.get(install));
    Result result = steamCMD.deploy(auth, Paths.get(application), appBuild);
    if (result.status() != 0) {
      throw new MojoFailureException("deployment failed");
    }
  }

  private Optional<URL> source() throws MojoFailureException {
    if (source == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(new URI(source).toURL());
    } catch (MalformedURLException | URISyntaxException e) {
      throw new MojoFailureException("Bad source URL", e);
    }
  }

  private Auth buildAuth() throws MojoFailureException {
    if (authVDF != null && password != null) {
      throw new MojoFailureException("password is extraneous when an auth VDF is supplied");
    }

    if (authVDF != null) {
      return new Auth(user, InjectableFile.ofB64(authVDF));
    } else if (password != null) {
      return new Auth(user, password);
    } else {
      return new Auth(user);
    }
  }

  private InjectableFile appBuild() throws MojoFailureException {
    if (script != null) {
      if (Stream.of(appId, depotId, description, verbose, preview).anyMatch(Objects::nonNull)) {
        throw new MojoFailureException("appBuild vdf parameters are extraneous"
            + "when custom script is supplied");
      }

      return InjectableFile.from(Paths.get(script));
    } else if (appId != null && depotId != null) {
      return InjectableFile.appBuild(
          appId,
          Optional.ofNullable(description)
              .orElse( System.getProperty("os.name") + "@" +  Instant.now()),
          Optional.ofNullable(verbose).orElse(false),
          Optional.ofNullable(preview).orElse(false),
          depotId);
    } else {
      throw new MojoFailureException(
          "Missing app build parameters. Supply either `script` or `appId` and `depotId");
    }
  }
}
