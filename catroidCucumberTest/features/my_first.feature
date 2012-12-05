Feature: Create project feature

  Scenario: I create a new project
    When I press "New"
    Then I see "New project"
    And I see "Project name"
    And I see "Project description"
    And I see "OK"
    And I see "Cancel"
    Then I enter "My project" into input field number 1
    Then I enter "My description" into input field number 2
    Then I wait a little
    When I press "OK"
    Then I see "My project"
    And I see "Background"
    And I see "Catroid"
    When I go back
    Then I see "New"
