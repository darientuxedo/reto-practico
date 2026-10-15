package com.ultimate.qa.support;

import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class BrowserSession {
  private static final ThreadLocal<WebDriver> CURRENT = new ThreadLocal<>();

  private BrowserSession() {}

  public static WebDriver driver() {
    WebDriver driver = CURRENT.get();
    if (driver == null) throw new IllegalStateException("No hay navegador para este escenario");
    return driver;
  }

  public static void start() {
    if (CURRENT.get() != null) throw new IllegalStateException("Ya existe una sesión activa");
    String browser = System.getProperty("browser", "chrome");
    boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
    WebDriver driver;
    switch (browser) {
      case "chrome":
      case "chromium":
        ChromeOptions chrome = new ChromeOptions();
        chrome.setExperimentalOption("prefs", automationPreferences());
        if (headless) chrome.addArguments("--headless=new");
        String binary = System.getProperty("browser.binary", "");
        if (!binary.isEmpty()) chrome.setBinary(binary);
        driver = new ChromeDriver(chrome);
        break;
      case "firefox":
        FirefoxOptions firefox = new FirefoxOptions();
        if (headless) firefox.addArguments("-headless");
        driver = new FirefoxDriver(firefox);
        break;
      case "edge":
        EdgeOptions edge = new EdgeOptions();
        edge.setExperimentalOption("prefs", automationPreferences());
        if (headless) edge.addArguments("--headless=new");
        driver = new EdgeDriver(edge);
        break;
      default:
        throw new IllegalArgumentException("Navegador no soportado: " + browser);
    }
    CURRENT.set(driver);
    try {
      driver.manage().timeouts().implicitlyWait(Duration.ZERO);
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
      driver
          .manage()
          .window()
          .setSize(
              new Dimension(
                  Integer.getInteger("viewport.width", 1366),
                  Integer.getInteger("viewport.height", 768)));
    } catch (RuntimeException error) {
      close();
      throw error;
    }
  }

  public static String secret(String name) {
    String value = System.getenv(name);
    if (value == null || value.isEmpty())
      throw new IllegalStateException("Define la variable de entorno " + name);
    return value;
  }

  private static Map<String, Object> automationPreferences() {
    // Only the disposable test profile: browser-owned save/autofill prompts
    // must not intercept keyboard input intended for the application.
    return Map.of(
        "credentials_enable_service",
        false,
        "profile.password_manager_enabled",
        false,
        "profile.password_manager_leak_detection",
        false,
        "autofill.profile_enabled",
        false,
        "autofill.credit_card_enabled",
        false);
  }

  public static void close() {
    WebDriver driver = CURRENT.get();
    CURRENT.remove();
    if (driver != null) driver.quit();
  }
}
