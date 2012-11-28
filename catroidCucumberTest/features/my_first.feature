Feature: Create project feature

  Scenario: I create a new project
    When I press "Neu"
    Then I see "Neues Projekt"
    And I see "Projektname"
    And I see "Projektbeschreibung"
    And I see "Ok"
    And I see "Abbrechen"
    Then I enter "My project" into input field number 1
    Then I enter "My description" into input field number 2
    When I press "Ok"
    Then I see "My project"
    And I see "Hintergrund"
    And I see "Catroid"
    When I go back
    Then I see "Neu"
