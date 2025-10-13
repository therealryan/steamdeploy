package dev.flowty.steamdeploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * How many times have I written this?
 */
class CommandLine {

  public record Result(int status, List<String> stdOut) {

  }

  private final Path workingDirectory;
  private Duration timeout = Duration.ofHours(1);
  private Duration inactivity = Duration.ofHours(1);

  private CommandLine(Path workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public static CommandLine here() {
    return in(null);
  }

  public static CommandLine in(Path workingDirectory) {
    return new CommandLine(workingDirectory);
  }

  public CommandLine failingAfter(Duration timeout) {
    this.timeout = timeout;
    return this;
  }

  public CommandLine toleratingInactivityOf(Duration inactivity) {
    this.inactivity = inactivity;
    return this;
  }

  public Result run(String... cmd) {
//    LOG.debug("Running {}", String.join(" ", cmd));
    try {
      ProcessBuilder builder = new ProcessBuilder(cmd)
          .redirectErrorStream(true);
      if (workingDirectory != null) {
        builder.directory(workingDirectory.toFile());
      }
      Process process = builder.start();
      List<String> lines = new ArrayList<>();
      AtomicReference<Instant> lastActivity = new AtomicReference<>(Instant.now());

      Thread outputReader = outputReader(process, lines, lastActivity);
      outputReader.start();
      Thread activityMonitor = activityMonitor(process, lastActivity);
      activityMonitor.start();

      boolean normalExit = process.waitFor(timeout.getSeconds(), TimeUnit.SECONDS);
      if (!normalExit) {
//        LOG.error("timeout breached!");
        process.destroyForcibly();
        return new Result(-1, lines);
      }
//      LOG.debug("status {}", process.exitValue());
      return new Result(process.exitValue(), lines);
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    } catch (InterruptedException ie) {
      throw new IllegalStateException("unexpected", ie);
    }
  }

  private static Thread outputReader(Process process, List<String> lines,
      AtomicReference<Instant> lastActivity) {
    Thread t = new Thread(() -> {
      try (BufferedReader br = process.inputReader()) {
        String line;
        while ((line = br.readLine()) != null) {
//          LOG.debug("\t{}", line);
          lines.add(line);
          lastActivity.set(Instant.now());
        }
      } catch (IOException ioe) {
//        LOG.error("Failed to stream command output", ioe);
      }
    });
    t.setDaemon(true);
    return t;
  }

  private Thread activityMonitor(Process process, AtomicReference<Instant> lastActivity) {
    Thread t = new Thread(() -> {

      while (process.isAlive()) {
        try {
          Thread.sleep(1000);
          Duration sinceLastActivity = Duration.between(lastActivity.get(), Instant.now());
          if (inactivity.getSeconds() < sinceLastActivity.getSeconds()) {
//            LOG.error("Killing process due to {} of inactivity", sinceLastActivity);
            process.destroyForcibly();
          }
        } catch (InterruptedException ie) {
//          LOG.warn("unexpected", ie);
        }
      }
    });
    t.setDaemon(true);
    return t;
  }
}
