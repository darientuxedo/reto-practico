package com.ultimate.qa.pages;

public final class CheckoutOverviewPage {
  private final RecordedPage page = new RecordedPage();

  public void assertOverview() {
    page.assertPath("/checkout-step-two.html");
    page.assertText(
        page.target(
            "txtCheckout",
            page.by("css", "span[data-test=\"title\"]"),
            page.by("xpath", "//span[@data-test='title']"),
            page.by("xpath", "//span[normalize-space()='Checkout: Overview']"),
            page.by("xpath", "//span[contains(normalize-space(), 'Checkout: Overview')]"),
            page.by("xpath", "//span[contains(@class, 'title')]"),
            page.by("xpath", "/html/body/div/div/div/header/div[2]/span")),
        "Checkout: Overview",
        "equals");
  }

  public void finishPurchase() {
    page.click(
        page.target(
            "finish",
            page.by("id", "finish"),
            page.by("css", "button[data-test=\"finish\"]"),
            page.by("xpath", "//button[@data-test='finish']"),
            page.by("css", "#finish"),
            page.by("xpath", "//*[@id='finish']"),
            page.by("css", "button[name=\"finish\"]")));
  }
}
