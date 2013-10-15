Feature: Repeat brick delay
  Each iteration of a Repeat brick takes at least 20 milliseconds.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: No more than 200 iterations in 2 seconds
    Given 'Object' has a Start script
    And this script has a set 'i' to 0 brick
    And this script has a set 'k' to 0 brick
    And this script has a Wait 2 seconds brick
    And this script has a set 'k' to 'i' brick

    Given 'Object' has a Start script
    And this script has a Repeat 400 times brick
    And this script has a change 'i' by 1 brick
    And this script has a Repeat end brick

    When I start the program
    And I wait until the program has stopped
    Then the variable 'k' should be be less than or equal 200
    And the variable 'k' should be greater than or equal 2
