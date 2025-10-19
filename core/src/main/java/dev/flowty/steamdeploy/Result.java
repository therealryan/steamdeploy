package dev.flowty.steamdeploy;

import java.util.List;

public record Result(int status, List<String> stdOut) {

}
