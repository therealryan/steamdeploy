package dev.flowty.steamdeploy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenient builder API for Valve Data File content
 */
public class VDF {

  private final List<String> lines = new ArrayList<>();

  /**
   * @param name The root node name
   */
  public VDF(String name) {
    lines.add("\"" + name + "\"");
    lines.add("{");
  }

  /**
   * Adds a name/value pair
   * @return {@code this}
   */
  public VDF v(String name, String value) {
    lines.add("  \"" + name + "\" \"" + value + "\"");
    return this;
  }

  /**
   * Adds a child object
   * @param name child name
   * @param content child content
   * @return {@code this}
   */
  public VDF o(String name, Consumer<VDF> content) {
    VDF child = new VDF(name);
    content.accept(child);
    child.lines()
        .map(line -> "  " + line)
        .forEach(lines::add);
    return this;
  }

  private Stream<String> lines() {
    return Stream.concat(lines.stream(), Stream.of("}"));
  }

  @Override
  public String toString() {
    return lines().collect(Collectors.joining("\n", "", "\n")) ;
  }
}
