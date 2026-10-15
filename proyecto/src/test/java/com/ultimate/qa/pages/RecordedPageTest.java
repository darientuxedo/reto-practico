package com.ultimate.qa.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Offline contracts: no website or browser is needed for these tests. */
public class RecordedPageTest {
  private WebElement element(String text, AtomicInteger clicks) {
    return (WebElement)
        Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[] {WebElement.class},
            (proxy, method, args) -> {
              switch (method.getName()) {
                case "getText":
                  return text;
                case "isDisplayed":
                case "isEnabled":
                  return true;
                case "click":
                  clicks.incrementAndGet();
                  return null;
                case "equals":
                  return proxy == args[0];
                case "hashCode":
                  return System.identityHashCode(proxy);
                case "toString":
                  return "fixture-element";
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }

  private RecordedPage page(Map<String, List<WebElement>> matches) {
    WebDriver driver =
        (WebDriver)
            Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {WebDriver.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("findElements"))
                    return matches.getOrDefault(args[0].toString(), List.of());
                  if (method.getName().equals("toString")) return "fixture-driver";
                  throw new UnsupportedOperationException(method.getName());
                });
    return new RecordedPage(driver, Duration.ofMillis(20));
  }

  @Test
  public void ambiguousPrimaryNeverClicks() {
    AtomicInteger clicks = new AtomicInteger();
    RecordedPage page =
        page(
            Map.of(
                By.id("target").toString(), List.of(element("A", clicks), element("A", clicks))));
    assertThrows(
        IllegalStateException.class, () -> page.click(page.target("Target", By.id("target"))));
    assertEquals(0, clicks.get());
  }

  @Test
  public void conflictingFallbacksNeverClick() {
    AtomicInteger clicks = new AtomicInteger();
    RecordedPage page =
        page(
            Map.of(
                By.id("a").toString(),
                List.of(element("A", clicks)),
                By.id("b").toString(),
                List.of(element("B", clicks))));
    assertThrows(
        IllegalStateException.class,
        () -> page.click(page.target("Target", By.id("missing"), By.id("a"), By.id("b"))));
    assertEquals(0, clicks.get());
  }

  @Test
  public void equivalentFallbacksPerformExactlyOneAction() {
    AtomicInteger clicks = new AtomicInteger();
    WebElement same = element("A", clicks);
    RecordedPage page =
        page(Map.of(By.id("a").toString(), List.of(same), By.id("b").toString(), List.of(same)));
    page.click(page.target("Target", By.id("missing"), By.id("a"), By.id("b")));
    assertEquals(1, clicks.get());
  }

  @Test
  public void priceAssertionsAreExactNotSubstring() {
    RecordedPage page =
        page(Map.of(By.id("price").toString(), List.of(element("$19.99", new AtomicInteger()))));
    assertThrows(
        AssertionError.class,
        () -> page.assertText(page.target("Price", By.id("price")), "$9.99", "equals"));
    page.assertText(page.target("Price", By.id("price")), "$19.99", "equals");
  }

  @Test
  public void configuredBaseIsValidatedAndPreservesPath() {
    String previous = System.getProperty("base.url");
    try {
      System.setProperty("base.url", "https://qa.example.test/app/");
      assertEquals(
          "https://qa.example.test/app/cart.html",
          RecordedPage.resolveUrl("https://www.saucedemo.com/cart.html"));
      for (String invalid :
          List.of(
              "file:///private",
              "https://user:password@example.test",
              "https://example.test/?token=private")) {
        System.setProperty("base.url", invalid);
        assertThrows(
            IllegalArgumentException.class,
            () -> RecordedPage.resolveUrl("https://www.saucedemo.com/"));
      }
    } finally {
      if (previous == null) System.clearProperty("base.url");
      else System.setProperty("base.url", previous);
    }
  }
}
