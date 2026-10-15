package com.ultimate.qa;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "build/generated-features",
    glue = "com.ultimate.qa",
    plugin = {
      "pretty",
      "html:build/reports/cucumber.html",
      "json:build/reports/cucumber.json",
      "junit:build/reports/cucumber.xml"
    },
    monochrome = true)
public class RunCucumberTest {}
