package com.ultimate.qa.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestDataTest {
  @Rule public TemporaryFolder temporary = new TemporaryFolder();
  private String previous;

  @Before
  public void saveProperty() {
    previous = System.getProperty("data.file");
    TestData.clear();
  }

  @After
  public void restoreProperty() {
    TestData.clear();
    if (previous == null) System.clearProperty("data.file");
    else System.setProperty("data.file", previous);
  }

  private Path workbook(String[][] values) throws Exception {
    Path path = temporary.newFile("cases-" + UUID.randomUUID() + ".xlsx").toPath();
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Case");
      for (int r = 0; r < values.length; r++) {
        Row row = sheet.createRow(r);
        for (int c = 0; c < values[r].length; c++) row.createCell(c).setCellValue(values[r][c]);
      }
      try (OutputStream output = Files.newOutputStream(path)) {
        workbook.write(output);
      }
    }
    System.setProperty("data.file", path.toString());
    return path;
  }

  @Test
  public void suppliedWorkbookHasCasesAndOnlySecretReferences() {
    System.clearProperty("data.file");
    for (String sheet : List.of("Compra", "UsuarioBloqueado", "Cancelacion")) {
      List<Map<String, String>> rows = TestData.rows(sheet);
      assertFalse(rows.isEmpty());
      assertTrue(
          rows.stream().allMatch(row -> row.get("clave").matches("\\$\\{QA_SECRET_[A-F0-9]+\\}")));
    }
  }

  @Test
  public void workbookSelectsExactCaseAndRetainsTextIdentifiers() throws Exception {
    workbook(new String[][] {{"caso_id", "postal"}, {"A", "00123"}, {"B", "00456"}});
    TestData.use("Case", "B");
    assertEquals("00456", TestData.value("Case.postal"));
    assertThrows(IllegalStateException.class, () -> TestData.value("Other.postal"));
    assertThrows(IllegalStateException.class, () -> TestData.value("Case.missing"));
    TestData.clear();
    assertThrows(IllegalStateException.class, () -> TestData.value("postal"));
  }

  @Test
  public void invalidOrDuplicateCaseIdsFailBeforeExecution() throws Exception {
    for (String[][] values :
        List.of(
            new String[][] {{"caso_id"}, {"A"}, {"A"}}, new String[][] {{"caso_id"}, {" A "}},
            new String[][] {{"caso_id"}, {"A|B"}},
                new String[][] {{"caso_id", "value"}, {"", "present"}})) {
      workbook(values);
      assertThrows(IllegalArgumentException.class, () -> TestData.rows("Case"));
    }
  }

  @Test
  public void missingOrDuplicateHeadersAndMissingCasesFail() throws Exception {
    workbook(new String[][] {{"caso_id", "caso_id"}, {"A", "A"}});
    assertThrows(IllegalArgumentException.class, () -> TestData.rows("Case"));
    workbook(new String[][] {{"value"}, {"A"}});
    assertThrows(IllegalArgumentException.class, () -> TestData.rows("Case"));
    workbook(new String[][] {{"caso_id"}, {"A"}});
    assertThrows(IllegalArgumentException.class, () -> TestData.rows("Missing"));
    assertThrows(IllegalArgumentException.class, () -> TestData.use("Case", "Missing"));
  }

  @Test
  public void formulaCellsAreRejectedInsteadOfUsingAStaleCachedValue() throws Exception {
    Path path = workbook(new String[][] {{"caso_id", "value"}, {"A", "1"}});
    try (Workbook workbook =
        WorkbookFactory.create(new java.io.ByteArrayInputStream(Files.readAllBytes(path)))) {
      workbook.getSheet("Case").getRow(1).getCell(1).setCellFormula("1+1");
      try (OutputStream output = Files.newOutputStream(path)) {
        workbook.write(output);
      }
    }
    assertThrows(IllegalArgumentException.class, () -> TestData.rows("Case"));
  }
}
