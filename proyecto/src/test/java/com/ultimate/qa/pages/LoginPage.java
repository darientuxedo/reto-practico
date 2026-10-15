package com.ultimate.qa.pages;

import com.ultimate.qa.support.DataValues;

public final class LoginPage {
  private final RecordedPage page = new RecordedPage();

  public void fillUsername(String usuarioValue) {
    page.fill(
        page.target(
            "userName",
            page.by("id", "user-name"),
            page.by("css", "input[data-test=\"username\"]"),
            page.by("xpath", "//input[@data-test='username']"),
            page.by("css", "#user-name"),
            page.by("css", "input[aria-label=\"Username\"]"),
            page.by("xpath", "//input[@aria-label='Username']")),
        usuarioValue);
  }

  public void fillPassword(String claveValue) {
    page.fill(
        page.target(
            "password",
            page.by("id", "password"),
            page.by("css", "input[data-test=\"password\"]"),
            page.by("xpath", "//input[@data-test='password']"),
            page.by("css", "#password"),
            page.by("css", "input[aria-label=\"Password\"]"),
            page.by("xpath", "//input[@aria-label='Password']")),
        DataValues.sensitive(claveValue));
  }

  public void submitLogin() {
    page.click(
        page.target(
            "loginButton",
            page.by("id", "login-button"),
            page.by("css", "input[data-test=\"login-button\"]"),
            page.by("xpath", "//input[@data-test='login-button']"),
            page.by("css", "#login-button"),
            page.by("xpath", "//*[@id='login-button']"),
            page.by("css", "input[name=\"login-button\"]")));
  }

  public void assertLoginState() {
    page.assertPath("/");
    page.visible(page.target("Formulario de acceso", page.by("id", "login-button")));
    page.notVisible(page.target("Catálogo protegido", page.by("id", "inventory_container")));
  }

  public void assertLockedAccount(String mensajeEsperadoValue) {
    assertLoginState();
    page.assertText(
        page.target(
            "txtLocked",
            page.by("css", "h3[data-test=\"error\"]"),
            page.by("xpath", "//h3[@data-test='error']"),
            page.by("css", "h3[role=\"alert\"]"),
            page.by("xpath", "//h3[@role='alert']"),
            page.by("xpath", "//*[@id='login_button_container']//h3"),
            page.by("xpath", "/html/body/div/div/div[2]/div/div/div/form/div[3]/h3")),
        mensajeEsperadoValue,
        "equals");
  }
}
