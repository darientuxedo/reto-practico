package com.ultimate.qa.pages;

public final class CheckoutInformationPage {
  private final RecordedPage page = new RecordedPage();

  private void assertInformationPage() {
    page.assertPath("/checkout-step-one.html");
    page.assertText(
        page.target("Información de compra", page.by("css", "[data-test='title']")),
        "Checkout: Your Information",
        "equals");
  }

  public void fillFirstName(String nombreValue) {
    assertInformationPage();
    page.fill(
        page.target(
            "firstName",
            page.by("id", "first-name"),
            page.by("css", "input[data-test=\"firstName\"]"),
            page.by("xpath", "//input[@data-test='firstName']"),
            page.by("css", "#first-name"),
            page.by("css", "input[aria-label=\"First\\ Name\"]"),
            page.by("xpath", "//input[@aria-label='First Name']")),
        nombreValue);
  }

  public void fillLastName(String apellidoValue) {
    page.fill(
        page.target(
            "lastName",
            page.by("id", "last-name"),
            page.by("css", "input[data-test=\"lastName\"]"),
            page.by("xpath", "//input[@data-test='lastName']"),
            page.by("css", "#last-name"),
            page.by("css", "input[aria-label=\"Last\\ Name\"]"),
            page.by("xpath", "//input[@aria-label='Last Name']")),
        apellidoValue);
  }

  public void fillPostalCode(String codigoPostalValue) {
    page.fill(
        page.target(
            "postalCode",
            page.by("id", "postal-code"),
            page.by("css", "input[data-test=\"postalCode\"]"),
            page.by("xpath", "//input[@data-test='postalCode']"),
            page.by("css", "#postal-code"),
            page.by("css", "input[aria-label=\"Zip\\/Postal\\ Code\"]"),
            page.by("xpath", "//input[@aria-label='Zip/Postal Code']")),
        codigoPostalValue);
  }

  public void continueCheckout() {
    page.click(
        page.target(
            "continue",
            page.by("id", "continue"),
            page.by("css", "input[data-test=\"continue\"]"),
            page.by("xpath", "//input[@data-test='continue']"),
            page.by("css", "#continue"),
            page.by("xpath", "//*[@id='continue']"),
            page.by("css", "input[name=\"continue\"]")));
  }

  public void cancelCheckout() {
    assertInformationPage();
    page.click(
        page.target(
            "cancel",
            page.by("id", "cancel"),
            page.by("css", "button[data-test=\"cancel\"]"),
            page.by("xpath", "//button[@data-test='cancel']"),
            page.by("css", "#cancel"),
            page.by("xpath", "//*[@id='cancel']"),
            page.by("css", "button[name=\"cancel\"]")));
  }
}
