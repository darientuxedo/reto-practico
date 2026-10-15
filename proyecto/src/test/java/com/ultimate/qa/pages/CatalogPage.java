package com.ultimate.qa.pages;

import com.ultimate.qa.support.DataValues;

public final class CatalogPage {
  private final RecordedPage page = new RecordedPage();

  public void assertCatalog() {
    page.assertPath("/inventory.html");
    page.assertText(
        page.target(
            "txtProducts",
            page.by("css", "span[data-test=\"title\"]"),
            page.by("xpath", "//span[@data-test='title']"),
            page.by("xpath", "//span[normalize-space()='Products']"),
            page.by("xpath", "//span[contains(normalize-space(), 'Products')]"),
            page.by("xpath", "//span[contains(@class, 'title')]"),
            page.by("xpath", "/html/body/div/div/div/header/div[2]/span")),
        "Products",
        "equals");
  }

  public void addProductToCart(String productoValue) {
    page.click(
        page.target(
            "addToCart",
            page.by(
                "xpath",
                "//div[@data-test="
                    + "'inventory-item-description'"
                    + "][.//div[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//button[normalize-space(.)="
                    + "'Add to cart'"
                    + "]"),
            page.by(
                "xpath",
                "//div[contains(concat("
                    + "' '"
                    + ",normalize-space(@class),"
                    + "' '"
                    + "),"
                    + "' inventory_item '"
                    + ")][.//div[normalize-space(.)="
                    + DataValues.xpath(productoValue)
                    + "]]//button[normalize-space(.)="
                    + "'Add to cart'"
                    + "]")));
  }

  public void openCart() {
    page.click(
        page.target(
            "btnCart",
            page.by("css", "a[data-test=\"shopping-cart-link\"]"),
            page.by("xpath", "//a[@data-test='shopping-cart-link']"),
            page.by("css", "a[aria-label=\"Cart\\,\\ 1\\ items\"]"),
            page.by("xpath", "//a[@aria-label='Cart, 1 items']"),
            page.by("xpath", "//*[@id='shopping_cart_container']//a"),
            page.by("xpath", "//a[contains(@class, 'shopping_cart_link')]")));
  }
}
