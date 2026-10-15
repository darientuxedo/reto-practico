package com.ultimate.qa.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/** Workbook is the data source. Nothing is cached between scenarios/runs. */
public final class TestData {
  private static final ThreadLocal<Map<String, String>> CURRENT = new ThreadLocal<>();
  private static final ThreadLocal<String> CURRENT_SHEET = new ThreadLocal<>();

  private TestData() {}

  public static List<Map<String, String>> rows(String sheetName) {
    Path path =
        Paths.get(System.getProperty("data.file", "src/test/resources/data/test-data.xlsx"));
    try {
      if (Files.size(path) > 20 * 1024 * 1024)
        throw new IllegalArgumentException("Excel supera 20 MB");
      try (Workbook workbook = WorkbookFactory.create(path.toFile(), null, true)) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) throw new IllegalArgumentException("No existe la hoja: " + sheetName);
        if (sheet.getLastRowNum() > 10000)
          throw new IllegalArgumentException("Máximo 10000 filas por hoja");
        Row header = sheet.getRow(0);
        if (header == null || header.getLastCellNum() < 1 || header.getLastCellNum() > 256)
          throw new IllegalArgumentException("Cabecera de Excel inválida");
        DataFormatter formatter = new DataFormatter(Locale.ROOT);
        List<String> columns = new ArrayList<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
          String name = text(header.getCell(c), formatter).trim();
          if (name.isEmpty() || columns.contains(name))
            throw new IllegalArgumentException("Columna vacía o duplicada en " + sheetName);
          columns.add(name);
        }
        if (!columns.contains("caso_id"))
          throw new IllegalArgumentException("Falta caso_id en " + sheetName);
        Set<String> identifiers = new HashSet<>();
        List<Map<String, String>> result = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
          Row row = sheet.getRow(r);
          if (row == null) continue;
          Map<String, String> values = new LinkedHashMap<>();
          for (int c = 0; c < columns.size(); c++)
            values.put(columns.get(c), text(row.getCell(c), formatter));
          if (values.values().stream().allMatch(String::isEmpty)) continue;
          String id = values.get("caso_id");
          if (id.trim().isEmpty()
              || !id.equals(id.trim())
              || id.contains("|")
              || id.contains("\n")
              || id.contains("\r")
              || !identifiers.add(id))
            throw new IllegalArgumentException(
                "caso_id vacío, inválido o duplicado en " + sheetName + ", fila " + (r + 1));
          result.add(values);
        }
        if (result.isEmpty()) throw new IllegalArgumentException("No hay casos en " + sheetName);
        return result;
      }
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo leer Excel: " + path, e);
    }
  }

  private static String text(Cell cell, DataFormatter formatter) {
    if (cell == null) return "";
    if (cell.getCellType() == CellType.FORMULA)
      throw new IllegalArgumentException("Usa valores, no fórmulas, en los datos de prueba");
    if (cell.getCellType() == CellType.ERROR)
      throw new IllegalArgumentException("La hoja contiene una celda con error");
    return formatter.formatCellValue(cell);
  }

  public static void use(String sheet, String caseId) {
    clear();
    Map<String, String> row =
        rows(sheet).stream()
            .filter(item -> caseId.equals(item.get("caso_id")))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("No existe el caso " + caseId + " en " + sheet));
    CURRENT.set(Collections.unmodifiableMap(row));
    CURRENT_SHEET.set(sheet);
  }

  public static String value(String column) {
    int separator = column.indexOf('.');
    if (separator >= 0) {
      String sheet = column.substring(0, separator);
      if (!sheet.equals(CURRENT_SHEET.get()))
        throw new IllegalStateException("La hoja seleccionada no corresponde a " + sheet);
      column = column.substring(separator + 1);
    }
    Map<String, String> row = CURRENT.get();
    if (row == null || !row.containsKey(column))
      throw new IllegalStateException("Falta la columna de datos: " + column);
    return row.get(column);
  }

  public static void clear() {
    CURRENT.remove();
    CURRENT_SHEET.remove();
  }
}
