package dev.flowty.steamdeploy;

import java.util.Optional;

/**
 * Encapsulates steam authorisation details
 */
public class Auth {

  private final String username;
  private Optional<String> password;
  private Optional<InjectableFile> vdf;

  /**
   * For when you're happy to manually handle 2FA flows
   */
  public Auth(String username, String password) {
    this.username = username;
    this.password = Optional.of(password);
    this.vdf = Optional.empty();
  }

  /**
   * For when you have the content of a previously-authorised session VDF. This is definitely the
   * way to go for usage in CI contexts.
   */
  public Auth(String username, InjectableFile vdf) {
    this.username = username;
    this.password = Optional.empty();
    this.vdf = Optional.of(vdf);
  }

  /**
   * For when you're confident that the local SDK installation already contains the VDF that
   * resulted from an earlier authorisation
   */
  public Auth(String username) {
    this.username = username;
    this.password = Optional.empty();
    this.vdf = Optional.empty();
  }

  String username() {
    return username;
  }

  Optional<String> password() {
    return password;
  }

  Optional<InjectableFile> vdf() {
    return vdf;
  }
}
