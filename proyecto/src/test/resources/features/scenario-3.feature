# ultimate-data-sheet: Q2FuY2VsYWNpb24=
Feature: SauceDemo Access and Purchase 

  @scenario_3
  Scenario Outline: Cancellation on first checkout information form
    Given the test data from "Cancelacion" for case "<caso_id>"
    And the user has opened the login page
    And the user has logged in with "{{Cancelacion.usuario}}" and "{{Cancelacion.clave}}" and has seen the "Products" title
    And the user has added "{{Cancelacion.producto}}" to the cart and has navigated to the checkout information form
    When the user cancels the checkout information form
    Then the user returns to the cart
    And the cart displays "{{Cancelacion.producto}}"
    And the item row for "{{Cancelacion.producto}}" displays quantity "{{Cancelacion.cantidad_esperada}}"
    And the item row for "{{Cancelacion.producto}}" displays price "{{Cancelacion.precio_esperado}}"

  Examples:
    | caso_id |
    | ALT-001 |
