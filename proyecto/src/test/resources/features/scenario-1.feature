# ultimate-data-sheet: Q29tcHJh
Feature: SauceDemo Access and Purchase 

  @scenario_1
  Scenario Outline: Completion and confirmation of demo purchase
    Given the test data from "Compra" for case "<caso_id>"
    And the user has navigated to the login page
    And the user has logged in with "{{Compra.usuario}}" and "{{Compra.clave}}" and has seen "Products"
    And the user has added "{{Compra.producto}}" to the cart and has proceeded to checkout
    And the user has entered checkout information with first name "{{Compra.nombre}}", last name "{{Compra.apellido}}", postal code "{{Compra.codigo_postal}}" and has seen "Checkout: Overview"
    When the user finishes the purchase
    Then the user sees the confirmation message "Thank you for your order!"

  Examples:
    | caso_id |
    | POS-001 |
