package com.ultimate.qa.steps;

import com.ultimate.qa.pages.ApplicationPage;
import com.ultimate.qa.pages.CartPage;
import com.ultimate.qa.pages.CatalogPage;
import com.ultimate.qa.pages.CheckoutCompletePage;
import com.ultimate.qa.pages.CheckoutInformationPage;
import com.ultimate.qa.pages.CheckoutOverviewPage;
import com.ultimate.qa.pages.LoginPage;
import com.ultimate.qa.support.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class RecordedSteps {
  private final ApplicationPage application = new ApplicationPage();
  private final LoginPage login = new LoginPage();
  private final CatalogPage catalog = new CatalogPage();
  private final CartPage cart = new CartPage();
  private final CheckoutInformationPage information = new CheckoutInformationPage();
  private final CheckoutOverviewPage overview = new CheckoutOverviewPage();
  private final CheckoutCompletePage complete = new CheckoutCompletePage();

  @Given("^los datos de la hoja \"([^\"]+)\" para el caso \"([^\"]+)\"$")
  public void selectDataCase(String sheet, String caseId) {
    TestData.use(sheet, caseId);
  }

  @Given("^the test data from \"([^\"]+)\" for case \"([^\"]+)\"$")
  public void selectEnglishDataCase(String sheet, String caseId) {
    TestData.use(sheet, caseId);
  }

  @Given("^the user has navigated to the login page$")
  public void openLoginForPurchase() {
    application.openLogin();
  }

  @Given(
      "^the user has logged in with \"\\{\\{Compra\\.usuario\\}\\}\" and"
          + " \"\\{\\{Compra\\.clave\\}\\}\" and has seen \"Products\"$")
  public void authenticateBuyer() {
    login.fillUsername(TestData.value("Compra.usuario"));
    login.fillPassword(TestData.value("Compra.clave"));
    login.submitLogin();
    catalog.assertCatalog();
  }

  @Given(
      "^the user has added \"\\{\\{Compra\\.producto\\}\\}\" to the cart and has proceeded to"
          + " checkout$")
  public void addPurchaseProductAndStartCheckout() {
    catalog.addProductToCart(TestData.value("Compra.producto"));
    catalog.openCart();
    cart.beginCheckout();
  }

  @Given(
      "^the user has entered checkout information with first name \"\\{\\{Compra\\.nombre\\}\\}\","
          + " last name \"\\{\\{Compra\\.apellido\\}\\}\", postal code"
          + " \"\\{\\{Compra\\.codigo_postal\\}\\}\" and has seen \"Checkout: Overview\"$")
  public void provideBuyerInformation() {
    information.fillFirstName(TestData.value("Compra.nombre"));
    information.fillLastName(TestData.value("Compra.apellido"));
    information.fillPostalCode(TestData.value("Compra.codigo_postal"));
    information.continueCheckout();
    overview.assertOverview();
  }

  @When("^the user finishes the purchase$")
  public void completePurchase() {
    overview.finishPurchase();
  }

  @Then("^the user sees the confirmation message \"Thank you for your order!\"$")
  public void assertOrderConfirmation() {
    complete.assertOrderConfirmation();
  }

  @Given("^the user is on the login page$")
  public void openLoginForLockedAccount() {
    application.openLogin();
  }

  @When(
      "^the user attempts to log in with \"\\{\\{UsuarioBloqueado\\.usuario\\}\\}\" and"
          + " \"\\{\\{UsuarioBloqueado\\.clave\\}\\}\"$")
  public void attemptLockedLogin() {
    login.fillUsername(TestData.value("UsuarioBloqueado.usuario"));
    login.fillPassword(TestData.value("UsuarioBloqueado.clave"));
    login.submitLogin();
  }

  @Then("^the user sees the error message \"\\{\\{UsuarioBloqueado\\.mensaje_esperado\\}\\}\"$")
  public void assertAccountRejection() {
    login.assertLockedAccount(TestData.value("UsuarioBloqueado.mensaje_esperado"));
  }

  @Then("^the user remains on the login page without the product catalog$")
  public void assertLoginRemainsVisible() {
    login.assertLoginState();
  }

  @Given("^the user has opened the login page$")
  public void openLoginForCancellation() {
    application.openLogin();
  }

  @Given(
      "^the user has logged in with \"\\{\\{Cancelacion\\.usuario\\}\\}\" and"
          + " \"\\{\\{Cancelacion\\.clave\\}\\}\" and has seen the \"Products\" title$")
  public void authenticateBeforeCancellation() {
    login.fillUsername(TestData.value("Cancelacion.usuario"));
    login.fillPassword(TestData.value("Cancelacion.clave"));
    login.submitLogin();
    catalog.assertCatalog();
  }

  @Given(
      "^the user has added \"\\{\\{Cancelacion\\.producto\\}\\}\" to the cart and has navigated to"
          + " the checkout information form$")
  public void addCancellationProductAndStartCheckout() {
    catalog.addProductToCart(TestData.value("Cancelacion.producto"));
    catalog.openCart();
    cart.beginCheckout();
  }

  @When("^the user cancels the checkout information form$")
  public void cancelCheckout() {
    information.cancelCheckout();
  }

  @Then("^the user returns to the cart$")
  public void assertReturnToCart() {
    cart.assertCartPage();
  }

  @Then("^the cart displays \"\\{\\{Cancelacion\\.producto\\}\\}\"$")
  public void assertProductRetained() {
    cart.assertProduct(TestData.value("Cancelacion.producto"));
  }

  @Then(
      "^the item row for \"\\{\\{Cancelacion\\.producto\\}\\}\" displays quantity"
          + " \"\\{\\{Cancelacion\\.cantidad_esperada\\}\\}\"$")
  public void assertQuantityRetained() {
    cart.assertQuantity(
        TestData.value("Cancelacion.producto"), TestData.value("Cancelacion.cantidad_esperada"));
  }

  @Then(
      "^the item row for \"\\{\\{Cancelacion\\.producto\\}\\}\" displays price"
          + " \"\\{\\{Cancelacion\\.precio_esperado\\}\\}\"$")
  public void assertPriceRetained() {
    cart.assertPrice(
        TestData.value("Cancelacion.producto"), TestData.value("Cancelacion.precio_esperado"));
  }
}
