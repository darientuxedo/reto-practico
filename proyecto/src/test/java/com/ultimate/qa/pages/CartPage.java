package com.ultimate.qa.pages;

import com.ultimate.qa.support.DataValues;

public final class CartPage {
  private final RecordedPage page = new RecordedPage();

  public void assertCartPage() {
    page.assertPath("/cart.html");
    page.assertText(
        page.target("Título del carrito", page.by("css", "[data-test='title']")),
        "Your Cart",
        "equals");
    page.visible(page.target("Checkout del carrito", page.by("id", "checkout")));
  }

  public void beginCheckout() {
    assertCartPage();
    page.click(
        page.target(
            "checkout",
            page.by("id", "checkout"),
            page.by("css", "button[data-test=\"checkout\"]"),
            page.by("xpath", "//button[@data-test='checkout']"),
            page.by("css", "#checkout"),
            page.by("xpath", "//*[@id='checkout']"),
            page.by("css", "button[name=\"checkout\"]")));
  }

  public void assertProduct(String productoValue) {
    assertCartPage();
    page.assertText(
        page.target(
            "txtProduc",
            page.by(
                "xpath",
                "//div[@data-test="
                    + "'inventory-item'"
                    + "][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]"),
            page.by(
                "xpath",
                "//div[contains(concat("
                    + "' '"
                    + ",normalize-space(@class),"
                    + "' '"
                    + "),"
                    + "' cart_item '"
                    + ")][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]")),
        productoValue,
        "equals");
  }

  public void assertQuantity(String productoValue, String cantidadEsperadaValue) {
    page.assertText(
        page.target(
            "txtAmount",
            page.by(
                "xpath",
                "//div[@data-test="
                    + "'inventory-item'"
                    + "][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//div[@data-test="
                    + "'item-quantity'"
                    + "]"),
            page.by(
                "xpath",
                "//div[contains(concat("
                    + "' '"
                    + ",normalize-space(@class),"
                    + "' '"
                    + "),"
                    + "' cart_item '"
                    + ")][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//div[@data-test="
                    + "'item-quantity'"
                    + "]")),
        cantidadEsperadaValue,
        "equals");
  }

  public void assertPrice(String productoValue, String precioEsperadoValue) {
    page.assertText(
        page.target(
            "txtPrice",
            page.by(
                "xpath",
                "//div[@data-test="
                    + "'inventory-item'"
                    + "][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//div[@data-test="
                    + "'inventory-item-price'"
                    + "]"),
            page.by(
                "xpath",
                "//div[contains(concat("
                    + "' '"
                    + ",normalize-space(@class),"
                    + "' '"
                    + "),"
                    + "' cart_item '"
                    + ")][.//a[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//div[@data-test="
                    + "'inventory-item-price'"
                    + "]")),
        precioEsperadoValue,
        "equals");
  }
}
