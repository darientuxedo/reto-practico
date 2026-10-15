package com.ultimate.qa.pages;

import com.ultimate.qa.support.BrowserSession;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Native actions, unique targets, bounded waits. Healing locates; it never retries an action. */
public final class RecordedPage {
  private final WebDriver suppliedDriver;
  private final Duration suppliedTimeout;

  public RecordedPage() {
    this(null, null);
  }

  RecordedPage(WebDriver driver, Duration timeout) {
    suppliedDriver = driver;
    suppliedTimeout = timeout;
  }

  public static final class Target {
    private final String label;
    private final List<By> candidates;

    private Target(String label, By... candidates) {
      this.label = label;
      this.candidates = Arrays.asList(candidates);
    }
  }

  private WebDriver driver() {
    return suppliedDriver != null ? suppliedDriver : BrowserSession.driver();
  }

  private WebDriverWait waitFor() {
    Duration timeout =
        suppliedTimeout != null
            ? suppliedTimeout
            : Duration.ofSeconds(Long.getLong("wait.seconds", 15));
    if (timeout.isZero() || timeout.isNegative())
      throw new IllegalArgumentException("wait.seconds debe ser positivo");
    WebDriverWait wait = new WebDriverWait(driver(), timeout);
    wait.ignoring(StaleElementReferenceException.class);
    return wait;
  }

  public Target target(String label, By... candidates) {
    return new Target(label, candidates);
  }

  public By by(String type, String selector) {
    switch (type) {
      case "css":
        return By.cssSelector(selector);
      case "xpath":
        return By.xpath(selector);
      case "id":
        return By.id(selector);
      case "name":
        return By.name(selector);
      case "linktext":
        return By.linkText(selector);
      default:
        throw new IllegalArgumentException("Selector no soportado: " + type);
    }
  }

  private WebElement find(Target target) {
    WebElement fallback = null;
    for (int i = 0; i < target.candidates.size(); i++) {
      List<WebElement> elements = driver().findElements(target.candidates.get(i));
      if (elements.size() > 1)
        throw new IllegalStateException(
            "Elemento ambiguo: " + target.label + "; coincidencias: " + elements.size());
      if (elements.isEmpty()) continue;
      WebElement found = elements.get(0);
      if (i == 0) return found;
      if (fallback != null && !fallback.equals(found))
        throw new IllegalStateException("Selectores alternativos en conflicto: " + target.label);
      fallback = found;
    }
    return fallback;
  }

  private WebElement ready(Target target, boolean clickable) {
    try {
      return waitFor()
          .until(
              d -> {
                WebElement element = find(target);
                return element != null
                        && element.isDisplayed()
                        && (!clickable || element.isEnabled())
                    ? element
                    : null;
              });
    } catch (TimeoutException error) {
      throw new AssertionError("Elemento no disponible: " + target.label, error);
    }
  }

  static String resolveUrl(String url) {
    java.net.URI original = java.net.URI.create(url);
    String scheme = original.getScheme();
    if ((!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))
        || original.getHost() == null
        || original.getUserInfo() != null)
      throw new IllegalArgumentException("URL HTTP(S) inválida o con credenciales");
    String base = System.getProperty("base.url", "");
    if (!base.isEmpty()) {
      java.net.URI configured = java.net.URI.create(base);
      if ((!"http".equalsIgnoreCase(configured.getScheme())
              && !"https".equalsIgnoreCase(configured.getScheme()))
          || configured.getHost() == null
          || configured.getUserInfo() != null
          || configured.getQuery() != null
          || configured.getFragment() != null)
        throw new IllegalArgumentException(
            "base.url debe ser HTTP(S), sin credenciales, consulta ni fragmento");
      url =
          base.replaceAll("/+$", "")
              + original.getRawPath()
              + (original.getRawQuery() == null ? "" : "?" + original.getRawQuery())
              + (original.getRawFragment() == null ? "" : "#" + original.getRawFragment());
    }
    return url;
  }

  public void open(String url) {
    driver().get(resolveUrl(url));
  }

  public void assertPath(String path) {
    java.net.URI expected = java.net.URI.create(resolveUrl("https://www.saucedemo.com" + path));
    try {
      waitFor()
          .until(
              d -> {
                java.net.URI actual = java.net.URI.create(d.getCurrentUrl());
                return Objects.equals(expected.getScheme(), actual.getScheme())
                    && Objects.equals(expected.getAuthority(), actual.getAuthority())
                    && Objects.equals(expected.getPath(), actual.getPath());
              });
    } catch (TimeoutException error) {
      throw new AssertionError("No se alcanzó la pantalla esperada: " + path, error);
    }
  }

  public void click(Target target) {
    ready(target, true).click();
  }

  public void fill(Target target, String value) {
    WebElement element = ready(target, true);
    element.click();
    element.clear();
    element.sendKeys(value);
    // Do not disclose input values (passwords/tokens) in failure messages.
    try {
      waitFor()
          .until(
              d -> {
                WebElement current = find(target);
                return current != null && value.equals(current.getDomProperty("value"));
              });
    } catch (TimeoutException error) {
      throw new AssertionError("El campo no conservó el dato: " + target.label, error);
    }
  }

  public void select(Target target, String value, String mode) {
    Select select = new Select(ready(target, true));
    if (mode.equals("index")) select.selectByIndex(Integer.parseInt(value));
    else if (mode.equals("label") || mode.equals("text")) select.selectByVisibleText(value);
    else select.selectByValue(value);
  }

  public void visible(Target target) {
    ready(target, false);
  }

  public void notVisible(Target target) {
    waitFor()
        .until(
            d -> {
              WebElement element = find(target);
              return element == null || !element.isDisplayed();
            });
  }

  public void assertText(Target target, String expected, String mode) {
    final String[] actual = {"<elemento ausente>"};
    try {
      waitFor()
          .until(
              d -> {
                WebElement element = find(target);
                if (element == null || !element.isDisplayed()) return false;
                actual[0] = element.getText();
                String observed = actual[0].replaceAll("(?U)\\s+", " ").trim();
                String wanted = expected.replaceAll("(?U)\\s+", " ").trim();
                return mode.equals("contains")
                    ? observed.contains(wanted)
                    : observed.equals(wanted);
              });
    } catch (TimeoutException error) {
      throw new AssertionError(
          target.label
              + " | esperado ("
              + mode
              + "): ["
              + expected
              + "] | obtenido: ["
              + actual[0]
              + "]",
          error);
    }
  }

  public void assertUrl(String expected, String mode) {
    try {
      waitFor()
          .until(
              d ->
                  mode.equals("contains")
                      ? d.getCurrentUrl().contains(expected)
                      : d.getCurrentUrl().equals(expected));
    } catch (TimeoutException error) {
      throw new AssertionError(
          "URL esperada: " + expected + " | obtenida: " + driver().getCurrentUrl(), error);
    }
  }

  public void pressEnter(Target target) {
    ready(target, true).sendKeys(Keys.ENTER);
  }

  public void hover(Target target) {
    new Actions(driver()).moveToElement(ready(target, false)).perform();
  }

  public void doubleClick(Target target) {
    new Actions(driver()).doubleClick(ready(target, true)).perform();
  }

  public void rightClick(Target target) {
    new Actions(driver()).contextClick(ready(target, true)).perform();
  }
}
