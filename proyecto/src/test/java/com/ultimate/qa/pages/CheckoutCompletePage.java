package com.ultimate.qa.pages;

public final class CheckoutCompletePage {
  private final RecordedPage page = new RecordedPage();

  public void assertOrderConfirmation() {
    page.assertPath("/checkout-complete.html");
    page.assertText(
        page.target(
            "txtOrder",
            page.by("css", "h2[data-test=\"complete-header\"]"),
            page.by("xpath", "//h2[@data-test='complete-header']"),
            page.by("xpath", "//*[@id='checkout_complete_container']//h2"),
            page.by("xpath", "//h2[normalize-space()='Thank you for your order!']"),
            page.by("xpath", "//h2[contains(normalize-space(), 'Thank you for your order!')]"),
            page.by("xpath", "//h2[contains(@class, 'complete-header')]")),
        "Thank you for your order!",
        "equals");
  }
}
