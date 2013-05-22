Feature: Main menu
  In order to give the user a starting point
  The main menu offers a number of distinctive options

  Scenario: The main menu has a list of labeled buttons
    Given I am in the main menu
    Then I should see the following buttons:
      | Continue  |
      | New       |
      | Programs  |
      | Forum     |
      | Community |
      | Upload    |

  Scenario: The Continue button leads to the program view
    Given I am in the main menu
    When I press the Continue button
    Then I should switch to the program view

  Scenario: The Programs button leads to the programs view
    Given I am in the main menu
    When I press the Programs button
    Then I should switch to the programs view
