#Feature: Create and delete a project.
#
#  Scenario: I create a new project and delete it again.
#    When I see "Catroid"
#    And I see "Continue"
#    And I see "New"
#    And I see "Programs"
#    Then I press "New"
#    Then I see "New project"
#    And I see "Project name:"
#    And I see "Project description:"
#    And I see "OK"
#    And I see "Cancel"
#    Then I enter "test-project" into input field number 1
#    Then I enter "test-description" into input field number 2
#    Then I wait a little
#    When I press "OK"
#    Then I see "test-project"
#    And I see "Background"
#    And I see "Catroid"
#    When I go back
#    Then I see "Catroid"
#    And I see "Continue"
#    And I see "New"
#    And I see "Programs"
#    When I press "Programs"
#    Then I see "Project: test-project"
#    When I long press list item number 1
#    Then I see "Rename"
#    And I see "Set description"
#    And I see "Delete"
#    And I see "Copy"
#    When I press "Delete"
#    Then I see "Project: My first project"
