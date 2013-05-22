Feature: RepeatBrick

  Scenario: I change the y position of the sprite four times.
    Given there is one sprite
    And the sprite has one script
    And the script has one RepeatBrick with 4
    And the script has one ChangeYbyNBrick with -10
    And the script has one LoopEndBrick
    When I run the script
    And I wait 80 ms
    Then the sprite has a y position of -40
