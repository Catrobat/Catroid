Feature: Default Program

  Scenario: Create and execute the default program
    Given I have a default program
    When I start the program
    And I tap the default object
    Then the default object is changing its costumes
