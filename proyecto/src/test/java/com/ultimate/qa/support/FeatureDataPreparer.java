package com.ultimate.qa.support;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/** Expand case identifiers from Excel before Cucumber discovers scenarios. */
public final class FeatureDataPreparer {
  private FeatureDataPreparer() {}

  public static void main(String[] args) throws Exception {
    Path source = Paths.get("src/test/resources/features");
    Path output = Paths.get("build/generated-features");
    Files.createDirectories(output);
    // Only files owned by this preparer, never user source features.
    try (Stream<Path> files = Files.list(output)) {
      for (Path file :
          (Iterable<Path>)
              files.filter(p -> p.getFileName().toString().matches("scenario-[0-9]+\\.feature"))
                  ::iterator) Files.delete(file);
    }
    try (Stream<Path> files = Files.list(source)) {
      for (Path file :
          (Iterable<Path>)
              files.filter(p -> p.getFileName().toString().endsWith(".feature"))::iterator) {
        String feature = Files.readString(file, StandardCharsets.UTF_8);
        String marker = "# ultimate-data-sheet: ";
        Optional<String> encoded =
            feature.lines().filter(line -> line.startsWith(marker)).findFirst();
        if (encoded.isPresent()) {
          String sheet =
              new String(
                  Base64.getDecoder().decode(encoded.get().substring(marker.length())),
                  StandardCharsets.UTF_8);
          int examples = feature.lastIndexOf("\n  Examples:");
          if (examples < 0)
            throw new IllegalArgumentException("Falta Examples en " + file.getFileName());
          StringBuilder table = new StringBuilder("\n  Examples:\n    | caso_id |\n");
          for (Map<String, String> row : TestData.rows(sheet))
            table.append("    | ").append(row.get("caso_id").replace("\\", "\\\\")).append(" |\n");
          feature = feature.substring(0, examples) + table;
        }
        Files.writeString(output.resolve(file.getFileName()), feature, StandardCharsets.UTF_8);
      }
    }
  }
}
