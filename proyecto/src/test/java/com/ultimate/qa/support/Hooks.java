package com.ultimate.qa.support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public final class Hooks {
  @Before
  public void beforeScenario(Scenario scenario) {
    BrowserSession.start();
  }

  @After
  public void afterScenario(Scenario scenario) {
    try {
      if (scenario.isFailed()) {
        try {
          scenario.attach(
              ((TakesScreenshot) BrowserSession.driver()).getScreenshotAs(OutputType.BYTES),
              "image/png",
              "Fallo del escenario");
        } catch (RuntimeException evidenceError) {
          scenario.log("No se pudo capturar la evidencia final");
        }
      }
    } finally {
      TestData.clear();
      BrowserSession.close();
    }
  }
}
