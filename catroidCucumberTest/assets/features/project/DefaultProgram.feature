# DEPRECATED
Feature: Default Program

  Scenario: Create and execute the default program
    Given I have a default program
    When I start the program
    Then the program is being executed
