package dev.flowty.steamdeploy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link VDF}
 */
class VDFTest {

  /**
   * Shows API and results
   */
  @Test
  void usage() {
    Assertions.assertEquals(""
            + "\"root\"\n"
            + "{\n"
            + "  \"abc\" \"123\"\n"
            + "  \"branch\"\n"
            + "  {\n"
            + "    \"def\" \"456\"\n"
            + "    \"leaf\"\n"
            + "    {\n"
            + "      \"ghi\" \"789\"\n"
            + "    }\n"
            + "  }\n"
            + "}\n",
        new VDF("root")
            .v("abc", "123")
            .o("branch", b -> b
                .v("def", "456")
                .o("leaf", l -> l
                    .v("ghi", "789")
                )
            )
            .toString());
  }
}