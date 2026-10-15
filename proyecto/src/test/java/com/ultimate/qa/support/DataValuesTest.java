package com.ultimate.qa.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import javax.xml.xpath.XPathFactory;
import org.junit.Test;

public class DataValuesTest {
  @Test
  public void xpathEscapingPreservesQuotesAndSpecialCharactersAsData() throws Exception {
    for (String value :
        List.of("", "Bike-Light_One", "Kid's bike", "Kid's \"Bike\" [x] | //button", "Árbol_日本")) {
      assertEquals(
          value,
          XPathFactory.newInstance().newXPath().evaluate(DataValues.xpath(value), (Object) null));
    }
  }

  @Test
  public void malformedSecretReferenceFailsWithoutPrintingItsValue() {
    assertThrows(IllegalArgumentException.class, () -> DataValues.sensitive("${QA_SECRET_typo}"));
    assertThrows(IllegalArgumentException.class, () -> DataValues.sensitive(null));
  }
}
