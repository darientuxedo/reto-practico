package com.ultimate.qa.support;

/** Values from the selected workbook row, escaped as data in selectors. */
public final class DataValues {
  private DataValues() {}

  public static String sensitive(String value) {
    if (value == null) throw new IllegalArgumentException("Falta el dato sensible");
    if (value.matches("\\$\\{QA_SECRET_[A-F0-9]+\\}"))
      return BrowserSession.secret(value.substring(2, value.length() - 1));
    if (value.startsWith("${"))
      throw new IllegalArgumentException(
          "Referencia a secreto inválida; usa la variable QA_SECRET indicada en README");
    return value;
  }

  public static String xpath(String value) {
    if (!value.contains("'")) return "'" + value + "'";
    if (!value.contains("\"")) return "\"" + value + "\"";
    return "concat('" + value.replace("'", "',\"'\",'") + "')";
  }

  public static String css(String value) {
    StringBuilder result = new StringBuilder();
    value
        .codePoints()
        .forEach(point -> result.append("\\").append(Integer.toHexString(point)).append(' '));
    return result.toString();
  }
}
