# ultimate-data-sheet: VXN1YXJpb0Jsb3F1ZWFkbw==
Feature: SauceDemo Access and Purchase 

  @scenario_2
  Scenario Outline: Access blocked for locked out account
    Given the test data from "UsuarioBloqueado" for case "<caso_id>"
    And the user is on the login page
    When the user attempts to log in with "{{UsuarioBloqueado.usuario}}" and "{{UsuarioBloqueado.clave}}"
    Then the user sees the error message "{{UsuarioBloqueado.mensaje_esperado}}"
    And the user remains on the login page without the product catalog

  Examples:
    | caso_id |
    | NEG-001 |
