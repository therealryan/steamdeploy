package dev.flowty.steamdeploy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InjectableFileTest {

  @Test
  void appBuild() {
    InjectableFile actual = InjectableFile.appBuild(1234, "", true, false, 5678);
    Path destination = Paths.get("target/InjectableFileTest/appBuild.vdf");
    actual.writeTo(destination);
    Assertions.assertEquals(""
            + "\"AppBuild\"\n"
            + "{\n"
            + "  \"AppId\" \"1234\"\n"
            + "  \"Desc\" \"\"\n"
            + "  \"verbose\" \"1\"\n"
            + "  \"preview\" \"0\"\n"
            + "  \"ContentRoot\" \"..\\content\\\"\n"
            + "  \"BuildOutput\" \"..\\output\\\"\n"
            + "  \"Depots\"\n"
            + "  {\n"
            + "    \"5678\"\n"
            + "    {\n"
            + "      \"FileMapping\"\n"
            + "      {\n"
            + "        \"LocalPath\" \"*\"\n"
            + "        \"DepotPath\" \".\"\n"
            + "        \"recursive\" \"1\"\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n",
        new String(QuietFiles.readAllBytes(destination), StandardCharsets.UTF_8));
  }
}