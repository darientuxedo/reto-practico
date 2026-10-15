package com.ultimate.qa.pages;

public final class ApplicationPage {
  private final RecordedPage page = new RecordedPage();

  public void openLogin() {
    page.open("https://www.saucedemo.com/");
  }
}
